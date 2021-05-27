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
import java.util.function.Function;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.annotation.Nullable;

public final class ContextualBlockingEventListener<T extends Event> extends BlockingEventListenerDecorator<T> {

    @Nullable
    private final EventContext<?> currentContext;

    @Nullable
    private final EventListener<? super T> currentChain;

    private final boolean initialChain;

    public ContextualBlockingEventListener(
        final BlockingEventListener<T> delegate,
        @Nullable List<EventContext<?>> contexts
    ) {
        super(delegate);
        contexts = (contexts == null) ? Collections.emptyList() : contexts;

        ContextualBlockingEventListener<T> chain = new ContextualBlockingEventListener<>(delegate, null, null);
        final ListIterator<EventContext<?>> iterator = contexts.listIterator(contexts.size());
        // Iterate and apply EventContext instances from lowest to highest priority
        while (iterator.hasPrevious()) {
            chain = new ContextualBlockingEventListener<>(delegate, iterator.previous(), chain);
        }

        currentContext = chain.currentContext;
        currentChain = chain.currentChain;
        initialChain = true;
    }

    private ContextualBlockingEventListener(
        final BlockingEventListener<T> delegate,
        @Nullable final EventContext<?> currentContext,
        @Nullable final EventListener<? super T> currentChain
    ) {
        super(delegate);
        this.currentContext = currentContext;
        this.currentChain = currentChain;
        initialChain = false;
    }

    @Override
    public Publisher<?> onEvent(final T event) {
        // Guarantee same Thread execution between blockOnEvent and useContext invocations
        final Scheduler scheduler = initialChain ? getScheduler() : Schedulers.immediate();

        if ((currentContext == null) || (currentChain == null)) {
            return Mono.fromRunnable(() -> blockOnEvent(event))
                .subscribeOn(scheduler);
        }

        return Flux.create(sink ->
            currentContext.useContext(ignored ->
                Flux.from(currentChain.onEvent(event))
                    .subscribeOn(Schedulers.immediate())
                    .subscribe(sink::next, sink::error, sink::complete, sink.currentContext())
            ).subscribeOn(Schedulers.immediate()
            ).subscribe(sink::next, sink::error, sink::complete, sink.currentContext())
        ).subscribeOn(scheduler);
    }

    @Override
    public String toString() {
        return "ContextualBlockingEventListener{" +
            "currentContext=" + currentContext +
            ", currentChain=" + currentChain +
            ", initialChain=" + initialChain +
            '}';
    }
}
