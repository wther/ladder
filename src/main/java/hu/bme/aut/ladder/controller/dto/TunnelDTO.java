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
    private FieldDTO from;
    
    /**
     * Field where the tunnel ends
     */
    private FieldDTO to;
    
    public FieldDTO getFrom() {
        return from;
    }

    public void setFrom(FieldDTO from) {
        this.from = from;
    }

    public FieldDTO getTo() {
        return to;
    }

    public void setTo(FieldDTO to) {
        this.to = to;
    }
    
    /**
     * Factory like method to create a tunnel
     */
    public static TunnelDTO forCoordinates(int x0, int y0, int x1, int y1){
        FieldDTO from = new FieldDTO();
        from.setX(x0);
        from.setY(y0);
        
        FieldDTO to = new FieldDTO();
        to.setX(x1);
        to.setY(y1);
        
        TunnelDTO tunnel = new TunnelDTO();
        tunnel.setFrom(from);
        tunnel.setTo(to);
        return tunnel;
    }
}
