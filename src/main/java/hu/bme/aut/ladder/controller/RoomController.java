package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.controller.dto.GameDTO;
import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.exception.GameActionNotAllowedException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for managing a game from within the room
 * 
 * @author Barnabas
 */
@Controller
public class RoomController extends BaseGameController {
    
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomController.class);
    
    /**
     * Controller URI to show details about a room
     */
    public static final String GAME_DETAILS = "/game/details";
    
    /**
     * URI to create leave a game
     */
    public static final String LEAVE_GAME_URI = "/game/leave";
        
    /**
     * URI to create leave a game
     */
    public static final String GAME_START_URI = "/game/start";
    
    /**
     * Returns a list of all the started games
     * @param request
     */
    @RequestMapping(value = GAME_DETAILS, method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<GameDTO> room(HttpServletRequest request) {
        
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("Room details requested by {}", user);
        
        GameEntity game = service.findGameById(user.getGame().getGameId());
        if(game == null){
            return new ResponseEntity<GameDTO>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<GameDTO>(dtoFromGame(game, user), HttpStatus.OK);
        }
    }    
    
    /**
     * Returns a list of all the started games
     * @param request
     */
    @RequestMapping(value = LEAVE_GAME_URI, method = RequestMethod.DELETE)
    public void leave(HttpServletRequest request) {
        
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("{} is leaving {}", user, user.getGame());
        service.leave(user);
    } 
    
    
    /**
     * Returns a list of all the started games
     * @param request
     */
    @RequestMapping(value = GAME_START_URI, method = RequestMethod.POST)
    public ResponseEntity<String> start(HttpServletRequest request) throws GameActionNotAllowedException {
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("{} is starting the game {}", user, user.getGame());
        
        if(user.getGame() == null){
            LOGGER.warn("No game associated with {}", user.getName());
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
        
        // If user is not host
        if(!user.getGame().getHost().equals(user)){
            LOGGER.warn("{} is not the host of the game, {} is ", user.getName(), user.getGame().getHost().getName());
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
        
        // Do start the game
        service.startGame(user.getGame());
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    
    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
