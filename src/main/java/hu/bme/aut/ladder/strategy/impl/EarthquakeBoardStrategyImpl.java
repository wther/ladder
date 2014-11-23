package hu.bme.aut.ladder.strategy.impl;

import hu.bme.aut.ladder.data.entity.AbilityEntity;
import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;
import java.util.Arrays;
import java.util.List;
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
            
            // Make sure that this player has earthquake uses left
            AbilityEntity earthquake = null;
            for(AbilityEntity ability : player.getAbilities()){
                if(ability.getAbility() == AbilityEntity.Ability.EARTHQUAKE && ability.getUsesLeft() > 0){
                    earthquake = ability;
                    break;
                }
            }
            
            if(earthquake == null){
                throw new BoardActionNotPermitted("Player " + player.getName() + " doesn't have any more earthquakes");
            }
            
            // Move each with the same sequence number
            for(PlayerEntity item : board.getPlayers()){
                executeEarthquakeForOnePlayer(board, item, sequenceNumber);
            }
            
            // Reduce the number of earthquakes for this player
            earthquake.setUsesLeft(earthquake.getUsesLeft() - 1);
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
        
        // Is the current row going right
        int newPosition;
        if((currentPosition/10) % 2 == 0){
            newPosition = currentPosition - currentPosition % 10 + 9;
        } else {
            newPosition = currentPosition - currentPosition % 10;
        }        
        
        movePlayerRecursively(board, player, newPosition, sequenceNumber, "EARTHQUAKE");
    }    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbilityEntity> getInitialAbilityKit() {
        AbilityEntity earthquake = new AbilityEntity();
        earthquake.setAbility(AbilityEntity.Ability.EARTHQUAKE);
        earthquake.setUsesLeft(2);
        return Arrays.asList(earthquake);
    }
}
