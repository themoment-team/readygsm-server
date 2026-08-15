package team.themoment.readygsmserver.domain.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team.themoment.readygsmserver.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsmserver.domain.application.dto.response.ApplicationResDto;
import team.themoment.readygsmserver.domain.application.entity.ApplicationJpaEntity;
import team.themoment.readygsmserver.domain.application.repository.ApplicationRepository;
import team.themoment.readygsmserver.domain.user.entity.UserJpaEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryApplicationsServiceTest {

    private static final Long ACTIVITY_ID = 1L;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private QueryApplicationsService queryApplicationsService;

    @Test
    void 예비인원들의_reserveOrder는_신청_순서대로_매겨진다() {
        ActivityJpaEntity activity = ActivityJpaEntity.builder().id(ACTIVITY_ID).build();
        LocalDateTime baseTime = LocalDateTime.now();

        ApplicationJpaEntity confirmed = applicationOf(activity, 1L, false, baseTime.minusMinutes(10));
        ApplicationJpaEntity reserveFirst = applicationOf(activity, 2L, true, baseTime.minusMinutes(5));
        ApplicationJpaEntity reserveSecond = applicationOf(activity, 3L, true, baseTime.minusMinutes(3));
        ApplicationJpaEntity reserveThird = applicationOf(activity, 4L, true, baseTime.minusMinutes(1));

        when(applicationRepository.findAllByActivity_Id(ACTIVITY_ID))
                .thenReturn(List.of(confirmed, reserveThird, reserveFirst, reserveSecond));

        List<ApplicationResDto> result = queryApplicationsService.execute(ACTIVITY_ID);

        Map<Long, Integer> reserveOrderById = new HashMap<>();
        result.forEach(dto -> reserveOrderById.put(dto.id(), dto.reserveOrder()));

        assertThat(reserveOrderById.get(1L)).isNull();
        assertThat(reserveOrderById.get(2L)).isEqualTo(1);
        assertThat(reserveOrderById.get(3L)).isEqualTo(2);
        assertThat(reserveOrderById.get(4L)).isEqualTo(3);
    }

    private ApplicationJpaEntity applicationOf(ActivityJpaEntity activity, Long id, boolean isReserve, LocalDateTime createdAt) {
        UserJpaEntity user = UserJpaEntity.builder().id(id).build();
        return ApplicationJpaEntity.builder()
                .id(id)
                .activity(activity)
                .user(user)
                .isReserve(isReserve)
                .createdAt(createdAt)
                .build();
    }
}