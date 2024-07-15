package org.example.model;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Comp {
    private static final Map<String, String> MACHINE_CODE_MAPPING = new ImmutableMap.Builder<String, String>()
            .put("0", "0101010")
            .put("1", "0111111")
            .put("-1", "0111010")
            .put("D", "0001100")
            .put("A", "0110000")
            .put("!D", "0001101")
            .put("!A", "0110001")
            .put("-D", "0001111")
            .put("-A", "0110011")
            .put("D+1", "0011111")
            .put("A+1", "0110111")
            .put("D-1", "0001110")
            .put("A-1", "0110010")
            .put("D+A", "0000010")
            .put("D-A", "0010011")
            .put("A-D", "0000111")
            .put("D&A", "0000000")
            .put("D|A", "0010101")
            .put("M", "1110000")
            .put("!M", "1110001")
            .put("-M", "1110011")
            .put("M+1", "1110111")
            .put("M-1", "1110010")
            .put("D+M", "1000010")
            .put("D-M", "1010011")
            .put("M-D", "1000111")
            .put("D&M", "1000000")
            .put("D|M", "1010101")
            .build();

    public static boolean isComp(String name) {
        return MACHINE_CODE_MAPPING.containsKey(name);
    }

    private final String instruction;

    public Comp(String instruction) {
        this.instruction = instruction;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getCode() {
        return MACHINE_CODE_MAPPING.get(instruction);
    }
}
