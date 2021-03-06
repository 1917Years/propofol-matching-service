package propofol.matchingservice.api.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import propofol.matchingservice.api.common.annotation.Jwt;
import propofol.matchingservice.api.common.annotation.Token;
import propofol.matchingservice.api.common.porperties.FileProperties;
import propofol.matchingservice.api.controller.dto.*;
import propofol.matchingservice.api.controller.dto.MemberInfoDto;
import propofol.matchingservice.api.feign.dto.*;
import propofol.matchingservice.api.service.BoardMemberApiService;
import propofol.matchingservice.api.service.TagService;
import propofol.matchingservice.api.service.UserService;
import propofol.matchingservice.domain.board.entitiy.*;
import propofol.matchingservice.domain.board.service.BoardMemberService;
import propofol.matchingservice.domain.board.service.BoardService;
import propofol.matchingservice.domain.board.service.ImageService;
import propofol.matchingservice.domain.exception.NotFoundBoardMemberException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static propofol.matchingservice.domain.board.entitiy.MemberStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class BoardMemberController {

    private final BoardMemberService boardMemberService;
    private final BoardMemberApiService boardMemberApiService;
    private final UserService userService;
    private final BoardService boardService;
    private final ImageService imageService;
    private final TagService tagService;
    private final FileProperties fileProperties;
    private final ModelMapper modelMapper;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto NotFoundBoardMemberException(NotFoundBoardMemberException e){
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "?????? ??????", e.getMessage());
    }

    /**
     * ?????? ?????? ?????? ?????? ??????
     */
    @GetMapping("/joining")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getMyJoiningList(@RequestParam("page") int page,
                                        @Token Long memberId,
                                        @Jwt String token){

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "????????? ?????? ??????", createBoardPageResponseDto(page, memberId, token, COMPLETE));
    }

    /**
     * ?????? ?????? ?????? ?????? ??????
     */
    @GetMapping("/waiting")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getMyWaitingList(@RequestParam("page") int page,
                                        @Token Long memberId,
                                        @Jwt String token){

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "????????? ?????? ??????", createBoardPageResponseDto(page, memberId, token, WAITING));
    }

    /**
     * ?????? ?????? ?????? ??????
     */
    @GetMapping("/{boardId}/waitingList")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getBoardWaitingList(@PathVariable("boardId") Long boardId,
                                           @RequestParam("page") int page,
                                           @Jwt String token){

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "?????? ?????? ?????? ??????", createPageMemberDto(boardId, page, token, true));
    }

    /**
     * ?????? ?????? ?????? ??????
     */
    @GetMapping("/{boardId}/membersList")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getBoardMembersList(@PathVariable("boardId") Long boardId,
                                           @RequestParam("page") int page,
                                           @Jwt String token){
        return new ResponseDto(HttpStatus.OK.value(), "success", "?????? ?????? ?????? ??????",
                createPageMemberDto(boardId, page, token, false));
    }

    /**
     * ?????? ?????? ?????? ??????
     */
    @GetMapping("/{boardId}/membersList/noPage")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getBoardMembersList(@PathVariable("boardId") Long boardId,
                                           @Jwt String token){
        return new ResponseDto(HttpStatus.OK.value(), "success", "?????? ?????? ?????? ??????",
                createMemberDto(boardId, token));
    }

    /**
     * ????????????
     */
    @PostMapping("/{boardId}/apply")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveBoardMember(@PathVariable("boardId") Long boardId,
                                       @Token Long memberId,
                                       @Jwt String token){

        return new ResponseDto(HttpStatus.OK.value(), "success", "?????? ??????",
                boardMemberApiService.applyWithAlarm(memberId, boardService.findById(boardId), token));
    }

    /**
     * ????????????
     */
    @DeleteMapping("/{boardId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto cancelBoardMember(@PathVariable("boardId") Long boardId,
                                         @Token Long memberId){

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "?????? ??????", boardMemberService.deleteMember(boardId, memberId, null));
    }

    /**
     * ?????? ??????
     */
    @DeleteMapping("/{boardId}/secession")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto memberSecession(@PathVariable("boardId") Long boardId,
                                       @Token Long memberId,
                                       @Jwt String token){

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "?????? ??????", boardMemberApiService.outWithAlarm(boardId, memberId, token));
    }

    /**
     * ?????? ??????
     */
    @PostMapping("/{boardId}/{memberId}/approve")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto memberApprove(@PathVariable("boardId") Long boardId,
                                     @PathVariable("memberId") Long memberId,
                                     @Token Long requestId,
                                     @Jwt String token){

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "?????? ??????", boardMemberApiService.ApproveWithAlarm(boardId, memberId, requestId, token));
    }

    /**
     * ?????? ??????
     */
    @PostMapping("/{boardId}/{memberId}/reject")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto memberReject(@PathVariable("boardId") Long boardId,
                                    @PathVariable("memberId") Long memberId,
                                    @Token Long requestId,
                                    @Jwt String token){

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "?????? ??????", boardMemberApiService.rejectWithAlarm(boardId, memberId, requestId, token));
    }

    /**
     * ???????????? ?????? ?????? ????????? ?????? ??????
     */
    @GetMapping("/{boardId}/allMemberId")
    public Set<Long> findNoWaitingMemberId(@PathVariable("boardId") Long boardId){
        return boardMemberService.findAllMemberId(boardId);
    }

    private List<MemberDto> createMemberDto(Long boardId, String token) {
        List<BoardMember> boarMembers
                    = boardMemberService.getBoardByBoardIdAndNoMatchMemberStatus(boardId, WAITING);


        Set<Long> memberIds = boarMembers.stream().map(BoardMember::getMemberId).collect(Collectors.toSet());

        List<MemberDto> membersNoPage = userService.getMembersNoPage(token, memberIds);

        membersNoPage.forEach(data -> {
            boarMembers.forEach(boardMember -> {
                if(data.getId() == boardMember.getMemberId()){
                    data.setStatus(boardMember.getStatus().toString());
                }
            });
        });

        return membersNoPage;
    }

    private PageMembersResponseDto createPageMemberDto(Long boardId, int page, String token, boolean type) {
        List<BoardMember> boarMembers;
        if(type) {
            boarMembers
                    = boardMemberService.getBoardByBoardIdAndMemberStatus(boardId, WAITING);
        }else{
            boarMembers
                    = boardMemberService.getBoardByBoardIdAndNoMatchMemberStatus(boardId, WAITING);
        }

        Set<Long> memberIds = boarMembers.stream().map(BoardMember::getMemberId).collect(Collectors.toSet());

        MembersInfoResponseDto membersInfos = userService.getMembersInfo(token, memberIds, page);

        PageMembersResponseDto responseDto
                = modelMapper.map(membersInfos, PageMembersResponseDto.class);

        responseDto.getData().forEach(data -> {
            boarMembers.forEach(boardMember -> {
                if(data.getId() == boardMember.getMemberId()){
                    data.setStatus(boardMember.getStatus().toString());
                }
            });
        });

        return responseDto;
    }

    private BoardPageResponseDto createBoardPageResponseDto(int page, long memberId, String token, MemberStatus status) {
        Page<BoardMember> boardMemberPage = boardMemberService.findBoardMemberByMemberIdAndStatus(memberId, page, status);
        BoardPageResponseDto boardPageResponseDto = new BoardPageResponseDto();

        boardPageResponseDto.setTotalPageCount(boardMemberPage.getTotalPages());
        boardPageResponseDto.setTotalCount(boardMemberPage.getTotalElements());

        List<Board> boards = boardMemberPage.getContent().stream().map(boardMember -> boardMember.getBoard()).collect(Collectors.toList());
        List<TagDetailDto> tags = getTagDtosByPageBoard(token, boards);

        return createPageDto(boardPageResponseDto, boards, tags);
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

    private BoardPageResponseDto createPageDto(BoardPageResponseDto boardPageResponseDto,
                                               List<Board> boards, List<TagDetailDto> tags) {
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

            tags.forEach(tag -> {
                board.getBoardTags().forEach(boardTag -> {
                    if (Objects.equals(tag.getId(), boardTag.getTagId())) {
                        responseDto.getTagInfos().add(modelMapper.map(tag, TagDetailResponseDto.class));
                    }
                });
            });

            boardsResponseDto.add(responseDto);
        });

        return boardPageResponseDto;
    }

    private String getProjectDir() {
        return fileProperties.getProjectDir();
    }

    static class MemberBoardStatus{
        long boardId;
        MemberStatus memberStatus;

        public MemberBoardStatus(long boardId, MemberStatus memberStatus) {
            this.boardId = boardId;
            this.memberStatus = memberStatus;
        }
    }

}
