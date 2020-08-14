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
package ru.juniperbot.worker.commands.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.juniperbot.common.service.ConfigService;
import ru.juniperbot.common.worker.command.model.BotContext;
import ru.juniperbot.common.worker.command.model.DiscordCommand;

import java.util.Date;

@DiscordCommand(key = "discord.command.info.key",
        description = "discord.command.info.desc",
        group = "discord.command.group.info",
        priority = 0)
public class InfoCommand extends AbstractInfoCommand {

    @Autowired
    private ConfigService configService;

    @Value("${dependencies.jda.version}")
    private String jdaVersion;

    @Value("${dependencies.lavaPlayer.version}")
    private String lavaPlayerVersion;

    @Value("${dependencies.spring.version}")
    private String springVersion;

    @Value("${spring.application.version}")
    private String appVersion;

    private DateTime buildTimestamp;

    @Override
    public boolean doCommand(GuildMessageReceivedEvent message, BotContext context, String query) {
        String prefix = context.getConfig() != null ? context.getConfig().getPrefix() : commonProperties.getDiscord().getDefaultPrefix();

        EmbedBuilder builder = messageService.getBaseEmbed(true);
        builder.setAuthor(message.getJDA().getSelfUser().getName(), messageService.getMessage("about.support.page"));
        builder.setThumbnail(commonProperties.getBranding().getAvatarSmallUrl());

        String helpCommand = messageService.getMessageByLocale("discord.command.help.key",
                context.getCommandLocale());

        builder.setDescription(messageService.getMessage("discord.command.info.description", prefix, helpCommand));

        builder.addField(
                messageService.getMessage("discord.command.info.author.title"),
                messageService.getMessage("about.support.developer"), true);

        DateTimeFormatter formatter = DateTimeFormat
                .mediumDateTime()
                .withLocale(contextService.getLocale())
                .withZone(context.getTimeZone());
        builder.addField(
                messageService.getMessage("discord.command.info.version.title"),
                String.format("%s (%s)", appVersion, formatter.print(buildTimestamp)), true);

        builder.addField(
                messageService.getMessage("discord.command.info.links.title"),
                messageService.getMessage("discord.command.info.links.content"), true);

        builder.addField(
                messageService.getMessage("discord.command.info.framework.title"),
                messageService.getMessage("discord.command.info.framework.content",
                        jdaVersion, lavaPlayerVersion, springVersion),
                true);

        messageService.sendMessageSilent(message.getChannel()::sendMessage, builder.build());
        return true;
    }

    @Value("${spring.application.timestamp}")
    public void setBuildTimestamp(String value) {
        buildTimestamp = new DateTime(StringUtils.isNumeric(value) ? Long.parseLong(value) : new Date())
                .withZone(DateTimeZone.UTC);
    }
}
