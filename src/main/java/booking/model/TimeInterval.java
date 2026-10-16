package booking.model;

import java.time.Duration;
import java.time.LocalDateTime;

public record TimeInterval(LocalDateTime start, LocalDateTime end) {

    public TimeInterval {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Начало и конец интервала не могут быть null");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                    "Конец интервала должен быть позже начала: " + start + " -> " + end);
        }
    }


  //  [a,b) и [c,d) пересекаются, если a<d и c<b.
   //  Касание границами (end == start) пересечением не считается.

    public boolean intersects(TimeInterval other) {
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }

    public long durationMinutes() {
        return Duration.between(start, end).toMinutes();
    }
}
