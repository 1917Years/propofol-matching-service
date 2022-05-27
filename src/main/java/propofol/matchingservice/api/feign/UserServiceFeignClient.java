package propofol.matchingservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import propofol.matchingservice.api.feign.dto.MemberInfoDto;
import propofol.matchingservice.api.feign.dto.MembersInfoResponseDto;

import java.util.Set;

@FeignClient("user-service")
public interface UserServiceFeignClient {

    @GetMapping("/api/v1/members/info/{memberId}")
    MemberInfoDto getMemberInfo(@RequestHeader(name = "Authorization") String token,
                                @PathVariable("memberId") String memberId);

    @GetMapping("/api/v1/members/info/{memberId}/nickName")
    String getMemberNickNAme(@RequestHeader(name = "Authorization") String token,
                             @PathVariable("memberId") String memberId);

    @GetMapping("/api/v1/members/info")
    MembersInfoResponseDto getMembersInfo(@RequestHeader(name = "Authorization") String token,
                                          @RequestParam("memberId") Set<Long> memberIds,
                                          @RequestParam("page") int page);
}
