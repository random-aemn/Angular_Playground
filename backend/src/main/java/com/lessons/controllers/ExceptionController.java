package com.lessons.controllers;

import com.lessons.models.GetExceptionDTO;
import com.lessons.services.ExceptionService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@Controller
public class ExceptionController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Resource
    private ExceptionService exceptionService;

    @RequestMapping(value="/api/get-exceptions/{filterNumber}", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAnyRole('APP16_SUPERVISOR',  'APP16_ADMIN', 'APP16_REVIEWER')")
    public ResponseEntity<?> getExceptionList(@PathVariable(name="filterNumber") Integer aFilterNumber) {
        logger.debug("getExceptionList() started");

        // Make sure the filter number falls in the range of 1-5
        if (aFilterNumber < 1 || aFilterNumber > 5) {
            // The number is outside the range
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Filter number was not valid");
        }

        // Get the list of exceptions
        List<GetExceptionDTO> dtoList = exceptionService.getExceptionList(aFilterNumber);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dtoList);
    }




    @RequestMapping(value="/api/blow-up", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAnyRole('APP16_SUPERVISOR',  'APP16_ADMIN')")
    public ResponseEntity<?> throwException() {

        int i=5;

        // Throw an exception
        int result = i / 0;

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }
}
