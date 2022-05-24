package propofol.matchingservice.api.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TimetableSaveRequestDto {
    @NotBlank(message = "week null")
    private String week;
    @NotBlank(message = "startTime null")
    private String startTime;
    @NotBlank(message = "endTime null")
    private String endTime;
}
