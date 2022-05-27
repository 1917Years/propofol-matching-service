package propofol.matchingservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.matchingservice.api.feign.AlarmServiceFeignClient;
import propofol.matchingservice.api.feign.AlarmType;
import propofol.matchingservice.api.feign.dto.AlarmSaveDto;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmServiceFeignClient alarmServiceFeignClient;

    public void save(String token, String toId, Long boardId, String message, AlarmType type) {
        alarmServiceFeignClient.saveAlarm(token, new AlarmSaveDto(Long.parseLong(toId), boardId, message, type));
    }
}
