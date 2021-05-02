package com.wincovid21.ingestion.controller;

import com.wincovid21.ingestion.service.IngestionServiceImpl;
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

    @Autowired
    public IngestionController(IngestionServiceImpl ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping(value = "/bulk/create")
    public ResponseEntity<String> resourceBulkCreate() {
        int rows = ingestionService.resourceBulkCreate();
        return new ResponseEntity<>("Successfully processed " + rows + " rows from the spreadsheet for creation", HttpStatus.OK);
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
