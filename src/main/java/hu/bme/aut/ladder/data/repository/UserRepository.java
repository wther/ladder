package hu.bme.aut.ladder.data.repository;

import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link UserEntity} entities
 * 
 * @author Barnabas
 */
public interface UserRepository extends JpaRepository<UserEntity, Long>{
    
    /**
     * Find user associated with session id 
     * @param sessionId
     * @return 
     */
    UserEntity findBySessionId(String sessionId);
    
    /**
     * Find all users in a game
     * @param game
     * @return 
     */
    List<UserEntity> findByGame(GameEntity game);
    
    /**
     * Find user by its unique name
     * 
     * @param name
     * @return 
     */
    UserEntity findByNameIgnoreCase(String name);
}
