package hu.bme.aut.ladder;

import hu.bme.aut.ladder.controller.LobbyController;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Base class for integration tests of controllers
 * 
 * @author Barnabas
 */
public abstract class BaseControllerTest extends BaseIntegrationTest {
    
    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Before
    public void setupBaseClass() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    /**
     * Perform POST to <code>/game</code> and create new game.
     * 
     * @return Session ID of the host
     */
    protected MockHttpSession createNewGame() throws Exception{
        
        MvcResult result = mockMvc
            .perform(post(LobbyController.CREATE_GAME_URI))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();
                
        return (MockHttpSession)result.getRequest().getSession();
    } 
}
