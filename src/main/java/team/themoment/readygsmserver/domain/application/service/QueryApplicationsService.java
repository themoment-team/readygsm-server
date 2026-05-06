package team.themoment.readygsmserver.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.application.dto.response.ApplicationResDto;
import team.themoment.readygsmserver.domain.application.repository.ApplicationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryApplicationsService {

    private final ApplicationRepository applicationRepository;

    public List<ApplicationResDto> execute(Long activityId) {
        return applicationRepository.findAllByActivity_Id(activityId).stream()
                .map(ApplicationResDto::from)
                .toList();
    }
}
