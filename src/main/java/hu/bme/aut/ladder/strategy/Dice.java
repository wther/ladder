package hu.bme.aut.ladder.strategy;

/**
 * Random generator for the {@link BoardStrategy} implementations
 * 
 * @author Barnabas
 */
public interface Dice {
    
    /**
     * Rolls the dice and returns the next value 
     * 
     * @return 
     */
    int getNext();
    
}
