package hu.bme.aut.ladder.data.repository;

import hu.bme.aut.ladder.data.entity.BoardEntity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for accessing boards.
 * 
 * This interface is instantiated at Runtime by Spring Data.
 * 
 * @author Barnabas
 */
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

}
