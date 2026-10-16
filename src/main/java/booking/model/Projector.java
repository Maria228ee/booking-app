package booking.model;

public record Projector(String model, long price) implements Equipment {

    public Projector {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Модель проектора обязательна");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Стоимость не может быть отрицательной");
        }
    }

    @Override
    public String description() {
        return "Проектор " + model;
    }

    @Override
    public long price() {
        return 0;
    }
}
