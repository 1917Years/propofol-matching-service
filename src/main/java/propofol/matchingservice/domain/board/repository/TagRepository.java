package propofol.matchingservice.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.matchingservice.domain.board.entitiy.BoardTag;

import java.util.Collection;

public interface TagRepository extends JpaRepository<BoardTag, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from BoardTag bt where bt.board.id = :boardId")
    void deleteAllByBoardId(@Param("boardId") Long boardId);

    @Query(value = "select bt from BoardTag bt where bt.tagId in :tagIds and not bt.board.createdBy = :memberId")
    Page<BoardTag> findAllByTagIdsAndNotMine(@Param("tagIds")Collection<Long> tagIds, @Param("memberId") String memberId,
                                             Pageable pageable);

    @Query(value = "select bt from BoardTag bt where bt.tagId in :tagIds and not bt.board.createdBy = :memberId " +
            "and bt.board.title like %:keyword%")
    Page<BoardTag> findAllByConditions(@Param("tagIds") Collection<Long> tagIds, @Param("keyword") String keyword,
                                       @Param("memberId") String memberId, Pageable pageable);
}
