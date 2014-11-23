package hu.bme.aut.ladder.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Special ability usable by players
 * 
 * @author Barnabas
 */
@Entity
@Table(name = "board_players_abilities")
@SuppressWarnings({"PersistenceUnitPresent", "SerializableClass"})
public class AbilityEntity {
    
    /**
     * Unique id for persistence
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long abilityId;
    
    /**
     * Name of that ability, e.g. <i>EARTHQUAKE</i>
     */
    @Column
    @Enumerated(EnumType.STRING)
    private Ability ability;
    
    /**
     * Number of uses left, e.g. 2 more earthquakes for a player
     */
    @Column
    private int usesLeft;

    public Long getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(Long abilityId) {
        this.abilityId = abilityId;
    }

    public int getUsesLeft() {
        return usesLeft;
    }

    public void setUsesLeft(int usesLeft) {
        this.usesLeft = usesLeft;
    }

    public Ability getAbility() {
        return ability;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
    public enum Ability {
        EARTHQUAKE
    };

    @Override
    public String toString() {
        return "AbilityEntity{" + "abilityId=" + abilityId + ", ability=" + ability + ", usesLeft=" + usesLeft + '}';
    }
}
