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
package ru.juniperbot.worker.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import ru.juniperbot.common.model.ModerationActionType;
import ru.juniperbot.common.worker.command.model.BotContext;
import ru.juniperbot.common.worker.command.model.DiscordCommand;
import ru.juniperbot.common.worker.modules.moderation.model.ModerationActionRequest;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DiscordCommand(key = "discord.command.mod.ban.key",
        description = "discord.command.mod.ban.desc",
        group = "discord.command.group.moderation",
        permissions = {
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.BAN_MEMBERS
        },
        priority = 25)
public class BanCommand extends ModeratorCommand {

    private final static Pattern BAN_PATTERN = Pattern.compile("([0-9]*)\\s*(.*)");

    @Override
    public boolean doCommand(GuildMessageReceivedEvent event, BotContext context, String query) {
        Member mentioned = getMentioned(event);
        if (mentioned != null) {
            if (moderationService.isModerator(mentioned) || Objects.equals(mentioned, event.getMember())) {
                return fail(event); // do not allow ban members or yourself
            }

            if (!event.getGuild().getSelfMember().canInteract(mentioned)) {
                messageService.onError(event.getChannel(), "discord.command.mod.ban.position");
                return false;
            }

            var builder = ModerationActionRequest.builder()
                    .type(ModerationActionType.BAN)
                    .moderator(event.getMember())
                    .violator(mentioned);

            query = removeMention(query);
            boolean perform = StringUtils.isEmpty(query);
            if (!perform) {
                Matcher matcher = BAN_PATTERN.matcher(query);
                if (matcher.find()) {
                    int delDays = 0;
                    if (StringUtils.isNotEmpty(matcher.group(1))) {
                        delDays = Integer.parseInt(matcher.group(1));
                    }

                    builder.reason(matcher.group(2));
                    if (delDays <= 7) {
                        builder.duration(delDays);
                        perform = true;
                    }
                }
            }
            if (perform) {
                moderationService.performAction(builder.build());
                return ok(event);
            }
        }
        String banCommand = messageService.getMessageByLocale("discord.command.mod.ban.key",
                context.getCommandLocale());
        messageService.onEmbedMessage(event.getChannel(), "discord.command.mod.ban.help",
                context.getConfig().getPrefix(), banCommand);
        return false;
    }
}