package propofol.matchingservice.domain.board.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardDto {
    private String title;
    private String content;
    private String startDate;
    private String endDate;
    private Integer recruit;
}
