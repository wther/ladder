package hu.bme.aut.ladder.data.service.impl;

import hu.bme.aut.ladder.data.entity.BoardEntity;
import hu.bme.aut.ladder.data.repository.BoardRepository;
import hu.bme.aut.ladder.data.service.BoardService;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link BoardService} service.
 * 
 * This is a service meaning, that this is added to the Spring context and can be injected
 * with Autowired.
 * 
 * This class is transactional (as opposed to {@link BoardRepository} which isn't), meaning
 * that all of it's methods have ACID attributes. 
 * 
 * @author Barnabas
 */
@Service
@Transactional
public class BoardServiceImpl implements BoardService {
    
    /**
     * Repository used to access {@link BoardEntity} entities.
     */
    @Autowired
    private BoardRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BoardEntity> getAllBoard() {
        return repository.findAll();
    }
}