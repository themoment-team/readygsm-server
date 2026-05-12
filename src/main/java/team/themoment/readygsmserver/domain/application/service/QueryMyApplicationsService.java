package team.themoment.readygsmserver.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.application.dto.response.ApplicationResDto;
import team.themoment.readygsmserver.domain.application.repository.ApplicationRepository;
import team.themoment.sdk.exception.ExpectedException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryMyApplicationsService {

    private final ApplicationRepository applicationRepository;

    public ApplicationResDto execute(Long userId) {
        return applicationRepository.findByUser_Id(userId)
                .map(ApplicationResDto::from)
                .orElseThrow(() -> new ExpectedException("신청 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }
}
