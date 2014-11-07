package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.controller.dto.GameDTO;
import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
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
     * @param gameId
     */
    @RequestMapping(value = LEAVE_GAME_URI, method = RequestMethod.DELETE)
    public @ResponseBody void leave(HttpServletRequest request) {
        
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("{} is leaving {}", user, user.getGame());
        service.leave(user);
    } 
}
