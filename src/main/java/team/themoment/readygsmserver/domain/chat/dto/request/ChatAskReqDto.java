package team.themoment.readygsmserver.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatAskReqDto(
        @NotBlank(message = "질문을 입력해주세요.")
        @Size(max = 500, message = "질문은 500자를 넘을 수 없습니다.")
        String message
) {
}
