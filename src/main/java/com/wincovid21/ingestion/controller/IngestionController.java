package com.wincovid21.ingestion.controller;

import com.wincovid21.ingestion.domain.ResourceDetailDTO;
import com.wincovid21.ingestion.exception.InvalidLeadCreationDataException;
import com.wincovid21.ingestion.service.IngestionServiceImpl;
import com.wincovid21.ingestion.util.monit.ProfileResponseType;
import com.wincovid21.ingestion.util.monit.Profiler;
import com.wincovid21.ingestion.util.monit.ProfilerNames;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sheet")
public class IngestionController {

    private final IngestionServiceImpl ingestionService;
    private final Profiler profiler;
    private static final Logger logger = LoggerFactory.getLogger(IngestionController.class);

    @Autowired
    public IngestionController(@NonNull final IngestionServiceImpl ingestionService,
                               @NonNull final Profiler profiler) {
        this.ingestionService = ingestionService;
        this.profiler = profiler;
    }

    @PostMapping(value = "/bulk/create")
    public ResponseEntity<String> resourceBulkCreate() {
        int rows = ingestionService.resourceBulkCreate();
        return new ResponseEntity<>("Successfully processed " + rows + " rows from the spreadsheet for creation", HttpStatus.OK);
    }

    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<String> resourceCreate(@RequestBody ResourceDetailDTO resourceDetailDTO) {
        try {
            profiler.incrementCount(ProfilerNames.LEAD_CREATE_FROM_PORTAL, ProfileResponseType.TOTAL);
            ingestionService.resourceCreate(resourceDetailDTO);
        } catch (InvalidLeadCreationDataException e) {
            profiler.incrementCount(ProfilerNames.LEAD_CREATE_FROM_PORTAL, ProfileResponseType.BAD_REQUEST);
            logger.error("The entry is invalid so discarding it", e);
            return new ResponseEntity<>("Invalid entry, please try again with valid values", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            profiler.incrementCount(ProfilerNames.LEAD_CREATE_FROM_PORTAL, ProfileResponseType.ERROR);
            logger.error("The lead entry creation was unsuccessful due to", e);
            return new ResponseEntity<>("The lead entry creation was unsuccessful, Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        profiler.incrementCount(ProfilerNames.LEAD_CREATE_FROM_PORTAL, ProfileResponseType.SUCCESS);
        return new ResponseEntity<>("Successfully created the lead entry", HttpStatus.OK);
    }


    @PutMapping(value = "/bulk/update")
    public ResponseEntity<String> resourceBulkUpdate() {
        int rows = ingestionService.resourceBulkUpdate();
        return new ResponseEntity<>("Successfully processed " + rows + " rows from the spreadsheet for updation", HttpStatus.OK);
    }

    @GetMapping(value = "/lastupdate")
    public ResponseEntity<String> fetchSheetLastModifiedOn() {
        Long lastModifiedOn = ingestionService.fetchLastModifiedOn();
        return new ResponseEntity<>("Successfully fetched lastmodifiedon timestamp from the spreadsheet " + lastModifiedOn, HttpStatus.OK);
    }

}
