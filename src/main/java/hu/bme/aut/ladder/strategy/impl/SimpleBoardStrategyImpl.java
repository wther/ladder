package hu.bme.aut.ladder.strategy.impl;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.data.entity.StateChangeEntity;
import hu.bme.aut.ladder.data.entity.TunnelEntity;
import hu.bme.aut.ladder.strategy.BoardActionNotPermitted;
import hu.bme.aut.ladder.strategy.BoardStrategy;
import hu.bme.aut.ladder.strategy.Dice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link BoardStrategy} interface which
 * allows ladders and snakes to take effect on players.
 * 
 * @author Barnabas
 */
@Service
public class SimpleBoardStrategyImpl implements BoardStrategy {
    
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleBoardStrategyImpl.class);
    
    /**
     * Dice used to determine random outcomes
     */
    private Dice dice = new SimpleDiceImpl();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void executeAction(BoardEntity board, PlayerEntity player, String action) throws BoardActionNotPermitted {
        
        // Only rolling is allowed in the simple version
        if(!"ROLL".equalsIgnoreCase(action)){
            throw new BoardActionNotPermitted("Only ROLL is permitted by " + this.getClass().getSimpleName());
        }
        
        // Determine new sequence number of action
        int sequenceNumber = 0;
        if(board.getStateChanges().size() > 0){
            sequenceNumber = board.getStateChanges().get(board.getStateChanges().size() - 1).getSequenceNumber();
        }
        
        int toPosition = player.getPosition() + dice.getNext();
        movePlayerRecursively(board, player, toPosition, sequenceNumber);
    }
    
    /**
     * Moves player recursively
     * 
     * @param board
     * @param player
     * @param toPosition
     * @param sequenceNumber 
     */
    private void movePlayerRecursively(BoardEntity board, PlayerEntity player, int toPosition, int sequenceNumber){
                
        // Move player
        StateChangeEntity change = new StateChangeEntity();
        change.setSequenceNumber(sequenceNumber + 1);
        
        change.setPlayer(player);
        change.setBeforeAt(player.getPosition());
        change.setAfterAt(Math.min(board.getBoardSize() - 1, toPosition));
        
        // Update board
        player.setPosition(change.getAfterAt());
        board.getStateChanges().add(change);
        
        LOGGER.debug("Moved player {} for board {} from {} to {}", player.getPlayerId(), board.getBoardId(), change.getBeforeAt(), change.getAfterAt());
        
        // At the opening of a tunnel?
        TunnelEntity tunnel = null;
        for(TunnelEntity item : board.getTunnels()){
            if(item.getFromField() == player.getPosition()){
                tunnel = item;
                break;
            }
        }
        
        // Moves player to the next station
        if(tunnel != null){
            movePlayerRecursively(board, player, tunnel.getToField(), sequenceNumber + 1);
        }
    }
    
    
    /**
     * Friend classes may manipulate the dice
     * 
     * @param dice 
     */
    protected void setDice(Dice dice) {
        this.dice = dice;
    }
    
    /**
     * Coordinates
     */
    private static class Coordinate {
        int x,y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
