package datn.datnbe.Handler.Sorted;

import datn.datnbe.Entity.Feedback;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FeedbackSorter {
    public static List<Feedback> sortFeedback(List<Feedback> feedbackList) {
        return feedbackList.stream()
                .sorted(Comparator.comparing(Feedback::getDatetime)
                        .thenComparing(Feedback::getRate, Comparator.reverseOrder())
                        .thenComparing(feedback -> feedback.getContent() != null && !feedback.getContent().isEmpty(), Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
