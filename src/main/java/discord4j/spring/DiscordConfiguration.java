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
import discord4j.core.GatewayDiscordClient;
import discord4j.core.shard.GatewayBootstrap;
import discord4j.spring.property.DiscordProperties;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DiscordProperties.class)
public class DiscordConfiguration {

    private final DiscordProperties discordProperties;

    private final DiscordConfigurer discordConfigurer;

    public DiscordConfiguration(
        final DiscordProperties discordProperties,
        final List<DiscordConfigurer> discordConfigurers
    ) {
        this.discordProperties = discordProperties;
        discordConfigurer = new CompositeDiscordConfigurer(discordConfigurers);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "discord4j", name = "token")
    public DiscordClient discordClient() {
        final String token = discordProperties.getToken();
        DiscordClientBuilder<DiscordClient, ?> builder = DiscordClient.builder(token);
        builder = discordConfigurer.configureDiscordClient(builder);
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(DiscordClient.class)
    public GatewayDiscordClient gatewayDiscordClient(final DiscordClient discordClient) {
        GatewayBootstrap<?> builder = discordClient.gateway();
        builder = discordConfigurer.configureGatewayBootstrap(builder);
        final GatewayDiscordClient gatewayDiscordClient = builder.login().block();
        return Objects.requireNonNull(gatewayDiscordClient, "GatewayDiscordClient is null");
    }

    @Bean("gatewayDiscordClientDisposableBean")
    @ConditionalOnBean(GatewayDiscordClient.class)
    public DisposableBean disposableBean(final GatewayDiscordClient gatewayDiscordClient) {
        final Thread thread = new Thread(() -> gatewayDiscordClient.onDisconnect().block());
        thread.setName("Discord4J Spring Boot Starter Keep-Alive Thread");
        thread.setDaemon(false);
        thread.start();

        return () -> gatewayDiscordClient.logout().block();
    }
}
