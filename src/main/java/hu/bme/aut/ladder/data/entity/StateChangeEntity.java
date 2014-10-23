package hu.bme.aut.ladder.data.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * State changes of the board
 * 
 * @author Barnabas
 */
@Entity
@Table(name = "board_state_change")
@SuppressWarnings({"PersistenceUnitPresent", "SerializableClass"})
public class StateChangeEntity {
    
    /**
     * Unique id for the board
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long stateChangeId;
    
    /**
     * State changes are in order
     */
    @Column
    private int sequenceNumber;
    
    /**
     * Player's X position before
     */
    @Column
    private int beforeX;
    
    /**
     * Player's Y position before
     */
    @Column
    private int beforeY;
    
    /**
     * Player's X position after
     */
    @Column
    private int afterX;
    
    /**
     * Player's Y position after
     */
    @Column
    private int afterY;
    
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private PlayerEntity player;

    public Long getStateChangeId() {
        return stateChangeId;
    }

    public void setStateChangeId(Long stateChangeId) {
        this.stateChangeId = stateChangeId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getBeforeX() {
        return beforeX;
    }

    public void setBeforeX(int beforeX) {
        this.beforeX = beforeX;
    }

    public int getBeforeY() {
        return beforeY;
    }

    public void setBeforeY(int beforeY) {
        this.beforeY = beforeY;
    }

    public int getAfterX() {
        return afterX;
    }

    public void setAfterX(int afterX) {
        this.afterX = afterX;
    }

    public int getAfterY() {
        return afterY;
    }

    public void setAfterY(int afterY) {
        this.afterY = afterY;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }
}
