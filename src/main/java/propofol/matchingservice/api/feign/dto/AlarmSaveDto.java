package propofol.matchingservice.api.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import propofol.matchingservice.api.feign.AlarmType;

@Data
@AllArgsConstructor
public class AlarmSaveDto {
    private long toId;
    private long boardId;
    private String message;
    private AlarmType type;
}
