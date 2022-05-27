package propofol.matchingservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.matchingservice.api.common.exception.NoMatchCreateByException;
import propofol.matchingservice.domain.board.entitiy.Board;
import propofol.matchingservice.domain.board.entitiy.BoardMember;
import propofol.matchingservice.domain.board.entitiy.MemberStatus;
import propofol.matchingservice.domain.board.repository.BoardMemberRepository;
import propofol.matchingservice.domain.exception.NotFoundBoardMemberException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardMemberService {
    private final BoardMemberRepository boardMemberRepository;

    public String save(long memberId, Board board){
        BoardMember boardMember = createBoardMember(memberId, MemberStatus.MASTER);
        boardMember.changeBoard(board);
        boardMemberRepository.save(boardMember);

        return "ok";
    }

    public List<BoardMember> getBoardByBoardIdAndMemberStatus(long boardId, MemberStatus status){
        return boardMemberRepository.findBoardMemberByBoardIdAndMemberStatus(boardId, status);
    }

    public List<BoardMember> getBoardByBoardIdAndNoMatchMemberStatus(long boardId, MemberStatus status){
        return boardMemberRepository.getBoardMemberByBoardIdAndNoMatchMemberStatus(boardId, status);
    }

    @Transactional
    public BoardMember updateMember(long boardId, long memberId, Long requestId){
        BoardMember boardMember
                = boardMemberRepository.findBoardMemberWithBoardByMemberIdAndBoardId(memberId, boardId).orElseThrow(() -> {
            throw new NotFoundBoardMemberException("BoardMember 조회 오류");
        });

        if(!boardMember.getBoard().getCreatedBy().equals(String.valueOf(requestId))) {
            throw new NoMatchCreateByException("권한이 없습니다.");
        }

        boardMember.getBoard().upRecruited();
        boardMember.changeStatus(MemberStatus.COMPLETE);

        return boardMember;
    }

    @Transactional
    public BoardMember deleteMember(long boardId, long memberId, Long requestId){
        BoardMember boardMember = null;
        if(requestId != null) {
            boardMember
                    = boardMemberRepository.findBoardMemberWithBoardByMemberIdAndBoardId(memberId, boardId).orElseThrow(() -> {
                throw new NotFoundBoardMemberException("BoardMember 조회 오류");
            });

            if (!boardMember.getBoard().getCreatedBy().equals(String.valueOf(requestId))) {
                throw new NoMatchCreateByException("권한이 없습니다.");
            }
        }

        boardMemberRepository.deleteBoardMemberByMemberIdAndBoardId(memberId, boardId);

        return boardMember;
    }

    @Transactional
    public String deleteAll(long boardId){
        boardMemberRepository.deleteAllMember(boardId);

        return "ok";
    }

    @Transactional
    public BoardMember secessionMember(long boardId, long memberId){
        BoardMember boardMember
                = boardMemberRepository.findBoardMemberWithBoardByMemberIdAndBoardId(memberId, boardId).orElseThrow(() -> {
            throw new NotFoundBoardMemberException("BoardMember 조회 오류");
        });

        boardMember.getBoard().downRecruited();
        boardMemberRepository.delete(boardMember);

        return boardMember;
    }

    public Page<BoardMember> findBoardMemberByMemberIdAndStatus(long memberId, int page, MemberStatus status){
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        return boardMemberRepository.findBoardMemberByMemberIdAndStatus(memberId, pageRequest, status);
    }

    private BoardMember createBoardMember(long memberId, MemberStatus status) {
        return BoardMember.createBoardMember().memberId(memberId).status(status).build();
    }
}
