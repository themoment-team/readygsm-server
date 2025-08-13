package team.themoment.readygsm.domain.reservation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import team.themoment.readygsm.domain.reservation.entity.ReservationJpaEntity;
import org.springframework.data.domain.Page;

import java.util.List;

@Repository
public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {
    @Query("SELECT r FROM ReservationJpaEntity r JOIN FETCH r.activity WHERE r.user.id = :userId")
    List<ReservationJpaEntity> findByUserId(Long userId);
    @Query("SELECT r FROM ReservationJpaEntity r JOIN FETCH r.activity WHERE r.user.id IN :userIds")
    List<ReservationJpaEntity> findByUserIdIn(List<Long> userIds);

    @Modifying
    @Query("DELETE FROM ReservationJpaEntity r WHERE r.user.id = :userId AND r.id = :reservationId")
    int deleteByIdAndUserId(Long userId, Long reservationId);

    // N+1 수정을 위해서 EntityGraph를 사용하여 Lazy 로딩을 즉시 로딩으로 처리되게 하였습니다.
    @EntityGraph(attributePaths = {"activity"})
    @Query("""
    SELECT r FROM ReservationJpaEntity r
    JOIN r.activity a
    WHERE (:activityName IS NULL OR a.name LIKE %:activityName%)
    AND (:applicantName IS NULL OR r.applicantName LIKE %:applicantName%)
    AND (:phoneNumber IS NULL OR r.phoneNumber = :phoneNumber)""")
    Page<ReservationJpaEntity> findByActivityNameAndApplicantNameAndPhoneNumberWithPaging(
            String activityName,
            String applicantName,
            String phoneNumber,
            Pageable pageable
    );
}