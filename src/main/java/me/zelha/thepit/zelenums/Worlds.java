package me.zelha.thepit.zelenums;

public enum Worlds {
    ELEMENTALS("Elementals"),
    CORALS("Corals"),
    SEASONS("Seasons"),
    CASTLE("Castle"),
    GENESIS("Genesis");

    private final String name;

    Worlds(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Worlds findByName(String string) {
        for (Worlds world : Worlds.values()) {
            if (world.getName().equals(string)) return world;
        }
        return null;
    }
}
