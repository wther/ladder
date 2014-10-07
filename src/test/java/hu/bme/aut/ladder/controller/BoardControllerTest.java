package hu.bme.aut.ladder.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import hu.bme.aut.ladder.BaseControllerTest;

import org.junit.Test;
import org.springframework.http.HttpStatus;

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
        mockMvc.perform(get("/board"))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.width", is(10)))
            .andExpect(jsonPath("$.height", is(10)))
            .andExpect(jsonPath("$.players[0].name", is("John")))
            .andExpect(jsonPath("$.ladders", hasSize(2)))
            .andExpect(jsonPath("$.snakes", hasSize(1)));
    }
}
