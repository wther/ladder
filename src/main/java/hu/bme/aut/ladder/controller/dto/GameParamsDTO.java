package hu.bme.aut.ladder.controller.dto;

/**
 * DTO sent to the RoomController indicating the number of robots, etc.
 *  
 * @author Barnabas
 */
public class GameParamsDTO {
    
    /**
     * Number of robots
     */
    private int robots;
    
    /**
     * Number of snakes
     */
    private int snakes;
    
    /**
     * Number of ladders
     */
    private int ladders;
    
    /**
     * Board size
     */
    private int size;

    public int getRobots() {
        return robots;
    }

    public void setRobots(int robots) {
        this.robots = robots;
    }

    public int getSnakes() {
        return snakes;
    }

    public void setSnakes(int snakes) {
        this.snakes = snakes;
    }

    public int getLadders() {
        return ladders;
    }

    public void setLadders(int ladders) {
        this.ladders = ladders;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "GameParamsDTO{" + "robots=" + robots + ", snakes=" + snakes + ", ladders=" + ladders + ", size=" + size + '}';
    }
}
