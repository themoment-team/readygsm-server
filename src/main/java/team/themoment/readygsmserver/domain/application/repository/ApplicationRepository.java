package team.themoment.readygsmserver.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.themoment.readygsmserver.domain.application.entity.ApplicationJpaEntity;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<ApplicationJpaEntity, Long> {
    boolean existsByUser_Id(Long userId);
    long countByActivity_Id(Long activityId);
    List<ApplicationJpaEntity> findAllByActivity_Id(Long activityId);

    @Modifying
    @Query("DELETE FROM ApplicationJpaEntity a WHERE a.activity.id = :activityId AND a.user.id = :userId")
    int deleteByActivity_IdAndUser_Id(@Param("activityId") Long activityId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM ApplicationJpaEntity a WHERE a.id = :id")
    int deleteByApplicationId(@Param("id") Long id);
}
