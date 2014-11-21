package hu.bme.aut.ladder.data.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
    
    /**
     * Value indicating that player is ready to start playing or not
     */
    @Column
    private Boolean ready;
    
    /**
     * The player this user represents
     */
    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private PlayerEntity player;

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

    public PlayerEntity getPlayer() {
        return player;
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }

    public Boolean getReady() {
        return ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }

    @Override
    public String toString() {
        return "UserEntity{" + "userId=" + userId + ", sessionId=" + sessionId + ", name=" + name + '}';
    }
}
