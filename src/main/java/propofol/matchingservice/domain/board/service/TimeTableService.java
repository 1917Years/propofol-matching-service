package propofol.matchingservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.matchingservice.domain.board.entitiy.BoardTimeTable;
import propofol.matchingservice.domain.board.repository.TimeTableRepository;

import java.util.List;

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

    public List<BoardTimeTable> findBoardTimeTables(Long boardId){
        return timeTableRepository.findTimeTablesByBoardId(boardId);
    }

}
