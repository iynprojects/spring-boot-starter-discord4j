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
import java.util.Collections;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.util.annotation.Nullable;

public final class CompositeDiscordConfigurer implements DiscordConfigurer {

    private final Iterable<DiscordConfigurer> discordConfigurers;

    public CompositeDiscordConfigurer(@Nullable final Iterable<DiscordConfigurer> discordConfigurers) {
        this.discordConfigurers = (discordConfigurers == null) ? Collections.emptyList() : discordConfigurers;
    }

    @Override
    public DiscordClientBuilder<DiscordClient, ?> configureDiscordClient(
        DiscordClientBuilder<DiscordClient, ?> discordClientBuilder
    ) {
        for (final DiscordConfigurer discordConfigurer : discordConfigurers) {
            discordClientBuilder = discordConfigurer.configureDiscordClient(discordClientBuilder);
        }

        return discordClientBuilder;
    }

    @Override
    public GatewayBootstrap<?> configureGatewayBootstrap(GatewayBootstrap<?> gatewayBootstrap) {
        for (final DiscordConfigurer discordConfigurer : discordConfigurers) {
            gatewayBootstrap = discordConfigurer.configureGatewayBootstrap(gatewayBootstrap);
        }

        return gatewayBootstrap;
    }

    @Override
    public Publisher<?> withEventDispatcher(final EventDispatcher eventDispatcher) {
        return Flux.fromIterable(discordConfigurers)
            // Error handling is neglected to match GatewayBootstrap#withEventDispatcher behavior
            .flatMap(discordConfigurer -> discordConfigurer.withEventDispatcher(eventDispatcher));
    }

    @Override
    public String toString() {
        return "CompositeDiscordConfigurer{" +
            "discordConfigurers=" + discordConfigurers +
            '}';
    }
}
