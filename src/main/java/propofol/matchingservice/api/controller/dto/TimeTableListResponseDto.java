package propofol.matchingservice.api.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TimeTableListResponseDto {
    List<TimeTableDetailResponseDto> data = new ArrayList<>();
}
