package hu.bme.aut.ladder.data.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
    
    /**
     * Value indicating that user is done playing the game, reached the last field on the board
     */
    @Column
    private boolean finishedPlaying = false;
    
    /**
     * Place at which this player finished
     */
    @Column
    private int finishedAtPlace = 0;
    
    /**
     * Abilities available for this user
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AbilityEntity> abilities;

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

    public boolean isFinishedPlaying() {
        return finishedPlaying;
    }

    public void setFinishedPlaying(boolean finishedPlaying) {
        this.finishedPlaying = finishedPlaying;
    }

    public int getFinishedAtPlace() {
        return finishedAtPlace;
    }

    public void setFinishedAtPlace(int finishedAtPlace) {
        this.finishedAtPlace = finishedAtPlace;
    }

    public List<AbilityEntity> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<AbilityEntity> abilities) {
        this.abilities = abilities;
    }
    
    public enum Color {
        RED, BLUE, GREEN, YELLOW
    }
    
    public enum Type {
        HUMAN, ROBOT
    }

    @Override
    public String toString() {
        return "PlayerEntity{" + "playerId=" + playerId + ", position=" + position + ", type=" + type + ", color=" + color + ", name=" + name + '}';
    }
}
