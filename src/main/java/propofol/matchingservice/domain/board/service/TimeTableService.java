package propofol.matchingservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.matchingservice.domain.board.repository.TimeTableRepository;

@Service
@RequiredArgsConstructor
public class TimeTableService {

    private final TimeTableRepository timeTableRepository;

    @Transactional
    public String deleteOneById(Long id){
        timeTableRepository.deleteOneById(id);
        return "ok";
    }

    @Transactional
    public String deleteAllById(Long boardId){
        timeTableRepository.deleteAllByBoardId(boardId);
        return "ok";
    }

}
