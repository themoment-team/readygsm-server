package team.themoment.readygsmserver.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.readygsmserver.domain.application.entity.ApplicationJpaEntity;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<ApplicationJpaEntity, Long> {
    boolean existsByUser_Id(Long userId);
    Optional<ApplicationJpaEntity> findByActivity_IdAndUser_Id(Long activityId, Long userId);
    long countByActivity_Id(Long activityId);
    List<ApplicationJpaEntity> findAllByActivity_Id(Long activityId);
}
