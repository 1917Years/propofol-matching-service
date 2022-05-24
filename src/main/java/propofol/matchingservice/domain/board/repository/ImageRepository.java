package propofol.matchingservice.domain.board.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.matchingservice.domain.board.entitiy.BoardImage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<BoardImage, Long> {

    Optional<List<BoardImage>> findAllByBoardId(Long boardId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from BoardImage i where i.board.id = :boardId")
    int deleteAllByBoardId(@Param("boardId") Long boardId);

    Optional<BoardImage> findFirstByBoardId(Long boardId);

    @Query("select bi from BoardImage bi where bi.board.id in :boardIds")
    List<BoardImage> findBySetOfBoardId(Collection<Long> boardIds);

    @Query("select bi from BoardImage bi where bi.storeFileName in :names")
    List<BoardImage> findAllByNames(@Param("names") Collection<String> names);
}
