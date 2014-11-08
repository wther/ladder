package hu.bme.aut.ladder.controller.dto;

/**
 * State changes are a sequence showing that a player has moved 
 * from field A to field B.
 * 
 * @author Barnabas
 */
public class StateChangeDTO {
    
    /**
     * Original position
     */
    private int from;
    
    /**
     * Position afterwards
     */
    private int to;
    
    /**
     * Sequence number of the state change, matching 
     * sequence number means simultaneous events
     */
    private int sequenceNumber;
    
    /**
     * Player who done this
     */
    private String playerColor;

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

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }    
}
