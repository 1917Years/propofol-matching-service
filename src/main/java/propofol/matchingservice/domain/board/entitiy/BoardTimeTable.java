package propofol.matchingservice.domain.board.entitiy;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_timetable")
public class BoardTimeTable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_board_timetable_id")
    private Long id;

    private String week;

    @Column(columnDefinition = "TIME")
    private LocalTime startTime;

    @Column(columnDefinition = "TIME")
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_board_id")
    private Board board;

    public void changeBoard(Board board){this.board = board;}

    @Builder(builderMethodName = "createTimeTable")
    public BoardTimeTable(String week, LocalTime startTime, LocalTime endTime) {
        this.week = week;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
