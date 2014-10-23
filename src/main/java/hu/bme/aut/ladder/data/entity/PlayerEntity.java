package hu.bme.aut.ladder.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A player on the {@link BoardEntity}. This is not the same as a user.
 * 
 * @author Barnabas
 */
@Entity
@Table(name = "board_players")
@SuppressWarnings({"PersistenceUnitPresent", "SerializableClass"})
public class PlayerEntity {
    
    /**
     * Unique id for persistence
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long playerId;
    
    /**
     * Player's position
     */
    @Column
    private int position;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
