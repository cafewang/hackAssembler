package org.example.model;

public class Variable implements Symbol {
    public Variable(String name, Integer address) {
        this.name = name;
        this.address = address;
    }

    private final String name;
    private final Integer address;

    public Integer getAddress() {
        return address;
    }

}
