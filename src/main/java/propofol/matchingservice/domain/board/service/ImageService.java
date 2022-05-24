package propofol.matchingservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import propofol.matchingservice.api.common.exception.SaveImageException;
import propofol.matchingservice.domain.board.entitiy.Board;
import propofol.matchingservice.domain.board.entitiy.BoardImage;
import propofol.matchingservice.domain.board.repository.ImageRepository;
import propofol.matchingservice.domain.exception.FileIoException;
import propofol.matchingservice.domain.exception.NotFoundFileException;
import propofol.matchingservice.domain.exception.NotFoundImageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public BoardImage saveImage(MultipartFile file, String dir) {
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        String path = findDirPath(dir);
        File projectDir = new File(path);

        if(!projectDir.exists()) projectDir.mkdir();

        String ext = getFileExt(originalFilename);
        String storeFilename = getStoreFilename(ext);

        String pathname = path + "/" + storeFilename;
        File saveFile = new File(pathname);
        try {
            file.transferTo(saveFile);
        }catch (IOException e){
            throw new SaveImageException("이미지 저장 실패!", e);
        }

        BoardImage image = BoardImage.createImage()
                .storeFileName(storeFilename)
                .uploadFileName(originalFilename)
                .contentType(contentType)
                .build();

        return image;
    }

    public void deleteImages(List<BoardImage> boardImages, String dir){
        boardImages.forEach(image -> {
            String path = findDirPath(dir) + "/" + image.getStoreFileName();
            File savedFile = new File(path);
            if(savedFile.exists()) savedFile.delete();
        });
    }

    public List<BoardImage> getImages(Long boardId){
        return imageRepository.findAllByBoardId(boardId).orElseThrow(() -> {
            throw new NotFoundImageException("이미지 조회 오류");
        });
    }

    private String getFileExt(String originalFilename) {
        int index = originalFilename.lastIndexOf(".");
        return originalFilename.substring(index + 1);
    }

    @Transactional
    public List<String> getStoredNamesAndSaveImages(List<MultipartFile> files, Board findBoard, String dir) {
        String path = "http://localhost:8000/matching-service/api/v1/images";
        List<String> fileNames = new ArrayList<>();
        List<BoardImage> images = new ArrayList<>();

        if(findBoard != null && findBoard.getBoardImages().size() != 0){
            deleteImages(findBoard.getBoardImages(), dir);
            imageRepository.deleteAllByBoardId(findBoard.getId());
        }

        if(files != null) {
            files.forEach(file -> {
                BoardImage image = saveImage(file, dir);
                images.add(image);
                fileNames.add(path + "/" + image.getStoreFileName());
            });
        }

        if(images.size() != 0) imageRepository.saveAll(images);

        return fileNames;
    }

    private String getStoreFilename(String ext) {
        return UUID.randomUUID().toString() + "." + ext;
    }

    private String findDirPath(String dir){
        Path relativePath = Paths.get("");
        return relativePath.toAbsolutePath().toString() + "/" + dir;
    }

    public String getBase64EncodeToString(String dir, String storeName) {
        byte[] bytes = null;
        String path = findDirPath(dir) + "/" + storeName;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
            bytes = IOUtils.toByteArray(fileInputStream);
        } catch (FileNotFoundException e) {
            throw new NotFoundFileException("파일을 찾을 수 없습니다.", e);
        } catch (IOException e) {
            throw new FileIoException("파일 변환 실패", e);
        }

        return Base64.getEncoder().encodeToString(bytes);
    }

    public List<BoardImage> getImagesByStoreNames(Set<String> names){
        return imageRepository.findAllByNames(names);
    }

    public String deleteAllByBoardId(Long boardId){
        imageRepository.deleteAllByBoardId(boardId);
        return "ok";
    }
}
