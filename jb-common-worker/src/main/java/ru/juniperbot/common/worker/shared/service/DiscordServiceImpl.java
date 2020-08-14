/*
 * This file is part of JuniperBot.
 *
 * JuniperBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * JuniperBot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JuniperBot. If not, see <http://www.gnu.org/licenses/>.
 */
package ru.juniperbot.common.worker.shared.service;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import com.neovisionaries.ws.client.WebSocketFrame;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.internal.requests.Requester;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.juniperbot.common.configuration.CommonProperties;
import ru.juniperbot.common.persistence.entity.WebHook;
import ru.juniperbot.common.support.ModuleListener;
import ru.juniperbot.common.worker.configuration.WorkerProperties;
import ru.juniperbot.common.worker.message.service.MessageService;
import ru.juniperbot.common.worker.shared.support.DiscordHttpRequestFactory;
import ru.juniperbot.common.worker.shared.support.JmxJDAMBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Service
public class DiscordServiceImpl extends ListenerAdapter implements DiscordService {

    @Autowired
    private WorkerProperties workerProperties;

    @Autowired
    private MessageService messageService;

    @Autowired
    private IEventManager eventManager;

    @Getter
    private ShardManager shardManager;

    @Autowired(required = false)
    private List<ModuleListener> moduleListeners;

    @Autowired(required = false)
    private AudioService audioService;

    @Autowired
    private MBeanExporter mBeanExporter;

    @Autowired
    private CommonProperties commonProperties;

    private volatile String cachedUserId;

    @PostConstruct
    public void init() {
        String token = workerProperties.getDiscord().getToken();
        Objects.requireNonNull(token, "No Discord Token specified");
        try {
            RestAction.setPassContext(false);
            DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                    .setToken(token)
                    .setEventManagerProvider(id -> eventManager)
                    .addEventListeners(this)
                    .setShardsTotal(workerProperties.getDiscord().getShardsTotal())
                    .setEnableShutdownHook(false);
            if (audioService != null) {
                audioService.configure(this, builder);
            }
            shardManager = builder.build();
        } catch (LoginException e) {
            log.error("Could not login user with specified token", e);
        }
    }

    @PreDestroy
    public void destroy() {
        // destroy every service manually before discord shutdown
        if (CollectionUtils.isNotEmpty(moduleListeners)) {
            moduleListeners.forEach(listener -> {
                try {
                    listener.onShutdown();
                } catch (Exception e) {
                    log.error("Could not shutdown listener [{}] correctly", listener, e);
                }
            });
        }
        shardManager.shutdown();
    }

    @Override
    public void onReady(ReadyEvent event) {
        mBeanExporter.registerManagedResource(new JmxJDAMBean(event.getJDA()));
        setUpStatus();
    }

    @Override
    public void onResume(ResumedEvent event) {
        setUpStatus();
    }

    @Override
    public void onException(ExceptionEvent event) {
        log.error("JDA error", event.getCause());
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        WebSocketFrame frame = event.getServiceCloseFrame();
        if (frame != null) {
            log.warn("WebSocket connection closed with code {}: {}", frame.getCloseCode(), frame.getCloseReason());
        }
    }

    @Override
    public void executeWebHook(WebHook webHook, WebhookMessage message, Consumer<WebHook> onAbsent) {
        if (message == null) {
            return;
        }
        WebhookClient client = new WebhookClientBuilder(webHook.getHookId(), webHook.getToken()).build();
        client.send(message).whenComplete((v, e) -> {
            if (e != null && e.getMessage().contains("Request returned failure 404")) {
                onAbsent.accept(webHook);
            }
            client.close();
        });
    }

    @Override
    public boolean isConnected() {
        return getJda() != null && JDA.Status.CONNECTED.equals(getJda().getStatus());
    }

    @Override
    public boolean isConnected(long guildId) {
        return shardManager != null && JDA.Status.CONNECTED.equals(getShard(guildId).getStatus());
    }

    @Override
    public JDA getJda() {
        if (shardManager == null) {
            return null;
        }
        return shardManager.getShards().iterator().next();
    }

    @Override
    public User getSelfUser() {
        return getJda().getSelfUser();
    }

    @Override
    public JDA getShardById(int shardId) {
        return shardManager.getShardById(shardId);
    }

    @Override
    public Guild getGuildById(long guildId) {
        return shardManager.getGuildById(guildId);
    }

    @Override
    public User getUserById(long userId) {
        return shardManager.getUserById(userId);
    }

    @Override
    public User getUserById(String userId) {
        return shardManager.getUserById(userId);
    }

    @Override
    public TextChannel getTextChannelById(long channelId) {
        return shardManager.getTextChannelById(channelId);
    }

    @Override
    public TextChannel getTextChannelById(String channelId) {
        return shardManager.getTextChannelById(channelId);
    }

    @Override
    public VoiceChannel getVoiceChannelById(long channelId) {
        return shardManager.getVoiceChannelById(channelId);
    }

    @Override
    public VoiceChannel getVoiceChannelById(String channelId) {
        return shardManager.getVoiceChannelById(channelId);
    }

    @Override
    public JDA getShard(long guildId) {
        return shardManager.getShardById((int) ((guildId >> 22) % workerProperties.getDiscord().getShardsTotal()));
    }

    @Override
    public boolean isSuperUser(User user) {
        return user != null && Objects.equals(user.getId(), commonProperties.getDiscord().getSuperUserId());
    }

    @Override
    public VoiceChannel getDefaultMusicChannel(long guildId) {
        if (!isConnected(guildId)) {
            return null;
        }
        Guild guild = shardManager.getGuildById(guildId);
        if (guild == null) {
            return null;
        }
        VoiceChannel channel;
        String channels = messageService.getMessage("discord.command.audio.channels");
        if (StringUtils.isNotEmpty(channels)) {
            for (String name : channels.split(",")) {
                channel = guild.getVoiceChannelsByName(name, true).stream().findAny().orElse(null);
                if (channel != null) {
                    return channel;
                }
            }
        }
        return guild.getVoiceChannels().stream().findAny().orElse(null);
    }

    @Override
    public Member getMember(long guildId, long userId) {
        Guild guild = shardManager.getGuildById(guildId);
        if (guild == null) {
            return null;
        }
        return guild.getMemberById(userId);
    }

    @Override
    public String getUserId() {
        if (cachedUserId != null) {
            return cachedUserId;
        }

        int attempt = 0;
        RestTemplate restTemplate = new RestTemplate(new DiscordHttpRequestFactory(workerProperties.getDiscord().getToken()));
        while (cachedUserId == null && attempt++ < 5) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(Requester.DISCORD_API_PREFIX + "/users/@me", String.class);
                if (!HttpStatus.OK.equals(response.getStatusCode())) {
                    log.warn("Could not get userId, endpoint returned {}", response.getStatusCode());
                    continue;
                }
                JSONObject object = new JSONObject(response.getBody());
                cachedUserId = object.getString("id");
                if (StringUtils.isNotEmpty(cachedUserId)) {
                    break;
                }
            } catch (Exception e) {
                // fall down
            }
            log.error("Could not request my own userId from Discord, will retry a few times");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        if (cachedUserId == null) {
            throw new RuntimeException("Failed to retrieve my own userId from Discord");
        }
        return cachedUserId;
    }

    private void setUpStatus() {
        shardManager.setStatus(OnlineStatus.IDLE);
        String playingStatus = workerProperties.getDiscord().getPlayingStatus();
        if (StringUtils.isNotEmpty(playingStatus)) {
            shardManager.setActivity(Activity.playing(playingStatus));
        }
    }


    @Override
    public void sendBonusMessage(long channelId) {
        TextChannel channel = shardManager.getTextChannelById(channelId);
        if (channel != null) {
            messageService.onEmbedMessage(channel, "discord.bonus.feature",
                    commonProperties.getBranding().getWebsiteUrl());
        }
    }

    @Override
    public void sendBonusMessage(long channelId, String title) {
        TextChannel channel = shardManager.getTextChannelById(channelId);
        if (channel != null) {
            messageService.onTitledMessage(channel, title, "discord.bonus.feature",
                    commonProperties.getBranding().getWebsiteUrl());
        }
    }
}
