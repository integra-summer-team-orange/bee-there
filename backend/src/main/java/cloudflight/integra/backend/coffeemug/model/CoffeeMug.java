package cloudflight.integra.backend.coffeemug.model;

import cloudflight.integra.backend.coffee.model.Coffee;

public class CoffeeMug {
    private Long id;
    private String color;
    private int capacityMl;
    private boolean clean;
    // TODO: replace with @ManyToOne when JPA is introduced
    private Coffee coffee;

    public CoffeeMug(Long id, String color, int capacityMl, boolean clean, Coffee coffee) {
        this.id = id;
        this.color = color;
        this.capacityMl = capacityMl;
        this.clean = clean;
        this.coffee = coffee;
    }

    public CoffeeMug() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCapacityMl() {
        return capacityMl;
    }

    public void setCapacityMl(int capacityMl) {
        this.capacityMl = capacityMl;
    }

    public boolean isClean() {
        return clean;
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }

    public Coffee getCoffee() {
        return coffee;
    }

    public void setCoffee(Coffee coffee) {
        this.coffee = coffee;
    }
}
