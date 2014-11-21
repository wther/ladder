package hu.bme.aut.ladder.controller.dto;

/**
 * DTO sent from the {@link UserController} when requesting user details
 * 
 * @author Barnabas
 */
public class UserDTO {
    
    /**
     * User's internal id
     */
    private Long userId;
    
    /**
     * User's name
     */
    private String name;
    
    /**
     * Is user assumed ready to start a game?
     */
    private boolean ready;  

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
