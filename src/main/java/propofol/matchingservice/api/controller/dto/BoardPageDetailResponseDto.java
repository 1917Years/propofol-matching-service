package propofol.matchingservice.api.controller.dto;

import lombok.Data;
import propofol.matchingservice.domain.board.entitiy.BoardStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class BoardPageDetailResponseDto {
    private Long id;
    private String title;
    private String content;
    private String nickName;
    private int recruit; // 모집 인원
    private int recruited; // 모집된 인원
    private LocalDate startDate; // 시작 기간
    private LocalDate endDate; // 종료 기간
    private BoardStatus status;
    private String createdDate;

    private String image;
    private String imageType;

    private List<TagDetailResponseDto> tagInfos = new ArrayList<>();
}
