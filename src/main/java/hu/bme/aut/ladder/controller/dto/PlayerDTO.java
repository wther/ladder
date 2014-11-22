package hu.bme.aut.ladder.controller.dto;

/**
 * DTO for players on the board
 * 
 * @author Barnabas
 */
public class PlayerDTO {

    /**
     * Player's position
     */
    private int position;
    
    /**
     * Player's name
     */
    private String name;
    
    /**
     * Player's color
     */
    private String color;
    
    /**
     * Value indicating if this is me
     */
    private boolean isMe;
    
    /**
     * Can be either <i>HUMAN</i> or <i>ROBOT</i>
     */
    private String type;
    
    /**
     * Value indicating that this player has finished playing
     */
    private boolean isFinished;
    
    /**
     * Value indicating the place (1st, 2nd, etc.) this player finished on if {@link #isFinished} is <i>true</i>
     */
    private int finishedAtPlace;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isIsMe() {
        return isMe;
    }

    public void setIsMe(boolean isMe) {
        this.isMe = isMe;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }   

    public boolean isIsFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public int getFinishedAtPlace() {
        return finishedAtPlace;
    }

    public void setFinishedAtPlace(int finishedAtPlace) {
        this.finishedAtPlace = finishedAtPlace;
    }
}
