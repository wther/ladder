package hu.bme.aut.ladder.data.service;

import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.GameEntity.GameState;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.exception.GameActionNotAllowedException;
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
    GameEntity intializeGame(UserEntity host) throws GameActionNotAllowedException;
    
    /**
     * Join user to a game
     * 
     * @param gameId
     * @param user
     * @return 
     */
    GameEntity join(Long gameId, UserEntity user) throws GameActionNotAllowedException;
    
    /**
     * Leave from a game 
     * 
     * @param user
     */
    void leave(UserEntity user);
    
    /**
     * Actually start the game, setup board and let the fun begin
     * @param game 
     */
    void startGame(GameEntity game) throws GameActionNotAllowedException;
    
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
    
    /**
     * Returns all the users in a given game
     * 
     * @param game
     * @return 
     */
    List<UserEntity> findUsersInGame(GameEntity game);    
}
