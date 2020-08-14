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
package ru.juniperbot.common.service.impl;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import ru.juniperbot.common.model.command.CommandInfo;
import ru.juniperbot.common.model.discord.GuildDto;
import ru.juniperbot.common.model.discord.WebhookDto;
import ru.juniperbot.common.model.request.*;
import ru.juniperbot.common.model.status.StatusDto;
import ru.juniperbot.common.service.GatewayService;

import java.util.List;

import static ru.juniperbot.common.configuration.RabbitConfiguration.*;

@Service
public class GatewayServiceImpl implements GatewayService {

    @Autowired
    private AmqpTemplate template;

    @Override
    public GuildDto getGuildInfo(long guildId) {
        GuildDto dto = template.convertSendAndReceiveAsType(QUEUE_GUILD_INFO_REQUEST, guildId,
                new ParameterizedTypeReference<>() {
                });
        return dto != null && dto.getId() != null ? dto : null;
    }

    @Override
    public void updateRanking(RankingUpdateRequest request) {
        template.convertAndSend(QUEUE_RANKING_UPDATE_REQUEST, request);
    }

    @Override
    public List<CommandInfo> getCommandList() {
        return template.convertSendAndReceiveAsType(QUEUE_COMMAND_LIST_REQUEST, "1",
                new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public StatusDto getWorkerStatus() {
        return template.convertSendAndReceiveAsType(QUEUE_STATUS_REQUEST, "1",
                new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public WebhookDto getWebhook(WebhookRequest request) {
        WebhookDto dto = template.convertSendAndReceiveAsType(QUEUE_WEBHOOK_GET_REQUEST, request,
                new ParameterizedTypeReference<>() {
                });
        return dto != null && dto.getId() != null ? dto : null;
    }

    @Override
    public void updateWebhook(WebhookDto webhook) {
        template.convertAndSend(QUEUE_WEBHOOK_UPDATE_REQUEST, webhook);
    }

    @Override
    public boolean deleteWebhook(WebhookRequest request) {
        return Boolean.TRUE.equals(template.convertSendAndReceiveAsType(QUEUE_WEBHOOK_DELETE_REQUEST, request,
                new ParameterizedTypeReference<Boolean>() {
                }));
    }

    @Override
    public boolean sendPatreonUpdate(PatreonRequest request) {
        return Boolean.TRUE.equals(template.convertSendAndReceiveAsType(QUEUE_PATREON_WEBHOOK_REQUEST, request,
                new ParameterizedTypeReference<Boolean>() {
                }));
    }

    @Override
    public boolean isChannelOwner(CheckOwnerRequest request) {
        return Boolean.TRUE.equals(template.convertSendAndReceiveAsType(QUEUE_CHECK_OWNER_REQUEST, request,
                new ParameterizedTypeReference<Boolean>() {
                }));
    }

    @Override
    public void evictCache(String cacheName, long guildId) {
        template.convertAndSend(QUEUE_CACHE_EVICT_REQUEST, new CacheEvictRequest(cacheName, guildId));
    }
}
