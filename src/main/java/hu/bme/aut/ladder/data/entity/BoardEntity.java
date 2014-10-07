package hu.bme.aut.ladder.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for boards.
 * 
 * The {@link DataConfig} is set up in manner, that entities in this package are
 * scanned, and DDL is generated appropriately to the annotiations of this class
 * in the H2Base DB.
 * 
 * This means that if you change the entities, the database schema will change
 * too.
 * 
 * @author Barnabas
 */
@Entity
@Table(name = "boards")
public class BoardEntity {

    /**
     * Unique id for the board
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long boardId;

    /**
     * Dimension X for the board
     */
    @Column
    private int width;

    /**
     * Dimension Y for the board
     */
    @Column
    private int height;

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
