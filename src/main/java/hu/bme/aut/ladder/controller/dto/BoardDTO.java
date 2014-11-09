package hu.bme.aut.ladder.controller.dto;

import java.util.List;

/**
 * DTO for sending Board data to the client side
 */
public class BoardDTO {

    /**
     * Size of the board
     */
    private int size;
    
    /**
     * Snakes on the board
     */
    private List<TunnelDTO> snakes;
    
    /**
     * Ladders on the board
     */
    private List<TunnelDTO> ladders;
    
    /**
     * Players on the board
     */
    private List<PlayerDTO> players;
    
    /**
     * State changes
     */
    private List<StateChangeDTO> stateChanges;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<TunnelDTO> getSnakes() {
        return snakes;
    }

    public void setSnakes(List<TunnelDTO> snakes) {
        this.snakes = snakes;
    }

    public List<TunnelDTO> getLadders() {
        return ladders;
    }

    public void setLadders(List<TunnelDTO> ladders) {
        this.ladders = ladders;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDTO> players) {
        this.players = players;
    }

    public List<StateChangeDTO> getStateChanges() {
        return stateChanges;
    }

    public void setStateChanges(List<StateChangeDTO> stateChanges) {
        this.stateChanges = stateChanges;
    }
}
