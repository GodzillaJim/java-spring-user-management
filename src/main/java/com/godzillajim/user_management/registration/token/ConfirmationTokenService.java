package com.godzillajim.user_management.registration.token;

import com.godzillajim.user_management.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    public void saveConfirmtionToken(ConfirmationToken token){
        confirmationTokenRepository.save(token);
    }

    public ConfirmationToken getToken(String token) {
        return confirmationTokenRepository
                  .findByToken(token)
                  .orElseThrow(
                          () -> new ResourceNotFoundException("Token not " +
                                  "found!"));
    }

    public void setConfirmedAt(String token) {
        ConfirmationToken confirmationToken = getToken(token);
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
    }
}
