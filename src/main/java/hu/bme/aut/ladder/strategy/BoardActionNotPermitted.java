package hu.bme.aut.ladder.strategy;

/**
 * Exception thrown from {@link BoardStrategy} implementations
 * @author Barnabas
 */
public class BoardActionNotPermitted extends Exception {
    
    /**
     * 
     * @param message 
     */
    public BoardActionNotPermitted(String message){
        super(message);
    }    
}
