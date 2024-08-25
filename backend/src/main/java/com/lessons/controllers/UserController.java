package com.lessons.controllers;

import com.lessons.models.authentication.UserInfoDTO;
import com.lessons.services.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller
public class UserController {

    @Resource
    private UserService userService;


    /**
     * REST endpoint /api/user/me
     */
    @RequestMapping(value = "/api/user/me", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAnyRole('APP16F_SUPERVISOR', 'APP16_SPECIALIST', 'APP16_ADMIN', 'APP16_REVIEWER')")
    public ResponseEntity<?> getUserInfo() {

        // Get information from about the logged-in user:  userid, username, full name, ....
        UserInfoDTO userInfoDTO = userService.getUserInfoForFrontend();

        // Return a response of 200 and the UserInfoDTO object
        return ResponseEntity.status(HttpStatus.OK).body(userInfoDTO);
    }


}
