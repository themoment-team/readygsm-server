package team.themoment.readygsm.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.themoment.readygsm.domain.reservation.entity.ReservationJpaEntity;

@Repository
public interface UserJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {
}