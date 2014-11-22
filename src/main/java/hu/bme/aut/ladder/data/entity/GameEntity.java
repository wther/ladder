package hu.bme.aut.ladder.data.entity;

import java.sql.Timestamp;
import java.util.Objects;
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
     * Which is the board associated with this game
     * 
     * <i>null</i> as long as the game has not yet started
     */
    @OneToOne(cascade = CascadeType.ALL, optional = true)
    private BoardEntity board;
    
    /**
     * This game's host
     * 
     */
    @OneToOne(cascade = CascadeType.REFRESH, optional = false)
    private UserEntity host;
    
    /**
     * Number of robots in this game, fixed after board is initialized
     */
    @Column
    private int numberOfRobots;
            
    /**
     * State of the game
     */
    public static enum GameState {
        INITIALIZED, BOARD_STARTED
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

    public void setCreated(java.util.Date created) {
        this.created = created;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public BoardEntity getBoard() {
        return board;
    }

    public void setBoard(BoardEntity board) {
        this.board = board;
    }

    public UserEntity getHost() {
        return host;
    }

    public void setHost(UserEntity host) {
        this.host = host;
    }

    public int getNumberOfRobots() {
        return numberOfRobots;
    }

    public void setNumberOfRobots(int numberOfRobots) {
        this.numberOfRobots = numberOfRobots;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameEntity other = (GameEntity) obj;
        if (!Objects.equals(this.gameId, other.gameId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GameEntity{" + "gameId=" + gameId + ", created=" + created + ", gameState=" + gameState + ", host=" + host + '}';
    }
}
