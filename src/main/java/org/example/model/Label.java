package org.example.model;

public class Label implements Symbol {
    public Label(String name, Integer address) {
        this.name = name;
        this.address = address;
    }

    private final String name;
    private final Integer address;

    public Integer getAddress() {
        return address;
    }
}
