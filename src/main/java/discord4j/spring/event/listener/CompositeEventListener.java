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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

public final class CompositeEventListener implements EventListener<Event> {

    private final Map<EventListener<?>, Class<?>> eventListeners;

    public CompositeEventListener(@Nullable Iterable<EventListener<?>> eventListeners) {
        eventListeners = (eventListeners == null) ? Collections.emptyList() : eventListeners;
        this.eventListeners = new HashMap<>(0);

        for (final EventListener<?> eventListener : eventListeners) {
            this.eventListeners.put(eventListener, eventListener.getEventType());
        }
    }

    @Override
    public Publisher<?> onEvent(final Event event) {
        final Collection<Publisher<?>> sources = new ArrayList<>(0);

        eventListeners.forEach((eventListener, eventType) -> {
            if (eventType.isInstance(event)) {
                @SuppressWarnings("unchecked")
                final EventListener<Event> typedEventListener = (EventListener<Event>) eventListener;
                final Publisher<?> source = Flux.defer(() -> typedEventListener.onEvent(event));
                sources.add(source);
            }
        });

        return Mono.when(sources);
    }

    @Override
    public String toString() {
        return "CompositeEventListener{" +
            "eventListeners=" + eventListeners +
            '}';
    }
}
