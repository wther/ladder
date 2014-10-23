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
     * Player's position before
     */
    @Column
    private int beforeAt;
    
    /**
     * Player's position after
     */
    @Column
    private int afterAt;
       
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

    public int getBeforeAt() {
        return beforeAt;
    }

    public void setBeforeAt(int beforeAt) {
        this.beforeAt = beforeAt;
    }

    public int getAfterAt() {
        return afterAt;
    }

    public void setAfterAt(int afterAt) {
        this.afterAt = afterAt;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }
}
