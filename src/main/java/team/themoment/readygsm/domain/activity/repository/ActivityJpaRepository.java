package team.themoment.readygsm.domain.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.themoment.readygsm.domain.activity.entity.ActivityJpaEntity;

@Repository
public interface ActivityJpaRepository extends JpaRepository<ActivityJpaEntity, Long> {
}