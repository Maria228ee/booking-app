package booking.model;

public record Whiteboard(int width, long price) implements Equipment {

    public Whiteboard {
        if (width <= 0) {
            throw new IllegalArgumentException("Ширина доски должна быть положительной");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Стоимость не может быть отрицательной");
        }
    }

    @Override
    public String description() {
        return "Доска " + width + " см";
    }

    @Override
    public long price() {
        return 0;
    }
}