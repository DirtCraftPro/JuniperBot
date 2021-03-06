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
package ru.juniperbot.api.subscriptions.handlers;

import com.google.api.services.youtube.model.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.juniperbot.api.dto.config.SubscriptionDto;
import ru.juniperbot.api.dto.request.SubscriptionCreateResponse;
import ru.juniperbot.api.model.SubscriptionStatus;
import ru.juniperbot.api.model.SubscriptionType;
import ru.juniperbot.api.subscriptions.integrations.YouTubeSubscriptionService;
import ru.juniperbot.common.persistence.entity.YouTubeConnection;
import ru.juniperbot.common.service.YouTubeService;

import java.util.HashMap;
import java.util.Map;

@Component
public class YouTubeSubscriptionHandler extends AbstractSubscriptionHandler<YouTubeConnection> {

    @Autowired
    private YouTubeService youTubeService;

    @Autowired
    private YouTubeSubscriptionService subscriptionService;

    @Override
    public SubscriptionDto getSubscription(YouTubeConnection connection) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("youtube.channelId", connection.getChannel().getChannelId());
        attributes.put("youtube.description", connection.getDescription());
        attributes.put("youtube.announce", connection.getAnnounceMessage());
        attributes.put("youtube.sendEmbed", connection.isSendEmbed());
        SubscriptionDto dto = getDtoForHook(connection.getGuildId(), connection.getWebHook());
        dto.setId(connection.getId());
        dto.setAttributes(attributes);
        dto.setType(SubscriptionType.YOUTUBE);
        if (StringUtils.isEmpty(dto.getName())) {
            dto.setName(connection.getName());
        }
        if (StringUtils.isEmpty(dto.getIconUrl())) {
            dto.setIconUrl(connection.getIconUrl());
        }
        dto.setStatus(SubscriptionStatus.ACTIVE);
        return dto;
    }

    @Override
    public SubscriptionCreateResponse create(long guildId, Map<String, ?> data) {
        String channelId = getValue(data, "id", String.class);
        if (StringUtils.isEmpty(channelId)) {
            throw new IllegalArgumentException("Wrong data");
        }

        Channel channel = youTubeService.getChannelById(channelId);
        if (channel == null) {
            return getFailedCreatedDto("wrong_channel");
        }
        YouTubeConnection connection = subscriptionService.create(guildId, channel);
        return getCreatedDto(getSubscription(connection));
    }

    @Override
    @Transactional
    public boolean update(SubscriptionDto subscription) {
        YouTubeConnection connection = subscriptionService.find(subscription.getId());
        if (!check(connection)) {
            return false;
        }
        updateWebHook(connection, subscription);
        subscriptionService.subscribe(connection.getChannel());

        String announce = getValue(subscription.getAttributes(), "youtube.announce", String.class);
        connection.setAnnounceMessage(announce);

        Boolean sendEmbed = getValue(subscription.getAttributes(), "youtube.sendEmbed", Boolean.class);
        connection.setSendEmbed(Boolean.TRUE.equals(sendEmbed));

        subscriptionService.save(connection);
        return true;
    }

    @Override
    @Transactional
    public void delete(long id) {
        YouTubeConnection connection = subscriptionService.find(id);
        if (check(connection)) {
            subscriptionService.delete(connection);
        }
    }

    @Override
    public Class<YouTubeConnection> getEntityType() {
        return YouTubeConnection.class;
    }

    @Override
    public SubscriptionType getType() {
        return SubscriptionType.YOUTUBE;
    }
}
