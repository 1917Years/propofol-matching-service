package propofol.matchingservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import propofol.matchingservice.api.feign.dto.AlarmSaveDto;

@FeignClient("alarm-service")
public interface AlarmServiceFeignClient {

    @PostMapping("api/v1/alarms")
    void saveAlarm(@RequestHeader(value = "Authorization", required = false) String token,
                   @RequestBody AlarmSaveDto alarmSaveDto);
}
