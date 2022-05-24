package propofol.matchingservice.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propofol.matchingservice.api.common.porperties.FileProperties;
import propofol.matchingservice.api.controller.dto.ResponseDto;
import propofol.matchingservice.domain.board.entitiy.Board;
import propofol.matchingservice.domain.board.service.BoardService;
import propofol.matchingservice.domain.board.service.ImageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matchings/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final BoardService boardService;
    private final FileProperties fileProperties;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getStoredNames(@RequestParam(value = "file", required = false) List<MultipartFile> files,
                                      @RequestParam(value = "boardId" , required = false) Long boardId){
        Board findBoard = null;
        if(boardId != null) {
            findBoard = boardService.findWithImagesById(boardId);
        }

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "이미지 저장 성공", imageService.getStoredNamesAndSaveImages(files, findBoard, getProjectDir()));
    }

    @GetMapping("{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public String findEncode64Image(@PathVariable("fileName") String fileName){
        return imageService.getBase64EncodeToString(getProjectDir(), fileName);
    }

    private String getProjectDir(){
        return fileProperties.getProjectDir();
    }
}
