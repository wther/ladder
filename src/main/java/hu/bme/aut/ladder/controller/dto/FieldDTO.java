package hu.bme.aut.ladder.controller.dto;

/**
 * Coordinate on the board
 */
public class FieldDTO {

    /**
     * Horizontal coordinate
     */
    private int x;
    
    /**
     * Vertical coordinate
     */
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
