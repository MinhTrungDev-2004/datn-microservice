package datn.category_service.models.entities.enums;

public enum TypeCategory {
    THU("Tiền Vào"),
    CHI("Tiền Ra");

    private final String displayName;
    TypeCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
