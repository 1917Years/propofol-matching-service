package propofol.matchingservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.matchingservice.domain.board.entitiy.*;
import propofol.matchingservice.domain.board.repository.BoardRepository;
import propofol.matchingservice.domain.board.service.dto.BoardDto;
import propofol.matchingservice.domain.exception.MailSendException;
import propofol.matchingservice.domain.exception.NoMatchMemberBoardException;
import propofol.matchingservice.domain.exception.NotFoundBoardException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final ImageService imageService;
    private final TimeTableService timeTableService;
    private final BoardTagService tagService;
    private final BoardMemberService boardMemberService;
    private final JavaMailSender mailSender;

    @Transactional
    public String saveMatchingBoard(String title, String content, String nickName, int recruit, String startDate,
                                    String endDate, Set<String> fileNames, List<Long> tagIds, List<String> weeks,
                                    List<String> startTimes, List<String> endTimes, Long memberId){
        Board saveBoard = boardRepository.save(createBoard(title, content, nickName, recruit, startDate, endDate));

        if(fileNames != null){
            List<BoardImage> images = imageService.getImagesByStoreNames(fileNames);
            images.forEach(image -> {
                image.changeBoard(saveBoard);
            });
        }

        if(tagIds != null){
            List<BoardTag> boardTags = saveBoard.getBoardTags();
            tagIds.forEach(tagId -> {
                BoardTag tag = BoardTag.createTag().tagId(tagId).build();
                tag.changeBoard(saveBoard);
                boardTags.add(tag);
            });
        }

        if(weeks != null){
            for (int i = 0; i < weeks.size(); i++) {
                String week = weeks.get(i);
                String startTime = startTimes.get(i);
                String endTime = endTimes.get(i);

                BoardTimeTable timeTable = createTimeTable(week, startTime, endTime);
                timeTable.changeBoard(saveBoard);
                saveBoard.getBoardTimeTables().add(timeTable);
            }
        }

        boardMemberService.save(memberId, saveBoard);

        return "ok";
    }

    @Transactional
    public String updateMatchingBoard(Long boardId, BoardDto boardDto,
                                      Set<String> fileNames, List<Long> tagIds){

        Board findBoard = findByBoardWithAllInfoId(boardId);
        LocalDate startT = LocalDate.parse(boardDto.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endT = LocalDate.parse(boardDto.getEndDate(), DateTimeFormatter.ISO_DATE);
        findBoard.updateBoard(boardDto.getTitle(), boardDto.getContent(), startT, endT, boardDto.getRecruit());

        if(fileNames != null){
            List<BoardImage> images = imageService.getImagesByStoreNames(fileNames);
            images.forEach(image -> {
                image.changeBoard(findBoard);
            });
        }

        if(tagIds != null){
            findBoard.getBoardTags().clear();

            List<BoardTag> boardTags = findBoard.getBoardTags();
            tagIds.forEach(tagId -> {
                BoardTag tag = BoardTag.createTag().tagId(tagId).build();
                tag.changeBoard(findBoard);
                boardTags.add(tag);
            });
        }

        return "ok";
    }

    @Transactional
    public String deleteMatchingBoard(Long boardId, Long memberId, String dir){
        Board findBoard = boardRepository.findBoardWithImages(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("???????????? ???????????? ????????????.");
        });

        if(!findBoard.getCreatedBy().equals(String.valueOf(memberId)))
            throw new NoMatchMemberBoardException("????????? ???????????? ???????????? ????????????.");

        imageService.deleteAllByBoardId(boardId);
        tagService.deleteAllByBoardId(boardId);
        timeTableService.deleteAllById(boardId);
        boardMemberService.deleteAll(boardId);
        boardRepository.delete(findBoard);

        return "ok";
    }

    @Transactional
    public String saveTimeTable(Long boardId, String week, String startTime, String endTime) {
        Board findBoard = findById(boardId);
        BoardTimeTable timeTable = createTimeTable(week, startTime, endTime);
        timeTable.changeBoard(findBoard);
        findBoard.getBoardTimeTables().add(timeTable);

        return "ok";
    }

    @Transactional
    public String deleteTimeTable(Long timeTableId) {
        return timeTableService.deleteOneById(timeTableId);
    }

    public Board findById(Long boardId){
        return boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("???????????? ?????? ??? ????????????.");
        });
    }

    public Page<Board> getMyBoards(Long memberId, int page){
        PageRequest pageRequest =
                PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findAllByMine(pageRequest, String.valueOf(memberId));
    }

    public Page<Board> getAllByKeyword(String keyword, int page, long memberId){
        PageRequest pageRequest =
                PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findAllByKeyword(pageRequest, keyword, String.valueOf(memberId));
    }

    public Board findWithImagesById(Long boardId){
        return boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("???????????? ?????? ??? ????????????.");
        });
    }

    public Page<Board> getPageMatchingBoard(int page, long memberId){
        PageRequest pageRequest =
                PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));

        return boardRepository.findAllByNotMine(pageRequest, String.valueOf(memberId));
    }

    private Board createBoard(String title, String content, String nickName, int recruit, String startDate,
                              String endDate) {
        LocalDate startD = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate endD = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        Board board = Board.boardCreate()
                .title(title)
                .content(content)
                .nickName(nickName)
                .recruit(recruit)
                .recruited(0)
                .startDate(startD)
                .endDate(endD)
                .build();
        board.changeBoardStatus(true);
        return board;
    }

    private BoardTimeTable createTimeTable(String week, String startTime, String endTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startT = LocalTime.parse(startTime, dateTimeFormatter);
        LocalTime endT = LocalTime.parse(endTime, dateTimeFormatter);
        BoardTimeTable timeTable =
                BoardTimeTable.createTimeTable().week(week).startTime(startT).endTime(endT).build();
        return timeTable;
    }

    public Board findByBoardWithAllInfoId(Long boardId) {
        return boardRepository.findBoardWithImages(boardId)
                .orElseThrow(() -> {throw new NotFoundBoardException("???????????? ?????? ??? ????????????.");});
    }

    @Transactional
    public String changeStatus(long boardId, Boolean status) {
        Board findBoard = findById(boardId);
        findBoard.changeBoardStatus(status);
        return "ok";
    }

    public Page<Board> getAllByTagIds(Set<Long> tagIds, int page, long memberId){
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        return boardRepository.findAllByTagIdsAndNotMine(tagIds, String.valueOf(memberId), pageRequest);
    }

    public Page<Board> getResultByConditions(String keyword, int page, Set<Long> tagIds, long memberId) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        return boardRepository.findAllByConditions(tagIds, keyword, String.valueOf(memberId), pageRequest);
    }

    public String sendMail(String leaderMail, HashMap<String, String> memberEmails, String projectMail, String title) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        Set<Map.Entry<String, String>> entries = memberEmails.entrySet();

        StringBuilder sb = new StringBuilder();
        try {
            mimeMessageHelper = new MimeMessageHelper(message, false, "UTF-8");
            mimeMessageHelper.setFrom(projectMail);
            mimeMessageHelper.setTo(leaderMail);
            mimeMessageHelper.setSubject("[propofol] "+ title + "??? ?????? ??????");
            for (Map.Entry<String, String> entry : entries) {
                sb.append("?????? : " + entry.getKey() + "?????? Email = " + entry.getValue() + "\n");
            }
            mimeMessageHelper.setText(sb.toString());
        } catch (MessagingException e) {
            throw new MailSendException("?????? ?????? ??????");
        }
        mailSender.send(message);

        return "ok";
    }
}

