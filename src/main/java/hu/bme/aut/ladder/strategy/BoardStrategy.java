package hu.bme.aut.ladder.strategy;

import hu.bme.aut.ladder.data.entity.AbilityEntity;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;
import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import java.util.List;

/**
 * Interface for the 
 * 
 * @author Barnabas
 */
public interface BoardStrategy {
    
    /**
     * Generate robot moves until the next player becomes a player
     */
    void resolveBoard(BoardEntity board) throws BoardActionNotPermitted;
    
    /**
     * Execute action 
     * 
     * @param board Board to modify
     * @param player Initiator of the action
     * @param action Action to be taken
     * 
     * @throws hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted
     */
    void executePlayerAction(BoardEntity board, PlayerEntity player, String action) throws BoardActionNotPermitted;
    
    /**
     * Determine whether or a not a given board is always solvable using this strategy
     * 
     * @param board
     * @return 
     */
    boolean isBoardAlwaysSolvable(BoardEntity board);
    
    /**
     * Returns the original ability kit available for players in the game,
     * e.g. 2 EARTHQUAKE
     * 
     * @return 
     */
    List<AbilityEntity> getInitialAbilityKit();
    
}
