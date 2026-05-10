package team.themoment.readygsmserver.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.readygsmserver.domain.user.entity.UserJpaEntity;
import team.themoment.readygsmserver.domain.user.entity.constant.AuthReferrerType;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByAuthReferrerTypeAndEmail(AuthReferrerType authReferrerType, String email);
    Optional<UserJpaEntity> findByEmail(String email);
}