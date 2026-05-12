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
public class QueryMyApplicationsService {

    private final ApplicationRepository applicationRepository;

    public List<ApplicationResDto> execute(Long userId) {
        return applicationRepository.findAllByUser_Id(userId).stream()
                .map(ApplicationResDto::from)
                .toList();
    }
}