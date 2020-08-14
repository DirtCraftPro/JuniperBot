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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import ru.juniperbot.common.worker.command.model.BotContext;
import ru.juniperbot.common.worker.command.model.DiscordCommand;
import ru.juniperbot.common.worker.command.model.MemberReference;
import ru.juniperbot.common.worker.modules.moderation.service.MuteService;

@DiscordCommand(key = "discord.command.mod.unmute.key",
        description = "discord.command.mod.unmute.desc",
        group = "discord.command.group.moderation",
        permissions = {Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_ROLES, Permission.MANAGE_PERMISSIONS, Permission.VOICE_MUTE_OTHERS},
        priority = 35)
public class UnMuteCommand extends MentionableModeratorCommand {

    @Autowired
    @Lazy
    private MuteService muteService;

    protected UnMuteCommand() {
        super(false, true);
    }

    @Override
    protected boolean doCommand(MemberReference reference, GuildMessageReceivedEvent event, BotContext context, String query) {
        Member violator = reference.getMember();
        if (violator == null) {
            messageService.onError(event.getChannel(), "discord.command.mod.unmute.mention");
            return false;
        }
        boolean unmuted = muteService.unmute(event.getMember(), event.getChannel(), violator);
        messageService.onEmbedMessage(event.getChannel(), unmuted
                ? "discord.command.mod.unmute.done" : "discord.command.mod.unmute.already", violator.getEffectiveName());
        return true;
    }
}
