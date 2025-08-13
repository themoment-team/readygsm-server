package team.themoment.readygsm.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.reservation.data.Reservation;
import team.themoment.readygsm.domain.reservation.entity.ReservationJpaEntity;
import team.themoment.readygsm.domain.reservation.presentation.data.constant.ActivityStatus;
import team.themoment.readygsm.domain.reservation.presentation.data.SearchReservationActivityDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.SearchReservationResDto;
import team.themoment.readygsm.domain.reservation.repository.ReservationJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchReservationService {
    private final ReservationJpaRepository reservationJpaRepository;
    public List<SearchReservationResDto> searchReservation(
            String activityName,
            String applicantName,
            String phoneNumber,
            int page,
            int limit
    ) {
        Page<Reservation> searchResult = reservationJpaRepository.findByActivityNameAndApplicantNameAndPhoneNumberWithPaging(
                activityName,
                applicantName,
                phoneNumber,
                PageRequest.of(page, limit)
        ).map(ReservationJpaEntity::toDto);

        return searchResult.getContent().stream()
                .map(r -> new SearchReservationResDto(
                        r.getId(),
                        r.getPhoneNumber(),
                        r.getSchoolName(),
                        r.getGrade(),
                        r.getClassNumber(),
                        r.getStudentNumber(),
                        r.getApplicantName(),
                        new SearchReservationActivityDto(
                                r.getActivityId().getId(),
                                r.getActivityId().getName(),
                                r.getActivityId().getImage(),
                                r.getActivityId().getDate(),
                                r.getActivityId().getCurrentApplicant(),
                                r.getActivityId().getMaxApplicant(),
                                r.getActivityId().getType(),
                                r.getActivityId().getPlace(),
                                r.getActivityId().getMajor(),
                                r.getActivityId().getApplicationStart(),
                                r.getActivityId().getApplicationEnd(),
                                getApplicationStatus(
                                        r.getActivityId().getApplicationStart(),
                                        r.getActivityId().getApplicationEnd())
                        )
                )).toList();
    }

    // 활동 상태
    private String getApplicationStatus(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        ActivityStatus status;

        if(now.isBefore(startTime)) {
            status = ActivityStatus.UPCOMING;
        } else if(now.isAfter(endTime)) {
            status = ActivityStatus.CLOSED;
        } else {
            status = ActivityStatus.OPEN;
        }
        return status.name();
    }
}
