package hu.bme.aut.ladder.strategy.impl;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;
import org.springframework.stereotype.Service;

/**
 * Board strategy implementation which accepts action <code>EARTHQUAKE</code>,
 * and moves all players to the right.
 * 
 * @author Barnabas
 */
@Service
public class EarthquakeBoardStrategyImpl extends BaseRollingBoardStrategy {

    /**
     * {@inheritDoc 
     */
    @Override
    public void executeAction(BoardEntity board, PlayerEntity player, String action) throws BoardActionNotPermitted {
        // If next player is not set let that be the player
        if(board.getNextPlayer() == null){
            board.setNextPlayer(player);
        }
        
        verifyThatPlayerCanTakeATurn(board, player);
       
        // Rolling
        if("ROLL".equalsIgnoreCase(action)){
            
            // Move the next player and any number of robot players
            // after him until a human player is reached
            PlayerEntity currentPlayer = player;
            do {
                executeRollForOnePlayer(board, currentPlayer);
                currentPlayer = board.getNextPlayer();            
            } while (board.getNextPlayer().getType() == PlayerEntity.Type.ROBOT && !currentPlayer.isFinishedPlaying());
            
        // Creating earthquake
        } else if ("EARTHQUAKE".equalsIgnoreCase(action)){
            
            final int sequenceNumber = getNextAvailableSequenceNumber(board);
            
            // Move each with the same sequence number
            for(PlayerEntity item : board.getPlayers()){
                if(!item.isFinishedPlaying()){
                    executeEarthquakeForOnePlayer(board, item, sequenceNumber);
                }
            }
        } else {
            throw new BoardActionNotPermitted("Only ROLL and EARTHQUAKE is permitted by " + this.getClass().getSimpleName());
        }
    }
    
    /**
     * Executes earthquake to one player
     * @param board
     * @param player 
     */
    private void executeEarthquakeForOnePlayer(BoardEntity board, PlayerEntity player, int sequenceNumber){
     
        final int currentPosition = player.getPosition();
        
        final int width = (int)Math.floor(Math.sqrt(board.getBoardSize()));
        if(board.getBoardSize() != width*width){
            throw new IllegalArgumentException("Earthquake is only possible for squared boards, boardSize " + board.getBoardSize() + " isn't");
        }
        
        // Is the current row going right
        int newPosition;
        if((currentPosition/width) % 2 == 0){
            newPosition = currentPosition - currentPosition % width + width -1;
        } else {
            newPosition = currentPosition - currentPosition % width;
        }        
        
        movePlayerRecursively(board, player, newPosition, sequenceNumber, "EARTHQUAKE");
    }    
}
