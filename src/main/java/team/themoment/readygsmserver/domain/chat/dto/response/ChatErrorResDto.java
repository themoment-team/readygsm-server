package team.themoment.readygsmserver.domain.chat.dto.response;

/**
 * {@code event: error}의 본문. 서버가 인지한 실패를 알린다.
 * 클라이언트는 중단을 표시하고 재시도할 수 있다.
 */
public record ChatErrorResDto(
        String reason
) {
}
