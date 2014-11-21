package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.BaseControllerTest;
import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.GameEntity;
import static hu.bme.aut.ladder.data.entity.PlayerEntity.Type.ROBOT;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        
        // Join the game
        MvcResult result = mockMvc
            .perform(put(LobbyController.JOIN_GAME_URI + "/" + games.get(0).getGameId()))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();
        
        // Request details on that game
        MockHttpSession session = (MockHttpSession)result.getRequest().getSession();
        
        mockMvc
            .perform(get(RoomController.GAME_DETAILS).session(session))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.numberOfRobots", is(0)))
            .andExpect(jsonPath("$.allPlayers", hasSize(2)));
    }
    
    /**
     * Create a game, join it by an other user and start it
     */
    @Test
    public void thatHostCanStartAGame() throws Exception {
        // Create game once
        MockHttpSession hostSession = createNewGame();
        
        List<GameEntity> games = gameRepository.findAll();
        
        // Join the game
        mockMvc
            .perform(put(LobbyController.JOIN_GAME_URI + "/" + games.get(0).getGameId()))
            .andExpect(status().is(HttpStatus.OK.value()));
        
        mockMvc
            .perform(post(RoomController.GAME_START_URI).session(hostSession))
            .andExpect(status().is(HttpStatus.OK.value()));
        
        // Assert
        final BoardEntity board = gameRepository.findAll().get(0).getBoard();
        
        assertNotNull(board);
        assertEquals(2, board.getPlayers().size());
        assertNotEquals(0, board.getTunnels().size());
        assertEquals(100, board.getBoardSize());
    }
    
    /**
     * Create a game, join it by an other user and start it by a third one
     */
    @Test
    public void thatOnlyTheHostCanStartTheGame() throws Exception {
        // Create game once
        createNewGame();
        
        List<GameEntity> games = gameRepository.findAll();
        
        // Join the game
        mockMvc
            .perform(put(LobbyController.JOIN_GAME_URI + "/" + games.get(0).getGameId()))
            .andExpect(status().is(HttpStatus.OK.value()));
        
        // Attemp to start by a third person
        mockMvc.perform(post(RoomController.GAME_START_URI))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }
    
     /**
     * Play this scenario:
     * <ul>
     *  <li> Create a new game </li>
     *  <li> Add robots to it </li>
     *  <li> Start the game </li>
     * </ul>
     */
    @Test
    public void thatHostCanStartAGameWithRobots() throws Exception {
        // Create game once
        MockHttpSession hostSession = createNewGame();
        
        final int numberOfPlayers = 4;
        
        // Add robots to the game
        mockMvc
            .perform(post(RoomController.GAME_ROBOT_NUMBER)
                    .session(hostSession)
                    .param("number", Integer.toString(numberOfPlayers-1)))
            .andExpect(status().is(HttpStatus.OK.value()));
        
        mockMvc
            .perform(post(RoomController.GAME_START_URI).session(hostSession))
            .andExpect(status().is(HttpStatus.OK.value()));
        
        // Assert
        final BoardEntity board = gameRepository.findAll().get(0).getBoard();
        
        assertNotNull(board);
        assertEquals(numberOfPlayers, board.getPlayers().size());
        
        for(int i = 1; i < numberOfPlayers; i++){
            assertEquals("Player " + i + " should be a ROBOT", ROBOT, board.getPlayers().get(i).getType());
        }
    }
}
