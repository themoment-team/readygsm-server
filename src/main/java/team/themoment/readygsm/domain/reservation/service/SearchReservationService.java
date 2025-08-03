package team.themoment.readygsm.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import team.themoment.readygsm.domain.reservation.entity.ReservationJpaEntity;
import team.themoment.readygsm.domain.reservation.presentation.data.dto.SearchReservationAcitivityDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.SearchReservationResDto;
import team.themoment.readygsm.domain.reservation.repository.ReservationJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchReservationService {
    private final ReservationJpaRepository reservationJpaRepository;
    public List<SearchReservationResDto> searchReservation(
            String activityName,
            String applicantName,
            String phoneNumber,
            int page,
            int limit
    ) {
        Page<ReservationJpaEntity> searchResult = reservationJpaRepository.findByInformation(
                activityName,
                applicantName,
                phoneNumber,
                PageRequest.of(page, limit)
        );

        return searchResult.getContent().stream()
                .map(r -> new SearchReservationResDto(
                        r.getId(),
                        r.getPhoneNumber(),
                        r.getSchoolName(),
                        r.getGrade(),
                        r.getClassNumber(),
                        r.getStudentNumber(),
                        r.getApplicantName(),
                        new SearchReservationAcitivityDto(
                                r.getActivity().getId(),
                                r.getActivity().getName(),
                                r.getActivity().getImage(),
                                r.getActivity().getDate(),
                                r.getActivity().getCurrentApplicant(),
                                r.getActivity().getMaxApplicant(),
                                r.getActivity().getType(),
                                r.getActivity().getPlace(),
                                r.getActivity().getMajor(),
                                r.getActivity().getApplicationStart(),
                                r.getActivity().getApplicationEnd(),
                                getApplicationStatus(
                                        r.getActivity().getApplicationStart(),
                                        r.getActivity().getApplicationEnd())
                        )
                )).toList();
    }

    // 활동 상태
    private String getApplicationStatus(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        if(now.isBefore(startTime)) {
            return "UPCOMING";
        } else if(now.isAfter(endTime)) {
            return "CLOSED";
        }
        return "OPEN";
    }
}
