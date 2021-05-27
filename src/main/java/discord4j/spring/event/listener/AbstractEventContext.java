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
import java.util.Optional;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public abstract class AbstractEventContext<C> implements EventContext<C>, EventFilter {

    private final ThreadLocal<Optional<C>> threadLocalContext;

    protected AbstractEventContext() {
        threadLocalContext = new ThreadLocal<>();
    }

    @Override
    public <T extends Event> Publisher<?> filter(final T event, final EventListener<T> listener) {
        return getContext(event, listener)
            .map(Optional::of)
            .defaultIfEmpty(Optional.empty())
            .flatMapMany(context -> Flux.from(listener.onEvent(event))
                .contextWrite(Context.of(this, context)));
    }

    @Override
    public Optional<C> getContext() {
        return Optional.ofNullable(threadLocalContext.get())
            .orElseThrow(() -> new IllegalStateException(
                "Current Thread is not associated with this EventContext"
            ));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Mono<T> useContext(final Function<? super C, ? extends T> handler) {
        return Mono.deferContextual(Mono::just)
            .map(contextView -> contextView.getOrEmpty(this))
            .cast(Optional.class) // Required for compilation
            .map(context -> (Optional<Optional<C>>) context)
            .flatMap(Mono::justOrEmpty)
            .switchIfEmpty(Mono.error(() -> new IllegalStateException(
                "Current Reactor Context is not associated with this EventContext"
            )))
            .map(context -> {
                try { // Enables getContext()
                    threadLocalContext.set(context);
                    final C contextValue = context.orElse(null);
                    final T result = handler.apply(contextValue);
                    return Optional.ofNullable(result);
                } finally {
                    threadLocalContext.set(null);
                }
            })
            .flatMap(Mono::justOrEmpty);
    }

    protected abstract <T extends Event> Mono<C> getContext(T event, EventListener<T> listener);

    @Override
    public String toString() {
        return "AbstractEventContext{" +
            "threadLocalContext=" + threadLocalContext.get() +
            '}';
    }
}
