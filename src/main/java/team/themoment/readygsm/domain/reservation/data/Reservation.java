package team.themoment.readygsm.domain.reservation.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.themoment.readygsm.domain.activity.data.Activity;
import team.themoment.readygsm.domain.reservation.entity.ReservationJpaEntity;
import team.themoment.readygsm.domain.user.data.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    private Long id;
    private Activity activityId;
    private User userId;
    private String phoneNumber;
    private String schoolName;
    private Integer grade;
    private Integer classNumber;
    private Integer studentNumber;
    private String applicantName;

    public ReservationJpaEntity toEntity() {
        return ReservationJpaEntity.builder()
                .id(id)
                .activity(activityId.toEntity())
                .user(userId.toEntity())
                .phoneNumber(phoneNumber)
                .schoolName(schoolName)
                .grade(grade)
                .classNumber(classNumber)
                .studentNumber(studentNumber)
                .applicantName(applicantName)
                .build();
    }
}