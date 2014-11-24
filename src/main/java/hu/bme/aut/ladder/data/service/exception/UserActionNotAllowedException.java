package hu.bme.aut.ladder.data.service.exception;

import hu.bme.aut.ladder.data.service.impl.UserServiceImpl;

/**
 * Exception thrown from {@link UserServiceImpl}
 * 
 * @author Barnabas
 */
public class UserActionNotAllowedException extends Exception {
    
    public UserActionNotAllowedException(String message){
        super(message);
    }
}
