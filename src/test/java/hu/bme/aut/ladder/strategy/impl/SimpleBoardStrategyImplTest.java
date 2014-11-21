package hu.bme.aut.ladder.strategy.impl;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.data.entity.StateChangeEntity;
import hu.bme.aut.ladder.data.entity.TunnelEntity;
import static hu.bme.aut.ladder.data.entity.TunnelEntity.Type.LADDER;
import static hu.bme.aut.ladder.data.entity.TunnelEntity.Type.SNAKE;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;
import hu.bme.aut.ladder.strategy.Dice;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for the {@link SimpleBoardStrategyImpl} class.
 * 
 * @author Barnabas
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleBoardStrategyImplTest {
    
    /**
     * Target being tested
     */
    private final SimpleBoardStrategyImpl target = new SimpleBoardStrategyImpl();
    
    /**
     * Test that rolling the dice moves the player
     * @throws hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted
     */
    @Test
    public void thatRollingTheDiceMovesThePlayer() throws BoardActionNotPermitted {
        
        // Arrange
        BoardEntity board = mockBoard(1);
        final int diceRolled = 5;
        target.setDice(diceWhichRollsTheSame(diceRolled));

        
        // Act
        target.executeAction(board, board.getPlayers().get(0), "ROLL");
        
        // Assert
        assertEquals(1, board.getStateChanges().size());
        assertEquals(diceRolled, board.getPlayers().get(0).getPosition());
    }
    
    /**
     * Test that a player can climb up on a ladder
     * @throws hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted
     */
    @Test
    public void thatPlayerClimbsUpOnLadder() throws BoardActionNotPermitted {
        
        // Arrange
        BoardEntity board = mockBoard(1);
        final int diceRolled = 5;
        target.setDice(diceWhichRollsTheSame(diceRolled));
        
        // Add ladder to board
        TunnelEntity ladder = new TunnelEntity();
        ladder.setFromField(5);
        ladder.setToField(45);
        ladder.setType(LADDER);
        board.setTunnels(Arrays.asList(ladder));
        
        // Act
        target.executeAction(board, board.getPlayers().get(0), "ROLL");
        
        // Assert
        assertEquals(2, board.getStateChanges().size());
        assertEquals(ladder.getToField(), board.getPlayers().get(0).getPosition());
    }
    
    
    /**
     * Tests that player falls down on a snake
     * @throws BoardActionNotPermitted 
     */
    @Test
    public void thatPlayerFallsDownOnSnakes() throws BoardActionNotPermitted {
        // Arrange
        BoardEntity board = mockBoard(1);
        final int diceRolled = 5;
        target.setDice(diceWhichRollsTheSame(diceRolled));
        
        // Add ladder to board
        TunnelEntity snake = new TunnelEntity();
        snake.setFromField(5);
        snake.setToField(2);
        snake.setType(SNAKE);
        board.setTunnels(Arrays.asList(snake));
        
         // Act
        target.executeAction(board, board.getPlayers().get(0), "ROLL");
        
        // Assert
        assertEquals(2, board.getStateChanges().size());
        assertEquals(snake.getToField(), board.getPlayers().get(0).getPosition());
    }
   
    /**
     * Test that the player will fall and climb in series
     * 
     * @throws BoardActionNotPermitted 
     */
    @Test
    public void thatPlayerCanMoveMultipleTimes() throws BoardActionNotPermitted {
        
         // Arrange
        BoardEntity board = mockBoard(1);
        final int diceRolled = 5;
        target.setDice(diceWhichRollsTheSame(diceRolled));
        
        // Set player's position
        int playerStartsFrom = 17;
        board.getPlayers().get(0).setPosition(playerStartsFrom);
        
        // Set series of ladders and snakes
        final int[] positions = {17 + diceRolled, 36, 30, 45, 74, 11, 64, 33};
        for(int i = 1; i < positions.length; i++){
            TunnelEntity tunnel = new TunnelEntity();
            tunnel.setFromField(positions[i-1]);
            tunnel.setToField(positions[i]);
            tunnel.setType(positions[i-1] < positions[i] ? LADDER : SNAKE);
            board.getTunnels().add(tunnel);
        }
        
        // Act
        target.executeAction(board, board.getPlayers().get(0), "ROLL");
     
        // Assert
        assertEquals("Expected all ladders and snakes to be in effect", positions.length, board.getStateChanges().size());
        assertEquals(6, board.getStateChanges().get(5).getSequenceNumber());
        assertEquals(positions[positions.length-1], board.getPlayers().get(0).getPosition());
    }
    
    
    /**
     * Test that when next player is missing the player who initiated the
     * action is assumed the next player, and the next player becomes
     * the {@link BoardEntity#nextPlayer}
     * 
     * @throws BoardActionNotPermitted 
     */
    @Test
    public void thatNextPlayerIsSetEvenIfMissing() throws BoardActionNotPermitted {
        
        BoardEntity board = mockBoard(2);
        
        // Act
        target.executeAction(board, board.getPlayers().get(0), "ROLL");
        
        // Assert
        assertEquals("Next player should be set to the player after 0", board.getPlayers().get(1), board.getNextPlayer());        
    }
    
    /**
     * Test that when the last player takes a turn the next player is 
     * the first player
     * 
     * @throws BoardActionNotPermitted 
     */
    @Test
    public void thatFirstPlayerComesAfterLastPlayer() throws BoardActionNotPermitted {
        
        final int numberOfPlayers = 4;
        BoardEntity board = mockBoard(numberOfPlayers);
        board.setNextPlayer(board.getPlayers().get(numberOfPlayers-1));
        
        // Act
        target.executeAction(board, board.getPlayers().get(numberOfPlayers-1), "ROLL");
        
        // Assert
        assertEquals("First player should be on turn", board.getPlayers().get(0), board.getNextPlayer());        
    }
    
    /**
     * Test that a player can't roll the dice out of turn
     * 
     * @throws BoardActionNotPermitted 
     */
    @Test(expected = BoardActionNotPermitted.class)
    public void thatPlayerOnlyNextPlayerCanTakeTurn() throws BoardActionNotPermitted {
        final int numberOfPlayers = 4;
        BoardEntity board = mockBoard(numberOfPlayers);
        board.setNextPlayer(board.getPlayers().get(numberOfPlayers-1));
        
        // Act
        target.executeAction(board, board.getPlayers().get(0), "ROLL");
    }
    
    /**
     * Test that if a Robot player is added, than the strategy doesn't have to be 
     * called with that player, it acts automatically.
     * 
     * @throws BoardActionNotPermitted 
     */
    @Test
    public void thatRobotPlayerTakesActionAutomatically() throws BoardActionNotPermitted {
        
        final int numberOfPlayers = 4;
        BoardEntity board = mockBoard(numberOfPlayers);
        board.getPlayers().get(1).setType(PlayerEntity.Type.ROBOT);
        
        // Act
        target.executeAction(board, board.getPlayers().get(0), "ROLL");
        
        // Assert
        assertEquals("Third player should be next", board.getPlayers().get(2), board.getNextPlayer());
        assertNotEquals("Robot should've moved", board.getPlayers().get(1).getPosition(), 0);
    }
    
    /**
     * Creates a simple board
     * 
     * @param numberOfPlayers
     * @return 
     */
    private static BoardEntity mockBoard(int numberOfPlayers){
        BoardEntity board = new BoardEntity();
        board.setBoardId(new Long(1));
        board.setBoardSize(100);
        board.setStateChanges(new ArrayList<StateChangeEntity>());
        board.setTunnels(new ArrayList<TunnelEntity>());
        
        List<PlayerEntity> players = new ArrayList<>();
        for(int i = 0; i < numberOfPlayers; i++){
            PlayerEntity player = new PlayerEntity();
            player.setPlayerId(new Long(i));
            player.setPosition(0);
            player.setType(PlayerEntity.Type.HUMAN);
            players.add(player);
        }
        
        board.setPlayers(players);
        return board;
    }

    
    /**
     * Returns a dice which always rolls five
     * @return 
     */
    private static Dice diceWhichRollsTheSame(int diceRolled){
        // Mock a dice to always throw 5
        Dice dice = mock(Dice.class);
        when(dice.getNext()).thenReturn(diceRolled);
        return dice;
    }
}
