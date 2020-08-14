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
package ru.juniperbot.common.worker.event.intercept;

import net.dv8tion.jda.api.events.Event;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BaseEventFilterFactory<T extends Event> implements EventFilterFactory<T> {

    private final ThreadLocal<FilterChainImpl<T>> chains = new ThreadLocal<>();

    @Autowired
    private List<Filter<T>> filterList;

    @Override
    public FilterChain<T> createChain(T event) {
        if (event == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(filterList)) {
            return null;
        }
        FilterChainImpl<T> chain = chains.get();
        if (chain == null) {
            chain = new FilterChainImpl<>(filterList);
            chains.set(chain);
        }
        chain.reset();
        return chain;
    }
}
