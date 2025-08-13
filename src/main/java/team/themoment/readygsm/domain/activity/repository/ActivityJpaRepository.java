package team.themoment.readygsm.domain.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import team.themoment.readygsm.domain.activity.entity.ActivityJpaEntity;

@Repository
public interface ActivityJpaRepository extends JpaRepository<ActivityJpaEntity, Long> {
    @Modifying
    @Query("""
    UPDATE ActivityJpaEntity a
    SET a.currentApplicant = a.currentApplicant - 1
    WHERE a.id = :activityId
    AND a.currentApplicant > 0""")
    void decreaseActivityApplicant(Long activityId);
}