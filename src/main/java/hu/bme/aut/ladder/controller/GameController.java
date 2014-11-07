package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.controller.dto.GameDTO;
import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.GameService;
import hu.bme.aut.ladder.data.service.UserService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Main controller for the game
 * 
 * @author Barnabas
 */
@Controller
public class GameController {
        
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);
    
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
     * URI to create leave a game
     */
    public static final String LEAVE_GAME_URI = "/game/leave";
    
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
    public @ResponseBody GameDTO startGame(HttpServletRequest request) {
        
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
    public @ResponseBody GameDTO join(HttpServletRequest request, @PathVariable Long gameId) {
        
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("{} is joining {}", user, gameId);
        return dtoFromGame(service.join(gameId, user), user);
    }  
    
    /**
     * Returns a list of all the started games
     * @param request
     * @param gameId
     */
    @RequestMapping(value = LEAVE_GAME_URI + "/{gameId}", method = RequestMethod.DELETE)
    public @ResponseBody GameDTO leave(HttpServletRequest request, @PathVariable Long gameId) {
        
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("{} is leaving {}", user, gameId);
        return dtoFromGame(service.leave(gameId, user), user);
    } 
    
    /**
     * Converts entity to DTO
     */
    private static GameDTO dtoFromGame(GameEntity game, UserEntity user){
        GameDTO dto = new GameDTO();
        dto.setGameId(game.getGameId());
        dto.setCreated(game.getCreated());
        dto.setHost(game.getHost().getName());

        boolean found = false;

        List<String> players = new ArrayList<String>();
        for(UserEntity gameUser : game.getUsers()){
            players.add(gameUser.getName());
            if(gameUser.equals(user)){
                found = true;
            }
        }

        dto.setAllPlayers(players);
        dto.setUserInGame(found);
        return dto;
    }
}
