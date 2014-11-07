package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.BaseControllerTest;
import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * IT tests for the {@link GameController}
 * 
 * @author Barnabas
 */
public class GameControllerTest extends BaseControllerTest {
    
    /**
     * Perform 3 POST requests to <code>games</code> and then
     * list created games using GET <code>games</code>
     */
    @Test
    public void thatGamesCreatedCanBeListedInTheLobby() throws Exception {
        
        MockHttpSession player1 = createNewGame();
        
        // Force override the name of the user
        final String name = "John";
        UserEntity user = userRepository.findAll().get(0);
        user.setName(name);
        userRepository.save(user);        
        
        MockHttpSession player2 = createNewGame();
        MockHttpSession player3 = createNewGame();
        
        // List games from the viewpoint of player1
        mockMvc
            .perform(get(GameController.LIST_GAMES_URI).session(player1))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$", hasSize(3)))
                
            // Anonymous is before John in the alphabet, so John is the last
            .andExpect(jsonPath("$[0].host", is("Anonymous")))
            .andExpect(jsonPath("$[0].allPlayers", hasSize(1)))
            .andExpect(jsonPath("$[0].userInGame", is(false)))
            .andExpect(jsonPath("$[2].host", is(name)))
            .andExpect(jsonPath("$[2].allPlayers", hasSize(1)))
            .andExpect(jsonPath("$[2].userInGame", is(true)));
    }
    
    /**
     * Send POST to <code>games</code> and create a new one
     * Then send POST to <code>games/join</code> and join it
     */
    @Test
    public void thatUserCanJoinAndLeaveAGame() throws Exception{
        
        // Create a game
        createNewGame();
        GameEntity game = gameRepository.findAll().get(0);
        
        // Join this game
        mockMvc
          .perform(put(GameController.JOIN_GAME_URI + "/" + game.getGameId()))
          .andExpect(status().is(HttpStatus.OK.value()))
          .andExpect(jsonPath("$.userInGame", is(true)))
          .andExpect(jsonPath("$.allPlayers", hasSize(2)));
        
        // Join this game by an other user
        MvcResult result = mockMvc
          .perform(put(GameController.JOIN_GAME_URI + "/" + game.getGameId()))
          .andExpect(status().is(HttpStatus.OK.value()))
          .andExpect(jsonPath("$.userInGame", is(true)))
          .andExpect(jsonPath("$.allPlayers", hasSize(3)))
          .andReturn();
        
        // Leave this game by this user
        MockHttpSession session = (MockHttpSession)result.getRequest().getSession();
        mockMvc
          .perform(delete(GameController.LEAVE_GAME_URI + "/" + game.getGameId()).session(session))                
          .andExpect(status().is(HttpStatus.OK.value()))
          .andExpect(jsonPath("$.userInGame", is(false)))
          .andExpect(jsonPath("$.allPlayers", hasSize(2)));
    }
    
    /**
     * Perform POST to <code>/game</code> and create new game.
     * 
     * @return Session ID of the host
     */
    private MockHttpSession createNewGame() throws Exception{
        
        MvcResult result = mockMvc
            .perform(post(GameController.CREATE_GAME_URI))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();
                
        return (MockHttpSession)result.getRequest().getSession();
    }    
}
