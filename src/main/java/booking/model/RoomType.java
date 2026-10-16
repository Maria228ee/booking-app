package booking.model;

public enum RoomType {
    MEETING_ROOM("Переговорная", false) {
        @Override
        public String getDescription() {
            return "Комната для переговоров небольшой группы";
        }
    },
    CONFERENCE_HALL("Конференц-зал", true) {
        @Override
        public String getDescription() {
            return "Большой зал для конференций и презентаций";
        }
    },
    CLASSROOM("Учебный класс", false) {
        @Override
        public String getDescription() {
            return "Аудитория для занятий и тренингов";
        }
    };

    private final String displayName;
    private final boolean requiresRegistration;

    RoomType(String displayName, boolean requiresRegistration) {
        this.displayName = displayName;
        this.requiresRegistration = requiresRegistration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRequiresRegistration() {
        return requiresRegistration;
    }

    public abstract String getDescription();
}

