package propofol.matchingservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.matchingservice.domain.board.entitiy.Board;
import propofol.matchingservice.domain.board.entitiy.BoardMember;
import propofol.matchingservice.domain.board.entitiy.MemberStatus;
import propofol.matchingservice.domain.board.repository.BoardMemberRepository;
import propofol.matchingservice.domain.board.service.BoardMemberService;

import static propofol.matchingservice.api.feign.AlarmType.*;
import static propofol.matchingservice.api.feign.MessageFormat.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardMemberApiService {
    private final BoardMemberRepository boardMemberRepository;
    private final UserService userService;
    private final AlarmService alarmService;
    private final BoardMemberService boardMemberService;

    @Transactional
    public String applyWithAlarm(long memberId, Board board, String token){
        BoardMember boardMember = createBoardMember(memberId, MemberStatus.WAITING);
        boardMember.changeBoard(board);
        boardMemberRepository.save(boardMember);

        String message =
                board.getTitle() + "에 " + userService.getMemberNickName(token, String.valueOf(memberId)) + "님이 신청하셨습니다.";

        alarmService.save(token, board.getCreatedBy(), board.getId(), message, APPLY);

        return "ok";
    }

    private BoardMember createBoardMember(long memberId, MemberStatus status) {
        return BoardMember.createBoardMember().memberId(memberId).status(status).build();
    }

    @Transactional
    public String ApproveWithAlarm(Long boardId, Long memberId, Long requestId, String token) {
        BoardMember boardMember = boardMemberService.updateMember(boardId, memberId, requestId);

        alarmService.save(token, String.valueOf(memberId), boardId,
                boardMember.getBoard().getTitle() + APPROVE_MESSAGE, APPROVE);

        return "ok";
    }

    @Transactional
    public String rejectWithAlarm(Long boardId, Long memberId, Long requestId, String token) {
        BoardMember boardMember = boardMemberService.deleteMember(boardId, memberId, requestId);

        alarmService.save(token, String.valueOf(memberId), boardId,
                boardMember.getBoard().getTitle() + REJECT_MESSAGE, REJECT);

        return "ok";
    }

    @Transactional
    public String outWithAlarm(Long boardId, Long memberId, String token) {
        BoardMember boardMember = boardMemberService.secessionMember(boardId, memberId);

        String message = boardMember.getBoard().getTitle() + "에서 "
                + userService.getMemberNickName(token, String.valueOf(memberId))
                + "님이 탈퇴하셨습니다.";

        alarmService.save(token, boardMember.getBoard().getCreatedBy(), boardId,
                message , OUT);

        return "ok";
    }
}
