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
package ru.juniperbot.api.dto.config;

import lombok.Getter;
import lombok.Setter;
import ru.juniperbot.api.dto.MessageTemplateDto;
import ru.juniperbot.common.model.CommandType;
import ru.juniperbot.common.model.EmojiRole;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CustomCommandDto extends CommandDto {

    private static final long serialVersionUID = 7586224126349916457L;

    private Long id;

    @NotNull
    private CommandType type;

    @Size(max = 25, message = "{validation.commands.key.Size.message}")
    @NotBlank(message = "{validation.commands.key.NotBlank.message}")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9]*$", message = "{validation.commands.key.pattern.message}")
    private String key;

    @NotBlank(message = "{validation.commands.content.NotBlank.message}")
    @Size(max = 2000, message = "{validation.commands.content.Size.message}")
    private String content;

    private MessageTemplateDto messageTemplate;

    private Set<String> rolesToAdd;

    private Set<String> rolesToRemove;

    private List<EmojiRole> emojiRoles;

}
