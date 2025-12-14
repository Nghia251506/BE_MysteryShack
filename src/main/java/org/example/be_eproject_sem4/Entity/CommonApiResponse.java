package org.example.be_eproject_sem4.Entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class CommonApiResponse<T> {  // ← Đổi tên ở đây
    private boolean success;
    private String message;
    private T data;

    public CommonApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    public CommonApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
