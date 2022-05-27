package propofol.matchingservice.domain.board.entitiy;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.StringUtils;
import propofol.matchingservice.domain.BaseEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@DynamicInsert
@Table(name = "project_board")
public class Board extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_board_id")
    private Long id;

    private String title;
    @Lob
    private String content;
    private String nickName;
    private int recruit; // 모집 인원
    private int recruited; // 모집된 인원
    private LocalDate startDate; // 시작 기간
    private LocalDate endDate; // 종료 기간

    @Enumerated(value = EnumType.STRING)
    private BoardStatus status;

    public void changeBoardStatus(boolean check){
        if(check) this.status = BoardStatus.ACTIVE;
        else this.status = BoardStatus.NONACTIVE;
    }

    public void upRecruited(){
        this.recruited += 1;
    }

    public void downRecruited(){
        this.recruited -= 1;
    }
    
    public void updateBoard(String title, String content, LocalDate startDate, LocalDate endDate, Integer recruit){
        if(StringUtils.hasText(title)) this.title = title;
        if(StringUtils.hasText(content)) this.content = content;
        if(recruit != null) this.recruit = recruit;
        if(startDate != null) this.startDate = startDate;
        if(endDate != null) this.endDate = endDate;
    }

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardTag> boardTags = new ArrayList<>();

    @BatchSize(size = 30)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardTimeTable> boardTimeTables = new ArrayList<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardImage> boardImages = new ArrayList<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardMember> boardMembers = new ArrayList<>();

    @Builder(builderMethodName = "boardCreate")
    public Board(String title, String content, String nickName, int recruit, int recruited,
                 LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.content = content;
        this.nickName = nickName;
        this.recruit = recruit;
        this.recruited = recruited;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
