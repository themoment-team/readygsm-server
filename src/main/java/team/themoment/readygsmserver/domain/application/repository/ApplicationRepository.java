package team.themoment.readygsmserver.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.themoment.readygsmserver.domain.application.entity.ApplicationJpaEntity;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<ApplicationJpaEntity, Long> {
    boolean existsByUser_Id(Long userId);
    long countByActivity_Id(Long activityId);
    long countByActivity_IdAndIsReserve(Long activityId, boolean isReserve);
    boolean existsByActivity_IdAndIsReserve(Long activityId, boolean isReserve);
    Optional<ApplicationJpaEntity> findByActivity_IdAndUser_Id(Long activityId, Long userId);
    Optional<ApplicationJpaEntity> findFirstByActivity_IdAndIsReserveTrueOrderByCreatedAtAsc(Long activityId);

    @Query("SELECT app.activity.id, COUNT(app) FROM ApplicationJpaEntity app GROUP BY app.activity.id")
    List<Object[]> countApplicantsGroupedByActivity();
    List<ApplicationJpaEntity> findAllByActivity_Id(Long activityId);
    Optional<ApplicationJpaEntity> findByUser_Id(Long userId);

    @Modifying
    @Query("DELETE FROM ApplicationJpaEntity a WHERE a.activity.id = :activityId AND a.user.id = :userId")
    int deleteByActivity_IdAndUser_Id(@Param("activityId") Long activityId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM ApplicationJpaEntity a WHERE a.id = :id")
    int deleteByApplicationId(@Param("id") Long id);
}
