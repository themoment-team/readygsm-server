package team.themoment.readygsmserver.domain.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import team.themoment.readygsmserver.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsmserver.domain.activity.repository.ActivityRepository;
import team.themoment.readygsmserver.domain.application.dto.request.ApplicationReqDto;
import team.themoment.readygsmserver.domain.application.dto.response.ApplicationResDto;
import team.themoment.readygsmserver.domain.application.entity.ApplicationJpaEntity;
import team.themoment.readygsmserver.domain.application.repository.ApplicationRepository;
import team.themoment.readygsmserver.domain.user.entity.UserJpaEntity;
import team.themoment.readygsmserver.domain.user.entity.constant.Role;
import team.themoment.readygsmserver.domain.user.repository.UserRepository;
import team.themoment.sdk.exception.ExpectedException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplyActivityServiceTest {

    private static final Long ACTIVITY_ID = 1L;
    private static final Long USER_ID = 1L;

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplyActivityService applyActivityService;

    private ApplicationReqDto req;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        ActivityJpaEntity activity = ActivityJpaEntity.builder()
                .id(ACTIVITY_ID)
                .name("체험")
                .place("장소")
                .description("설명")
                .maxApplicant(2)
                .activityDate(now.toLocalDate())
                .registrationStartAt(now.minusDays(1))
                .registrationEndAt(now.plusDays(1))
                .activityStartTime(now.toLocalTime())
                .activityEndTime(now.toLocalTime().plusHours(1))
                .build();

        UserJpaEntity user = UserJpaEntity.builder()
                .id(USER_ID)
                .role(Role.USER)
                .build();

        req = new ApplicationReqDto(
                "홍길동", 1, 1, 1, "광주소프트웨어마이스터고", "010-1234-5678", "010-8765-4321"
        );

        when(activityRepository.findByIdWithLock(ACTIVITY_ID)).thenReturn(Optional.of(activity));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(applicationRepository.existsByUser_Id(USER_ID)).thenReturn(false);
        lenient().when(applicationRepository.save(any(ApplicationJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void 정원이_남아있으면_확정으로_저장된다() {
        when(applicationRepository.countByActivity_IdAndIsReserve(ACTIVITY_ID, false)).thenReturn(0L);
        when(applicationRepository.countByActivity_IdAndIsReserve(ACTIVITY_ID, true)).thenReturn(0L);

        ApplicationResDto result = applyActivityService.execute(USER_ID, ACTIVITY_ID, req);

        assertThat(result.isReserve()).isFalse();
    }

    @Test
    void 예비인원이_3명_미만이면_예비인원으로_저장된다() {
        when(applicationRepository.countByActivity_IdAndIsReserve(ACTIVITY_ID, false)).thenReturn(2L);
        when(applicationRepository.countByActivity_IdAndIsReserve(ACTIVITY_ID, true)).thenReturn(2L);

        ApplicationResDto result = applyActivityService.execute(USER_ID, ACTIVITY_ID, req);

        assertThat(result.isReserve()).isTrue();
        verify(applicationRepository).save(any(ApplicationJpaEntity.class));
    }

    @Test
    void 예비인원이_3명이면_추가_신청은_예외가_발생한다() {
        when(applicationRepository.countByActivity_IdAndIsReserve(ACTIVITY_ID, false)).thenReturn(2L);
        when(applicationRepository.countByActivity_IdAndIsReserve(ACTIVITY_ID, true)).thenReturn(3L);

        assertThatThrownBy(() -> applyActivityService.execute(USER_ID, ACTIVITY_ID, req))
                .asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.throwable(ExpectedException.class))
                .satisfies(ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getMessage()).isEqualTo("예비인원이 마감되었습니다.");
                });

        verify(applicationRepository, never()).save(any());
    }
}
