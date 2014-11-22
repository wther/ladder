package hu.bme.aut.ladder.strategy.impl;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.entity.PlayerEntity;
import hu.bme.aut.ladder.data.entity.StateChangeEntity;
import hu.bme.aut.ladder.data.entity.TunnelEntity;
import hu.bme.aut.ladder.strategy.Dice;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Base class for testing board strategies
 * 
 * @author Barnabas
 */
public abstract class BaseBoardStrategyImplTest {
    
    /**
     * Creates a simple board
     * 
     * @param numberOfPlayers
     * @return 
     */
    protected static BoardEntity mockBoard(int numberOfPlayers){
        BoardEntity board = new BoardEntity();
        board.setBoardId(new Long(1));
        board.setBoardSize(100);
        board.setStateChanges(new ArrayList<StateChangeEntity>());
        board.setTunnels(new ArrayList<TunnelEntity>());
        
        List<PlayerEntity> players = new ArrayList<>();
        for(int i = 0; i < numberOfPlayers; i++){
            PlayerEntity player = new PlayerEntity();
            player.setPlayerId(new Long(i));
            player.setPosition(0);
            player.setType(PlayerEntity.Type.HUMAN);
            players.add(player);
        }
        
        board.setPlayers(players);
        return board;
    }
    
    /**
     * Returns a dice which always rolls five
     * @return 
     */
    protected static Dice diceWhichRollsTheSame(int diceRolled){
        // Mock a dice to always throw 5
        Dice dice = mock(Dice.class);
        when(dice.getNext()).thenReturn(diceRolled);
        return dice;
    }
}
