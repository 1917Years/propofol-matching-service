package propofol.matchingservice.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.matchingservice.domain.board.entitiy.BoardTimeTable;

import java.util.List;

public interface TimeTableRepository extends JpaRepository<BoardTimeTable, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from BoardTimeTable btt where btt.id = :id")
    void deleteOneById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from BoardTimeTable btt where btt.board.id = :boardId")
    void deleteAllByBoardId(Long boardId);

    @Query("select bt from BoardTimeTable bt where bt.board.id = :boardId")
    List<BoardTimeTable> findTimeTablesByBoardId(@Param("boardId") Long boardId);
}
