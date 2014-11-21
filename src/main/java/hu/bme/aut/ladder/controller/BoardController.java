package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.controller.dto.BoardDTO;
import static hu.bme.aut.ladder.data.entity.GameEntity.GameState.BOARD_STARTED;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.BoardService;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for accessing the board.
 *
 * @author Barnabas
 */
@Controller
public class BoardController extends BaseGameController {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LobbyController.class);

    /**
     * URI for accessing details on the board
     */
    public static final String BOARD_DETAILS_URI = "/board";

    /**
     * URI for making a move
     */
    public static final String BOARD_ACTION_URI = "/board/action";

    /**
     * Board strategy for manipulation of boards
     */
    @Autowired
    private BoardService boardService;

    /**
     * Controller to request the current state of the board
     */
    @RequestMapping(value = BOARD_DETAILS_URI, method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<BoardDTO> board(HttpServletRequest request) {
        final UserEntity user = findAndVerifyUserEntity(request);
        if (user == null) {
            return new ResponseEntity<BoardDTO>(HttpStatus.BAD_REQUEST);
        }

        LOGGER.info("Sending DTO for board: {}", user.getGame().getBoard());
        
        return new ResponseEntity<BoardDTO>(dtoFromBoard(user.getGame().getBoard(), user), HttpStatus.OK);
    }

    /**
     * Controller for rolling the dice etc.
     *
     * @return
     */
    @RequestMapping(value = BOARD_ACTION_URI, method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<BoardDTO> takeAction(HttpServletRequest request, @RequestParam String action) throws BoardActionNotPermitted {
        final UserEntity user = findAndVerifyUserEntity(request);
        if (user == null) {
            return new ResponseEntity<BoardDTO>(HttpStatus.BAD_REQUEST);
        }
        
        
        LOGGER.info("{} is attempting to make a move: {}", user, action);
        boardService.executeAction(user.getGame().getBoard(), user.getPlayer(), action);
        
        final BoardDTO retVal = dtoFromBoard(user.getGame().getBoard(), user);
        
        return new ResponseEntity<BoardDTO>(retVal, HttpStatus.OK);
        
    }

    private UserEntity findAndVerifyUserEntity(HttpServletRequest request) {
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.info("Board details requested by: {}", user);

        // Make sure user is in a game
        if (user.getGame() == null) {
            LOGGER.warn("User has no game: {}", user);
            return null;
        }

        if (user.getGame().getGameState() != BOARD_STARTED) {
            LOGGER.warn("Game is not running for: {}, game: {}", user, user.getGame());
            return null;
        }
        return user;
    }

    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
