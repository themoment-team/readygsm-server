package team.themoment.readygsmserver.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.application.entity.ApplicationJpaEntity;
import team.themoment.readygsmserver.domain.application.repository.ApplicationRepository;
import team.themoment.sdk.exception.ExpectedException;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelApplicationService {

    private final ApplicationRepository applicationRepository;

    public void execute(Long userId, Long activityId) {
        ApplicationJpaEntity application = applicationRepository.findByActivity_IdAndUser_Id(activityId, userId)
                .orElseThrow(() -> new ExpectedException("신청 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        boolean wasConfirmed = !application.isReserve();
        applicationRepository.delete(application);

        if (wasConfirmed) {
            applicationRepository.findFirstByActivity_IdAndIsReserveTrueOrderByCreatedAtAscIdAsc(activityId)
                    .ifPresent(ApplicationJpaEntity::promote);
        }
    }
}
