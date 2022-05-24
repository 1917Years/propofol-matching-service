package propofol.matchingservice.api.feign.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MemberInfoDto {
    private String nickName;
    private String profileString;
    private String profileType;

    private List<TagDetailDto> tagInfos = new ArrayList<>();
}
