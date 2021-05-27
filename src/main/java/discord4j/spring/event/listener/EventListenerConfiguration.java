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

import discord4j.spring.event.EventDispatcherConfigurer;
import java.util.List;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventListenerConfiguration {

    public static final String EVENT_LISTENER_POST_PROCESSOR = "eventListenerPostProcessor";

    public static final String EVENT_LISTENER_EVENT_DISPATCHER_CONFIGURER = "eventListenerEventDispatcherConfigurer";

    @Bean(EVENT_LISTENER_POST_PROCESSOR)
    @ConditionalOnMissingBean(name = EVENT_LISTENER_POST_PROCESSOR)
    public BeanPostProcessor beanPostProcessor(final List<EventContext<?>> contexts, final List<EventFilter> filters) {
        return new EventListenerPostProcessor(contexts, filters);
    }

    @Bean(EVENT_LISTENER_EVENT_DISPATCHER_CONFIGURER)
    @ConditionalOnMissingBean(name = EVENT_LISTENER_EVENT_DISPATCHER_CONFIGURER)
    public EventDispatcherConfigurer eventDispatcherConfigurer(final List<EventListener<?>> eventListeners) {
        final EventListener<?> eventListener = new CompositeEventListener(eventListeners);
        return new EventDispatcherConfigurerAdapter(eventListener);
    }
}
