package org.example.be_eproject_sem4.Entity;

public enum ReadingStatus {
    PENDING,        // Đã gửi yêu cầu, chưa có Reader nhận
    MATCHED,
    PROCESSING,     // Reader đang xem/đang soạn câu trả lời
    COMPLETED,      // Đã trả kết quả
    CANCELLED,      // Khách hủy hoặc Reader từ chối
    REJECTED,
    ACCEPTED,
    WAITING_PAYMENT
}
