package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.BaseControllerTest;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    
    /**
     * That request to <code>user/details</code> returns something
     */
    @Test
    public void thatUserDetailsContainsName() throws Exception {
        mockMvc.perform(get(UserController.DETAIL_URL))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.name", notNullValue()));
    }
    
    /**
     * That request to <code>user/name</code> updates the user's name
     */
    @Test
    public void thatUserCanChangeTheName() throws Exception {
        final String name = "JohnySmith69";
        
        MvcResult result = mockMvc.perform(post(UserController.UPDATE_NAME)
                .param("name", name))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();
        
        MockHttpSession session = (MockHttpSession)result.getRequest().getSession();
        
        mockMvc.perform(get(UserController.DETAIL_URL).session(session))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.name", is(name)));
    }  
    
    /**
     * That request to <code>user/ready</code> updates the user's ready value
     */
    @Test
    public void thatUserCanChangeTheReadyValue() throws Exception {
        MvcResult result = mockMvc.perform(post(UserController.SET_READY)
                .param("ready", "true"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();
        
        
        MockHttpSession session = (MockHttpSession)result.getRequest().getSession();
        
        mockMvc.perform(get(UserController.DETAIL_URL).session(session))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.ready", is(true)));
    }   
}
