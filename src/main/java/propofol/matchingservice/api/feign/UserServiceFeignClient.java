package propofol.matchingservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import propofol.matchingservice.api.feign.dto.MemberInfoDto;

@FeignClient("user-service")
public interface UserServiceFeignClient {

    @GetMapping("/api/v1/members/info/{memberId}")
    MemberInfoDto getMemberInfo(@RequestHeader(name = "Authorization") String token,
                                @PathVariable("memberId") String memberId);

    @GetMapping("/api/v1/members/info/{memberId}/nickName")
    String getMemberNickNAme(@RequestHeader(name = "Authorization") String token,
                                    @PathVariable("memberId") String memberId);
}
