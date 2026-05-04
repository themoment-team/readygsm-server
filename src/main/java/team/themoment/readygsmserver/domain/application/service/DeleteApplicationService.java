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
public class DeleteApplicationService {

    private final ApplicationRepository applicationRepository;

    public void execute(Long applicationId) {
        applicationRepository.delete(
                applicationRepository.findById(applicationId)
                        .orElseThrow(() -> new ExpectedException("신청 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND))
        );
    }
}
