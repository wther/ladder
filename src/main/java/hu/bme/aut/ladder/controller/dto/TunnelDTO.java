package hu.bme.aut.ladder.controller.dto;

/**
 * Tunnel between two fields on the board
 * 
 * @author Barnabas
 */
public class TunnelDTO {
    
    /**
     * Field where the tunnel starts
     */
    private int from;
    
    /**
     * Field where the tunnel ends
     */
    private int to;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }
    
    
}
