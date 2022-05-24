package propofol.matchingservice.api.feign.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TagDto {
    private List<TagDetailDto> tags = new ArrayList<>();
}
