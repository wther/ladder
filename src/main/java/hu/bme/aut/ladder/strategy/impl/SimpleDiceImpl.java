package hu.bme.aut.ladder.strategy.impl;

import hu.bme.aut.ladder.strategy.Dice;
import java.util.Random;

/**
 * Dice implementation returning values between 1 and 6
 * 
 * @author Barnabas
 */
public class SimpleDiceImpl implements Dice {
    
    /**
     * Source of the dice
     */
    private Random random = new Random();

    /**
     * {@inheritDoc}
     * @return 
     */
    @Override
    public int getNext() {
        return 1 + random.nextInt(DICE_LIMIT);
    }
}
