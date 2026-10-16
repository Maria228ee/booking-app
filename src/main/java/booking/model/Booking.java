package booking.model;

import java.util.Objects;


 // Поле cancelled только чтение + метод cancel().

public class Booking {

    private final Room room;
    private final TimeInterval interval;
    private final Employee employee;
    private boolean cancelled;

    public Booking(Room room, TimeInterval interval, Employee employee) {
        if (room == null || interval == null || employee == null) {
            throw new IllegalArgumentException("Помещение, интервал и сотрудник обязательны");
        }
        this.room = room;
        this.interval = interval;
        this.employee = employee;
        this.cancelled = false;
    }

    public Room getRoom() { return room; }
    public TimeInterval getInterval() { return interval; }
    public Employee getEmployee() { return employee; }
    public boolean isCancelled() { return cancelled; }

    public void cancel() {
        this.cancelled = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking booking)) return false;
        return room.equals(booking.room)
                && interval.equals(booking.interval)
                && employee.equals(booking.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(room, interval, employee);
    }

    @Override
    public String toString() {
        return "Бронь: " + room.getNumber() + " " + interval.start() + "–" + interval.end()
                + " (" + employee.getFullName() + ")"
                + (cancelled ? "ОТМЕНЕНА" : "");
    }
}