package com.lessons.utilities;

import com.lessons.services.ExceptionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;

@ControllerAdvice
public class MyExceptionHandler
{
    private static final Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);

    @Resource
    private ExceptionService exceptionService;

    @Value("${exception_handler.return_dev_info:false}")
    private boolean showDevelopmentInfo;


    /**
     * This is the Backend Exception Handler.  Any exceptions from a REST call will hit this method
     * -- If running in local-dev mode, then return the real exception back to the frontend  (so the developer can see it)
     * -- If running in production mode, then return a generic exception message (that includes the generated exception id)
     *
     * @param aException holds the exception that we want to log
     * @return ResponseEntity object
     */
    @ExceptionHandler( Exception.class )
    public ResponseEntity<?> handleException(Exception aException)
    {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        // Log the error *and* stack trace
        if (request != null) {
            logger.error("Exception raised from call to " + request.getRequestURI(), aException);
        } else {
            logger.error("Exception raised from null request.", aException);
        }

        // Return a ResponseEntity with media type as text_plain so that the spring does not convert error message into a JSON map
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        // Get a unique exceptionId to be passed to the user and create an entry in the exception database table
        Integer exceptionId = exceptionService.saveExceptionReturnId(aException, request.getRequestURI());

        // If the resource is not found (404), make sure we're showing a 404 to the user and not a 500
        if (aException instanceof NoResourceFoundException) {
            String mesg = "The requested resource was not found";
            return new ResponseEntity<>(mesg, headers, HttpStatus.NOT_FOUND);
        }

        if (showDevelopmentInfo) {
            // I am in developer mode so send the *real* error message to the front-end

            // Construct the message (to be returned to the frontend)
            String mesg = aException.getLocalizedMessage();

            // Return the message
            return new ResponseEntity<>(mesg, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else {
            // I am in production mode so send a *generic* error message to the front-end
            return new ResponseEntity<>(("An unexpected error occurred. Refer to error code " + exceptionId), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}