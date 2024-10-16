package com.lessons.controllers;
import com.lessons.models.preferences.GetOnePreferenceDTO;
import com.lessons.models.preferences.SetPreferenceDTO;
import com.lessons.models.preferences.SetPreferenceWithPageDTO;
import com.lessons.services.PreferenceService;
import com.lessons.services.UserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class PreferenceController {
    private static final Logger logger = LoggerFactory.getLogger(PreferenceController.class);

    @Resource
    private PreferenceService preferenceService;

    @Resource
    private UserService userService;


    /**
     * GET /api/preferences/get/{preferenceName}/{pageName}  REST call
     * Returns the single preference value
     */
    @RequestMapping(value = "/api/preferences/get/{preferenceName}/{pageName}", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAnyRole('APP16_SUPERVISOR', 'APP16_SPECIALIST', 'APP16_ADMIN', 'APP16_REVIEWER')")
    public ResponseEntity<?> getPreferenceForPage(@PathVariable(value="preferenceName") String aPreferenceName,
                                                  @PathVariable(value="pageName") String aPageName) {

        int loggedInUserId = this.userService.getLoggedInUserId();

        // Get the preference value
        GetOnePreferenceDTO getOnePreferenceDTO = this.preferenceService.getOnePreferenceWithPage(loggedInUserId, aPreferenceName, aPageName);

        // Return the GridPreferenceDTO back to the front-end and a 200 status code
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getOnePreferenceDTO);
    }



    /**
     * GET /api/preferences/get/{preferenceName}}  REST call
     * Returns the single preference value
     */
    @RequestMapping(value = "/api/preferences/get/{preferenceName}", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAnyRole('APP16_SUPERVISOR', 'APP16_SPECIALIST', 'APP16_ADMIN', 'APP16_REVIEWER')")
    public ResponseEntity<?> getPreference(@PathVariable(value="preferenceName") String aPreferenceName) {

        int loggedInUserId = this.userService.getLoggedInUserId();

        // Get the preference value
        // NOTE:  Pass-in null for the page value
        GetOnePreferenceDTO getOnePreferenceDTO = this.preferenceService.getOnePreferenceWithoutPage(loggedInUserId, aPreferenceName);

        // Return the GridPreferenceDTO back to the front-end and a 200 status code
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getOnePreferenceDTO);
    }


    /**
     * POST /api/preferences/page/set REST call
     * This REST call sets the theme name preference only
     * Returns 200 status code if it works
     */
    @RequestMapping(value = "/api/preferences/page/set", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasAnyRole('APP16_SUPERVISOR', 'APP16_SPECIALIST', 'APP16_ADMIN', 'APP16_REVIEWER')")
    public ResponseEntity<?> setPreferenceValueWithPage(@RequestBody SetPreferenceWithPageDTO aSetPreferenceDTO) {

        if (StringUtils.isBlank(aSetPreferenceDTO.getName() )) {
            // The passed-in name is invalid
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The passed-in name is blank.  This must be set.");
        }
        else if (StringUtils.isBlank(aSetPreferenceDTO.getPage() )) {
            // The passed-in name is page
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The passed-in page is blank.  This must be set.");
        }

        int loggedInUserId = this.userService.getLoggedInUserId();

        // Set the preference in the system
        this.preferenceService.setPreferenceValueWithPage(loggedInUserId, aSetPreferenceDTO.getName(), aSetPreferenceDTO.getValue(), aSetPreferenceDTO.getPage()  );

        // Return a 200 status code and a null object
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }



    /**
     * POST /api/preferences/set REST call
     * This REST call sets the theme name preference only
     * Returns 200 status code if it works
     */
    @RequestMapping(value = "/api/preferences/set", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasAnyRole('APP16_SUPERVISOR', 'APP16_SPECIALIST', 'APP16_ADMIN', 'APP16_REVIEWER')")
    public ResponseEntity<?> setPreferenceValueWithoutPage(@RequestBody SetPreferenceDTO aSetPreferenceDTO) {

        if (StringUtils.isBlank(aSetPreferenceDTO.getName() )) {
            // The passed-in name is invalid
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The passed-in name is blank.  This must be set.");
        }

        int loggedInUserId = this.userService.getLoggedInUserId();

        // Set the preference in the system
        this.preferenceService.setPreferenceValueWithoutPage(loggedInUserId, aSetPreferenceDTO.getName(), aSetPreferenceDTO.getValue() );

        // Return a 200 status code and a null object
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }
}

