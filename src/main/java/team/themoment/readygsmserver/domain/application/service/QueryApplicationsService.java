package team.themoment.readygsmserver.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.application.dto.response.ApplicationResDto;
import team.themoment.readygsmserver.domain.application.entity.ApplicationJpaEntity;
import team.themoment.readygsmserver.domain.application.repository.ApplicationRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryApplicationsService {

    private final ApplicationRepository applicationRepository;

    public List<ApplicationResDto> execute(Long activityId) {
        List<ApplicationJpaEntity> applications = applicationRepository.findAllByActivity_Id(activityId);

        Map<Long, Integer> reserveOrderById = new HashMap<>();
        applications.stream()
                .filter(ApplicationJpaEntity::isReserve)
                .sorted(Comparator.comparing(ApplicationJpaEntity::getCreatedAt).thenComparing(ApplicationJpaEntity::getId))
                .forEach(app -> reserveOrderById.put(app.getId(), reserveOrderById.size() + 1));

        return applications.stream()
                .map(app -> ApplicationResDto.from(app, reserveOrderById.get(app.getId())))
                .toList();
    }
}
