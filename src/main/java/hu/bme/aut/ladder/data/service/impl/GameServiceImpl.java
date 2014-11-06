package hu.bme.aut.ladder.data.service.impl;

import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.repository.GameRepository;
import hu.bme.aut.ladder.data.service.GameService;
import java.util.Arrays;
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
    private GameRepository repository;

    /**
     * {@inheritDoc}
     * @param host 
     */
    @Override
    public GameEntity startGame(UserEntity host) {
        GameEntity game = new GameEntity();
        game.setGameState(GameEntity.GameState.STARTED);
        
        game.setUsers(Arrays.asList(host));
        game.setHost(host);
        
        repository.save(game);
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
    public GameEntity join(Long gameId, UserEntity user) {
        GameEntity game = repository.findOne(gameId);
        if(game == null){
            throw new IllegalArgumentException("Game with gameId not found: " + gameId);
        }
        
        if(!game.getUsers().contains(user)){
            game.getUsers().add(user);
        }
        repository.save(game);
        return game;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameEntity leave(Long gameId, UserEntity user) {
        GameEntity game = repository.findOne(gameId);
        if(game == null){
            throw new IllegalArgumentException("Game with gameId not found: " + gameId);
        }
        
        if(game.getUsers().contains(user)){
            game.getUsers().remove(user);
        }
        repository.save(game);
        return game;
    }
}
