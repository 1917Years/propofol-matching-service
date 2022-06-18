package propofol.matchingservice.api.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String nickName;
    private String email;
    private String profileString;
    private String profileType;
    private String status;

    private List<TagDetailDto> tags = new ArrayList<>();
}
