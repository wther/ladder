package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.controller.dto.GameDTO;
import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.GameService;
import hu.bme.aut.ladder.data.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for controllers managing {@link GameEntity} instances.
 * 
 * @author Barnabas
 */
public abstract class BaseGameController {
    
    /**
     * Service used
     */
    @Autowired
    protected GameService service;
    
    /**
     * User service managing the session
     */
    @Autowired
    protected UserService userService;
    
    /**
     * Converts entity to DTO
     */
    protected GameDTO dtoFromGame(GameEntity game, UserEntity user){
        GameDTO dto = new GameDTO();
        dto.setGameId(game.getGameId());
        dto.setCreated(game.getCreated());
        dto.setHost(game.getHost().getName());
        dto.setUser(user.getName());

        boolean found = false;

        List<String> players = new ArrayList<String>();
        for(UserEntity gameUser : service.findUsersInGame(game)){
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
