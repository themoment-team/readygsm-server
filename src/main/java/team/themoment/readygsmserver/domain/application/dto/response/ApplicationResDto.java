package team.themoment.readygsmserver.domain.application.dto.response;

import team.themoment.readygsmserver.domain.application.entity.ApplicationJpaEntity;

public record ApplicationResDto(
        Long id,
        Long activityId,
        Long userId,
        String name,
        Integer grade,
        Integer classNumber,
        Integer number,
        String schoolName,
        String phoneNumber,
        String familyPhoneNumber,
        boolean isReserve
) {
    public static ApplicationResDto from(ApplicationJpaEntity entity) {
        return new ApplicationResDto(
                entity.getId(),
                entity.getActivity().getId(),
                entity.getUser().getId(),
                entity.getName(),
                entity.getGrade(),
                entity.getClassNumber(),
                entity.getNumber(),
                entity.getSchoolName(),
                entity.getPhoneNumber(),
                entity.getFamilyPhoneNumber(),
                entity.isReserve()
        );
    }
}
