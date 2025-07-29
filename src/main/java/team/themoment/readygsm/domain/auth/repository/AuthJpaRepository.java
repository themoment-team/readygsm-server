package team.themoment.readygsm.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.readygsm.domain.auth.entity.AuthJpaEntity;
import team.themoment.readygsm.domain.user.entity.UserJpaEntity;

import java.util.Optional;

public interface AuthJpaRepository extends JpaRepository<AuthJpaEntity, Long> {
    Optional<AuthJpaEntity> findByEmail(String email);
}
