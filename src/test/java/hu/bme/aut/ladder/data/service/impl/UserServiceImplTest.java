package hu.bme.aut.ladder.data.service.impl;

import hu.bme.aut.ladder.BaseIntegrationTest;
import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.service.UserService;
import hu.bme.aut.ladder.data.service.exception.UserActionNotAllowedException;
import org.apache.commons.lang.StringUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration tests for the {@link UserServiceImpl}
 * 
 * @author Barnabas
 */
public class UserServiceImplTest extends BaseIntegrationTest {
    
    /**
     * Target being tested
     */
    @Autowired
    private UserService target;
    
    /**
     * Test that user is added to the database 
     */
    @Test
    public void thatCreatesUserIfNotInDB(){
        
        // Arrange
        final String sessionId = "ABC123";
        
        // Act
        UserEntity user = target.findOrCreateUser(sessionId);
        
        // Assert
        assertEquals(1, target.findAll().size());
    }

    /**
     * Test that when a user is already in the <code>users</code> table
     * that one is returned
     */
    @Test
    public void thatUserIsFoundBySessionId(){
        
        // Arrange
        final String sessionId = "ABC123";
        final String userName = "John";
        
        UserEntity entity = new UserEntity();
        entity.setName(userName);
        entity.setSessionId(sessionId);
        
        userRepository.save(entity);
        
        // Act
        UserEntity result = target.findOrCreateUser(sessionId);
        
        // Assert
        assertEquals(userName, result.getName());
    }
    
    /**
     * Playing this scenario:
     * <ul>
     *  <li>User arrives on the page</li>
     *  <li>User joins a game</li>
     *  <li>That game starts</li>
     * </ul>
     * 
     * Verify that the user is redirected to the right page 
     * all the time
     */
    @Test
    public void thatUserIsRedirectedToTheRightPage(){
                
        // Arrange
        final String sessionId = "DEF456";
        final String userName = "Johnny";
        
        UserEntity entity = new UserEntity();
        entity.setName(userName);
        entity.setSessionId(sessionId);
        
        userRepository.save(entity);
        
        // Arriving for the first time
        assertEquals(UserService.LOBBY_PAGE, target.getUserPage(entity));
        
        // Joining a room
        GameEntity game = mock(GameEntity.class); 
        when(game.getGameState()).thenReturn(GameEntity.GameState.INITIALIZED);
        
        entity.setGame(game);
        
        // In the game
        assertEquals(UserService.ROOM_PAGE, target.getUserPage(entity));
        
        // Let that game start
        when(game.getGameState()).thenReturn(GameEntity.GameState.BOARD_STARTED);
        assertEquals(UserService.GAME_PAGE, target.getUserPage(entity));
    }
    
    
    /**
     * Test that there is a limit on the length of the user's name
     */
    @Test(expected = UserActionNotAllowedException.class)
    public void thatUserCantHaveTooLongName() throws UserActionNotAllowedException{
       
        // Arrange
        final String sessionId = "DEF456";
        final String userName = "Johnny";
        
        UserEntity entity = new UserEntity();
        entity.setName(userName);
        entity.setSessionId(sessionId);
        
        userRepository.save(entity);
        
        // Act
        final String someThingLong = StringUtils.repeat("abc123", 50);
        target.setNameForUser(entity, someThingLong);        
    }
    
    /**
     * Test that user cant set its name to that of an existing user
     */
    @Test(expected = UserActionNotAllowedException.class)
    public void thatTwoUsersCantHaveTheSameName() throws UserActionNotAllowedException{
     
        // Arrange
        final String userName = "Johnny";
        
        UserEntity entity = new UserEntity();
        entity.setName(userName);
        entity.setSessionId("DEF456");
        userRepository.save(entity);
        
        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setSessionId("FFFXXX");
        newUserEntity.setName("ABC123");
        userRepository.save(newUserEntity);
        
        // Act
        target.setNameForUser(newUserEntity, userName);
    }
}
