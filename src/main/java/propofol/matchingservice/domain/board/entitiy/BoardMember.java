package propofol.matchingservice.domain.board.entitiy;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_member")
public class BoardMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private long memberId;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @BatchSize(size = 10)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_board_id")
    private Board board;

    public void changeBoard(Board board) {this.board = board;}
    public void changeStatus(MemberStatus status) {this.status = status;}

    @Builder(builderMethodName = "createBoardMember")
    public BoardMember(long memberId, MemberStatus status) {
        this.memberId = memberId;
        this.status = status;
    }
}
