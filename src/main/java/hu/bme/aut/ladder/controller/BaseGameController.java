package hu.bme.aut.ladder.controller;

import hu.bme.aut.ladder.controller.dto.BoardDTO;
import hu.bme.aut.ladder.controller.dto.GameDTO;
import hu.bme.aut.ladder.controller.dto.PlayerDTO;
import hu.bme.aut.ladder.controller.dto.StateChangeDTO;
import hu.bme.aut.ladder.controller.dto.TunnelDTO;
import hu.bme.aut.ladder.controller.dto.UserDTO;
import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.data.entity.StateChangeEntity;
import hu.bme.aut.ladder.data.entity.TunnelEntity;
import static hu.bme.aut.ladder.data.entity.TunnelEntity.Type.SNAKE;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.GameService;
import hu.bme.aut.ladder.data.service.UserService;
import hu.bme.aut.ladder.data.service.exception.GameActionNotAllowedException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Base class for controllers managing {@link GameEntity} instances.
 * 
 * @author Barnabas
 */
public abstract class BaseGameController {
    
    /**
     * Service used
     */
    @Autowired
    protected GameService service;
    
    /**
     * User service managing the session
     */
    @Autowired
    protected UserService userService;
    
    /**
     * Error handler which reports the problem to the client
     * @param exception 
     */
    @ExceptionHandler(GameActionNotAllowedException.class)
    public ResponseEntity<String> handleException(GameActionNotAllowedException exception){
        logger().warn("Request failed", exception);
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Child rooter
     * @return 
     */
    protected abstract Logger logger();
    
    /**
     * Converts entity to DTO
     */
    protected GameDTO dtoFromGame(GameEntity game, UserEntity user){
        GameDTO dto = new GameDTO();
        dto.setGameId(game.getGameId());
        dto.setCreated(game.getCreated());
        dto.setHost(game.getHost().getName());
        dto.setUser(user.getName());
        dto.setNumberOfRobots(game.getNumberOfRobots());

        boolean found = false;

        List<UserDTO> players = new ArrayList<UserDTO>();
        for(UserEntity gameUser : service.findUsersInGame(game)){
            UserDTO userDto = new UserDTO();
            userDto.setName(gameUser.getName());
            userDto.setReady(Boolean.TRUE.equals(gameUser.getReady()));
            userDto.setUserId(gameUser.getUserId());
            players.add(userDto);
            
            if(gameUser.equals(user)){
                found = true;
            }
        }

        dto.setAllPlayers(players);
        dto.setUserInGame(found);
        
        return dto;
    }
    
    /**
     * Converts entity to DTO
     * @param board
     * @return 
     */
    protected BoardDTO dtoFromBoard(BoardEntity board, UserEntity user){
        BoardDTO dto = new BoardDTO();
        dto.setSize(board.getBoardSize());
        
        // Add snakes and ladders
        dto.setLadders(new ArrayList<TunnelDTO>());
        dto.setSnakes(new ArrayList<TunnelDTO>());
        
        for(TunnelEntity tunnel : board.getTunnels()){
            TunnelDTO tunnelDTO = new TunnelDTO();
            tunnelDTO.setFrom(tunnel.getFromField());
            tunnelDTO.setTo(tunnel.getToField());
            
            // Add to the respective list
            (tunnel.getType() == SNAKE ? dto.getSnakes() : dto.getLadders()).add(tunnelDTO);
        }
        
        // Add players
        List<PlayerDTO> playerDTOList = new ArrayList<PlayerDTO>();
        for(PlayerEntity player : board.getPlayers()){
            PlayerDTO playerDTO = new PlayerDTO();
            playerDTO.setColor(player.getColor().name());
            playerDTO.setPosition(player.getPosition());
            playerDTO.setName(player.getName());
            playerDTO.setIsMe(player.equals(user.getPlayer()));
            playerDTO.setIsFinished(player.isFinishedPlaying());
            playerDTO.setFinishedAtPlace(player.getFinishedAtPlace());
            playerDTO.setType(player.getType().name());
            playerDTOList.add(playerDTO);
            
            // Is this the next player (if there is any)
            if(board.getNextPlayer() != null && player.getPlayerId().equals(board.getNextPlayer().getPlayerId())){
                dto.setNextPlayer(playerDTO);
            }            
        }
        dto.setPlayers(playerDTOList);
        
        // Add state changes
        List<StateChangeDTO> stateChangeDTOList = new ArrayList<StateChangeDTO>();
        for(StateChangeEntity stateChange : board.getStateChanges()){
            StateChangeDTO stateChangeDTO = new StateChangeDTO();
            stateChangeDTO.setFrom(stateChange.getBeforeAt());
            stateChangeDTO.setTo(stateChange.getAfterAt());
            stateChangeDTO.setSequenceNumber(stateChange.getSequenceNumber());
            stateChangeDTO.setPlayerColor(stateChange.getPlayer().getColor().name());
            stateChangeDTOList.add(stateChangeDTO);
        }
        dto.setStateChanges(stateChangeDTOList);
        
        return dto;
    }
}
