package hu.bme.aut.ladder.data.repository;

import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for games
 * 
 * @author Barnabas
 */
public interface GameRepository extends JpaRepository<GameEntity, Long>{
    
    @Query("SELECT g FROM GameEntity g ORDER BY g.host.name")
    List<GameEntity> findAllOrderedByHostName();
    
    @Query("SELECT g FROM GameEntity g WHERE g.gameState = :gameState ORDER BY g.host.name")
    List<GameEntity> findByGameStateOrderedByHostName(@Param(value = "gameState") GameEntity.GameState gameState);
}
