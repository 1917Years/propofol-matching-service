package propofol.matchingservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.matchingservice.api.feign.UserServiceFeignClient;
import propofol.matchingservice.api.feign.dto.MemberInfoDto;

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
}
