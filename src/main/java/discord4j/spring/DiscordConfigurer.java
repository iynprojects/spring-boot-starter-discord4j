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
package discord4j.spring;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.EventDispatcher;
import discord4j.core.shard.GatewayBootstrap;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface DiscordConfigurer {

    default DiscordClientBuilder<DiscordClient, ?> configureDiscordClient(
        final DiscordClientBuilder<DiscordClient, ?> discordClientBuilder
    ) {
        return discordClientBuilder;
    }

    default GatewayBootstrap<?> configureGatewayBootstrap(final GatewayBootstrap<?> gatewayBootstrap) {
        return gatewayBootstrap;
    }

    default Publisher<?> withEventDispatcher(final EventDispatcher eventDispatcher) {
        return Mono.empty();
    }
}
