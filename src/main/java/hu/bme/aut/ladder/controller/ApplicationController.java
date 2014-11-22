package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.UserService;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for the <i>index.html</i> path
 * 
 * @author Barnabas
 */
@Controller
public class ApplicationController {
    
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);
    
    /**
     * The index page can be accessed at the root address and at this address
     */
    public static final String INDEX_PAGE_ALIAS = "index.html";
    
    /**
     * User service managing the session
     */
    @Autowired
    protected UserService userService;
    
    /**
     * Index page
     */
    @RequestMapping(value = {"/", INDEX_PAGE_ALIAS}, method = RequestMethod.GET)
    public String index(HttpServletRequest request){
       return redirectOrServeView(request, INDEX_PAGE_ALIAS);
    }
    
    @RequestMapping(value = {UserService.LOBBY_PAGE + ".html"}, method = RequestMethod.GET)
    public String lobby(HttpServletRequest request){
        return redirectOrServeView(request, UserService.LOBBY_PAGE);
    }
    
    @RequestMapping(value = {UserService.ROOM_PAGE + ".html"}, method = RequestMethod.GET)
    public String room(HttpServletRequest request){
        return redirectOrServeView(request, UserService.ROOM_PAGE);
    }
    
    @RequestMapping(value = {UserService.GAME_PAGE + ".html"}, method = RequestMethod.GET)
    public String game(HttpServletRequest request){
        return redirectOrServeView(request, UserService.GAME_PAGE);
    }
    
    private String redirectOrServeView(HttpServletRequest request, String accessedPage){
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        final String page = userService.getUserPage(user);
        
        
        if(page.equals(accessedPage)){
            LOGGER.info("Accessing index by {} and showing {}", user.getName(), page);
            return page;
        } else {
            LOGGER.info("Accessing index by {} and redirecting to {}", user.getName(), page);            
            return "redirect:" + page + ".html";
        }
    }
}
