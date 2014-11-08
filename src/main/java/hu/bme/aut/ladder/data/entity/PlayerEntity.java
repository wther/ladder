package hu.bme.aut.ladder.data.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    
    /**
     * Value indicating if this is a human player or a robot
     */
    @Column
    @Enumerated(EnumType.STRING)
    private Type type;
    
    /**
     * Value indicating how the player's token should be rendered
     */
    @Column
    @Enumerated(EnumType.STRING)
    private Color color;
    
    /**
     * User's name
     */
    @Column
    private String name;

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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public enum Color {
        RED, BLUE, GREEN, YELLOW
    };
    
    public enum Type {
        HUMAN, ROBOT
    };

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.playerId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlayerEntity other = (PlayerEntity) obj;
        if (!Objects.equals(this.playerId, other.playerId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PlayerEntity{" + "playerId=" + playerId + ", position=" + position + ", type=" + type + ", color=" + color + ", name=" + name + '}';
    }
}
