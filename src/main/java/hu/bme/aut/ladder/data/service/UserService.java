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
    
}
