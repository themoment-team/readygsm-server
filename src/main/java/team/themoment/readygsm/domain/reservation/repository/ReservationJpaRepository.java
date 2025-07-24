package team.themoment.readygsm.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import team.themoment.readygsm.domain.reservation.entity.ReservationJpaEntity;

import java.util.List;

@Repository
public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {
    @Query("SELECT r FROM ReservationJpaEntity r JOIN FETCH r.activity WHERE r.user.id = :userId")
    List<ReservationJpaEntity> findByUserId(Long userId);
    @Query("SELECT r FROM ReservationJpaEntity r JOIN FETCH r.activity WHERE r.user.id IN :userIds")
    List<ReservationJpaEntity> findByUserIdIn(List<Long> userIds);
}