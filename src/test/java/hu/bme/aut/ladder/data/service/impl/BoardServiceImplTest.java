package hu.bme.aut.ladder.data.service.impl;

import static org.junit.Assert.*;
import hu.bme.aut.ladder.BaseIntegrationTest;
import hu.bme.aut.ladder.data.service.BoardService;
import hu.bme.aut.ladder.data.service.impl.BoardServiceImpl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for the {@link BoardServiceImpl} implementation.
 * 
 * @author Barnabas
 */
public class BoardServiceImplTest extends BaseIntegrationTest {

    /**
     * Target implementation
     */
    @Autowired
    private BoardService target;

    /**
     * Test that the {@link BoardService#getAllBoard()} returns an empty
     * collection by default.
     */
    @Test
    public void thatThereAreNoBoardsByDefault() {
        assertTrue(target.getAllBoard().isEmpty());
    }
}
