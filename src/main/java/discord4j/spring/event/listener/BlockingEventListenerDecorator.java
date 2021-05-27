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
import reactor.core.scheduler.Scheduler;

public class BlockingEventListenerDecorator<T extends Event>
    extends EventListenerDecorator<T>
    implements BlockingEventListener<T> {

    @SuppressWarnings("TypeMayBeWeakened")
    public BlockingEventListenerDecorator(final BlockingEventListener<T> delegate) {
        super(delegate);
    }

    @Override
    public Scheduler getScheduler() {
        final BlockingEventListener<T> delegate = getDelegate();
        return delegate.getScheduler();
    }

    @Override
    public void blockOnEvent(final T event) {
        final BlockingEventListener<T> delegate = getDelegate();
        delegate.blockOnEvent(event);
    }

    @Override
    public BlockingEventListener<T> getDelegate() {
        return (BlockingEventListener<T>) super.getDelegate();
    }
}
