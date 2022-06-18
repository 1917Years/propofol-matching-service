package propofol.matchingservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import propofol.matchingservice.domain.board.entitiy.BoardTag;
import propofol.matchingservice.domain.board.repository.TagRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BoardTagService {

    private final TagRepository tagRepository;

    public String deleteAllByBoardId(Long boardId){
        tagRepository.deleteAllByBoardId(boardId);
        return "ok";
    }

    public void saveTag(BoardTag tag) {
        tagRepository.save(tag);
    }
}
