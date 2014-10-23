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
        board.setWidth(10);
        board.setHeight(10);

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
        board.setWidth(10);
        board.setHeight(10);
        
        PlayerEntity player = new PlayerEntity();
        player.setX(5);
        player.setY(4);
        
        board.setPlayers(Arrays.asList(player));
        
        // Act
        boardRepository.save(board);
                
        // Assert
        BoardEntity retVal = boardRepository.findAll().get(0);
        assertNotNull(retVal.getPlayers());
        assertEquals("Expected one player", 1, retVal.getPlayers().size());
        assertEquals(player.getX(), retVal.getPlayers().get(0).getX());
        assertEquals(player.getY(), retVal.getPlayers().get(0).getY());
    }
    
    /**
     * Test that when saving a board with a tunnel the tunnel is also persisted
     */
    @Test
    public void thatTunnelsArePersistedWithBoards(){
        
        // Arrange
        BoardEntity board = new BoardEntity();
        board.setWidth(10);
        board.setHeight(10);
        
        TunnelEntity snake = mockTunnel(4,5,2,3);
        snake.setType(TunnelEntity.Type.SNAKE);
        
        TunnelEntity ladder = mockTunnel(6,7,8,5);
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
        board.setWidth(10);
        board.setHeight(10);
        
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
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @return 
     */
    private static TunnelEntity mockTunnel(int fromX, int fromY, int toX, int toY){
        TunnelEntity retVal = new TunnelEntity();
        retVal.setFromX(fromX);
        retVal.setFromY(fromY);
        retVal.setToX(toX);
        retVal.setToY(toY);
        return retVal;
    }
}
