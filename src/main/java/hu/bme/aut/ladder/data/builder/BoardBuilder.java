package hu.bme.aut.ladder.data.builder;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.data.entity.StateChangeEntity;
import hu.bme.aut.ladder.data.entity.TunnelEntity;
import static hu.bme.aut.ladder.data.entity.TunnelEntity.Type.LADDER;
import static hu.bme.aut.ladder.data.entity.TunnelEntity.Type.SNAKE;
import hu.bme.aut.ladder.strategy.BoardStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Builder for creating {@link BoardEntity} instances.
 * 
 * @author Barnabas
 */
public final class BoardBuilder {
    
    /**
     * Board size
     */
    private int size = 100;
    
    /**
     * Number of snakes on the board
     */
    private int snakeCount = 5;
    
    /**
     * Number of ladders on the board
     */
    private int ladderCount = 5;
    
    
    /**
     * Build board entity
     * 
     * @return 
     */
    public BoardEntity build(BoardStrategy verifier){
        
        BoardEntity board = new BoardEntity();
        
        board.setBoardSize(size);
        board.setStateChanges(new ArrayList<StateChangeEntity>());
        
        // Prepare snakes and ladders
        for(int i = 0; i < 200; i++){
        
            List<TunnelEntity> tunnels = generateTunnels(snakeCount, SNAKE);
            tunnels.addAll(generateTunnels(ladderCount, LADDER));

            board.setTunnels(tunnels);
            
            if(verifier.isBoardAlwaysSolvable(board)){
                break;
            }
        }
         
        board.setPlayers(new ArrayList<PlayerEntity>());
        
        return board;
    }
    
    private List<TunnelEntity> generateTunnels(int count , TunnelEntity.Type type){
        Random random = new Random();
        
        // Snakes go down, ladders go up
        final boolean descending = type == SNAKE ? true : false;
        
        // For each snake
        List<TunnelEntity> retVal = new ArrayList<TunnelEntity>();
        for(int i = 0; i < count; i++){
            int from = 0, to = 0;
            
            for(int j = 0; j < 200; j++){
                from = random.nextInt(size);
                to = random.nextInt(size);
                
                // Only pass of tunnel is not too big,
                // but big enough
                if(Math.abs(to - from) < 40 && Math.abs(to-from) > 15){
                    break;
                }
            }
            
            // They can't be equal
            if(to == from){
                from = to+1;
            }
            
            // Switch them if not in order
            if((from < to && descending) || (from > to && !descending)){
                int temp = from;
                from = to;
                to = temp;
            }
            
            TunnelEntity tunnel = new TunnelEntity();
            tunnel.setType(type);
            tunnel.setFromField(from);
            tunnel.setToField(to);
            retVal.add(tunnel);
        }        
        return retVal;
    }
    
    /**
     * Set size of the board
     * @param size 
     * @return
     */
    public BoardBuilder withSize(int size){
        if(size <= 0){
            throw new IllegalArgumentException("Size expected to be a positive intenger, " + size + " isn't");
        }
        this.size = size;
        return this;
    }
    
    /**
     * Set number of snakes on the board
     * 
     * @param snakeCount
     * @return 
     */
    public BoardBuilder withSnakes(int snakeCount){
        if(snakeCount < 0){
            throw new IllegalArgumentException("Expected snakeCount to be at least 0, but " + snakeCount + " isn't");
        }
        this.snakeCount = snakeCount;
        return this;
    }
    
    /**
     * Set number of ladders on the board
     * 
     * @param ladderCount
     * @return 
     */
    public BoardBuilder withLadders(int ladderCount){
        if(ladderCount < 0){
            throw new IllegalArgumentException("Expected ladderCount to be at least 0, but " + snakeCount + " isn't");
        }
        this.ladderCount = ladderCount;
        return this;
    }    
}
