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

    public Page<BoardTag> getAllByTagIds(Set<Long> tagIds, int page, long memberId){
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return tagRepository.findAllByTagIdsAndNotMine(tagIds, String.valueOf(memberId), pageRequest);
    }

    public Page<BoardTag> getResultByConditions(String keyword, int page, Set<Long> tagIds, long memberId) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return tagRepository.findAllByConditions(tagIds, keyword, String.valueOf(memberId), pageRequest);
    }
}
