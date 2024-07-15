package org.example.model;

public class Label implements Symbol {
    public Label(String name, Integer address) {
        this.name = name;
        this.address = address;
    }

    private String name;
    private Integer address;

    public String getName() {
        return name;
    }

    public Integer getAddress() {
        return address;
    }
}
