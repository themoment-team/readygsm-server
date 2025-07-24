package team.themoment.readygsm.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.themoment.readygsm.domain.reservation.entity.ReservationJpaEntity;

@Repository
public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {
}