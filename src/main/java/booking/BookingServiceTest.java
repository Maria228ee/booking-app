package booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import booking.model.*;
import booking.service.BookingService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
 // Тесты

class BookingServiceTest {

    private BookingService service;
    private Room room;
    private Employee employee;
    private LocalDate day;

    @BeforeEach
    void setUp() {
        service = new BookingService();
        room = new Room("A1", RoomType.MEETING_ROOM, 5, List.of(new Projector("X", 1000)));
        employee = new Employee("E1", "Тест Тестов");
        service.addRoom(room);
        day = LocalDate.of(2025, 6, 10);
    }

    // 1. Нормальный интервал
    @Test
    void validInterval() {
        TimeInterval i = new TimeInterval(day.atTime(9, 0), day.atTime(10, 0));
        assertEquals(60, i.durationMinutes());
    }

    // 2. Конец раньше начала — исключение
    @Test
    void EndBeforeStart() {
        assertThrows(IllegalArgumentException.class,
                () -> new TimeInterval(day.atTime(10, 0), day.atTime(9, 0)));
    }

    // 3. Граница: конец равен началу — исключение
    @Test
    void EqualBounds() {
        assertThrows(IllegalArgumentException.class,
                () -> new TimeInterval(day.atTime(9, 0), day.atTime(9, 0)));
    }

    // 4. null в интервале — исключение
    @Test
    void intervalNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new TimeInterval(null, day.atTime(9, 0)));
    }

    // 5. Пересечение интервалов
    @Test
    void intervalsIntersect() {
        TimeInterval a = new TimeInterval(day.atTime(9, 0), day.atTime(10, 0));
        TimeInterval b = new TimeInterval(day.atTime(9, 30), day.atTime(10, 30));
        assertTrue(a.intersects(b));
    }

    // 6. Граница: касание
    @Test
    void touching() {
        TimeInterval a = new TimeInterval(day.atTime(9, 0), day.atTime(10, 0));
        TimeInterval b = new TimeInterval(day.atTime(10, 0), day.atTime(11, 0));
        assertFalse(a.intersects(b));
    }

    // 7. Успешная бронь
    @Test
    void Booking() throws Exception {
        Booking b = service.book(room,
                new TimeInterval(day.atTime(9, 0), day.atTime(10, 0)), employee);
        assertFalse(b.isCancelled());
        assertEquals(1, service.getBookings().size());
    }

    // 8. Конфликт BookingConflictException
    @Test
    void conflicting() throws Exception {
        service.book(room, new TimeInterval(day.atTime(9, 0), day.atTime(10, 0)), employee);
        assertThrows(BookingConflictException.class, () ->
                service.book(room,
                        new TimeInterval(day.atTime(9, 30), day.atTime(10, 30)), employee));
    }

    // 9. Отмена и освобождение слота
    @Test
    void cancel() throws Exception {
        Booking b = service.book(room,
                new TimeInterval(day.atTime(9, 0), day.atTime(10, 0)), employee);
        assertTrue(service.cancel(b));
        assertDoesNotThrow(() ->
                service.book(room,
                        new TimeInterval(day.atTime(9, 0), day.atTime(10, 0)), employee));
    }

    // 10. Повторная отмена
    @Test
    void secondCancel() throws Exception {
        Booking b = service.book(room,
                new TimeInterval(day.atTime(9, 0), day.atTime(10, 0)), employee);
        assertTrue(service.cancel(b));
        assertFalse(service.cancel(b));
    }

    // 11. Свободные окна пустого дня
    @Test
    void freeSlots() {
        List<TimeInterval> free = service.freeSlots(room, day);
        assertEquals(1, free.size());
        assertEquals(day.atTime(9, 0), free.get(0).start());
        assertEquals(day.atTime(18, 0), free.get(0).end());
    }

    // 12. Свободные окна с бронью среди дня
    @Test
    void SlotsWithBooking() throws Exception {
        service.book(room,
                new TimeInterval(day.atTime(10, 0), day.atTime(11, 0)), employee);
        List<TimeInterval> free = service.freeSlots(room, day);
        assertEquals(2, free.size());
        assertEquals(day.atTime(9, 0), free.get(0).start());
        assertEquals(day.atTime(10, 0), free.get(0).end());
        assertEquals(day.atTime(11, 0), free.get(1).start());
        assertEquals(day.atTime(18, 0), free.get(1).end());
    }

    // 13. Подбор помещения по вместимости
    @Test
    void Capacity() {
        TimeInterval want = new TimeInterval(day.atTime(15, 0), day.atTime(16, 0));
        assertTrue(service.findSuitableRooms(5, want).contains(room));
        assertTrue(service.findSuitableRooms(6, want).isEmpty());
    }

    // 14. Контракт equals/hashCode
    @Test
    void equalsHashCode() {
        Employee a = new Employee("E1", "Имя1");
        Employee b = new Employee("E1", "Имя2");
        Employee c = new Employee("E2", "Имя1");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "строка");
    }
}