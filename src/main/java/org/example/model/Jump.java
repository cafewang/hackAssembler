package org.example.model;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Jump {
    private static final Map<String, String> MACHINE_CODE_MAPPING = ImmutableMap.of(
            "", "000",
            "JGT", "001",
            "JEQ", "010",
            "JGE", "011",
            "JLT", "100",
            "JNE", "101",
            "JLE", "110",
            "JMP", "111"
    );

    public static boolean isJump(String str) {
        return !str.isEmpty() && MACHINE_CODE_MAPPING.containsKey(str);
    }

    private final String instruction;

    public Jump(String instruction) {
        this.instruction = instruction;
    }

    public String getCode() {
        return MACHINE_CODE_MAPPING.get(instruction);
    }
}
