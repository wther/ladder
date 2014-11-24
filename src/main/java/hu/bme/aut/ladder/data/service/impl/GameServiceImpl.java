package hu.bme.aut.ladder.data.service.impl;

import hu.bme.aut.ladder.controller.BoardController;
import hu.bme.aut.ladder.controller.dto.GameParamsDTO;
import hu.bme.aut.ladder.data.builder.BoardBuilder;
import hu.bme.aut.ladder.data.entity.AbilityEntity;
import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import static hu.bme.aut.ladder.data.entity.PlayerEntity.Type.HUMAN;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.repository.GameRepository;
import hu.bme.aut.ladder.data.repository.UserRepository;
import hu.bme.aut.ladder.data.service.GameService;
import hu.bme.aut.ladder.data.service.exception.GameActionNotAllowedException;
import hu.bme.aut.ladder.strategy.BoardStrategy;
import hu.bme.aut.ladder.strategy.exception.BoardActionNotPermitted;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service for creating and managing {@link GameEntity} instances.
 * 
 * @author Barnabas
 */
@Service
@Transactional
public class GameServiceImpl implements GameService {
    
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);
    
    /**
     * Repository of games
     */
    @Autowired 
    private UserRepository userRepository;
    
    /**
     * Repository of games
     */
    @Autowired 
    private GameRepository repository;
    
    /**
     * Board strategy used
     * @TODO Enable setting game type
     */
    @Autowired
    @Qualifier("earthquakeBoardStrategyImpl")
    private BoardStrategy boardStrategy;
    
    /**
     * Allowed sizes for boards
     */
    private static final List<Integer> ALLOWED_SIZES = Arrays.asList(8*8, 10*10, 12*12);

    /**
     * {@inheritDoc}
     */
    @Override
    public GameEntity intializeGame(UserEntity host) throws GameActionNotAllowedException {
        if(host == null){
            throw new IllegalArgumentException("host is null");
        }
        
        if(host.getGame() != null){
            throw new GameActionNotAllowedException(host.getName() + " is already in a game hosted by: " + host.getGame().getHost().getName());
        }
        
        GameEntity game = new GameEntity();
        game.setGameState(GameEntity.GameState.INITIALIZED);
        game.setCreated(new Timestamp(new Date().getTime()));
        game.setHost(host);
        
        // Set default params
        game.setNumberOfRobots(0);
        game.setBoardSize(100);
        game.setNumberOfLadders(5);
        game.setNumberOfSnakes(5);
        
        host.setGame(game);
        
        repository.save(game);
        userRepository.save(host);
        
        return game;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startGame(GameEntity game) throws GameActionNotAllowedException{
        if(game == null){
            throw new IllegalArgumentException("game is null");
        }
        
        if(game.getGameState() != GameEntity.GameState.INITIALIZED){
            throw new GameActionNotAllowedException("Expected game to have INITIALIZED state but was " + game.getGameState() + " for " + game.getGameId());
        }
        
        List<UserEntity> users = userRepository.findByGame(game);
        
        // Make sure that there are at least 2 players
        if(users.size() + game.getNumberOfRobots() < 2){
            throw new GameActionNotAllowedException("Unable to start game with only " + users.size() + " players and " + game.getNumberOfRobots() + " robots");
        }
        
        // Can't play with more than the number of colors available
        if(users.size() + game.getNumberOfRobots() > PlayerEntity.Color.values().length){
            throw new GameActionNotAllowedException("Unable to start game, the maximum number of players allowed is: " + PlayerEntity.Color.values().length);
        }
        
        // We're OK to take off
        BoardEntity board = new BoardBuilder()
                            .withSize(game.getBoardSize())
                            .withLadders(game.getNumberOfLadders())
                            .withSnakes(game.getNumberOfSnakes())
                            .build(boardStrategy);
        
        // Add players
        int colorIndex = 0;
        for(UserEntity user : users){
            
            // Make sure that this player is ready
            if(!user.equals(game.getHost()) && !Boolean.TRUE.equals(user.getReady())){
                throw new GameActionNotAllowedException("Can't start game, " + user.getName() + " isn't ready yet");
            }
            
            PlayerEntity player = new PlayerEntity();
            player.setPosition(0);
            player.setColor(PlayerEntity.Color.values()[colorIndex++]);
            player.setType(PlayerEntity.Type.HUMAN);
            player.setName(user.getName());
            
            user.setPlayer(player);
            
            // We need to save the user (and the player) here
            // so it becomes a part of the persistence context
            // and doesn't get saved again when the board is saved
            userRepository.save(user);            
            board.getPlayers().add(user.getPlayer());
        }
        
        // Set the next player on the board
        board.setNextPlayer(board.getPlayers().get(0));
        
        // Add robots
        for(int i = 0; i < game.getNumberOfRobots(); i++){
            PlayerEntity player = new PlayerEntity();
            player.setPosition(0);
            player.setColor(PlayerEntity.Color.values()[colorIndex++]);
            player.setType(PlayerEntity.Type.ROBOT);
            player.setName("Robot");
            board.getPlayers().add(player);
        }
        
        // Add abilities to players
        for(PlayerEntity player : board.getPlayers()){
            List<AbilityEntity> abilities = new ArrayList<AbilityEntity>();
            for(AbilityEntity ability : boardStrategy.getInitialAbilityKit()){
                // Clone the ability
                AbilityEntity clone = new AbilityEntity();
                clone.setAbility(ability.getAbility());
                clone.setUsesLeft(ability.getUsesLeft());
                abilities.add(clone);
            }
            player.setAbilities(abilities);
        }
        
        game.setBoard(board);
        game.setGameState(GameEntity.GameState.BOARD_STARTED);
        
        // Persist in the DB
        repository.save(game);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameEntity findGameById(Long id){
        return repository.findOne(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GameEntity> findActiveGamesByState(GameEntity.GameState gameState) {
                
        // Returns subset of all games
        return repository.findByGameStateOrderedByHostNameAfterDate(gameState, DateUtils.addHours(new Date(), -1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameEntity join(Long gameId, UserEntity user) throws GameActionNotAllowedException {
        GameEntity game = repository.findOne(gameId);
        if(game == null){
            throw new GameActionNotAllowedException("Game with gameId not found: " + gameId);
        }
        
        if(user == null){
            throw new IllegalArgumentException("user is null");
        }
        
        if(user.getGame() != null && !user.getGame().getGameId().equals(gameId)){
            throw new GameActionNotAllowedException(user.getName() + " is already in a game hosted by: " + user.getGame().getHost().getName());
        }
        
        user.setGame(game);
        userRepository.save(user);
        return game;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void leave(UserEntity user) throws GameActionNotAllowedException {
        
        if(user.getGame() == null){
            // Do nothing
            return;
        }
        
        // If game hasn't started yet
        if(user.getGame().getGameState() == GameEntity.GameState.INITIALIZED){
            
            final GameEntity game = user.getGame();
            
            // Kick off every player if I'm the host
            if(game.getHost().equals(user)){
                for(UserEntity item : userRepository.findByGame(game)){
                    item.setGame(null);
                    userRepository.save(item);
                }            
                
                // And delete game entirely
                repository.delete(game);
                
            } else {
                user.setGame(null);
                userRepository.save(user);
            }
            
        } else if(user.getGame().getGameState() == GameEntity.GameState.BOARD_STARTED){
            
            final GameEntity game = user.getGame();
            
            // Replace user with robot
            user.getPlayer().setType(PlayerEntity.Type.ROBOT);
            
            // Was this player on turn? In that case we have to resolve,
            // the board, by forcing the robot of this player to take a turn
            if(game.getBoard().getNextPlayer().equals(user.getPlayer())){
                try {
                    boardStrategy.resolveBoard(game.getBoard());
                } catch (BoardActionNotPermitted ex) {
                    throw new GameActionNotAllowedException("Couldn't leave the game", ex);
                }
            }
            
            // Detach user from game
            user.setPlayer(null);
            user.setGame(null);
            userRepository.save(user);
            
            // Are there any human players in the game?
            boolean foundHuman = false;
            for(PlayerEntity player : game.getBoard().getPlayers()){
                if(player.getType().equals(HUMAN)){
                    foundHuman = true;
                    break;
                }
            }
            
            // If no more humans left, then delete this game
            if(!foundHuman){
                repository.delete(game);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserEntity> findUsersInGame(GameEntity game) {
        return userRepository.findByGame(game);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGameParams(GameEntity game, GameParamsDTO params) {
        
        if(params.getRobots() < 0){
            throw new IllegalArgumentException("Number of robots has to be 0 or higher");
        }
        
        if(params.getRobots() > 3){
            throw new IllegalArgumentException("Number of robots has to be between 0 and 3");
        }
        
        if(params.getLadders() < 0){
            throw new IllegalArgumentException("Number of ladders has to higher than or equal to 0, " + params.getLadders() + " isn't");
        }
        
        if(params.getSnakes() < 0){
            throw new IllegalArgumentException("Number of snakes has to higher than or equal to 0, " + params.getSnakes() + " isn't");
        }
        
        if(!ALLOWED_SIZES.contains(params.getSize())){
            throw new IllegalArgumentException("Unexpected board size " + params.getSize() + " allowed values are " + ALLOWED_SIZES.toString());
        }
        
        game.setBoardSize(params.getSize());
        game.setNumberOfRobots(params.getRobots());
        game.setNumberOfSnakes(params.getSnakes());
        game.setNumberOfLadders(params.getLadders());
        
        repository.save(game);
    }

    /**
     * {@inheritDoc}
     * 
     * @param game 
     */
    @Override
    public void handleNotResponsiveUsers(GameEntity game) throws GameActionNotAllowedException {
        if(game == null){
            throw new IllegalArgumentException("game is null");
        }
        
        if(game.getGameState() != GameEntity.GameState.BOARD_STARTED){
            // Do nothing
            return;
        }
        
        final int numberOfMinutesBeforeKickoff = 1;
        
        // If next player should've done something for a long time, kick him off
        final java.util.Date lastAction = game.getBoard().getNextPlayerAssignedAt();
        if(lastAction != null && lastAction.before(DateUtils.addMinutes(new Date(), -numberOfMinutesBeforeKickoff))){
            final List<UserEntity> usersInGame = userRepository.findByGame(game);
            
            // If there is only one human player, let him be idle for as long as he wishes
            if(usersInGame.size() == 1){
                // Do nothing
                return;
            }
            
            for(UserEntity user : usersInGame){
                // If this is the inactive player
                if(user.getPlayer().equals(game.getBoard().getNextPlayer())){
                    
                    LOGGER.info("Kicking off {} from {}'s game because it hasn't been responding since {}", user.getName(), game.getHost().getName(), lastAction);
                    leave(user);
                    return;
                }
            }
        }
    }
}
