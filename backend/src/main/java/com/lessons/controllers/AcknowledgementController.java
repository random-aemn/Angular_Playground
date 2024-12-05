package com.lessons.controllers;

import com.lessons.services.AcknowledgmentService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AcknowledgementController {

    Logger logger = LoggerFactory.getLogger(AcknowledgementController.class);

    @Resource
    private  AcknowledgmentService acknowledgmentService;

    @RequestMapping(value = "/api/set/acknowledge", method = RequestMethod.PUT, produces = "application/json")
    @PreAuthorize("hasAnyRole('APP16_SUPERVISOR', 'APP16_SPECIALIST', 'APP16_ADMIN', 'APP16_REVIEWER')")
    public ResponseEntity<?> setAcknowledgeDate() {

        logger.debug("I'm in the Acknowledgement Controller");

        this.acknowledgmentService.setAcknowledgementDate();

        return ResponseEntity.status(HttpStatus.OK).build();

    }




}
