package team.themoment.readygsmserver.domain.application.service;

import org.junit.jupiter.api.BeforeEach;
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
import team.themoment.sdk.exception.ExpectedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryMyApplicationsServiceTest {

    private static final Long ACTIVITY_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long APPLICATION_ID = 10L;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private QueryMyApplicationsService queryMyApplicationsService;

    private ActivityJpaEntity activity;
    private UserJpaEntity user;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        activity = ActivityJpaEntity.builder().id(ACTIVITY_ID).build();
        user = UserJpaEntity.builder().id(USER_ID).build();
        createdAt = LocalDateTime.now();
    }

    @Test
    void 확정된_신청은_reserveOrder가_없다() {
        ApplicationJpaEntity application = ApplicationJpaEntity.builder()
                .id(APPLICATION_ID)
                .activity(activity)
                .user(user)
                .isReserve(false)
                .createdAt(createdAt)
                .build();
        when(applicationRepository.findByUser_Id(USER_ID)).thenReturn(Optional.of(application));

        ApplicationResDto result = queryMyApplicationsService.execute(USER_ID);

        assertThat(result.isReserve()).isFalse();
        assertThat(result.reserveOrder()).isNull();
    }

    @Test
    void 예비인원은_앞에_대기중인_인원_수_1을_reserveOrder로_받는다() {
        ApplicationJpaEntity application = ApplicationJpaEntity.builder()
                .id(APPLICATION_ID)
                .activity(activity)
                .user(user)
                .isReserve(true)
                .createdAt(createdAt)
                .build();
        when(applicationRepository.findByUser_Id(USER_ID)).thenReturn(Optional.of(application));
        when(applicationRepository.countReserveApplicantsAheadOf(eq(ACTIVITY_ID), eq(createdAt), eq(APPLICATION_ID)))
                .thenReturn(1L);

        ApplicationResDto result = queryMyApplicationsService.execute(USER_ID);

        assertThat(result.isReserve()).isTrue();
        assertThat(result.reserveOrder()).isEqualTo(2);
    }

    @Test
    void 신청_내역이_없으면_예외가_발생한다() {
        when(applicationRepository.findByUser_Id(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryMyApplicationsService.execute(USER_ID))
                .isInstanceOf(ExpectedException.class);
    }
}