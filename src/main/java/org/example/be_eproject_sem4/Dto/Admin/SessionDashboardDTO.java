package org.example.be_eproject_sem4.Dto.Admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionDashboardDTO {
    private String id;
    private String cName;      // customer.fullName
    private String cId;        // customer.id
    private String rName;      // reader.fullName (nếu có)
    private String rId;        // reader.id
    private String topic;      // question.topic.name (giả định TopicQuestion có quan hệ này)
    private String question;   // question.content
    private String status;     // status
    private String time;       // Sẽ format kiểu "5 phút trước" hoặc Instant
}
