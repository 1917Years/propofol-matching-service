package propofol.matchingservice.api.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.matchingservice.api.common.annotation.Jwt;
import propofol.matchingservice.api.common.annotation.Token;
import propofol.matchingservice.api.common.exception.SaveImageException;
import propofol.matchingservice.api.common.porperties.FileProperties;
import propofol.matchingservice.api.common.porperties.MailProperties;
import propofol.matchingservice.api.controller.dto.*;
import propofol.matchingservice.api.feign.dto.MemberDto;
import propofol.matchingservice.api.feign.dto.MemberInfoDto;
import propofol.matchingservice.api.feign.dto.TagDetailDto;
import propofol.matchingservice.api.service.TagService;
import propofol.matchingservice.api.service.UserService;
import propofol.matchingservice.domain.board.entitiy.*;
import propofol.matchingservice.domain.board.service.BoardService;
import propofol.matchingservice.domain.board.service.ImageService;
import propofol.matchingservice.domain.board.service.TimeTableService;
import propofol.matchingservice.domain.board.service.dto.BoardDto;
import propofol.matchingservice.domain.exception.NoMatchMemberBoardException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/matchings")
public class BoardController {

    private final BoardService boardService;
    private final ImageService imageService;
    private final UserService userService;
    private final TagService tagService;
    private final FileProperties fileProperties;
    private final ModelMapper modelMapper;
    private final TimeTableService timeTableService;
    private final MailProperties mailProperties;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto NoMatchMemberBoardException(NoMatchMemberBoardException e){
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "게시글 삭제 실패", e.getMessage());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto saveMatchingBoard(@RequestParam("title") String title,
                                         @RequestParam("content") String content,
                                         @RequestParam("recruit") int recruit,
                                         @RequestParam("startDate") String startDate,
                                         @RequestParam("endDate") String endDate,
                                         @RequestParam(value = "tagId", required = false) List<Long> tagIds,
                                         @RequestParam(value = "fileName", required = false) Set<String> filesNames,
                                         @RequestParam(value = "week", required = false) List<String> weeks,
                                         @RequestParam(value = "startTime", required = false) List<String> startTimes,
                                         @RequestParam(value = "endTime", required = false) List<String> endTimes,
                                         @Token Long memberId,
                                         @Jwt String token) throws SaveImageException {
        String nickName = userService.getMemberNickName(token, String.valueOf(memberId));
        return new ResponseDto(HttpStatus.CREATED.value(), "success", "매칭 게시글 저장 성공",
                boardService.saveMatchingBoard(title, content, nickName, recruit,
                        startDate, endDate, filesNames, tagIds, weeks, startTimes, endTimes, memberId));
    }

    @DeleteMapping("/{boardId}/{timeTableId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteTimeTable(@PathVariable("boardId") Long boardId,
                                       @PathVariable("timeTableId") Long timeTableId){

        return new ResponseDto(HttpStatus.OK.value(), "success", "시간표 삭제 성공",
                boardService.deleteTimeTable(timeTableId));
    }

    @PostMapping("/{boardId}/timeTable")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto createTimeTable(@PathVariable("boardId") Long boardId,
                                       @Validated @RequestBody TimetableSaveRequestDto requestDto){

        return new ResponseDto(HttpStatus.CREATED.value(), "success", "시간표 저장 성공",
                boardService.saveTimeTable(boardId, requestDto.getWeek(), requestDto.getStartTime(), requestDto.getEndTime()));
    }

    @PostMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto updateBoard(@PathVariable("boardId") Long boardId,
                                   @RequestParam("title") String title,
                                   @RequestParam("content") String content,
                                   @RequestParam("recruit") int recruit,
                                   @RequestParam("startDate") String startDate,
                                   @RequestParam("endDate") String endDate,
                                   @RequestParam(value = "tagId", required = false) List<Long> tagIds,
                                   @RequestParam(value = "fileName", required = false) Set<String> filesNames){
        BoardDto boardDto = createBoardDto(title, content, recruit, startDate, endDate);

        return new ResponseDto(HttpStatus.OK.value(), "success", "게시글 수정 성공",
                boardService.updateMatchingBoard(boardId, boardDto, filesNames, tagIds));
    }

    @PostMapping("/{boardId}/status")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto updateBoardStatus(@PathVariable("boardId") Long boardId,
                                         @RequestParam("status") Boolean status){

        return new ResponseDto(HttpStatus.OK.value(), "success", "게시글 수정 성공",
                boardService.changeStatus(boardId, status));
    }

    @DeleteMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteMatchingBoard(@PathVariable(value = "boardId") Long boardId,
                                           @Token Long memberId) {
        return new ResponseDto(HttpStatus.OK.value(), "success", "게시글 삭제 성공",
                boardService.deleteMatchingBoard(boardId, memberId, getProjectDir()));
    }

    /**
     * 전체 게시글 페이지 반환
     **/
    @GetMapping("/page")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getPageMatchingBoard(@RequestParam("page") int page,
                                            @Token Long memberId,
                                            @Jwt String token){
        MemberInfoDto memberInfo = userService.getMemberInfo(token, String.valueOf(memberId));
        BoardPageResponseDto boardPageResponseDto = createBoardPageResponseDto(page, token, memberInfo, memberId);

        return new ResponseDto(HttpStatus.OK.value(), "success", "게시글 조회 성공", boardPageResponseDto);
    }

    /**
     * 검색
     */
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getSearchResult(@RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "page", required = false) int page,
                                       @RequestParam(value = "tagId", required = false) Set<Long> tagIds,
                                       @Token Long memberId,
                                       @Jwt String token){
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "게시글 조회 성공", searchResult(keyword, page, tagIds, token, memberId));
    }

    /**
     * 게시글 한 개 상세 반환
     */
    @GetMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getMatchingBoard(@PathVariable("boardId") Long boardId,
                                        @Jwt String token,
                                        @Token Long memberId){
        Board board = boardService.findByBoardWithAllInfoId(boardId);
        MemberInfoDto memberInfo = userService.getMemberInfo(token, board.getCreatedBy());

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 조회 성공", createBoardDetailResponse(token, board,
                memberInfo.getProfileString(), memberInfo.getProfileType(), memberId));
    }

    /**
     * 게시글 시간표 조회
     */
    @GetMapping("/{boardId}/timetables")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getMatchingBoardTimeTables(@PathVariable("boardId") Long boardId){
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 조회 성공", createTimeTableListResponseDto(boardId));
    }

    /**
     * 자신의 게시글만 반환
     */
    @GetMapping("/myBoard")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getMyBoards(@Token Long memberId,
                                   @Jwt String token,
                                   @RequestParam("page") int page){
        BoardPageResponseDto boardPageResponseDto = createBoardPageResponseDto(page, token, null, memberId);

        return new ResponseDto(HttpStatus.OK.value(), "success", "게시글 조회 성공", boardPageResponseDto);
    }

    /**
     * 팀 생성 완료 시 메일 전송
     */
    @GetMapping("/completeTeam")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto completeTeam(@RequestParam("memberId") Set<Long> memberIds,
                                     @RequestParam("title") String title,
                                     @Jwt String token,
                                     @Token Long memberId){
        List<MemberDto> findMembers = userService.getMembersNoPage(token, memberIds);
        HashMap<String, String> memberEmails = new HashMap<>();
        String leaderEmail = null;

        for (MemberDto findMember : findMembers) {
            if(ObjectUtils.equals(findMember.getId(), memberId)) {
                leaderEmail = findMember.getEmail();
            }else {
                memberEmails.put(findMember.getNickName(), findMember.getEmail());
            }
        }

        return new ResponseDto(HttpStatus.OK.value(), "success", "팀 생성 성공 ",
                boardService.sendMail(leaderEmail, memberEmails, mailProperties.getUsername(), title));
    }


    private BoardPageResponseDto searchResult(String keyword, int page, Set<Long> tagIds, String token, long memberId) {
        BoardPageResponseDto boardPageResponseDto = new BoardPageResponseDto();
        Page<Board> boardPage;

        // QueryDsl 추후 추가 동적 조회
        if(keyword == null && tagIds == null){
            boardPage = boardService.getPageMatchingBoard(page, memberId);
        }else if(keyword != null && tagIds == null){
            boardPage = boardService.getAllByKeyword(keyword, page, memberId);
        }else if(keyword == null && tagIds != null){
            // 태그 아이들만
            boardPage = boardService.getAllByTagIds(tagIds, page, memberId);
        }else{
            // 모든 조건
            boardPage = boardService.getResultByConditions(keyword, page, tagIds, memberId);
        }

        List<Board> boards = null;
        if(boardPage != null){
            boardPageResponseDto.setTotalPageCount(boardPage.getTotalPages());
            boardPageResponseDto.setTotalCount(boardPage.getTotalElements());
            boards = boardPage.getContent();
        }

        List<TagDetailDto> tags = getTagDtosByPageBoard(token, boards);

        return createPageDto(boardPageResponseDto, boards, tags);
    }

    private String getProjectDir() {
        return fileProperties.getProjectDir();
    }

    private BoardPageResponseDto createBoardPageResponseDto(int page, String token,
                                                            MemberInfoDto memberInfo, Long memberId) {
        BoardPageResponseDto boardPageResponseDto = new BoardPageResponseDto();

        if(memberInfo != null && memberInfo.getTagInfos().size() != 0){

            memberInfo.getTagInfos().forEach(tag -> {
                boardPageResponseDto.getUserTags().add(tag.getName());
            });

            Set<Long> tagIds = memberInfo.getTagInfos().stream().map(TagDetailDto::getId).collect(Collectors.toSet());
            Page<Board> boardPages = boardService.getAllByTagIds(tagIds, page, memberId);
            if(boardPages.getTotalElements() != 0) {
                boardPageResponseDto.setTotalPageCount(boardPages.getTotalPages());
                boardPageResponseDto.setTotalCount(boardPages.getTotalElements());

                List<Board> boards = boardPages.getContent();
                List<TagDetailDto> tags = getTagDtosByPageBoard(token, boards);

                return createPageDto(boardPageResponseDto, boards, tags);
            }
        }

        Page<Board> pageMatchingBoard = null;
        if(memberInfo != null){
            pageMatchingBoard = boardService.getPageMatchingBoard(page, memberId);
        }else {
            pageMatchingBoard = boardService.getMyBoards(memberId, page);
        }
        boardPageResponseDto.setTotalPageCount(pageMatchingBoard.getTotalPages());
        boardPageResponseDto.setTotalCount(pageMatchingBoard.getTotalElements());

        List<TagDetailDto> tags = getTagDtosByPageBoard(token, pageMatchingBoard.getContent());

        return createPageDto(boardPageResponseDto, pageMatchingBoard.getContent(), tags);
    }

    private BoardPageResponseDto createPageDto(BoardPageResponseDto boardPageResponseDto, List<Board> boards, List<TagDetailDto> tags) {
        List<BoardPageDetailResponseDto> boardsResponseDto = boardPageResponseDto.getBoards();
        boards.forEach(board -> {
            BoardPageDetailResponseDto responseDto = modelMapper.map(board, BoardPageDetailResponseDto.class);


            List<BoardImage> boardImages = board.getBoardImages();
            if (boardImages.size() != 0) {
                BoardImage boardImage = boardImages.get(0);
                responseDto.setImage(imageService
                        .getBase64EncodeToString(getProjectDir(), boardImage.getStoreFileName())
                );
                responseDto.setImageType(boardImage.getContentType());
            }

            for (TagDetailDto tag : tags) {
                for (BoardTag boardTag : board.getBoardTags()) {
                    if(Objects.equals(tag.getId(), boardTag.getTagId())){
                        responseDto.getTagInfos().add(modelMapper.map(tag, TagDetailResponseDto.class));
                    }
                }
            }

            boardsResponseDto.add(responseDto);
        });

        return boardPageResponseDto;
    }

    private List<TagDetailDto> getTagDtosByPageBoard(String token, List<Board> boards) {
        Set<Long> tagIds = new HashSet<>();
        boards.forEach(board -> {
            List<BoardTag> boardTags = board.getBoardTags();
            boardTags.forEach(boardTag -> {
                tagIds.add(boardTag.getTagId());
            });
        });

        List<TagDetailDto> tags = tagService.getTagsByTagIds(token, tagIds).getTags();
        return tags;
    }

    private BoardDetailResponseDto createBoardDetailResponse(String token, Board board, String profileString,
                                                             String profileType, Long memberId) {
        BoardDetailResponseDto responseDto = modelMapper.map(board, BoardDetailResponseDto.class);

        // TAG
        List<TagDetailDto> tags = getTagDtos(token, board);
        tags.forEach(tag -> {
            responseDto.getTagInfos().add(modelMapper.map(tag, TagDetailResponseDto.class));
        });

        // IMAGE
        List<BoardImage> boardImages = board.getBoardImages();
        boardImages.forEach(boardImage -> {
            responseDto.getImageStrings()
                    .add(imageService.getBase64EncodeToString(getProjectDir(), boardImage.getStoreFileName()));
            responseDto.getImageTypes().add(boardImage.getContentType());
        });

        responseDto.setProfileString(profileString);
        responseDto.setProfileType(profileType);
        responseDto.setApply(false);
        responseDto.setMaster(false);
        responseDto.setJoin(false);

        List<BoardMember> boardMembers = board.getBoardMembers();
        boardMembers.forEach(boardMember -> {
            if(boardMember.getMemberId() == memberId) {
                responseDto.setApply(true);
                if(boardMember.getStatus() == MemberStatus.COMPLETE) responseDto.setJoin(true);
            }
        });
        if(board.getCreatedBy().equals(String.valueOf(memberId))) responseDto.setMaster(true);

        return responseDto;
    }

    private List<TagDetailDto> getTagDtos(String token, Board board) {
        List<BoardTag> boardTags = board.getBoardTags();
        Set<Long> tagIds = boardTags.stream().map(BoardTag::getTagId).collect(Collectors.toSet());
        return tagService.getTagsByTagIds(token, tagIds).getTags();
    }

    private BoardDto createBoardDto(String title, String content, int recruit, String startDate, String endDate) {
        return new BoardDto(title, content, startDate, endDate, recruit);
    }

    private TimeTableListResponseDto createTimeTableListResponseDto(Long boardId) {
        TimeTableListResponseDto responseDto = new TimeTableListResponseDto();
        List<BoardTimeTable> timeTables = timeTableService.findBoardTimeTables(boardId);
        timeTables.forEach(timeTable -> {
            responseDto.getData().add(modelMapper.map(timeTable, TimeTableDetailResponseDto.class));
        });
        return responseDto;
    }
}
