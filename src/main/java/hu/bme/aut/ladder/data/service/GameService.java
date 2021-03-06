package hu.bme.aut.ladder.data.service;

import hu.bme.aut.ladder.controller.dto.GameParamsDTO;
import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.GameEntity.GameState;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.exception.GameActionNotAllowedException;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;
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
    void leave(UserEntity user) throws GameActionNotAllowedException;
    
    /**
     * Actually start the game, setup board and let the fun begin
     * @param game 
     */
    void startGame(GameEntity game) throws GameActionNotAllowedException;
    
    /**
     * Set the number of robots for the game
     * 
     * @param game
     * @param params
     */
    void setGameParams(GameEntity game, GameParamsDTO params);
    
    /**
     * Find game by its id 
     * 
     * @param id
     * @return 
     */
    GameEntity findGameById(Long id);
    
    /**
     * Returns all games ordered by the name of the host
     * @param gameState
     * @return 
     */
    List<GameEntity> findActiveGamesByState(GameState gameState);
    
    /**
     * Returns all the users in a given game
     * 
     * @param game
     * @return 
     */
    List<UserEntity> findUsersInGame(GameEntity game);    
    
    /**
     * Execute action 
     * 
     * @param board Board to modify
     * @param player Initiator of the action
     * @param action Action to be taken
     * @throws hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted
     * 
     * @throws hu.bme.aut.ladder.strategy.BoardActionNotPermitted
     */
    void executeAction(GameEntity game, PlayerEntity player, String action) throws BoardActionNotPermitted;
    
    /**
     * Remove not responsive users from the game
     * @param game 
     */
    void handleNotResponsiveUsers(GameEntity game) throws GameActionNotAllowedException;
}
