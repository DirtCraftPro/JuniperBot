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
package ru.juniperbot.common.worker.feature.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.juniperbot.common.model.FeatureSet;
import ru.juniperbot.common.worker.feature.provider.FeatureProvider;
import ru.juniperbot.common.worker.shared.support.TriFunction;

import java.util.*;
import java.util.function.BiFunction;

@Service
public class FeatureSetServiceImpl implements FeatureSetService {

    private List<FeatureSetProvider> providers;

    @Override
    public boolean isAvailable(long guildId, FeatureSet featureSet) {
        return getAnyAvailable(guildId, featureSet, FeatureSetProvider::isAvailable);
    }

    @Override
    public boolean isAvailableForUser(long userId, FeatureSet featureSet) {
        return getAnyAvailable(userId, featureSet, FeatureSetProvider::isAvailableForUser);
    }

    @Override
    public Set<FeatureSet> getByGuild(long guildId) {
        return calculateFeatures(guildId, FeatureSetProvider::getByGuild);
    }

    @Override
    public Set<FeatureSet> getByUser(long userId) {
        return calculateFeatures(userId, FeatureSetProvider::getByUser);
    }

    @Autowired(required = false)
    private void addProviders(List<FeatureSetProvider> providers) {
        if (CollectionUtils.isNotEmpty(providers)) {
            this.providers = new ArrayList<>(providers);
            this.providers.sort(Comparator.comparingInt(e ->
                    e != null && e.getClass().isAnnotationPresent(FeatureProvider.class)
                            ? e.getClass().getAnnotation(FeatureProvider.class).priority()
                            : Integer.MAX_VALUE));
        }
    }

    private boolean getAnyAvailable(long id, FeatureSet featureSet, TriFunction<FeatureSetProvider, Long, FeatureSet, Boolean> supplier) {
        if (CollectionUtils.isEmpty(providers)) {
            return false;
        }
        for (FeatureSetProvider provider : providers) {
            if (supplier.apply(provider, id, featureSet)) {
                return true;
            }
        }
        return false;
    }

    private Set<FeatureSet> calculateFeatures(long id, BiFunction<FeatureSetProvider, Long, Set<FeatureSet>> supplier) {
        if (CollectionUtils.isEmpty(providers)) {
            return Set.of(FeatureSet.values());
        }
        int size = FeatureSet.values().length;
        Set<FeatureSet> result = new HashSet<>(size);
        for (FeatureSetProvider provider : providers) {
            result.addAll(supplier.apply(provider, id));
            if (result.size() == size) {
                break; // we have all possible features, pulling other providers doesn't make sense
            }
        }
        return result;
    }
}
