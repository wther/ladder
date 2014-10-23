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
     * Player's horizontal position
     */
    @Column
    private int x;
    
    /**
     * Player's vertical position
     */
    @Column
    private int y;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
