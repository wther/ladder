package hu.bme.aut.ladder.data.repository;

import static org.junit.Assert.*;
import hu.bme.aut.ladder.BaseIntegrationTest;
import hu.bme.aut.ladder.data.entity.BoardEntity;

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
     * Tear down test context
     */
    @After
    public void tearDown() {
        boardRepository.deleteAll();
    }
}
