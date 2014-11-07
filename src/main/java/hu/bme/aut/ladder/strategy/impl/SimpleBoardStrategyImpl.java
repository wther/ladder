package hu.bme.aut.ladder.strategy.impl;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.data.entity.StateChangeEntity;
import hu.bme.aut.ladder.data.entity.TunnelEntity;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;
import hu.bme.aut.ladder.strategy.BoardStrategy;
import hu.bme.aut.ladder.strategy.Dice;
import java.util.List;
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
        TunnelEntity tunnel = getTunnelFrom(board.getTunnels(), player.getPosition());
        
        // Moves player to the next station
        if(tunnel != null){
            movePlayerRecursively(board, player, tunnel.getToField(), sequenceNumber + 1);
        }
    }
    
    /**
     * Finds a tunnel which starts from a certain point 
     * 
     * @param tunnels All tunnels to search in 
     * @param field Position of the start
     * @return <i>null</i> of none found
     */
    private TunnelEntity getTunnelFrom(List<TunnelEntity> tunnels, int field){
        for(TunnelEntity item : tunnels){
            if(item.getFromField() == field){
                return item;
            }
        }
        return null;
    }
    

    /**
     * {@inheritDoc} 
     * 
     * @param board
     * @return 
     */
    @Override
    public boolean isBoardAlwaysSolvable(BoardEntity board) {
        final int size = board.getBoardSize();
        
        // Build a graph where the vertices are the fields
        // and the edges indicate that a certain field can
        // be reached from the other
        boolean[][] edge= new boolean[size][size];
        
        // Assume that none exist
        for(int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                edge[i][j] = false;
            }
        }
        
        // From any field if there is no ladder the next 1..DICE_LIMIT is reachable
        for(int i = 0; i < size; i++){
            TunnelEntity tunnel = getTunnelFrom(board.getTunnels(), i);
            
            // If there is a ladder from here
            if(tunnel != null){
                edge[i][tunnel.getToField()] = true;
            } else {
                 // Otherwise we could move from here with the dice
                 for (int j = i+1; j < Math.min(i + Dice.DICE_LIMIT, size); j++){
                     edge[i][j] = true;
                 }
            }
        }
        
        // Determine that the last cell is reachable from any
        for(int i = 0; i < size-1; i++){

            // Prepare visited array
            boolean[] visited = new boolean[size];
            for(int j = 0; j < size; j++){
                visited[i] = false;
            }
            
            runDFS(i, edge, visited);
            if(!visited[size-1]){
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Helper function which determines the reachable items for a given point
     * 
     * @param edge
     * @param visited
     * @return 
     */
    private static void runDFS(int from, boolean[][] edge, boolean[] visited){
        
        LOGGER.trace("Running DFS from {}", from);
        
        visited[from] = true;
        for(int i = 0; i < edge[from].length; i++){
            // If there is an edge from -> i
            if(i != from && edge[from][i]){
                // If this is visited that there's a loop
                if(visited[i]){
                    return;
                } else {
                    runDFS(i, edge, visited);
                }
            }
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
}
