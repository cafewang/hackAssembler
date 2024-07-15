package org.example.model;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Dest {
    private static final Map<String, String> MACHINE_CODE_MAPPING = ImmutableMap.of(
            "", "000",
            "M", "001",
            "D", "010",
            "MD", "011",
            "A", "100",
            "AM", "101",
            "AD", "110",
            "AMD", "111"
    );

    public static boolean isDest(String name) {
        return !name.isEmpty() && MACHINE_CODE_MAPPING.containsKey(name);
    }

    private final String instruction;

    public Dest(String instruction) {
        this.instruction = instruction;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getCode() {
        return MACHINE_CODE_MAPPING.get(instruction);
    }
}
