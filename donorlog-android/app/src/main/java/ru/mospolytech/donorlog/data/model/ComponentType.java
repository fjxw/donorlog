package ru.mospolytech.donorlog.data.model;

public enum ComponentType {
    WHOLE_BLOOD("Цельная кровь", 60),
    PLASMA("Плазма", 14),
    PLATELETS("Тромбоциты", 14),
    ERYTHROCYTES("Эритроциты", 60);

    private final String titleRu;
    private final int intervalDays;

    ComponentType(String titleRu, int intervalDays) {
        this.titleRu = titleRu;
        this.intervalDays = intervalDays;
    }

    public String getTitleRu() {
        return titleRu;
    }

    public int getIntervalDays() {
        return intervalDays;
    }

    public static ComponentType fromString(String text) {
        if (text == null) return WHOLE_BLOOD;
        for (ComponentType type : ComponentType.values()) {
            if (type.name().equalsIgnoreCase(text) || type.titleRu.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return WHOLE_BLOOD;
    }
}
