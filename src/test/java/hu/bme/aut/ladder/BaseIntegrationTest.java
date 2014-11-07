package hu.bme.aut.ladder;

import hu.bme.aut.ladder.data.repository.BoardRepository;
import hu.bme.aut.ladder.data.repository.GameRepository;
import hu.bme.aut.ladder.data.repository.UserRepository;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Basic integration tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public abstract class BaseIntegrationTest {

    /**
     * User repository for setting up the environment
     */
    @Autowired 
    protected UserRepository userRepository;
    
    /**
     * Game repository for cleaning
     */
    @Autowired
    protected GameRepository gameRepository;
    
    /**
     * Board repository for cleaning up afterwards
     */
    @Autowired
    protected BoardRepository boardRepository;
    
    /**
     * Port for the Tomcat launched for unit testing
     */
    @Value("${local.server.port}")
    private int port = 0;

    /**
     * Get the port for the Tomcat launched for unit testing
     */
    public int getPort() {
        return port;
    }
    
    /**
     * Purge all data
     */
    @After
    public void tearDown(){
        boardRepository.deleteAll();
        gameRepository.deleteAll();
        userRepository.deleteAll();
    }
}
