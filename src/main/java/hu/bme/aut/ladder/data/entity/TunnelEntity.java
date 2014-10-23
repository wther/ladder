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
 * A tunnel can be a snake or a ladder on a board
 * 
 * @author Barnabas
 */
@Entity
@Table(name = "board_tunnels")
@SuppressWarnings({"PersistenceUnitPresent", "SerializableClass"})
public class TunnelEntity {
    
    /**
     * Unique id for persistence
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long tunnelId;
    
    /**
     * Coordinate of the start
     */
    @Column
    private int fromField;
    
    /**
     * Vertical coordinate of the start
     */
    @Column
    private int toField;
    
    
    /**
     * Type of the enumeration
     */
    @Column
    @Enumerated(EnumType.STRING)
    private Type type;
    
    /**
     * Type of ladder
     */
    public static enum Type {
        /**
         * Snakes are bad
         */
        SNAKE, 
        
        /**
         * Ladders are good
         */
        LADDER
    }

    public Long getTunnelId() {
        return tunnelId;
    }

    public void setTunnelId(Long tunnelId) {
        this.tunnelId = tunnelId;
    }

    public int getFromField() {
        return fromField;
    }

    public void setFromField(int fromField) {
        this.fromField = fromField;
    }

    public int getToField() {
        return toField;
    }

    public void setToField(int toField) {
        this.toField = toField;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
