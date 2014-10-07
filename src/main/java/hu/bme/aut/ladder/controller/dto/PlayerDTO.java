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
    private FieldDTO position;
    
    /**
     * Player's name
     */
    private String name;

    public FieldDTO getPosition() {
        return position;
    }

    public void setPosition(FieldDTO position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
