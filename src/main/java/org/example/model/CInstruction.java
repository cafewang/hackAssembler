package org.example.model;

public class CInstruction implements Instruction {
    private final Dest dest;
    private final Comp comp;
    private final Jump jump;

    public CInstruction(Dest dest, Comp comp, Jump jump) {
        this.dest = dest;
        this.comp = comp;
        this.jump = jump;
    }

    public String machineCode() {
        return "111" + comp.getCode() + dest.getCode() + jump.getCode();
    }
}
