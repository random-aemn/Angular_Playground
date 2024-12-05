package com.lessons.controllers;

import com.lessons.models.MyLookupDTO;
import com.lessons.services.MyLookupService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

// The @Controller annotation tells Spring that REST calls will originate from this class
@Controller
public class MyLookupController {

    private static final Logger logger = LoggerFactory.getLogger(MyLookupController.class);

    @Resource
    private MyLookupService myLookupService;

    @RequestMapping(value = "/api/get/priorities", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAnyRole('APP16_SUPERVISOR', 'APP16_SPECIALIST', 'APP16_ADMIN', 'APP16_REVIEWER')")
    public ResponseEntity<?> getAllPriorities(){

        logger.debug("I'm in the getAllPriorities method");

        List<MyLookupDTO> priorities = myLookupService.getAllPriorities();

        return ResponseEntity.status(HttpStatus.OK).body(priorities);

    }


}
