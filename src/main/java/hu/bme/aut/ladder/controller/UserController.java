package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.controller.dto.UserDTO;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.UserService;
import hu.bme.aut.ladder.data.service.exception.UserActionNotAllowedException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User controller can be used the access and manipulate
 * the {@link UserEntity} associated with the session
 * 
 * @author Barnabas
 */

@Controller
public class UserController {
    
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    
    /**
     * URI for accessing the user's session id, mainly for debugging purposes
     */
    public static final String SESSION_URL = "/user/session";
    
    /**
     * URI to access details on the user, like its name, the game he's involved in, etc.
     */
    public static final String DETAIL_URL = "/user/details";
    
    /**
     * POST to this URI will override the user's name
     */
    public static final String UPDATE_NAME = "/user/name";
    
    /**
     * POST to this URI will override the user's ready value
     */
    public static final String SET_READY = "/user/ready";
    
    /**
     * User service managing the session
     */
    @Autowired
    protected UserService userService;
    
    /**
     * Access information on the user
     */
    @RequestMapping(SESSION_URL)
    public @ResponseBody ResponseEntity<String> userSession(HttpServletRequest request) {
        return new ResponseEntity<String>(request.getSession().getId(), HttpStatus.OK);
    }
    
    /**
     * Access information on the user
     */
    @RequestMapping(DETAIL_URL)
    public @ResponseBody UserDTO details(HttpServletRequest request) {
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("{} is accessing user details", user);
        UserDTO retVal = new UserDTO();
        retVal.setName(user.getName());
        retVal.setReady(Boolean.TRUE.equals(user.getReady()));
        retVal.setUserId(user.getUserId());
        return retVal;
    }
    
    /**
     * Set user's name
     */
    @RequestMapping(value = UPDATE_NAME, method = RequestMethod.POST)
    public ResponseEntity<String> updateName(HttpServletRequest request, @RequestParam String name) throws UserActionNotAllowedException {
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("{} is attempting to change name value to {}", user, name);
        userService.setNameForUser(user, name);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    /**
     * Set user's name
     */
    @RequestMapping(value = SET_READY, method = RequestMethod.POST)
    public ResponseEntity<String> setReady(HttpServletRequest request, @RequestParam boolean ready) {
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("{} is attempting to change ready value to {}", user, ready);
        userService.setUserReady(user, ready);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * Error handler which reports the problem to the client
     * @param exception 
     */
    @ExceptionHandler(UserActionNotAllowedException.class)
    public ResponseEntity<String> handleException(UserActionNotAllowedException exception){
        LOGGER.warn("Request failed", exception);
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
