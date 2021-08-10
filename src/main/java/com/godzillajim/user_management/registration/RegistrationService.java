package com.godzillajim.user_management.registration;

import com.godzillajim.user_management.appuser.AppUser;
import com.godzillajim.user_management.appuser.AppUserRole;
import com.godzillajim.user_management.appuser.AppUserService;
import com.godzillajim.user_management.exceptions.EmailAlreadyConfirmedException;
import com.godzillajim.user_management.exceptions.TokenExpiredException;
import com.godzillajim.user_management.exceptions.WrongEmailFormatException;
import com.godzillajim.user_management.registration.token.ConfirmationToken;
import com.godzillajim.user_management.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {
    private EmailValidator emailValidator;
    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    public String register(RegistrationRequest request){
        boolean isValid = emailValidator.test(request.getEmail());
        if(!isValid){
            throw new WrongEmailFormatException("Email not valid");
        }
        return appUserService.signUpUser(
                new AppUser(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.USER
                )
        );
    }
    @Transactional
    public String confirmToken(String token){
        ConfirmationToken confirmationToken =
                confirmationTokenService.getToken(token);
        if(confirmationToken.getConfirmedAt() != null){
            throw new EmailAlreadyConfirmedException("Email already " +
                    "confirmed!");
        }
        LocalDateTime expiresAt = confirmationToken.getExpiresAt();
        if(expiresAt.isBefore(LocalDateTime.now())){
            throw new TokenExpiredException("Token already expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());
        return "confirmed";
    }
}
