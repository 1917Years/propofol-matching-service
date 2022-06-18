package propofol.matchingservice.api.feign.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MembersInfoResponseDto {
    private Integer totalPageCount;
    private Long totalCount;

    private List<MemberDto> data = new ArrayList<>();
}
