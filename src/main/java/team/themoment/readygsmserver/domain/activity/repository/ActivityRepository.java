package team.themoment.readygsmserver.domain.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.readygsmserver.domain.activity.entity.ActivityJpaEntity;

public interface ActivityRepository extends JpaRepository<ActivityJpaEntity, Long> {
}
