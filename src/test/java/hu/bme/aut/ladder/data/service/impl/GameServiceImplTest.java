package hu.bme.aut.ladder.data.service.impl;

import hu.bme.aut.ladder.BaseIntegrationTest;
import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.GameService;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration tests for the {@link GameServiceImpl} class
 *
 * @author Barnabas
 */
public class GameServiceImplTest extends BaseIntegrationTest {
 
    /**
     * Target under test
     */
    @Autowired
    private GameService target;
    
    /**
     * Test that when creating a new game no board is set yet
     */
    @Test
    public void thatGameStartedHasNoBoardByDefault(){
     
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("Test");
        userRepository.save(user);
        
        // Act
        GameEntity game = target.startGame(user);
        
        // Assert
        assertEquals(GameEntity.GameState.STARTED, game.getGameState());
        assertNull("No board should be created yet", game.getBoard());
    }    
    
    /**
     * Test when creating a game the host becomes a user and host
     */
    @Test
    public void thatHostIsAddedToTheGame(){
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("Test");
        userRepository.save(user);
        
        // Act
        GameEntity game = target.startGame(user);
        
        // Assert
        assertEquals(user, game.getHost());
        assertEquals("Host should be a user", 1, game.getUsers().size());
        assertEquals("Host should be a user", user, game.getUsers().get(0));
    }
    
    /**
     * Test that when A, B and C creates three games the {@link GameService#findGames()} 
     * returns them in order
     */
    @Test
    public void thatGamesAreReturnedInOrderByTheHostsName(){
        
        final String[] names = {"BBB", "AAA", "CCC"};
        
        // Create three games
        for(String name : names){
            UserEntity user = new UserEntity();
            user.setName(name);

            userRepository.save(user);
            target.startGame(user);
        }
        
        // Act
        List<GameEntity> games = target.findGames();
        List<GameEntity> ongoingGames = target.findGamesByState(GameEntity.GameState.INITIALIZED);
        
        // Assert
        assertEquals("AAA", games.get(0).getHost().getName());
        assertEquals("CCC", games.get(games.size()-1).getHost().getName());
        
        assertEquals("No games should be running", 0, ongoingGames.size());
    }    
}
