package team.themoment.readygsmserver.domain.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.activity.dto.response.ActivityResDto;
import team.themoment.readygsmserver.domain.activity.repository.ActivityRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryAllActivitiesService {

    private final ActivityRepository activityRepository;

    public List<ActivityResDto> execute() {
        Map<Long, Long> countMap = activityRepository.countApplicantsGroupedByActivity()
                .stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        return activityRepository.findAll().stream()
                .map(a -> ActivityResDto.from(a, countMap.getOrDefault(a.getId(), 0L)))
                .toList();
    }
}
