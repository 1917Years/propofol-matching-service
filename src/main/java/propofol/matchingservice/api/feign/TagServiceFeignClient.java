package propofol.matchingservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import propofol.matchingservice.api.feign.dto.TagDto;

import java.util.Set;

@FeignClient("tag-service")
public interface TagServiceFeignClient {

    @GetMapping("/api/v1/tags/setIds")
    TagDto getTags(@RequestHeader("Authorization") String token,
                   @RequestParam("ids") Set<Long> tagIds);
}
