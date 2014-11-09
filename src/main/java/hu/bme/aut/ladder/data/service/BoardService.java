package hu.bme.aut.ladder.data.service;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;

import java.util.List;

/**
 * Service for accessing boards.
 * 
 * @author Barnabas
 */
public interface BoardService {

    /**
     * Returns all boards current available.
     * 
     * @return
     */
    List<BoardEntity> getAllBoard();
    
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
    void executeAction(BoardEntity board, PlayerEntity player, String action) throws BoardActionNotPermitted;

}
