/*
 * This file is part of spring-boot-starter-discord4j.
 *
 * spring-boot-starter-discord4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * spring-boot-starter-discord4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with spring-boot-starter-discord4j.  If not, see <https://www.gnu.org/licenses/>.
 */
package discord4j.spring.event.listener;

import discord4j.core.event.domain.Event;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import org.reactivestreams.Publisher;
import reactor.util.annotation.Nullable;

public final class FilteredEventListener<T extends Event> extends EventListenerDecorator<T> {

    @Nullable
    private final EventFilter currentFilter;

    @Nullable
    private final EventListener<? super T> currentChain;

    public FilteredEventListener(final EventListener<T> delegate, @Nullable List<EventFilter> filters) {
        super(delegate);
        filters = (filters == null) ? Collections.emptyList() : filters;

        FilteredEventListener<T> chain = new FilteredEventListener<>(delegate, null, null);
        final ListIterator<EventFilter> iterator = filters.listIterator(filters.size());
        // Iterate and apply EventFilter instances from lowest to highest priority
        while (iterator.hasPrevious()) {
            chain = new FilteredEventListener<>(delegate, iterator.previous(), chain);
        }

        currentFilter = chain.currentFilter;
        currentChain = chain.currentChain;
    }

    private FilteredEventListener(
        final EventListener<T> delegate,
        @Nullable final EventFilter currentFilter,
        @Nullable final EventListener<? super T> currentChain
    ) {
        super(delegate);
        this.currentFilter = currentFilter;
        this.currentChain = currentChain;
    }

    @Override
    public Publisher<?> onEvent(final T event) {
        return ((currentFilter == null) || (currentChain == null)
            ? super.onEvent(event)
            : currentFilter.filter(event, currentChain));
    }

    @Override
    public String toString() {
        return "FilteredEventListener{" +
            "currentFilter=" + currentFilter +
            ", currentChain=" + currentChain +
            '}';
    }
}
