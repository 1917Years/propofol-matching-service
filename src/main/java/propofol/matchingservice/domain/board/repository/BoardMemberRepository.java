package propofol.matchingservice.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.matchingservice.domain.board.entitiy.BoardMember;
import propofol.matchingservice.domain.board.entitiy.MemberStatus;

import java.util.List;
import java.util.Optional;

public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {

    @Query("select bm from BoardMember bm where bm.board.id = :boardId and bm.status = :status")
    List<BoardMember> findBoardMemberByBoardIdAndMemberStatus(@Param("boardId") Long boardId,
                                                              @Param("status") MemberStatus status);

    @Query("select bm from BoardMember bm where bm.board.id = :boardId and not bm.status = :status")
    List<BoardMember> getBoardMemberByBoardIdAndNoMatchMemberStatus(@Param("boardId") long boardId,
                                                                    @Param("status") MemberStatus status);

    @Query("select bm from BoardMember bm join fetch bm.board where bm.memberId = :memberId and bm.board.id = :boardId")
    Optional<BoardMember> findBoardMemberWithBoardByMemberIdAndBoardId(@Param("memberId") Long memberId,
                                                                       @Param("boardId") Long boardId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from BoardMember bm where bm.memberId = :memberId and bm.board.id = :boardId")
    void deleteBoardMemberByMemberIdAndBoardId(@Param("memberId") Long memberId,
                                               @Param("boardId") Long boardId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from BoardMember bm where bm.board.id = :boardId")
    void deleteAllMember(@Param("boardId") Long boardId);

    @Query("select bm from BoardMember bm where bm.memberId = :memberId and bm.status = :status order by bm.id")
    Page<BoardMember> findBoardMemberByMemberIdAndStatus(@Param("memberId") Long memberId, Pageable pageable,
                                                         @Param("status") MemberStatus status);

    List<BoardMember> findAllByBoardId(Long boardId);
}
