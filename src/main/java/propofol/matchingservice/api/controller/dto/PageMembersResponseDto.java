package propofol.matchingservice.api.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PageMembersResponseDto {
    private Integer totalPageCount;
    private Long totalCount;

    private List<MemberInfoDto> data = new ArrayList<>();
}
