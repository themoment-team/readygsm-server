package team.themoment.readygsmserver.domain.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.activity.dto.response.ActivityResDto;
import team.themoment.readygsmserver.domain.activity.repository.ActivityRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryAllActivitiesService {

    private final ActivityRepository activityRepository;

    public List<ActivityResDto> execute() {
        return activityRepository.findAll().stream()
                .map(ActivityResDto::from)
                .toList();
    }
}
