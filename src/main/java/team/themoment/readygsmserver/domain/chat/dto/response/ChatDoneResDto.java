package team.themoment.readygsmserver.domain.chat.dto.response;

/**
 * {@code event: done}의 본문. 클라이언트는 이 이벤트를 받았을 때만 정상 완료로 간주한다.
 *
 * @param finishReason {@code "stop"} 또는 {@code "length"}.
 *                     {@code "length"}면 클라이언트가 "답변이 길어 잘렸습니다"를 표시한다.
 */
public record ChatDoneResDto(
        String finishReason
) {
}
