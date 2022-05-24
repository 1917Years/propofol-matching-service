package propofol.matchingservice.api.controller.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class TimeTableDetailResponseDto {
    private Long id;
    private String week;
    private LocalTime startTime;
    private LocalTime endTime;
}
