package hu.bme.aut.ladder.data.builder;

import hu.bme.aut.ladder.data.entity.BoardEntity;
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
        
        BoardStrategy mockStrategy = mock(BoardStrategy.class);
        
        // We don't care whether or not board is solvable
        when(mockStrategy.isBoardAlwaysSolvable(any(BoardEntity.class)))
                .thenReturn(true);
        
        // Act
        BoardEntity result = builder
                                .withLadders(ladders)
                                .withSnakes(snakes)
                                .withSize(size)
                                .build(mockStrategy);
        
        // Assert
        assertEquals(size, result.getBoardSize());
        assertEquals(snakes + ladders, result.getTunnels().size());
        assertEquals(0, result.getStateChanges().size());  
        assertEquals(0, result.getPlayers().size());  
    }
}
