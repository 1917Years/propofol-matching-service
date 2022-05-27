package propofol.matchingservice.api.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoDto {
    private Long id;
    private String nickName;
    private String profileString;
    private String profileType;
    private String status;

    private List<TagDetailResponseDto> tagInfos = new ArrayList<>();
}
