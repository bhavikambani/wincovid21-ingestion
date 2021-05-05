package com.wincovid21.ingestion.controller;

import com.wincovid21.ingestion.entity.UserDetails;
import com.wincovid21.ingestion.repository.CityRepository;
import com.wincovid21.ingestion.repository.UserActionAuditRepository;
import com.wincovid21.ingestion.repository.UserRepository;
import com.wincovid21.ingestion.repository.UserTypeAllowedFeedbackTypesRepository;
import com.wincovid21.ingestion.service.UserActionService;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import com.wincovid21.ingestion.util.monit.Profiler;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HelloCoviMyn {

    private final UserActionAuditRepository userActionFlagRepository;
    @Autowired
    private final UserActionService userActionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final CityRepository cityRepository;
    @Autowired
    private final CacheUtil cacheUtil;


    @Autowired
    private UserTypeAllowedFeedbackTypesRepository userTypeAllowedFeedbackTypesRepository;

    private final Profiler profiler;

    public HelloCoviMyn(@NonNull final UserActionAuditRepository userActionFlagRepository,
                        @NonNull final Profiler profiler,
                        @NonNull final UserActionService userActionService,
                        @NonNull final CacheUtil cacheUtil, CityRepository cityRepository) {
        this.userActionFlagRepository = userActionFlagRepository;
        this.profiler = profiler;
        this.userActionService = userActionService;
        this.cacheUtil = cacheUtil;
        this.cityRepository = cityRepository;
    }

    @GetMapping("/")
    public Boolean sayHello(@RequestParam Long userId) {
        log.info("Request received with id # {} and {}", userId, userRepository.findById(userId));
        UserDetails userDetails = userRepository.findById(userId).get();
        log.info("Types # {}", userDetails.getUserType());

        log.info("Feedback List # {}", cacheUtil.getUseWiseAllowedFeedback(userDetails.getUserType().getId()));

        log.info("All States # {}", cacheUtil.getAllStateCityDetails());


        log.info("Sessions # {}", userTypeAllowedFeedbackTypesRepository.findAllByUserType(userDetails.getUserType().getId()));
        return true;
    }

}

@Builder
@Data
class Response {
    String a;
    String b;
}
