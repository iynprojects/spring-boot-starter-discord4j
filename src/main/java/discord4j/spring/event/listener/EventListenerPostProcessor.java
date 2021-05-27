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

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.config.BeanPostProcessor;
import reactor.util.annotation.Nullable;

public final class EventListenerPostProcessor implements BeanPostProcessor {

    private final List<EventContext<?>> contexts;

    private final List<EventFilter> filters;

    public EventListenerPostProcessor(
        @Nullable final List<EventContext<?>> contexts,
        @Nullable final List<EventFilter> filters
    ) {
        this.contexts = (contexts == null) ? Collections.emptyList() : contexts;
        this.filters = (filters == null) ? Collections.emptyList() : filters;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, final String beanName) {
        if (bean instanceof BlockingEventListener) {
            final BlockingEventListener<?> listener = (BlockingEventListener<?>) bean;
            bean = new ContextualBlockingEventListener<>(listener, contexts);
        }

        if (bean instanceof EventListener) {
            final EventListener<?> listener = (EventListener<?>) bean;
            bean = new FilteredEventListener<>(listener, filters);
        }

        return bean;
    }

    @Override
    public String toString() {
        return "EventListenerPostProcessor{" +
            "contexts=" + contexts +
            ", filters=" + filters +
            '}';
    }
}
