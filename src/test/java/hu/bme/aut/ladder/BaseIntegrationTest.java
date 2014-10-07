package hu.bme.aut.ladder;

import org.junit.runner.RunWith;
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
}
