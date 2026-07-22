package cloudflight.integra.backend.coffee.model;

public class Coffee {
    private Long id;
    private String origin;
    private BrewMethod brewMethod;

    public Coffee() {}

    public Coffee(Long id, String origin, BrewMethod brewMethod) {
        this.id = id;
        this.origin = origin;
        this.brewMethod = brewMethod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public BrewMethod getBrewMethod() {
        return brewMethod;
    }

    public void setBrewMethod(BrewMethod brewMethod) {
        this.brewMethod = brewMethod;
    }
}
