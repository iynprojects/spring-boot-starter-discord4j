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

import discord4j.spring.DiscordConfigurer;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {

    public static final String EVENT_DISPATCHER_DISCORD_CONFIGURER = "eventDispatcherDiscordConfigurer";

    @Bean(EVENT_DISPATCHER_DISCORD_CONFIGURER)
    @ConditionalOnMissingBean(name = EVENT_DISPATCHER_DISCORD_CONFIGURER)
    public DiscordConfigurer discordConfigurer(final List<EventDispatcherConfigurer> configurers) {
        final EventDispatcherConfigurer configurer = new CompositeEventDispatcherConfigurer(configurers);
        return new DiscordConfigurerAdapter(configurer);
    }
}
