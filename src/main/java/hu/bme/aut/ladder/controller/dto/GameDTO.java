package hu.bme.aut.ladder.controller.dto;

import hu.bme.aut.ladder.controller.LobbyController;
import java.util.Date;
import java.util.List;

/**
 * DTO sent from the {@link LobbyController}
 * 
 * @author Barnabas
 */
public class GameDTO {
    
    /**
     * Game's ID
     */
    private Long gameId;
    
    /**
     * Name of the game's host
     */
    private String host;
    
    /**
     * Players in the game
     */
    private List<String> allPlayers;
    
    /**
     * Date the game was created
     */
    private Date created;
    
    /**
     * Value indicating that the current user is in this game
     */
    private boolean userInGame;
    
    /**
     * Name of the user requesting this DTO
     */
    private String user;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<String> getAllPlayers() {
        return allPlayers;
    }

    public void setAllPlayers(List<String> allPlayers) {
        this.allPlayers = allPlayers;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isUserInGame() {
        return userInGame;
    }

    public void setUserInGame(boolean userInGame) {
        this.userInGame = userInGame;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
