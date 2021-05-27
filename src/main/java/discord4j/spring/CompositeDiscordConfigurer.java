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
import discord4j.core.shard.GatewayBootstrap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import reactor.util.annotation.Nullable;

public final class CompositeDiscordConfigurer implements DiscordConfigurer {

    private final List<DiscordConfigurer> discordConfigurers;

    public CompositeDiscordConfigurer(@Nullable final Collection<DiscordConfigurer> discordConfigurers) {
        this.discordConfigurers = (discordConfigurers == null)
            ? new ArrayList<>(0)
            : new ArrayList<>(discordConfigurers);

        Collections.reverse(this.discordConfigurers);
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
    public String toString() {
        return "CompositeDiscordConfigurer{" +
            "discordConfigurers=" + discordConfigurers +
            '}';
    }
}
