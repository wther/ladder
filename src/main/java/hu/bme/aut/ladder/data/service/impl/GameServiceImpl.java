package hu.bme.aut.ladder.data.service.impl;

import hu.bme.aut.ladder.data.builder.BoardBuilder;
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
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.apache.commons.lang.time.DateUtils;
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
        game.setNumberOfRobots(0);
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
        BoardEntity board = new BoardBuilder().build(boardStrategy);
        
        // @TODO
        // Randomize the order of the players
        
        // Add players
        int colorIndex = 0;
        for(UserEntity user : users){
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
        
        // Add robots
        for(int i = 0; i < game.getNumberOfRobots(); i++){
            PlayerEntity player = new PlayerEntity();
            player.setPosition(0);
            player.setColor(PlayerEntity.Color.values()[colorIndex++]);
            player.setType(PlayerEntity.Type.ROBOT);
            player.setName("Robot");
            board.getPlayers().add(player);
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
    public void leave(UserEntity user) {
        
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
    public void setNumberOfRobots(GameEntity game, int numberOfRobots) {
        if(numberOfRobots < 0){
            throw new IllegalArgumentException("Number of robots has to be 0 or higher");
        }
        game.setNumberOfRobots(numberOfRobots);
        repository.save(game);
    }
}
