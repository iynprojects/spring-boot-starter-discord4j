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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public final class EventContext {

    private static final ThreadLocal<EventContext> EVENT_CONTEXT = new ThreadLocal<>();

    private static final Object REACTOR_CONTEXT_KEY = EventContext.class;

    public static <T> Mono<T> reactive(final Function<? super EventContext, ? extends T> scope) {
        return Mono.deferContextual(Mono::just)
            .map(context -> context.getOrEmpty(REACTOR_CONTEXT_KEY)
                .orElseThrow(MissingEventContextException::new))
            .cast(EventContext.class)
            .map(eventContext -> {
                EVENT_CONTEXT.set(eventContext);
                try { // Execute on assembly for the ThreadLocal value
                    return Mono.justOrEmpty(scope.apply(eventContext));
                } finally {
                    EVENT_CONTEXT.set(null);
                }
            }).flatMap(Function.identity());
    }

    static <T> Function<Publisher<T>, Publisher<T>> setReactive(final Event event) {
        return publisher -> Flux.from(publisher)
            .contextWrite(Context.of(REACTOR_CONTEXT_KEY, new EventContext(event)));
    }

    public static EventContext threadLocal() {
        final EventContext eventContext = EVENT_CONTEXT.get();

        if (eventContext == null) {
            throw new MissingEventContextException();
        }

        return eventContext;
    }

    private final Event event;

    private final Map<String, Runnable> destructionCallbacks;

    private final Map<String, Object> objects;

    private EventContext(final Event event) {
        this.event = event;

        destructionCallbacks = new ConcurrentHashMap<>(0);
        objects = new ConcurrentHashMap<>(0);
    }

    public Event getEvent() {
        return event;
    }

    Map<String, Runnable> getDestructionCallbacks() {
        return destructionCallbacks;
    }

    Map<String, Object> getObjects() {
        return objects;
    }

    @Override
    public String toString() {
        return "EventContext{" +
            "event=" + event +
            ", destructionCallbacks=" + destructionCallbacks +
            ", objects=" + objects +
            '}';
    }
}
