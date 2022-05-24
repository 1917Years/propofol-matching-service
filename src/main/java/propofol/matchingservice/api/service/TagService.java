package propofol.matchingservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.matchingservice.api.feign.TagServiceFeignClient;
import propofol.matchingservice.api.feign.dto.TagDto;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagServiceFeignClient tagServiceFeignClient;

    public TagDto getTagsByTagIds(String token, Set<Long> tagIds){
        return tagServiceFeignClient.getTags(token, tagIds);
    }

}
