package hu.bme.aut.ladder.data.entity;

import java.sql.Timestamp;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Game entity is the root entity for games, it 
 * contains the users and boards once the game actually starts
 * 
 * @author Barnabas
 */
@Entity
@Table(name = "games")
@SuppressWarnings({"PersistenceUnitPresent", "SerializableClass"})
public class GameEntity {
    
    /**
     * Unique id for the game
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long gameId;
    
    /**
     * When this game was initialized
     */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date created;
    
    /**
     * State of the game
     */
    @Column
    @Enumerated(EnumType.STRING)
    private GameState gameState;
    
    /**
     * State of the game
     */
    public static enum GameState {
        INITIALED, STARTED, FINISHED
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public java.util.Date getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
