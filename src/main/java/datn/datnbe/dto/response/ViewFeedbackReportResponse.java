package datn.datnbe.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewFeedbackReportResponse {
    private int feedbackId;
    private int rate;
    private String content;
    private LocalDateTime datetime;
    private int bookingId;
    private int carId;
    private int carOwnerId;
    private int userId;
}
