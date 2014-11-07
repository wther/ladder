package hu.bme.aut.ladder.data.service.impl;

import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.repository.GameRepository;
import hu.bme.aut.ladder.data.repository.UserRepository;
import hu.bme.aut.ladder.data.service.GameService;
import hu.bme.aut.ladder.data.service.exception.GameActionNotAllowedException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
     * {@inheritDoc}
     * @param host 
     * @throws hu.bme.aut.ladder.data.service.exception.GameActionNotAllowedException 
     */
    @Override
    public GameEntity startGame(UserEntity host) throws GameActionNotAllowedException {
        if(host == null){
            throw new IllegalArgumentException("host is null");
        }
        
        if(host.getGame() != null){
            throw new GameActionNotAllowedException(host.getName() + " is already in a game hosted by: " + host.getGame().getHost().getName());
        }
        
        GameEntity game = new GameEntity();
        game.setGameState(GameEntity.GameState.STARTED);
        game.setCreated(new Timestamp(new Date().getTime()));
        game.setHost(host);
        host.setGame(game);
        
        repository.save(game);
        userRepository.save(host);
        
        return game;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GameEntity> findGames() {
        return repository.findAllOrderedByHostName();
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
    public List<GameEntity> findGamesByState(GameEntity.GameState gameState) {
        return repository.findByGameStateOrderedByHostName(gameState);
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
        user.setGame(null);
        userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserEntity> findUsersInGame(GameEntity game) {
        return userRepository.findByGame(game);
    }
}
