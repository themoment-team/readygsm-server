package team.themoment.readygsmserver.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.application.repository.ApplicationRepository;
import team.themoment.sdk.exception.ExpectedException;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelApplicationService {

    private final ApplicationRepository applicationRepository;

    public void execute(Long userId, Long activityId) {
        int deleted = applicationRepository.deleteByActivity_IdAndUser_Id(activityId, userId);
        if (deleted == 0) {
            throw new ExpectedException("신청 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }
}
