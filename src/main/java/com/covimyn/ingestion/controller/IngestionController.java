package com.covimyn.ingestion.controller;

import com.covimyn.ingestion.service.impl.IngestionServiceImpl;
import com.google.api.client.util.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingestion/sheet")
public class IngestionController {

    private final IngestionServiceImpl ingestionService;

    @Autowired
    public IngestionController(IngestionServiceImpl ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping(value = "/bulk/create")
    public ResponseEntity<String> resourceBulkCreate() {
        ingestionService.resourceBulkCreate();
        return new ResponseEntity<>("Successfully updated rows from the spreadsheet", HttpStatus.OK);
    }

    @GetMapping(value = "/lastupdate")
    public ResponseEntity<String> fetchSheetLastModifiedOn() {
        DateTime lastModifiedOn = ingestionService.fetchLastModifiedOn();
        return new ResponseEntity<>("Successfully fetched lastmodifiedon from the spreadsheet " + lastModifiedOn , HttpStatus.OK);
    }

}
