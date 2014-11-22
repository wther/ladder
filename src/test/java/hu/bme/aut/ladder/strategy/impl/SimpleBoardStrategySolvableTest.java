package hu.bme.aut.ladder.strategy.impl;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.data.entity.StateChangeEntity;
import hu.bme.aut.ladder.data.entity.TunnelEntity;
import hu.bme.aut.ladder.strategy.BoardStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit tests for the {@link SimpleBoardStrategyImpl#isBoardAlwaysSolvable(hu.bme.aut.ladder.data.entity.BoardEntity)}
 * function.
 * 
 * @author Barnabas
 */
public class SimpleBoardStrategySolvableTest {
    
    /**
     * Target being tested
     */
    private BoardStrategy target = new SimpleBoardStrategyImpl();
    
    /**
     * Test that a board with a cycle is not considered solvable
     */
    @Test
    public void thatBoardWithCycleIsNotSolvable(){
     
        BoardEntity board = prepareTestBoard();
        
        TunnelEntity ladderUp = new TunnelEntity();
        ladderUp.setFromField(4);
        ladderUp.setToField(8);
        ladderUp.setType(TunnelEntity.Type.LADDER);
        
        TunnelEntity nextLadder = new TunnelEntity();
        nextLadder.setFromField(8);
        nextLadder.setToField(20);
        nextLadder.setType(TunnelEntity.Type.LADDER);
        
        TunnelEntity snakeBack = new TunnelEntity();
        snakeBack.setFromField(20);
        snakeBack.setToField(4);
        snakeBack.setType(TunnelEntity.Type.SNAKE);
        
        board.setTunnels(Arrays.asList(nextLadder, ladderUp, snakeBack));
        
        // Act
        boolean result = target.isBoardAlwaysSolvable(board);
        
        // Assert
        assertEquals("Expected board to be unsolvable", false, result);
    }
    
    /**
     * Test that a simple board can be solvable
     */
    @Test
    public void thatABoardCanBeSolvable(){
        
        BoardEntity board = prepareTestBoard();
        
        TunnelEntity ladderUp = new TunnelEntity();
        ladderUp.setFromField(4);
        ladderUp.setToField(8);
        ladderUp.setType(TunnelEntity.Type.LADDER);
        
        board.setTunnels(Arrays.asList(ladderUp));
        
        // Act
        boolean result = target.isBoardAlwaysSolvable(board);
        
        // Assert
        assertEquals("Board should be solvable", true, result);
        
    }
    
    /**
     * Test that board with 10 snakes after each other is not solvable
     */
    @Test
    public void thatABoardWithConsecutiveSnakesIsNotSolvable(){
        
        BoardEntity board = prepareTestBoard();
        
        List<TunnelEntity> snakes = new ArrayList<TunnelEntity>();
        for(int field = 15; field < 25; field++){
           TunnelEntity snake = new TunnelEntity();
           snake.setFromField(field);
           snake.setToField(5);
           snake.setType(TunnelEntity.Type.SNAKE);
           snakes.add(snake);
        }
        
        board.setTunnels(snakes);
        
        // Act
        boolean result = target.isBoardAlwaysSolvable(board);
        
        // Assert
        assertEquals("Board should not be solvable", false, result);
    }
    
    
    /**
     * Test that board with a snake at the end (100) is not solvable
     */
    @Test
    public void thatABoardWithSnakeOnTheFinalFieldIsNotSolvable(){
        
        BoardEntity board = prepareTestBoard();
        
        TunnelEntity snake = new TunnelEntity();
        snake.setFromField(board.getBoardSize() - 1);
        snake.setToField(5);
        snake.setType(TunnelEntity.Type.SNAKE);
        
        board.setTunnels(Arrays.asList(snake));
        
        // Act
        boolean result = target.isBoardAlwaysSolvable(board);
        
        // Assert
        assertEquals("Board should not be solvable", false, result);
    }
    
    /**
     * Prepare a board for testing
     * 
     * @return 
     */
    private static BoardEntity prepareTestBoard(){
        BoardEntity board = new BoardEntity();
        board.setBoardId(1L);
        board.setPlayers(Arrays.asList(new PlayerEntity()));
        board.setStateChanges(new ArrayList<StateChangeEntity>());
        board.setBoardSize(100);
        return board;        
    }
}
