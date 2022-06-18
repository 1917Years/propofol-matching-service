package propofol.matchingservice.domain.board.entitiy;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_board_image")
public class BoardImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_board_image_id")
    private Long id;

    private String uploadFileName; // 업로드된 파일 이름
    private String storeFileName; // 저장 이름
    private String contentType; // 타입

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_board_id")
    private Board board;

    public void changeBoard(Board board){
        this.board = board;
    }

    @Builder(builderMethodName = "createImage")
    public BoardImage(String uploadFileName, String storeFileName, String contentType) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.contentType = contentType;
    }
}
