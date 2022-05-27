package propofol.matchingservice.api.feign.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MembersInfoResponseDto<T> {
    private Integer totalPageCount;
    private Long totalCount;

    private List<MemberInfoDto> data = new ArrayList<>();
}
