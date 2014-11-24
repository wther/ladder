package hu.bme.aut.ladder.strategy.impl;

import hu.bme.aut.ladder.data.entity.AbilityEntity;
import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;
import hu.bme.aut.ladder.strategy.BoardStrategy;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link BoardStrategy} interface which
 * allows ladders and snakes to take effect on players.
 * 
 * @author Barnabas
 */
@Service
public class SimpleBoardStrategyImpl extends BaseRollingBoardStrategy {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void executePlayerAction(BoardEntity board, PlayerEntity player, String action) throws BoardActionNotPermitted {
    
        // If next player is not set let that be the player
        if(board.getNextPlayer() == null){
            board.setNextPlayer(player);
        }
        
        verifyThatPlayerCanTakeATurn(board, player);
       
        // Only rolling is allowed in the simple version
        if(!"ROLL".equalsIgnoreCase(action)){
            throw new BoardActionNotPermitted("Only ROLL is permitted by " + this.getClass().getSimpleName());
        }
        
        // Move the next player and any number of robot players
        // after him until a human player is reached
        PlayerEntity currentPlayer = player;
        do {
            executeRollForOnePlayer(board, currentPlayer);
            currentPlayer = board.getNextPlayer();            
        } while (board.getNextPlayer().getType() == PlayerEntity.Type.ROBOT  && !currentPlayer.isFinishedPlaying());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbilityEntity> getInitialAbilityKit() {
        return new ArrayList<AbilityEntity>();
    }
}
