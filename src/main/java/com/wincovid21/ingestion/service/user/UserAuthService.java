package com.wincovid21.ingestion.service.user;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.LoginRequest;
import com.wincovid21.ingestion.entity.UserDetails;
import com.wincovid21.ingestion.entity.UserSession;
import com.wincovid21.ingestion.exception.UnAuthorizedUserException;
import com.wincovid21.ingestion.repository.UserRepository;
import com.wincovid21.ingestion.repository.UserSessionRepository;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserAuthService {
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;

    @Value("${wincovid.auth.token.expiry.hours}")
    @Getter
    private int loginAuthTokenExpiryHours;

    public UserAuthService(@Autowired final UserRepository userRepository,
                           @Autowired final UserSessionRepository userSessionRepository) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
    }


    public UserSession login(@NonNull final LoginRequest loginRequest)
            throws UnAuthorizedUserException {

        log.info("User Login # {}", loginRequest);
        if (!(StringUtils.hasText(loginRequest.getUser()))) {
            throw new IllegalArgumentException("Uer name can not be null or empty");
        }
        if (!(StringUtils.hasText(loginRequest.getPassword()))) {
            throw new IllegalArgumentException("Password name can not be null or empty");
        }

        Optional<UserDetails> byUserNameAndPassword = userRepository.findByUserNameAndPassword(loginRequest.getUser(), loginRequest.getPassword());

        if (!(byUserNameAndPassword.isPresent())) {
            throw new UnAuthorizedUserException("Username or password is not valid.");
        }
        UserDetails userDetails = byUserNameAndPassword.get();

        final UserSession userSession = new UserSession();
        userSession.setUser(userDetails);
        userSession.setTokenId(UUID.randomUUID().toString());
        userSession.setCreatedOn(new Date());
        userSession.setName(userDetails.getName());

        userSessionRepository.save(userSession);
        log.info("User Session Updated for user # {}, with # {}", userDetails, userSession);

        isAuthorised(userSession.getTokenId());

        return userSession;
    }

    @Trace
    public boolean isAuthorised(@NonNull final String token) {
        Optional<UserSession> userSessionOptional = userSessionRepository.findByTokenId(token);

        if (!(userSessionOptional.isPresent())) {
            return false;
        }

        UserSession userSession = userSessionOptional.get();
        Calendar tokenCreteTime = Calendar.getInstance();
        tokenCreteTime.setTime(userSession.getCreatedOn());
        tokenCreteTime.add(Calendar.HOUR_OF_DAY, loginAuthTokenExpiryHours);
        Date tokenExpiryTime = tokenCreteTime.getTime();
        log.info("Token Expiry Time # {}", tokenExpiryTime);

        if (tokenExpiryTime.after(new Date())) {
            return true;
        } else {
            return false;
        }
    }

}
