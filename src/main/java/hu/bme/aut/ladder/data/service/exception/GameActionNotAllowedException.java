package hu.bme.aut.ladder.data.service.exception;

import hu.bme.aut.ladder.data.service.GameService;

/**
 * Exception thrown from {@link GameService} when an illegal action is attempted
 * 
 * @author Barnabas
 */
public class GameActionNotAllowedException extends Exception {
    
    public GameActionNotAllowedException(String message){
        super(message);
    }    
    
    public GameActionNotAllowedException(String message, Throwable t){
        super(message, t);
    }
}
