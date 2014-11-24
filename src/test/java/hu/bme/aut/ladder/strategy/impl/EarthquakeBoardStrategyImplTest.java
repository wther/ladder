package hu.bme.aut.ladder.strategy.impl;

import hu.bme.aut.ladder.data.entity.AbilityEntity;
import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit tests for the @{@link EarthquakeBoardStrategyImpl} class
 * 
 * @author Barnabas
 */
public class EarthquakeBoardStrategyImplTest extends BaseBoardStrategyImplTest {
    
    /**
     * Target being tested
     */
    private final EarthquakeBoardStrategyImpl target = new EarthquakeBoardStrategyImpl();
    
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
        target.executePlayerAction(board, board.getPlayers().get(0), "ROLL");
        
        // Assert
        assertEquals(1, board.getStateChanges().size());
        assertEquals(diceRolled, board.getPlayers().get(0).getPosition());
    }
    
    /**
     * Test that if a player generates an earthquake than each everybody
     * falls to the right-most field
     */
    @Test
    public void thatPlayerCanGenerateEarthquake() throws BoardActionNotPermitted {
        
        // Arrange
        BoardEntity board = mockBoard(4);
        setMockAbilityEarthquake(board);
        
        // Move players to a given position
        int[] startingPositions = {0, 37, 42, 94};
        for(int i = 0; i < board.getPlayers().size(); i++){
            board.getPlayers().get(i).setPosition(startingPositions[i]);
        }
                
        // Act
        target.executePlayerAction(board, board.getPlayers().get(0), "EARTHQUAKE");
        
        // Assert
        assertEquals("Every player should be moved", board.getPlayers().size(), board.getStateChanges().size());
        
        for(int i = 1; i < board.getPlayers().size(); i++){
            assertEquals("All sequence numbers should match",
                    board.getStateChanges().get(0).getSequenceNumber(), 
                    board.getStateChanges().get(i).getSequenceNumber());
        }
        
        assertEquals("0 should advance to 9", 9, board.getPlayers().get(0).getPosition());
        assertEquals("37 should fall back to to 30", 30, board.getPlayers().get(1).getPosition());
        assertEquals("42 should advance to to 49", 49, board.getPlayers().get(2).getPosition());
        assertEquals("94 should fall back to to 90", 90, board.getPlayers().get(3).getPosition());
    }
    
    /**
     * Test earthquake for a board of size 8x8
     */
    @Test
    public void thatEarthquakePointsAreDifferentWhenBoardSizeChanges() throws BoardActionNotPermitted {
        
        // Arrange
        BoardEntity board = mockBoard(4);
        board.setBoardSize(8*8);
        setMockAbilityEarthquake(board);
        
        // Move players to a given position
        int[] startingPositions = {0, 37, 42, 59};
        for(int i = 0; i < board.getPlayers().size(); i++){
            board.getPlayers().get(i).setPosition(startingPositions[i]);
        }
                
        // Act
        target.executePlayerAction(board, board.getPlayers().get(0), "EARTHQUAKE");
        
        // Assert
        assertEquals("Every player should be moved", board.getPlayers().size(), board.getStateChanges().size());
        assertEquals("0 should advance to 7", 7, board.getPlayers().get(0).getPosition());
        assertEquals("37 should advence to to 39", 39, board.getPlayers().get(1).getPosition());
        assertEquals("42 should fall back to 40", 40, board.getPlayers().get(2).getPosition());
        assertEquals("59 should fall back to to 56", 56, board.getPlayers().get(3).getPosition());
    }
    
    /**
     * Test that using earthquake adjusts the usesLeft value of the ability
     * 
     * @throws BoardActionNotPermitted 
     */
    @Test
    public void thatAbilityUsesAreReducedWhenUsingEarthquake() throws BoardActionNotPermitted {
        
         // Arrange
        BoardEntity board = mockBoard(4);
        setMockAbilityEarthquake(board);
        
        // Act
        target.executePlayerAction(board, board.getPlayers().get(0), "EARTHQUAKE");
        
        // Abilities uses should be adjusted
        assertEquals("Player who uses should be adjusted", 1, board.getPlayers().get(0).getAbilities().get(0).getUsesLeft());
        assertEquals("Player who doesn't use should not be adjusted", 2, board.getPlayers().get(1).getAbilities().get(0).getUsesLeft());
        
    }

    /**
     * Add earthquake ability to all players
     * 
     * @param board 
     */
    private static void setMockAbilityEarthquake(BoardEntity board){
        for(int i = 0; i < board.getPlayers().size(); i++){
            AbilityEntity ability = new AbilityEntity();
            ability.setAbilityId(new Long(i));
            ability.setAbility(AbilityEntity.Ability.EARTHQUAKE);
            ability.setUsesLeft(2);
            board.getPlayers().get(i).setAbilities(Arrays.asList(ability));
        }
    }
}
