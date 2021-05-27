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
import org.reactivestreams.Publisher;

public class EventListenerDecorator<T extends Event> implements EventListener<T> {

    private final EventListener<T> delegate;

    public EventListenerDecorator(final EventListener<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Class<? extends T> getEventType() {
        return delegate.getEventType();
    }

    @Override
    public Publisher<?> onEvent(final T event) {
        return delegate.onEvent(event);
    }

    public EventListener<T> getDelegate() {
        return delegate;
    }

    @Override
    public String toString() {
        return "EventListenerDelegate{" +
            "delegate=" + delegate +
            '}';
    }
}
