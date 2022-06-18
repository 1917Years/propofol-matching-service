package propofol.matchingservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.matchingservice.api.feign.UserServiceFeignClient;
import propofol.matchingservice.api.feign.dto.MemberDto;
import propofol.matchingservice.api.feign.dto.MemberInfoDto;
import propofol.matchingservice.api.feign.dto.MembersInfoResponseDto;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserServiceFeignClient userServiceFeignClient;

    public MemberInfoDto getMemberInfo(String token, String memberId){
        return userServiceFeignClient.getMemberInfo(token, memberId);
    }

    public String getMemberNickName(String token, String memberId){
        return userServiceFeignClient.getMemberNickNAme(token, memberId);
    }

    public MembersInfoResponseDto getMembersInfo(String token, Set<Long> memberIds, int page){
        return userServiceFeignClient.getMembersInfo(token, memberIds, page);
    }

    public List<MemberDto> getMembersNoPage(String token, Set<Long> memberIds){
        return userServiceFeignClient.getMembersNoPage(token, memberIds);
    }
}
