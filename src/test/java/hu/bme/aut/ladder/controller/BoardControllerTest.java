package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.BaseControllerTest;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the {@link BoardController} REST controller
 * 
 * @author Barnabas
 */
public class BoardControllerTest extends BaseControllerTest {

    /**
     * Tests that the board controllers response contains snakes, ladders and
     * players, and has 100 fields
     */
    @Test
    public void thatControllerReturnsBoardWithData() throws Exception {
        
        // Arrange
        List<MockHttpSession> sessions = startGame();
        
        // Act
        mockMvc
            .perform(get(BoardController.BOARD_DETAILS_URI).session(sessions.get(1)))
            .andExpect(jsonPath("$.size", is(100)))
            .andExpect(jsonPath("$.players", hasSize(2)))
            .andExpect(jsonPath("$.ladders", hasSize(5)))
            .andExpect(jsonPath("$.snakes", hasSize(5)))
            .andExpect(status().is(HttpStatus.OK.value()));
    }
    
    /**
     * Tests that players can each make a move
     */
    @Test
    public void thatPlayersCanTakeATurnAfterEachOther() throws Exception {
        
        // Arrange
        List<MockHttpSession> sessions = startGame();
        
        // Act
        mockMvc
            .perform(post(BoardController.BOARD_ACTION_URI)
                    .session(sessions.get(0))
                    .param("action", "ROLL"))
            .andExpect(jsonPath("$.size", is(100)))
            .andExpect(jsonPath("$.stateChanges", not(hasSize(0))))
            .andExpect(jsonPath("$.players[0].position", not(is(0))))
            .andExpect(status().is(HttpStatus.OK.value()));
        
        mockMvc
            .perform(post(BoardController.BOARD_ACTION_URI)
                    .session(sessions.get(1))
                    .param("action", "ROLL"))
            .andExpect(jsonPath("$.size", is(100)))
            .andExpect(jsonPath("$.stateChanges", not(hasSize(1))))
            .andExpect(jsonPath("$.players[1].position", not(is(0))))
            .andExpect(status().is(HttpStatus.OK.value()));
    }
    
    /**
     * This method creates a game with two users, and 
     * returns the session for the 2nd player
     * 
     * @return 
     */
    private List<MockHttpSession> startGame() throws Exception{
        
        MockHttpSession hostSession = createNewGame();
        
        // Join the game
        MvcResult result = mockMvc
            .perform(put(LobbyController.JOIN_GAME_URI + "/" + gameRepository.findAll().get(0).getGameId()))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();
        
        MockHttpSession playerSession = (MockHttpSession)result.getRequest().getSession();
        
        // Start the game
        mockMvc
            .perform(post(RoomController.GAME_START_URI).session(hostSession))
            .andExpect(status().is(HttpStatus.OK.value()));
        
        return Arrays.asList(hostSession, playerSession);
    }
}
