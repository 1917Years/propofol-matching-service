package propofol.matchingservice.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import propofol.matchingservice.domain.board.entitiy.BoardTimeTable;

public interface TimeTableRepository extends JpaRepository<BoardTimeTable, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from BoardTimeTable btt where btt.id = :id")
    void deleteOneById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from BoardTimeTable btt where btt.board.id = :boardId")
    void deleteAllByBoardId(Long boardId);
}
