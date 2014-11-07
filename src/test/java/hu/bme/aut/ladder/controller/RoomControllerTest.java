package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.BaseControllerTest;
import hu.bme.aut.ladder.data.entity.GameEntity;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * IT for the {@link RoomController}  class.
 *
 * @author Barnabas
 */
public class RoomControllerTest extends BaseControllerTest {
    
    /**
     * Create a game, join it and then request details on the game
     */
    @Test
    public void thatUserCanRequestRoomDetails() throws Exception {
        // Create game once
        createNewGame();
        
        List<GameEntity> games = gameRepository.findAll();
        
        // Join one game
        MvcResult result = mockMvc
            .perform(put(LobbyController.JOIN_GAME_URI + "/" + games.get(0).getGameId()))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();
        
        // Request details on that game
        MockHttpSession session = (MockHttpSession)result.getRequest().getSession();
        
        mockMvc
            .perform(get(RoomController.GAME_DETAILS).session(session))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.allPlayers", hasSize(2)));
    }
    
}
