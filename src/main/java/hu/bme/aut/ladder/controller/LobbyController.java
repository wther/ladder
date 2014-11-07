package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.controller.dto.GameDTO;
import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.GameService;
import hu.bme.aut.ladder.data.service.UserService;
import hu.bme.aut.ladder.data.service.exception.GameActionNotAllowedException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Main controller for the game
 * 
 * @author Barnabas
 */
@Controller
public class LobbyController extends BaseGameController {
        
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LobbyController.class);
    
    /**
     * Controller URI to list games
     */
    public static final String LIST_GAMES_URI = "/games";
        
    /**
     * URI to create new game
     */
    public static final String CREATE_GAME_URI = "/game/new";
    
    /**
     * URI to join a game
     */
    public static final String JOIN_GAME_URI = "/game/join";
    
    /**
     * Service used
     */
    @Autowired
    private GameService service;
    
    /**
     * User service managing the session
     */
    @Autowired
    private UserService userService;
        
    /**
     * Starts a new game with user as host
     * @param request
     */
    @RequestMapping(value = CREATE_GAME_URI, method = RequestMethod.POST)
    public @ResponseBody GameDTO startGame(HttpServletRequest request) throws GameActionNotAllowedException {
        
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("Starting game with host: {}", user);
        
        // Start new game
        GameEntity game = service.startGame(user);
        return dtoFromGame(game, user);
    }
    
    /**
     * Returns a list of all the started games
     * @param request
     */
    @RequestMapping(value = LIST_GAMES_URI, method = RequestMethod.GET)
    public @ResponseBody List<GameDTO> lobby(HttpServletRequest request) {
        
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("List of games for the lobby requested by {}", user);
        
        // Started means no board yet
        List<GameEntity> games = service.findGamesByState(GameEntity.GameState.STARTED);
                
        List<GameDTO> retVal = new ArrayList<GameDTO>();
        for(GameEntity game : games){
            retVal.add(dtoFromGame(game, user));
        }
        return retVal;        
    }    
    
    /**
     * Returns a list of all the started games
     * @param request
     * @param gameId
     */
    @RequestMapping(value = JOIN_GAME_URI + "/{gameId}", method = RequestMethod.PUT)
    public @ResponseBody GameDTO join(HttpServletRequest request, @PathVariable Long gameId) throws GameActionNotAllowedException {
        
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("{} is joining {}", user, gameId);
        return dtoFromGame(service.join(gameId, user), user);
    }  
    
    /**
     * Error handler which reports the problem to the client
     * @param exception 
     */
    @ExceptionHandler(GameActionNotAllowedException.class)
    public ResponseEntity<String> handleException(GameActionNotAllowedException exception){
        LOGGER.warn("Request failed", exception);
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
