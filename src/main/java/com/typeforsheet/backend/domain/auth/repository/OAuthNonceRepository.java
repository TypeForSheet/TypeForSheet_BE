package com.typeforsheet.backend.domain.auth.repository;

import com.typeforsheet.backend.domain.auth.entity.OAuthNonce;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OAuthNonceRepository extends JpaRepository<OAuthNonce, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT n FROM OAuthNonce n " + "WHERE n.nonceHash = :nonceHash")
  Optional<OAuthNonce> findByNonceHashForUpdate(@Param("nonceHash") String nonceHash);

  @Modifying
  @Query("DELETE FROM OAuthNonce n " + "WHERE n.expiresAt < :now")
  int deleteExpiredNonces(@Param("now") LocalDateTime now);
}
