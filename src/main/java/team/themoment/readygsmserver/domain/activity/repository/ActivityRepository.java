package team.themoment.readygsmserver.domain.activity.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.themoment.readygsmserver.domain.activity.entity.ActivityJpaEntity;

import java.util.Optional;

public interface ActivityRepository extends JpaRepository<ActivityJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM ActivityJpaEntity a WHERE a.id = :id")
    Optional<ActivityJpaEntity> findByIdWithLock(Long id);

    @Modifying
    @Query("DELETE FROM ActivityJpaEntity a WHERE a.id = :id")
    int deleteByActivityId(@Param("id") Long id);
}
