package propofol.matchingservice.api.controller.dto;

import lombok.Data;

@Data
public class ResponseDto<T> {
    private int status;
    private String result;
    private String message;
    private T data;

    public ResponseDto(int status, String result, String message, T data) {
        this.status = status;
        this.result = result;
        this.message = message;
        this.data = data;
    }
}
