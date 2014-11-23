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
    private List<UserDTO> allPlayers;
    
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
    
    /**
     * Number of robots in the game
     */
    private int numberOfRobots;
    
    /**
     * Size of the board
     */
    private int boardSize;
    
    /**
     * Number of snakes on the board
     */
    private int numberOfSnakes;
    
    /**
     * Number of ladders
     */
    private int numberOfLadders;
    
    /**
     * Value indicating that game has started
     */
    private boolean gameStarted;

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

    public List<UserDTO> getAllPlayers() {
        return allPlayers;
    }

    public void setAllPlayers(List<UserDTO> allPlayers) {
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

    public int getNumberOfRobots() {
        return numberOfRobots;
    }

    public void setNumberOfRobots(int numberOfRobots) {
        this.numberOfRobots = numberOfRobots;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public int getNumberOfSnakes() {
        return numberOfSnakes;
    }

    public void setNumberOfSnakes(int numberOfSnakes) {
        this.numberOfSnakes = numberOfSnakes;
    }

    public int getNumberOfLadders() {
        return numberOfLadders;
    }

    public void setNumberOfLadders(int numberOfLadders) {
        this.numberOfLadders = numberOfLadders;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }  
}
