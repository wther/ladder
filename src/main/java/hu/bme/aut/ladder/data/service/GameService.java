package hu.bme.aut.ladder.data.service;

import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.GameEntity.GameState;
import hu.bme.aut.ladder.data.entity.UserEntity;
import java.util.List;

/**
 * Service for creating, destroying game etc.
 * 
 * @author Barnabas
 */
public interface GameService {
    
    /**
     * Starts a game and sets its host
     * @param host 
     * @return  
     */
    GameEntity startGame(UserEntity host);
    
    /**
     * Join user to a game
     * 
     * @param gameId
     * @param user
     * @return 
     */
    GameEntity join(Long gameId, UserEntity user);
    
    /**
     * Leave from a game 
     * 
     * @param gameId
     * @param user
     * @return 
     */
    GameEntity leave(Long gameId, UserEntity user);
    
    /**
     * Find game by its id 
     * 
     * @param id
     * @return 
     */
    GameEntity findGameById(Long id);
    
    /**
     * Returns all games ordered by the name of the host
     * @return 
     */
    List<GameEntity> findGames();
    
    /**
     * Returns all games ordered by the name of the host
     * @param gameState
     * @return 
     */
    List<GameEntity> findGamesByState(GameState gameState);
    
}
