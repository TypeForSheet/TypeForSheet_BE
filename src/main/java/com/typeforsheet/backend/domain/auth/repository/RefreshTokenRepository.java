package com.typeforsheet.backend.domain.auth.repository;

import com.typeforsheet.backend.domain.auth.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
            SELECT rt
            FROM RefreshToken rt
            JOIN FETCH rt.user
            WHERE rt.tokenHash = :tokenHash
            """)
  Optional<RefreshToken> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);
}
