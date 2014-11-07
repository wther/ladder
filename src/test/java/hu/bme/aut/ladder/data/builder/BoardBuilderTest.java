package hu.bme.aut.ladder.data.builder;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.TunnelEntity;
import static hu.bme.aut.ladder.data.entity.TunnelEntity.Type.LADDER;
import static hu.bme.aut.ladder.data.entity.TunnelEntity.Type.SNAKE;
import hu.bme.aut.ladder.strategy.BoardStrategy;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link BoardBuilder} class.
 *
 * @author Barnabas
 */
public class BoardBuilderTest {
        
    /**
     *  Test that the number of builder parameters are reflected in the return value
     */
    @Test
    public void thatParameterCountMatches(){
        
        final int size = 50;
        final int snakes = 3;
        final int ladders = 5;
        
        BoardBuilder builder = new BoardBuilder();
        
        
        // Act
        BoardEntity result = builder
                                .withLadders(ladders)
                                .withSnakes(snakes)
                                .withSize(size)
                                .build(mockAlwaysAcceptionStrategy());
        
        // Assert
        assertEquals(size, result.getBoardSize());
        assertEquals(snakes + ladders, result.getTunnels().size());
        assertEquals(0, result.getStateChanges().size());  
        assertEquals(0, result.getPlayers().size());  
    }
    
    /**
     * Test that the generated tunnels are snakes if they lead to a smaller field,
     * and they are ladders if they lead to a higher field
     */
    @Test
    public void thatSnakesAreDescendingAndLaddersAreAscending(){
     
        BoardBuilder builder = new BoardBuilder();
                
        // Act
        BoardEntity entity = builder.build(mockAlwaysAcceptionStrategy());
        
        // Assert
        for(TunnelEntity tunnel : entity.getTunnels()){
         
            if(tunnel.getFromField() < tunnel.getToField()){
                assertEquals(tunnel.getFromField() + "->" + tunnel.getToField() + " has invalid type ", LADDER, tunnel.getType());
            } else {
                assertEquals(tunnel.getFromField() + "->" + tunnel.getToField() + " has invalid type ", SNAKE, tunnel.getType());
            }
        }        
    }
    
    private static BoardStrategy mockAlwaysAcceptionStrategy(){
        BoardStrategy mockStrategy = mock(BoardStrategy.class);
        
        // We don't care whether or not board is solvable
        when(mockStrategy.isBoardAlwaysSolvable(any(BoardEntity.class)))
                .thenReturn(true);
        
        return mockStrategy;
    }
}
