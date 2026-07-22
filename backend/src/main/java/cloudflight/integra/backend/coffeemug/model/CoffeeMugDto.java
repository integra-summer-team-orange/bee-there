package cloudflight.integra.backend.coffeemug.model;

import cloudflight.integra.backend.coffee.model.CoffeeDto;

public record CoffeeMugDto(Long id, String color, int capacityMl, boolean clean, CoffeeDto coffee) {}
