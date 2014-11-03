package hu.bme.aut.ladder.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;

/**
 * The user entity represents the a browser session
 * 
 * @author Barnabas
 */

@Entity
@Table(name = "users")
@SuppressWarnings({"PersistenceUnitPresent", "SerializableClass"})
public class UserEntity {
    
    /**
     * Unique id for the user's session
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long userId;
    
    /**
     * Session ID known from {@link HttpServletRequest}
     */
    @Column(unique = true)
    private String sessionId;
    
    /**
     * Name of user, e.g. <i>John</i>
     */
    @Column
    private String name;
    
    /**
     * Game this user is joined in
     */
    @ManyToOne
    private GameEntity game;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameEntity getGame() {
        return game;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }
}