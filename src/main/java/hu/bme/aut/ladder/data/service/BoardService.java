package hu.bme.aut.ladder.data.service;

import hu.bme.aut.ladder.data.entity.BoardEntity;

import java.util.List;

/**
 * Service for accessing boards.
 * 
 * @author Barnabas
 */
public interface BoardService {

    /**
     * Returns all boards current available.
     * 
     * @return
     */
    List<BoardEntity> getAllBoard();

}
