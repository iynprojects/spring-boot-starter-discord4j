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
package discord4j.spring.event;

import discord4j.core.event.EventDispatcher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

public final class CompositeEventDispatcherConfigurer implements EventDispatcherConfigurer {

    private final Iterable<EventDispatcherConfigurer> configurers;

    public CompositeEventDispatcherConfigurer(@Nullable final Iterable<EventDispatcherConfigurer> configurers) {
        this.configurers = (configurers == null) ? Collections.emptyList() : configurers;
    }

    @Override
    public Publisher<?> configureEventDispatcher(final EventDispatcher eventDispatcher) {
        final Collection<Publisher<?>> sources = new ArrayList<>(0);

        for (final EventDispatcherConfigurer configurer : configurers) {
            final Publisher<?> source = configurer.configureEventDispatcher(eventDispatcher);
            sources.add(source);
        }

        return Mono.when(sources);
    }

    @Override
    public String toString() {
        return "CompositeEventDispatcherConfigurer{" +
            "configurers=" + configurers +
            '}';
    }
}
