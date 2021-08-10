package com.godzillajim.user_management.appuser;

import com.godzillajim.user_management.exceptions.ResourceNotFoundException;
import com.godzillajim.user_management.exceptions.WrongEmailFormatException;
import com.godzillajim.user_management.registration.token.ConfirmationToken;
import com.godzillajim.user_management.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return repository.findByEmail(s)
                .orElseThrow(()->new UsernameNotFoundException("Username not found!"));
    }
    public String signUpUser(AppUser appUser){
        boolean userExists = repository
                .findByEmail(appUser.getEmail()).isPresent();
        if(userExists){
            throw new WrongEmailFormatException("Email already exists");
        }
        // Encrypting password
        String encodedPassword = passwordEncoder
                .encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);

        // save user
        repository.save(appUser);

        // Generating token
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15), appUser
        );
        confirmationTokenService.saveConfirmtionToken(confirmationToken);
        return token;
        // TODO: send email
    }

    public void enableAppUser(String email) {
        AppUser user = repository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User does " +
                        "not exist!"));
        user.setEnabled(true);
        repository.save(user);

    }
}
