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
package discord4j.spring.event.context;

import discord4j.core.event.domain.Event;
import discord4j.spring.event.BlockingEventListener;
import discord4j.spring.event.EventListener;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

final class ContextualEventListener<T extends Event> implements EventListener<T> {

    private final EventListener<T> delegate;

    ContextualEventListener(final EventListener<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Class<T> getEventType() {
        return delegate.getEventType();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Publisher<?> onEvent(final T event) {
        return Flux.just(delegate)
            .ofType(BlockingEventListener.class)
            .flatMap(eventListener ->
                EventContext.reactive(eventContext -> {
                    eventListener.blockOnEvent(event);
                    return eventContext;
                }).subscribeOn(Schedulers.boundedElastic()))
            .cast(Object.class)
            .switchIfEmpty(Flux.defer(() -> delegate.onEvent(event)))
            .transform(EventContext.setReactive(event));
    }

    @Override
    public String toString() {
        return "EventContextListenerProxy{" +
            "delegate=" + delegate +
            '}';
    }
}
