package team.themoment.readygsmserver.global.discord;

import java.util.List;

public record DiscordWebhookPayload(List<Embed> embeds) {

    public record Embed(String title, String description, int color) {}

    public static DiscordWebhookPayload serverError(String title, String description) {
        return new DiscordWebhookPayload(List.of(new Embed(title, description, 0xFF4C4C)));
    }

    public static DiscordWebhookPayload applicationCreated(String description) {
        return new DiscordWebhookPayload(List.of(new Embed("📥 활동 신청", description, 0x4C9AFF)));
    }

    public static DiscordWebhookPayload applicationCancelled(String description) {
        return new DiscordWebhookPayload(List.of(new Embed("📤 활동 신청 취소", description, 0xFFA24C)));
    }
}
