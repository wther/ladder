package hu.bme.aut.ladder.strategy;

/**
 * Random generator for the {@link BoardStrategy} implementations
 * 
 * @author Barnabas
 */
public interface Dice {
    
    /**
     * The highest number rollable by the dice
     */
    int DICE_LIMIT = 6;
    
    /**
     * Rolls the dice and returns the next value 
     * 
     * @return 
     */
    int getNext();
    
}
