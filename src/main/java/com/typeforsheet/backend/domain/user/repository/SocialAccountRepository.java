package com.typeforsheet.backend.domain.user.repository;

import com.typeforsheet.backend.domain.user.entity.SocialAccount;
import com.typeforsheet.backend.domain.user.entity.SocialProvider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

  Optional<SocialAccount> findByProviderAndProviderUserId(
      SocialProvider provider, String providerUserId);
}
