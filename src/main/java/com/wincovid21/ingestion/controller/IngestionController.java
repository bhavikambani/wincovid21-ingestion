package com.wincovid21.ingestion.controller;

import com.wincovid21.ingestion.domain.ResourceDetailDTO;
import com.wincovid21.ingestion.service.IngestionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sheet")
public class IngestionController {

    private final IngestionServiceImpl ingestionService;
    private static final Logger logger = LoggerFactory.getLogger(IngestionController.class);

    @Autowired
    public IngestionController(IngestionServiceImpl ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping(value = "/bulk/create")
    public ResponseEntity<String> resourceBulkCreate() {
        int rows = ingestionService.resourceBulkCreate();
        return new ResponseEntity<>("Successfully processed " + rows + " rows from the spreadsheet for creation", HttpStatus.OK);
    }

    @PostMapping(value = "/create",consumes = "application/json")
    public ResponseEntity<String> resourceCreate(@RequestBody ResourceDetailDTO resourceDetailDTO) {
        try {
              ingestionService.resourceCreate(resourceDetailDTO);
        } catch (Exception e) {
            logger.error("The lead entry creation was unsuccessfull due to", e);
            return new ResponseEntity<>("The lead entry creation was unsuccessfull, Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        return new ResponseEntity<>("Successfully fetched lastmodifiedon timestamp from the spreadsheet " + lastModifiedOn , HttpStatus.OK);
    }

}
