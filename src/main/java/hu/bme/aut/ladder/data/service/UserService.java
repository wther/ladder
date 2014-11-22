package hu.bme.aut.ladder.data.service;

import hu.bme.aut.ladder.data.entity.UserEntity;
import java.util.List;

/**
 * Service for managing {@link UserEntity} entities.
 * 
 * @author Barnabas
 */
public interface UserService {
    
    /**
     * Page of the lobby
     */
    String LOBBY_PAGE = "lobby";
    
    /**
     * Page of the room
     */
    String ROOM_PAGE = "room";
    
    /**
     * Page where the actual game is going on
     */
    String GAME_PAGE = "game";
    
    /**
     * Finds user by session id or creates new one
     * 
     * @param sessionId
     * @return 
     */
    UserEntity findOrCreateUser(String sessionId);
    
    /**
     * Returns all users
     * @return 
     */
    List<UserEntity> findAll();
    
    /**
     * Set the name for a user
     */
    void setNameForUser(UserEntity user, String newName);
    
    /**
     * Set the value indicating that user is ready or not for a game to start
     */
    void setUserReady(UserEntity user, boolean isReady);
    
    /**
     * Get page the user is supposed to be on
     * @return 
     */
    String getUserPage(UserEntity user);
    
}
