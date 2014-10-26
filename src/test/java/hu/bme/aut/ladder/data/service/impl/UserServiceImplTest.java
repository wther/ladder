package hu.bme.aut.ladder.data.service.impl;

import hu.bme.aut.ladder.BaseIntegrationTest;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.repository.UserRepository;
import hu.bme.aut.ladder.data.service.UserService;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
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
     * User repository
     */
    @Autowired
    private UserRepository repository;
    
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
        
        repository.save(entity);
        
        // Act
        UserEntity result = target.findOrCreateUser(sessionId);
        
        // Assert
        assertEquals(userName, result.getName());
    }
    
    /**
     * Cleans up the <code>users</code> table
     */
    @After
    public void tearDown(){
        repository.deleteAll();
    }
}
