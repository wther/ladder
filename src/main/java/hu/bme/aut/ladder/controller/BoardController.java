package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.controller.dto.BoardDTO;
import hu.bme.aut.ladder.controller.dto.FieldDTO;
import hu.bme.aut.ladder.controller.dto.PlayerDTO;
import hu.bme.aut.ladder.controller.dto.TunnelDTO;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for accessing the board.
 * 
 * @author Barnabas
 */
@Controller
public class BoardController {

    /**
     * Dummy implementation for the Snakes & Ladders game with returning the
     * same board all the time
     */
    @RequestMapping("/board")
    public @ResponseBody ResponseEntity<BoardDTO> board() {
        BoardDTO board = new BoardDTO();
        board.setWidth(10);
        board.setHeight(10);

        // Set ladders
        board.setLadders(Arrays.asList(new TunnelDTO[] { TunnelDTO.forCoordinates(3, 2, 7, 6), TunnelDTO.forCoordinates(8, 8, 4, 9) }));

        // Set snakes
        board.setSnakes(Arrays.asList(new TunnelDTO[] { TunnelDTO.forCoordinates(3, 9, 7, 6) }));

        FieldDTO playerPosition = new FieldDTO();
        playerPosition.setX(0);
        playerPosition.setY(0);

        PlayerDTO player = new PlayerDTO();
        player.setName("John");
        player.setPosition(playerPosition);

        // Set players
        board.setPlayers(Arrays.asList(new PlayerDTO[] { player }));

        // Return with HTTP 200 OK
        return new ResponseEntity<BoardDTO>(board, HttpStatus.OK);
    }

}
