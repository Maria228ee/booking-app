package booking.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Room {

    private final String number;
    private final RoomType type;
    private final int capacity;
    private final List<Equipment> equipment;


    //  number - номер помещения
    // capacity - вместимость
    // equipment - список оснащения

    public Room(String number, RoomType type, int capacity, List<Equipment> equipment) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Номер помещения обязателен");
        }
        if (type == null) {
            throw new IllegalArgumentException("Тип помещения обязателен");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Вместимость должна быть положительной");
        }
        if (equipment == null) {
            throw new IllegalArgumentException("Список оснащения не может быть null");
        }
        this.number = number;
        this.type = type;
        this.capacity = capacity;
        this.equipment = new ArrayList<>(equipment); // защитная копия
    }

    public String getNumber() { return number; }
    public RoomType getType() { return type; }
    public int getCapacity() { return capacity; }

    public List<Equipment> getEquipment() {
        return Collections.unmodifiableList(equipment);
    }

    public boolean fitsCapacity(int people) {
        return people > 0 && people <= capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room room)) return false; // pattern matching
        return number.equals(room.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return "Помещение " + number + " (" + type.getDisplayName()
                + ", до " + capacity + " чел.)";
    }
}