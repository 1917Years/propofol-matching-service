package propofol.matchingservice.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.matchingservice.domain.board.entitiy.Board;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b")
    Page<Board> findAll(Pageable pageable);

    @Query("select b from Board b where b.title like %:keyword% and not b.createdBy = :memberId")
    Page<Board> findAllByKeyword(Pageable pageable, @Param("keyword") String keyword, @Param("memberId") String memberId);

    @Query("select b from Board b where not b.createdBy = :memberId")
    Page<Board> findAllByNotMine(Pageable pageable, @Param("memberId") String memberId);

    @Query("select b from Board b where b.createdBy = :memberId")
    Page<Board> findAllByMine(Pageable pageable, @Param("memberId") String memberId);

    @Query("select b from Board b left join fetch b.boardImages bi where b.id = :boardId")
    Optional<Board> findBoardWithImages(@Param("boardId") Long boardId);
}
