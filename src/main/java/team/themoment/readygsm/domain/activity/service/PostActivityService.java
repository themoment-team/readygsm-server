package team.themoment.readygsm.domain.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.activity.data.Activity;
import team.themoment.readygsm.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsm.domain.activity.presentation.data.request.PostActivityReqDto;
import team.themoment.readygsm.domain.activity.presentation.data.response.PostActivityResDto;
import team.themoment.readygsm.domain.activity.repository.ActivityJpaRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PostActivityService {
    private final ActivityJpaRepository activityJpaRepository;

    public PostActivityResDto execute(
            final PostActivityReqDto reqDto
    ) {
        Activity savedActivity = activityJpaRepository.save(
                Activity.builder()
                        .name(reqDto.activityName())
                        .date(reqDto.activityDate())
                        .maxApplicant(reqDto.maxApplicant())
                        .type(reqDto.activityType())
                        .place(reqDto.activityPlace())
                        .major(reqDto.activityMajor())
                        .applicationStart(reqDto.applicationStart())
                        .applicationEnd(reqDto.applicationEnd())
                        .build()
                        .toEntity()
        ).toDto();

        return new PostActivityResDto(
                savedActivity.getId(),
                reqDto.activityName(),
                reqDto.activityDate(),
                savedActivity.getCurrentApplicant(),
                reqDto.maxApplicant(),
                reqDto.activityType(),
                reqDto.activityPlace(),
                reqDto.activityMajor(),
                reqDto.applicationStart(),
                reqDto.applicationEnd(),
                ActivityJpaEntity.getApplicationStatus(
                        reqDto.applicationStart(),
                        reqDto.applicationEnd()
                )
        );
    }
}
