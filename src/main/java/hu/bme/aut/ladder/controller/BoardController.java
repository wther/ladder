package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.controller.dto.BoardDTO;
import static hu.bme.aut.ladder.data.entity.GameEntity.GameState.BOARD_STARTED;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.GameService;
import hu.bme.aut.ladder.data.service.exception.GameActionNotAllowedException;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(BoardController.class);

    /**
     * URI for accessing details on the board
     */
    public static final String BOARD_DETAILS_URI = "/board";

    /**
     * URI for making a move
     */
    public static final String BOARD_ACTION_URI = "/board/action";

    /**
     * Game service used to manage the game
     */
    @Autowired
    private GameService gameService;

    /**
     * Controller to request the current state of the board
     */
    @RequestMapping(value = BOARD_DETAILS_URI, method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<BoardDTO> board(HttpServletRequest request) throws GameActionNotAllowedException {
        final UserEntity user = findAndVerifyUserEntity(request);
        if (user == null) {
            return new ResponseEntity<BoardDTO>(HttpStatus.BAD_REQUEST);
        }
        
        if(user.getGame() == null || user.getGame().getBoard() == null){
            return new ResponseEntity<BoardDTO>(HttpStatus.NOT_FOUND);
        }
        
        // Kick off any not responsive users
        gameService.handleNotResponsiveUsers(user.getGame());

        LOGGER.debug("Sending DTO for board: {}", user.getGame().getBoard());
        
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
        gameService.executeAction(user.getGame(), user.getPlayer(), action);
        
        final BoardDTO retVal = dtoFromBoard(user.getGame().getBoard(), user);
        
        return new ResponseEntity<BoardDTO>(retVal, HttpStatus.OK);
        
    }

    private UserEntity findAndVerifyUserEntity(HttpServletRequest request) {
        final UserEntity user = userService.findOrCreateUser(request.getSession().getId());
        LOGGER.debug("Board details requested by: {} with player: {}", user, user.getPlayer());

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
