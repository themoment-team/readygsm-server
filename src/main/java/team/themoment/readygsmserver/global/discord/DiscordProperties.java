package team.themoment.readygsmserver.global.discord;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("discord")
public record DiscordProperties(String webhookUrl) {
}