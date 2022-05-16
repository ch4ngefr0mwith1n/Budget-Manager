package budget;

public enum Category {
    FOOD("Food:"),
    CLOTHES("Clothes:"),
    ENTERTAINMENT("Entertainment:"),
    OTHER("Other:");

    String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}