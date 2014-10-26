package hu.bme.aut.ladder.strategy;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;

/**
 * Interface for the 
 * 
 * @author Barnabas
 */
public interface BoardStrategy {
    
    /**
     * Execute action 
     * 
     * @param board Board to modify
     * @param player Initiator of the action
     * @param action Action to be taken
     * 
     * @throws hu.bme.aut.ladder.strategy.BoardActionNotPermitted
     */
    void executeAction(BoardEntity board, PlayerEntity player, String action) throws BoardActionNotPermitted;
    
}
