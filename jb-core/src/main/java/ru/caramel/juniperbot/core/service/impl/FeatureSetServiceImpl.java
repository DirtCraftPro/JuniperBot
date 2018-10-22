/*
 * This file is part of JuniperBotJ.
 *
 * JuniperBotJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * JuniperBotJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JuniperBotJ. If not, see <http://www.gnu.org/licenses/>.
 */
package ru.caramel.juniperbot.core.service.impl;

import org.springframework.stereotype.Service;
import ru.caramel.juniperbot.core.model.enums.FeatureSet;
import ru.caramel.juniperbot.core.service.FeatureSetService;

import java.util.Set;

@Service
public class FeatureSetServiceImpl implements FeatureSetService {

    @Override
    public boolean isAvailable(long guildId, FeatureSet featureSet) {
        return getByGuild(guildId).contains(featureSet);
    }

    @Override
    public boolean isAvailableForUser(long userId, FeatureSet featureSet) {
        return getByUser(userId).contains(featureSet);
    }

    @Override
    public Set<FeatureSet> getByGuild(long guildId) {
        return Set.of();//Set.of(FeatureSet.values());
    }

    @Override
    public Set<FeatureSet> getByUser(long userId) {
        return Set.of();//Set.of(FeatureSet.values());
    }
}
