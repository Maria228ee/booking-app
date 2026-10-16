package booking.model;


 // Исключение.

public class BookingConflictException extends Exception {

    private final TimeInterval requested;
    private final TimeInterval existing;


   // requested - запрошенный интервал
   // existing - конфликтующий существующий интервал

    public BookingConflictException(String message, TimeInterval requested, TimeInterval existing) {
        super(message);
        this.requested = requested;
        this.existing = existing;
    }

    public TimeInterval getRequested() { return requested; }
    public TimeInterval getExisting() { return existing; }
}
