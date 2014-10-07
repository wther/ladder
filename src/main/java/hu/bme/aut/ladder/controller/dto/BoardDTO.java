package hu.bme.aut.ladder.controller.dto;

import java.util.List;

/**
 * DTO for sending Board data to the client side
 */
public class BoardDTO {

    /**
     * Width of the board
     */
    private int width;
    
    /**
     * Height of the board
     */
    private int height;
    
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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
}
