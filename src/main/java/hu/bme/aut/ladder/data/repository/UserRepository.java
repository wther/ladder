package hu.bme.aut.ladder.data.repository;

import hu.bme.aut.ladder.data.entity.UserEntity;
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
    
}
