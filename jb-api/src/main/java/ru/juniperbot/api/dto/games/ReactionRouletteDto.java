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
package ru.juniperbot.api.dto.games;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class ReactionRouletteDto implements Serializable {

    private static final long serialVersionUID = -1859770585473921470L;

    private boolean enabled;

    private boolean reaction;

    private Set<String> ignoredChannels;

    private Set<String> selectedEmotes;

    @Min(1)
    @Max(5)
    private int percent = 1;

    @Min(0)
    @Max(999999999)
    private int threshold = 0;
}
