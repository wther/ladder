package hu.bme.aut.ladder.data.entity;

import hu.bme.aut.ladder.DataConfig;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * Entity for boards.
 * 
 * The {@link DataConfig} is set up in manner, that entities in this package are
 * scanned, and DDL is generated appropriately to the annotations of this class
 * in the H2Base DB.
 * 
 * This means that if you change the entities, the database schema will change
 * too.
 * 
 * @author Barnabas
 */
@Entity
@Table(name = "boards")
@SuppressWarnings({"PersistenceUnitPresent", "SerializableClass"})
public class BoardEntity {

    /**
     * Unique id for the board
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long boardId;

    /**
     * Size of the board
     */
    @Column
    private int boardSize;
    /**
     * Snakes &amp; Ladders on the board
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TunnelEntity> tunnels;
    
    /**
     * Players on the board
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PlayerEntity> players;
    
    /**
     * Value indicating who is the next player
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private PlayerEntity nextPlayer;
    
    /**
     * Player state changes on board, e.g. moves
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy(value = "sequenceNumber ASC")
    private List<StateChangeEntity> stateChanges;

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public List<TunnelEntity> getTunnels() {
        return tunnels;
    }

    public void setTunnels(List<TunnelEntity> tunnels) {
        this.tunnels = tunnels;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerEntity> players) {
        this.players = players;
    }

    public List<StateChangeEntity> getStateChanges() {
        return stateChanges;
    }

    public void setStateChanges(List<StateChangeEntity> stateChanges) {
        this.stateChanges = stateChanges;
    }

    public PlayerEntity getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(PlayerEntity nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    @Override
    public String toString() {
        return "BoardEntity{" + "boardId=" + boardId + ", boardSize=" + boardSize + ", tunnels=" + tunnels + ", players=" + players + ", stateChanges=" + stateChanges + '}';
    }
}
