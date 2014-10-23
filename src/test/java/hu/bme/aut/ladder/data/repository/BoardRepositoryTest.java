package hu.bme.aut.ladder.data.repository;

import static org.junit.Assert.*;
import hu.bme.aut.ladder.BaseIntegrationTest;
import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.data.entity.StateChangeEntity;
import hu.bme.aut.ladder.data.entity.TunnelEntity;
import java.util.Arrays;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Unit test for the {@
 * @author Barnabas
 */
public class BoardRepositoryTest extends BaseIntegrationTest {

    /**
     * Target
     */
    @Autowired
    private BoardRepository boardRepository;

    /**
     * Test that repository is empty by default
     */
    @Test
    public void thatRepositoryIsEmptyByDefault() {
        assertTrue(boardRepository.findAll().isEmpty());
    }

    /**
     * Test that if we save a board in the repository we can read it back later.
     */
    @Test
    public void thatSavingBoardIsPersisted() {

        // Arrange
        BoardEntity board = new BoardEntity();
        board.setBoardSize(100);

        // Act
        boardRepository.save(board);

        // Assert
        assertEquals(1, boardRepository.findAll().size());
    }
    
    /**
     * Test that when saving a board with a player the player is also saved
     */
    @Test
    public void thatPlayersArePersistedWithBoards(){
        
        // Arrange
        BoardEntity board = new BoardEntity();
        board.setBoardSize(100);
        
        PlayerEntity player = new PlayerEntity();
        player.setPosition(5);
        
        board.setPlayers(Arrays.asList(player));
        
        // Act
        boardRepository.save(board);
                
        // Assert
        BoardEntity retVal = boardRepository.findAll().get(0);
        assertNotNull(retVal.getPlayers());
        assertEquals("Expected one player", 1, retVal.getPlayers().size());
        assertEquals(player.getPosition(), retVal.getPlayers().get(0).getPosition());
    }
    
    /**
     * Test that when saving a board with a tunnel the tunnel is also persisted
     */
    @Test
    public void thatTunnelsArePersistedWithBoards(){
        
        // Arrange
        BoardEntity board = new BoardEntity();
        board.setBoardSize(100);
        
        TunnelEntity snake = mockTunnel(4,2);
        snake.setType(TunnelEntity.Type.SNAKE);
        
        TunnelEntity ladder = mockTunnel(6,8);
        ladder.setType(TunnelEntity.Type.LADDER);
        
        board.setTunnels(Arrays.asList(snake, ladder));
        
        // Act
        boardRepository.save(board);
                
        // Assert
        BoardEntity retVal = boardRepository.findAll().get(0);
        assertNotNull(retVal.getTunnels());
        assertEquals("Expected two tunnels", 2, retVal.getTunnels().size());
        assertEquals(TunnelEntity.Type.SNAKE, retVal.getTunnels().get(0).getType());
        assertEquals(TunnelEntity.Type.LADDER, retVal.getTunnels().get(1).getType());
    }
    
    /**
     * Test that state changes are returned in order
     */
    @Test
    public void thatStateChangesAreReturnedInOrder(){
        
         // Arrange
        BoardEntity board = new BoardEntity();
        board.setBoardSize(100);
        
        StateChangeEntity first = new StateChangeEntity();
        first.setSequenceNumber(100);
        
        StateChangeEntity second = new StateChangeEntity();
        second.setSequenceNumber(200);
        
        board.setStateChanges(Arrays.asList(second, first));
        
        // Act
        boardRepository.save(board);
                
        // Assert
        BoardEntity retVal = boardRepository.findAll().get(0);
        assertEquals(first.getSequenceNumber(), retVal.getStateChanges().get(0).getSequenceNumber());
        assertEquals(second.getSequenceNumber(), retVal.getStateChanges().get(1).getSequenceNumber());
        
    }

    /**
     * Tear down test context
     */
    @After
    public void tearDown() {
        boardRepository.deleteAll();
    }
    
    /**
     * Create mock tunnel
     * 
     * @return 
     */
    private static TunnelEntity mockTunnel(int from, int to){
        TunnelEntity retVal = new TunnelEntity();
        retVal.setFromField(from);
        retVal.setToField(to);
        return retVal;
    }
}
