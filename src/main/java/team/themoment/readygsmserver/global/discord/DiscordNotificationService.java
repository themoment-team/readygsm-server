package team.themoment.readygsmserver.global.discord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordNotificationService {

    private final DiscordProperties discordProperties;
    private final RestClient restClient;

    @Async("discordExecutor")
    public void sendApplicationCreated(
            String activityName,
            Long activityId,
            String applicantName,
            String schoolName,
            long currentApplicants,
            long currentReserveApplicants) {
        String webhookUrl = discordProperties.webhookUrl();
        if (webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }
        try {
            String detail = buildApplicationDetail(
                    activityName, activityId, applicantName, schoolName,
                    currentApplicants, currentReserveApplicants);
            DiscordWebhookPayload payload = DiscordWebhookPayload.applicationCreated(detail);
            restClient.post()
                    .uri(webhookUrl)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("[DISCORD] 알림 전송 실패", e);
        }
    }

    @Async("discordExecutor")
    public void sendApplicationCancelled(
            String activityName,
            Long activityId,
            String applicantName,
            String schoolName,
            long currentApplicants,
            long currentReserveApplicants) {
        String webhookUrl = discordProperties.webhookUrl();
        if (webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }
        try {
            String detail = buildApplicationDetail(
                    activityName, activityId, applicantName, schoolName,
                    currentApplicants, currentReserveApplicants);
            DiscordWebhookPayload payload = DiscordWebhookPayload.applicationCancelled(detail);
            restClient.post()
                    .uri(webhookUrl)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("[DISCORD] 알림 전송 실패", e);
        }
    }

    private String buildApplicationDetail(
            String activityName,
            Long activityId,
            String applicantName,
            String schoolName,
            long currentApplicants,
            long currentReserveApplicants) {
        return String.format("""
                **활동:** %s (ID: %d)
                **신청자:** %s
                **학교:** %s
                **현재 인원:** %d명 (대기 %d명)""",
                activityName, activityId,
                maskName(applicantName),
                schoolName,
                currentApplicants, currentReserveApplicants);
    }

    private String maskName(String name) {
        if (name == null || name.isBlank()) return "N/A";
        if (name.length() < 2) return name;
        return name.charAt(0) + "*" + name.substring(2);
    }

    @Async("discordExecutor")
    public void sendServerError(
            String title,
            String description,
            String httpMethod,
            String requestUri,
            String clientIp,
            String threadName,
            Throwable cause) {
        String webhookUrl = discordProperties.webhookUrl();
        if (webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }
        try {
            String detail = buildDetail(description, httpMethod, requestUri, clientIp, threadName, cause);
            DiscordWebhookPayload payload = DiscordWebhookPayload.serverError(title, detail);
            restClient.post()
                    .uri(webhookUrl)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("[DISCORD] 알림 전송 실패", e);
        }
    }

    private String buildDetail(String description, String httpMethod,
                               String requestUri, String clientIp,
                               String threadName, Throwable cause) {
        String truncatedDesc = truncate(description, 1000);

        if (cause == null) {
            return String.format("""
                    **메시지:** %s
                    **API:** `[%s] %s`
                    **클라이언트 IP:** `%s`
                    **쓰레드:** `%s`
                    **발생 지점:** (예외 정보 없음)""",
                    truncatedDesc,
                    httpMethod, requestUri,
                    clientIp != null ? clientIp : "N/A",
                    threadName);
        }

        StackTraceElement[] frames = cause.getStackTrace();
        String stackTrace = frames.length == 0
                ? "(스택트레이스 없음)"
                : Arrays.stream(frames)
                        .limit(5)
                        .map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n  at "));
        String truncatedMessage = truncate(cause.getMessage(), 1000);

        return String.format("""
                **메시지:** %s
                **API:** `[%s] %s`
                **클라이언트 IP:** `%s`
                **쓰레드:** `%s`
                **발생 지점:**
                ```
                %s: %s
                  at %s
                ```""",
                truncatedDesc,
                httpMethod, requestUri,
                clientIp != null ? clientIp : "N/A",
                threadName,
                cause.getClass().getName(),
                truncatedMessage,
                stackTrace);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return "";
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 3) + "...";
    }
}