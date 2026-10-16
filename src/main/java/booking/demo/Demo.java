package booking.demo;

import booking.model.*;
import booking.service.BookingService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


 // Консольная демонстрация всех операций базового уровня.
 // Наполняет модель 30+ объектами, последовательно показывает каждую операцию.

public class Demo {

    public static void main(String[] args) {
        BookingService service = new BookingService();

        // ---- Наполняем модель ----
        Equipment projector1 = new Projector("Epson EB-X05", 15_000);
        Equipment projector2 = new Projector("BenQ MX560", 12_000);
        Equipment board1 = new Whiteboard(120, 500);
        Equipment board2 = new Whiteboard(200, 800);

        service.addRoom(new Room("A101", RoomType.MEETING_ROOM, 6, List.of(projector1, board1)));
        service.addRoom(new Room("A102", RoomType.MEETING_ROOM, 4, List.of(board1)));
        service.addRoom(new Room("A103", RoomType.CLASSROOM, 20, List.of(projector2, board2)));
        service.addRoom(new Room("B201", RoomType.CONFERENCE_HALL, 100, List.of(projector1, projector2, board2)));
        service.addRoom(new Room("B202", RoomType.MEETING_ROOM, 8, List.of(projector2)));
        service.addRoom(new Room("B203", RoomType.CLASSROOM, 25, List.of(projector1, board1, board2)));
        service.addRoom(new Room("C301", RoomType.MEETING_ROOM, 5, List.of(board2)));
        service.addRoom(new Room("C302", RoomType.CONFERENCE_HALL, 80, List.of(projector1, board2)));
        service.addRoom(new Room("C303", RoomType.CLASSROOM, 15, List.of(projector2)));
        service.addRoom(new Room("D401", RoomType.MEETING_ROOM, 10, List.of(projector1, board1, board2)));

        Employee[] employees = {
                new Employee("E001", "Иванов Иван"),
                new Employee("E002", "Петрова Анна"),
                new Employee("E003", "Сидоров Пётр"),
                new Employee("E004", "Кузнецова Мария"),
                new Employee("E005", "Смирнов Алексей"),
                new Employee("E006", "Попова Ольга"),
                new Employee("E007", "Волков Дмитрий"),
                new Employee("E008", "Морозова Елена"),
                new Employee("E009", "Новиков Сергей"),
                new Employee("E010", "Фёдорова Ирина")
        };

        System.out.println("=== Модель заполнена ===");
        System.out.println("Помещений: " + service.getRooms().size());
        System.out.println("Сотрудников: " + employees.length);
        System.out.println("Оснащения: 4");
        System.out.println();


        System.out.println("=== Операция 1: создание броней ===");
        LocalDate day = LocalDate.of(2025, 6, 10);
        List<Booking> allBookings = new ArrayList<>();
        int created = 0;
        for (Room room : service.getRooms()) {
            for (int h = 9; h < 12; h++) {
                try {
                    TimeInterval interval = new TimeInterval(day.atTime(h, 0), day.atTime(h + 1, 0));
                    Employee emp = employees[created % employees.length];
                    Booking b = service.book(room, interval, emp);
                    allBookings.add(b);
                    created++;
                } catch (BookingConflictException e) {
                    // осмысленная обработка: показываем, с чем конфликт
                    System.out.println("  Конфликт: " + e.getMessage()
                            + " (запрошено " + e.getRequested()
                            + ", занято " + e.getExisting() + ")");
                }
            }
        }
        System.out.println("Создано броней: " + created);
        System.out.println();


        System.out.println("=== Операция 2: конфликт бронирования ===");
        Room a101 = service.getRooms().get(0);
        try {
            service.book(a101,
                    new TimeInterval(day.atTime(9, 30), day.atTime(10, 30)),
                    employees[0]);
            System.out.println("Ошибка: конфликт не обнаружен!");
        } catch (BookingConflictException e) {
            System.out.println("Ожидаемый конфликт: " + e.getMessage());
            System.out.println("Запрошено:      " + e.getRequested());
            System.out.println("Конфликтует с:  " + e.getExisting());
        }
        System.out.println();


        System.out.println("=== Операция 3: отмена брони ===");
        Booking toCancel = allBookings.get(0);
        System.out.println("До отмены:     " + toCancel);
        boolean ok = service.cancel(toCancel);
        System.out.println("Отмена выполнена: " + ok);
        System.out.println("После отмены:  " + toCancel);
        boolean again = service.cancel(toCancel);
        System.out.println("Повторная отмена: " + again);
        System.out.println();

        System.out.println("=== Операция 4: свободные окна помещения A101 на " + day + " ===");
        for (TimeInterval slot : service.freeSlots(a101, day)) {
            System.out.println("  " + slot.start().toLocalTime()
                    + " – " + slot.end().toLocalTime()
                    + " (" + slot.durationMinutes() + " мин)");
        }
        System.out.println();

        System.out.println("=== Операция 5: подбор помещения на 5 человек, 15:00–16:00 ===");
        TimeInterval want = new TimeInterval(day.atTime(15, 0), day.atTime(16, 0));
        List<Room> suitable = service.findSuitableRooms(5, want);
        if (suitable.isEmpty()) {
            System.out.println("  Нет подходящих помещений");
        } else {
            for (Room r : suitable) {
                System.out.println("  " + r + " оснащение: " + describeEquipment(r));
            }
        }
        System.out.println();

        System.out.println("=== Операция 6: полиморфный вывод оснащения A101 ===");
        for (Equipment e : a101.getEquipment()) {
            System.out.println("  " + e.description()
                    + " — " + e.price() + " коп/час");
        }
        System.out.println();

        System.out.println("=== Операция 7: equals/hashCode сотрудника ===");
        Employee e1 = new Employee("E001", "Иванов Иван");
        Employee e2 = new Employee("E001", "Другое Имя");
        Employee e3 = new Employee("E002", "Иванов Иван");
        System.out.println("e1.equals(e2) = " + e1.equals(e2));
        System.out.println("hashCode равны: " + (e1.hashCode() == e2.hashCode()));
        System.out.println("e1.equals(e3) = " + e1.equals(e3));
        System.out.println();

        System.out.println("=== Демонстрация завершена ===");
        System.out.println("Всего броней в системе: " + service.getBookings().size());
    }

    private static String describeEquipment(Room room) {
        StringBuilder sb = new StringBuilder();
        for (Equipment e : room.getEquipment()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(e.description());
        }
        return sb.toString();
    }
}