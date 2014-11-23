package hu.bme.aut.ladder.strategy.impl;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.data.entity.StateChangeEntity;
import hu.bme.aut.ladder.data.entity.TunnelEntity;
import hu.bme.aut.ladder.strategy.BoardStrategy;
import hu.bme.aut.ladder.strategy.Dice;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;
import java.util.List;

/**
 * Base class for board strategies where players take a turn after
 * each other by rolling their dice.
 * 
 * @author Barnabas
 */
public abstract class BaseRollingBoardStrategy implements BoardStrategy {
    
     /**
     * Dice used to determine random outcomes
     */
    protected Dice dice = new SimpleDiceImpl();
    
    /**
     * Helper function to be ran from {@link BoardStrategy#executeAction} implementations
     */
    protected void verifyThatPlayerCanTakeATurn(BoardEntity board, PlayerEntity player) throws BoardActionNotPermitted {
        if(player.getType() != PlayerEntity.Type.HUMAN){
            throw new IllegalArgumentException("Only human players can take actions");
        }
            
        // Otherwise verify that it is indeed player's turn
        // @TODO why only ID comparision works?
        if(!player.getPlayerId().equals(board.getNextPlayer().getPlayerId())){
            throw new BoardActionNotPermitted(
                    "Expected " + board.getNextPlayer().toString()+ 
                    " to take a turn but was attempted by " + player.toString());
        }
        
        // If player has already finished playing then this is not allowed
        if(player.isFinishedPlaying()){
            throw new BoardActionNotPermitted("Player has already finished playing: " + player.getName());
        }
    }
    
    /**
     * Returns the next unused sequence number
     */
    protected int getNextAvailableSequenceNumber(BoardEntity board){
        // Determine new sequence number of action
        int sequenceNumber = 0;
        if(board.getStateChanges().size() > 0){
            sequenceNumber = board.getStateChanges().get(board.getStateChanges().size() - 1).getSequenceNumber();
        }
        
        return sequenceNumber;
    }
    
    /**
     * Executes an action for a single player, human/robot the same. Doesn't move any other player
     */
    protected void executeRollForOnePlayer(BoardEntity board, PlayerEntity player) throws BoardActionNotPermitted {
                
        // Determine new sequence number of action
        int sequenceNumber = getNextAvailableSequenceNumber(board);
        
        int toPosition = player.getPosition() + dice.getNext();
        movePlayerRecursively(board, player, toPosition, sequenceNumber, "ROLL");
                
        int i = 0;
        for(; i < board.getPlayers().size(); i++){
            if(board.getPlayers().get(i).equals(player)){
                break;
            }
        }
        
        // Did this player finish?
        if(player.getPosition() >= board.getBoardSize()-1){
            player.setFinishedPlaying(true);
            
            // How many others have finished already (including me!)
            int count = 0;
            for(PlayerEntity playersInGame : board.getPlayers()){
                if(playersInGame.isFinishedPlaying()){
                    ++count;
                }
            }
            
            player.setFinishedAtPlace(count);            
        }
        
        // Set next player to (i+1) mod NumberOfPlayers, but skip as long
        // as the given player has already finished playing!
        for(int j = 1; j < board.getPlayers().size(); j++){
            final PlayerEntity nextPlayer = board.getPlayers().get((i+j) % board.getPlayers().size());
            if(!nextPlayer.isFinishedPlaying()){
                board.setNextPlayer(nextPlayer);
                break;
            }
        }
    }
    
    /**
     * Moves player recursively
     * 
     * @param board
     * @param player
     * @param toPosition
     * @param sequenceNumber 
     */
    protected void movePlayerRecursively(BoardEntity board, PlayerEntity player, int toPosition, int sequenceNumber, String causedBy){
                
        // Move player
        StateChangeEntity change = new StateChangeEntity();
        change.setSequenceNumber(sequenceNumber + 1);
        
        change.setPlayer(player);
        change.setBeforeAt(player.getPosition());
        change.setAfterAt(Math.min(board.getBoardSize() - 1, toPosition));
        change.setCausedBy(causedBy);
        
        // Update board
        player.setPosition(change.getAfterAt());
        board.getStateChanges().add(change);
        
        // At the opening of a tunnel?
        TunnelEntity tunnel = getTunnelFrom(board.getTunnels(), player.getPosition());
        
        // Moves player to the next station
        if(tunnel != null){
            movePlayerRecursively(board, player, tunnel.getToField(), sequenceNumber + 1, tunnel.getType().name());
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
        
        // If there is a snake at the end or a ladder at the beginning the board is not solvable
        for(TunnelEntity tunnel : board.getTunnels()){
            if(tunnel.getType() == TunnelEntity.Type.SNAKE && tunnel.getFromField() == size-1){
                return false;
            }
            if(tunnel.getType() == TunnelEntity.Type.LADDER && tunnel.getFromField() == 0){
                return false;
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
