package hu.bme.aut.ladder.strategy.impl;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.data.entity.StateChangeEntity;
import hu.bme.aut.ladder.data.entity.TunnelEntity;
import hu.bme.aut.ladder.strategy.BoardStrategy;
import hu.bme.aut.ladder.strategy.Dice;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * {@inheritDoc}
     */
    @Override
    public void resolveBoard(BoardEntity board) throws BoardActionNotPermitted {
        
        if(board == null){
            throw new IllegalArgumentException("board is null");
        }
        
        // Move the next player and any number of robot players
        // after him until a human player is reached
        while (board.getNextPlayer().getType() == PlayerEntity.Type.ROBOT  && !board.getNextPlayer().isFinishedPlaying()) {
            executeRollForOnePlayer(board, board.getNextPlayer());
        }
    }
    
    /**
     * Cause string for rolling
     */
    protected static final String ROLL_CAUSE = "ROLL";
    
    /**
     * Cause for rolling the 3rd size
     */
    protected static final String PENALTY_CAUSE = "PENALTY";
    
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
     * @returns The dice rolled, e.g. 5
     */
    protected void executeRollForOnePlayer(BoardEntity board, PlayerEntity player) throws BoardActionNotPermitted {
        
        // Repeat ROLL until player rolls 6, but only two times
        int turn = 0;
        for(; turn < 2; turn++){
        
            final int diceRolled = dice.getNext();
            executeSingleRollForOnePlayer(board, player, player.getPosition() + diceRolled, ROLL_CAUSE);
            
            if(diceRolled != Dice.DICE_LIMIT || player.isFinishedPlaying()){
                break;
            }
        }
        
        // If player has rolled 6 twice, let him roll once again
        if(turn == 2 && !player.isFinishedPlaying()){
            final int diceRolled = dice.getNext();

            // If this is 6, then throw him back to 1
            if(diceRolled == Dice.DICE_LIMIT){
                executeSingleRollForOnePlayer(board, player, 0, PENALTY_CAUSE);
            } else {
                executeSingleRollForOnePlayer(board, player, player.getPosition() + diceRolled, ROLL_CAUSE);
            }
        }
             
        int i = 0;
        for(; i < board.getPlayers().size(); i++){
            if(board.getPlayers().get(i).equals(player)){
                break;
            }
        }
        
        // Set next player to (i+1) mod NumberOfPlayers, but skip as long
        // as the given player has already finished playing!
        for(int j = 1; j < board.getPlayers().size(); j++){
            final PlayerEntity nextPlayer = board.getPlayers().get((i+j) % board.getPlayers().size());
            if(!nextPlayer.isFinishedPlaying()){
                board.setNextPlayer(nextPlayer);
                board.setNextPlayerAssignedAt(new java.util.Date());
                break;
            }
        }
    }
    
    /**
     * Executes an action for a single player, human/robot the same. Doesn't move any other player
     * @returns The dice rolled, e.g. 5
     */
    private void executeSingleRollForOnePlayer(BoardEntity board, PlayerEntity player, int toPosition, String causeName) throws BoardActionNotPermitted {
                
        // Determine new sequence number of action
        int sequenceNumber = getNextAvailableSequenceNumber(board);
        
        movePlayerRecursively(board, player, toPosition, sequenceNumber, causeName);
        
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
        
        // Make sure there are no tunnels starting from the same point,
        // as in this case the second one would have no effect
        Set<Integer> tunnelStarts = new HashSet<Integer>();
        for(TunnelEntity tunnel : board.getTunnels()){
            final Integer from = tunnel.getFromField();
            if(tunnelStarts.contains(from)){
                return false;
            } else {
                tunnelStarts.add(from);
            }
        }
        
        // In order to make the game more FUN, make sure that there are no ladders
        // leading to the last sqrt(size) fields, so nobody can get too lucky
        final int noLadderZone = size - (int)Math.sqrt(size) - 1;
        for(TunnelEntity tunnel : board.getTunnels()){
            if(tunnel.getType() == TunnelEntity.Type.LADDER && tunnel.getToField() > noLadderZone){
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
