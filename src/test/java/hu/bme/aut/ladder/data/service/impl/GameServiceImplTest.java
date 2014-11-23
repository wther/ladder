package hu.bme.aut.ladder.data.service.impl;

import hu.bme.aut.ladder.BaseIntegrationTest;
import hu.bme.aut.ladder.data.entity.AbilityEntity.Ability;
import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import static hu.bme.aut.ladder.data.entity.PlayerEntity.Type.ROBOT;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.GameService;
import hu.bme.aut.ladder.data.service.exception.GameActionNotAllowedException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.time.DateUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
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
    public void thatGameStartedHasNoBoardByDefault() throws GameActionNotAllowedException{
     
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("Test");
        userRepository.save(user);
        
        // Act
        GameEntity game = target.intializeGame(user);
        
        // Assert
        assertEquals(GameEntity.GameState.INITIALIZED, game.getGameState());
        assertNull("No board should be created yet", game.getBoard());
    }    
    
    /**
     * Test that when creating a new game the number robots is set to <i>0</i>
     */
    @Test
    public void thatGameStartedHasNoRobotsByDefault() throws GameActionNotAllowedException{
     
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("Test");
        userRepository.save(user);
        
        // Act
        GameEntity game = target.intializeGame(user);
        
        // Assert
        assertEquals(0, game.getNumberOfRobots());
    }   
    
    /**
     * Test when creating a game the host becomes a user and host
     */
    @Test
    public void thatHostIsAddedToTheGame() throws GameActionNotAllowedException{
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("Test");
        userRepository.save(user);
        
        // Act
        GameEntity game = target.intializeGame(user);
        
        // Assert
        assertEquals(user, game.getHost());
        assertNotNull("User should have a game", user.getGame());
        assertEquals(game.getGameId(), user.getGame().getGameId());
        assertEquals("Host should be a user", 1, userRepository.findByGame(game).size());
    }
    
    /**
     * Test that when A, B and C creates three games the {@link GameService#findGames()} 
     * returns them in order
     */
    @Test
    public void thatGamesAreReturnedInOrderByTheHostsName() throws GameActionNotAllowedException{
        
        final String[] names = {"BBB", "AAA", "CCC"};
        
        // Create three games
        for(String name : names){
            UserEntity user = new UserEntity();
            user.setName(name);

            userRepository.save(user);
            target.intializeGame(user);
        }
        
        // Act
        List<GameEntity> games = target.findActiveGamesByState(GameEntity.GameState.INITIALIZED);
        List<GameEntity> ongoingGames = target.findActiveGamesByState(GameEntity.GameState.BOARD_STARTED);
        
        // Assert
        assertEquals("AAA", games.get(0).getHost().getName());        
        assertEquals("CCC", games.get(games.size()-1).getHost().getName());        
        assertEquals("No games should be running", 0, ongoingGames.size());
    }
    
    /**
     * Test that games created more than an hour ago are deleted
     * 
     * @TODO check for actual deletion. The problem is that {@link UserEntity} may be referencing old games,
     * so they can't actually be deleted with <i>DELETE</i>
     */
    @Test
    public void thatGamesStartedLongAgoAreNotActive() throws GameActionNotAllowedException{
        UserEntity user = new UserEntity();
        user.setName("Test user");

        userRepository.save(user);
        GameEntity game = target.intializeGame(user);
        
        // Set back the date of this game with 2 hours
        game.setCreated(DateUtils.addHours(game.getCreated(), -2));
        
        // Act
        List<GameEntity> games = target.findActiveGamesByState(GameEntity.GameState.INITIALIZED);
        
        // Assert
        assertEquals(0, games.size());
    }
    
    /**
     * Test that when there is a host player who is human, he can start a game 
     * with a single robot
     */
    @Test
    public void thatGameCanBeStartedWithARobot() throws GameActionNotAllowedException{
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("Test");
        userRepository.save(user);
        
        GameEntity game = target.intializeGame(user);
        game.setNumberOfRobots(1);
        
        // Act
        target.startGame(game);
        
        // Assert
        assertEquals(GameEntity.GameState.BOARD_STARTED, game.getGameState());
        
        assertNotNull(game.getBoard());
        assertEquals("2 players should've been added", 2, game.getBoard().getPlayers().size());
        
        assertEquals(userRepository.findAll().get(0).getPlayer(), game.getBoard().getPlayers().get(0));
        assertEquals("Both players should be at 0", 0, game.getBoard().getPlayers().get(0).getPosition());
        assertEquals("Both players should be at 0", 0, game.getBoard().getPlayers().get(1).getPosition());
        assertNotEquals("Players should have different colors", 
                game.getBoard().getPlayers().get(0).getColor(),
                game.getBoard().getPlayers().get(1).getColor());
    }
    
    /**
     * Test that if the host leaves the game it ends
     */
    @Test
    public void thatIfHostLeavesTheGameEnds() throws GameActionNotAllowedException{
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("Test");
        userRepository.save(user);
        
        GameEntity game = target.intializeGame(user);
        game.setNumberOfRobots(1);
        
        // Act
        target.leave(user);
        
        // Assert
        assertNull("User shouldn't be in a game", user.getGame());
        assertEquals("Game should've been deleted", 0, gameRepository.findAll().size());
    }
    
    /**
     * Test that if the host leaves the game it ends
     */
    @Test
    public void thatIfHostLeavesRunningGameHeIsReplacedByRobot() throws GameActionNotAllowedException{
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("Test");
                
        UserEntity otherUser = new UserEntity();
        otherUser.setName("Other user");
        userRepository.save(Arrays.asList(user, otherUser));
        
        GameEntity game = target.intializeGame(user);
        
        // Other user joins
        target.join(game.getGameId(), otherUser);
        
        // Start
        target.startGame(game);
        
        // Act
        target.leave(user);
        
        // Assert
        assertNull("User shouldn't be in a game", user.getGame());
        assertEquals("Game shouldn't have been deleted", 1, gameRepository.findAll().size());
        assertEquals("User should be replaced with robot", ROBOT, gameRepository.findAll().get(0).getBoard().getPlayers().get(0).getType());
    }
    
    /**
     * Test that if I leave a game and there are only robot players remaining 
     * it ends.
     * 
     * @throws GameActionNotAllowedException 
     */
    @Test
    public void thatGameIsDeletedIfNoHumanPlayerIsLeft() throws GameActionNotAllowedException {
         // Arrange
        UserEntity user = new UserEntity();
        user.setName("Test");
        userRepository.save(user);
        
        GameEntity game = target.intializeGame(user);
        game.setNumberOfRobots(1);
        
        // Start the game
        target.startGame(game);
        
        // Act
        target.leave(user);
        
         // Assert
        assertNull("User shouldn't be in a game", user.getGame());
        assertEquals("Game should've been deleted", 0, gameRepository.findAll().size());
    }
    
    /**
     * Test that each player added to the game has earthquake ability
     * 
     * @throws GameActionNotAllowedException 
     */
    @Test
    public void thatGameCreatedWithPlayersHavingEarthquakeAbility() throws GameActionNotAllowedException {
         // Arrange
        UserEntity user = new UserEntity();
        user.setName("Test");
        userRepository.save(user);
        
        GameEntity game = target.intializeGame(user);
        game.setNumberOfRobots(1);
        
        // Start the game
        target.startGame(game);
        
        // Assert
        for(PlayerEntity player : game.getBoard().getPlayers()){
            assertEquals("Should have ability", 1, player.getAbilities().size());
            assertEquals("Unexpected ability in: " + player.getAbilities(), Ability.EARTHQUAKE, player.getAbilities().get(0).getAbility());
        }
    }
}
