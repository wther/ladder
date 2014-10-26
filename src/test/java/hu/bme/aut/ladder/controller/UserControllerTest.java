package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.BaseControllerTest;
import org.junit.Test;
import static org.mockito.Matchers.isNotNull;
import org.springframework.http.HttpStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link UserController} class.
 * 
 * @author Barnabas
 */
public class UserControllerTest extends BaseControllerTest {
    
    /**
     * That request to <code>user/session</code> returns something
     */
    @Test
    public void thatSessionIsAccessible() throws Exception {
        mockMvc.perform(get("/user/session"))
            .andExpect(status().is(HttpStatus.OK.value()));
    }
    
}
