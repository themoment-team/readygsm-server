package team.themoment.readygsm.domain.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.domain.user.entity.UserJpaEntity;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    @Query("SELECT u FROM UserJpaEntity u WHERE " +
            "(:name IS NULL OR :name = '' OR u.name LIKE %:name%) AND " +
            "(:email IS NULL OR :email = '' OR u.email LIKE %:email%) AND " +
            "(:role IS NULL OR u.role = :role)")
    Page<UserJpaEntity> findByNameContainingAndEmailContainingAndRole(
            @Param("name") String name,
            @Param("email") String email,
            @Param("role") UserRole role,
            Pageable pageable
    );
}