package booking.service;

import booking.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingService {

    private final List<Room> rooms = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    public void addRoom(Room room) {
        if (room == null) throw new IllegalArgumentException("Помещение не может быть null");
        rooms.add(room);
    }

    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public List<Booking> getBookings() {
        return Collections.unmodifiableList(bookings);
    }


     // Создаёт бронь, проверяя пересечение с активными бронями этого помещения.
    // BookingConflictException если есть пересечение с активной бронью

    public Booking book(Room room, TimeInterval interval, Employee employee)
            throws BookingConflictException {
        if (room == null || interval == null || employee == null) {
            throw new IllegalArgumentException("Аргументы не могут быть null");
        }
        for (Booking existing : bookings) {
            if (existing.isCancelled()) continue;
            if (!existing.getRoom().equals(room)) continue;
            if (existing.getInterval().intersects(interval)) {
                throw new BookingConflictException(
                        "Помещение " + room.getNumber() + " занято в это время",
                        interval, existing.getInterval());
            }
        }
        Booking booking = new Booking(room, interval, employee);
        bookings.add(booking);
        return booking;
    }

    public boolean cancel(Booking booking) {
        if (booking == null || booking.isCancelled()) return false;
        booking.cancel();
        return true;
    }

     // Свободные окна помещения на указанный день в рабочих часах [9:00, 18:00).

    public List<TimeInterval> freeSlots(Room room, LocalDate day) {
        if (room == null || day == null) {
            throw new IllegalArgumentException("Помещение и день обязательны");
        }
        LocalDateTime dayStart = day.atTime(9, 0);
        LocalDateTime dayEnd = day.atTime(18, 0);

        List<TimeInterval> busy = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.isCancelled()) continue;
            if (!b.getRoom().equals(room)) continue;
            TimeInterval i = b.getInterval();
            if (i.start().toLocalDate().equals(day)) {
                busy.add(i);
            }
        }
        busy.sort((a, b) -> a.start().compareTo(b.start()));

        List<TimeInterval> free = new ArrayList<>();
        LocalDateTime cursor = dayStart;
        for (TimeInterval b : busy) {
            if (b.start().isAfter(cursor)) {
                free.add(new TimeInterval(cursor, b.start()));
            }
            if (b.end().isAfter(cursor)) {
                cursor = b.end();
            }
        }
        if (cursor.isBefore(dayEnd)) {
            free.add(new TimeInterval(cursor, dayEnd));
        }
        return free;
    }


    // Подбирает помещения, подходящие по вместимости и свободные в интервале.

    public List<Room> findSuitableRooms(int capacity, TimeInterval interval) {
        List<Room> result = new ArrayList<>();
        for (Room room : rooms) {
            if (!room.fitsCapacity(capacity)) continue;
            if (isRoomFree(room, interval)) {
                result.add(room);
            }
        }
        return result;
    }

    private boolean isRoomFree(Room room, TimeInterval interval) {
        for (Booking b : bookings) {
            if (b.isCancelled()) continue;
            if (!b.getRoom().equals(room)) continue;
            if (b.getInterval().intersects(interval)) return false;
        }
        return true;
    }
}