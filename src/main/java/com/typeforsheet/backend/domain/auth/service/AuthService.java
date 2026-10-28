package com.typeforsheet.backend.domain.auth.service;

import com.typeforsheet.backend.domain.user.entity.SocialAccount;
import com.typeforsheet.backend.domain.user.entity.SocialProvider;
import com.typeforsheet.backend.domain.user.entity.User;
import com.typeforsheet.backend.domain.user.repository.SocialAccountRepository;
import com.typeforsheet.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final SocialAccountRepository socialAccountRepository;

  @Transactional
  public User findOrCreateUser(SocialProvider provider, String providerUserId) {

    return socialAccountRepository
        .findByProviderAndProviderUserId(provider, providerUserId)
        .map(SocialAccount::getUser)
        .orElseGet(() -> createUser(provider, providerUserId));
  }

  private User createUser(SocialProvider provider, String providerUserId) {

    User user = userRepository.save(new User(null));

    SocialAccount socialAccount = new SocialAccount(user, provider, providerUserId);

    socialAccountRepository.save(socialAccount);

    return user;
  }
}
