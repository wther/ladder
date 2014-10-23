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
     * Horizontal coordinate of the start
     */
    @Column
    private int fromX;
    
    /**
     * Vertical coordinate of the start
     */
    @Column
    private int fromY;
    
    /**
     * Horizontal coordinate of the end
     */
    @Column
    private int toX;
    
    /**
     * Vertical coordinate of the end
     */
    @Column
    private int toY;
    
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
    };

    public Long getTunnelId() {
        return tunnelId;
    }

    public void setTunnelId(Long tunnelId) {
        this.tunnelId = tunnelId;
    }

    public int getFromX() {
        return fromX;
    }

    public void setFromX(int fromX) {
        this.fromX = fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public void setFromY(int fromY) {
        this.fromY = fromY;
    }

    public int getToX() {
        return toX;
    }

    public void setToX(int toX) {
        this.toX = toX;
    }

    public int getToY() {
        return toY;
    }

    public void setToY(int toY) {
        this.toY = toY;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
