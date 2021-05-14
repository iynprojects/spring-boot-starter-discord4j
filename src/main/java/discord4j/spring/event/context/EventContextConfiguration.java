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
import discord4j.spring.event.EventListener;
import discord4j.spring.event.context.annotation.EventScope;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventContextConfiguration implements BeanPostProcessor {

    public static final String SCOPE_EVENT = "event";

    @Bean
    @EventScope
    public Event event() {
        final EventContext eventContext = EventContext.threadLocal();
        return eventContext.getEvent();
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
        if (bean instanceof EventListener) {
            final EventListener<?> eventListener = (EventListener<?>) bean;
            return new ContextualEventListener<>(eventListener);
        }

        return bean;
    }
}
