package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.data.entity.UserEntity;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * Access information on the user
     */
    @RequestMapping("/user/session")
    public @ResponseBody ResponseEntity<String> userSession(HttpServletRequest request) {
        return new ResponseEntity<String>(request.getSession().getId(), HttpStatus.OK);
    }
}
