package propofol.matchingservice.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.matchingservice.domain.board.entitiy.Board;

import java.util.Collection;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b")
    Page<Board> findAll(Pageable pageable);

    @Query("select b from Board b where upper(b.title) like upper(concat('%', :keyword, '%')) and not b.createdBy = :memberId")
    Page<Board> findAllByKeyword(Pageable pageable, @Param("keyword") String keyword, @Param("memberId") String memberId);

    @Query("select b from Board b where not b.createdBy = :memberId")
    Page<Board> findAllByNotMine(Pageable pageable, @Param("memberId") String memberId);

    @Query("select b from Board b where b.createdBy = :memberId")
    Page<Board> findAllByMine(Pageable pageable, @Param("memberId") String memberId);

    @Query("select b from Board b left join fetch b.boardImages bi where b.id = :boardId")
    Optional<Board> findBoardWithImages(@Param("boardId") Long boardId);

    @Query("select bt.board from BoardTag bt join bt.board where bt.tagId in :tagIds and not bt.board.createdBy = :memberId " +
            "group by bt.board.id order by count(bt) desc, bt.board.id desc")
    Page<Board> findAllByTagIdsAndNotMine(@Param("tagIds") Collection<Long> tagIds, @Param("memberId") String memberId,
                                          Pageable pageable);

    @Query("select bt.board from BoardTag bt join bt.board b where bt.tagId in :tagIds and upper(b.title) like upper(concat('%', :keyword, '%'))" +
            "and not b.createdBy = :memberId group by bt.board.id order by count(bt) desc, bt.board.id desc")
    Page<Board> findAllByConditions(@Param("tagIds") Collection<Long> tagIds, @Param("keyword") String keyword,
                                    @Param("memberId") String memberId, Pageable pageable);
}
