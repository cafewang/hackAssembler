package org.example;

import org.example.model.Instruction;

import java.io.*;
import java.util.List;

public class Driver {
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("请输入文件路径");
        }

        String path = args[0];
        if (path.isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        File file;
        try (BufferedReader br = new BufferedReader(new FileReader(file = new File(path)));
             BufferedWriter writer = new BufferedWriter(new FileWriter(file.getParent() + "/" + file.getName().split("\\.")[0] + ".hack"))) {
            Parser parser = new Parser(br);
            List<Instruction> instructions = parser.parse();
            MachineCodeGenerator generator = new MachineCodeGenerator(instructions);
            List<String> machineCodes = generator.generate();
            for (String machineCode : machineCodes) {
                writer.write(machineCode);
                writer.write("\r\n");
            }
            writer.flush();
        } catch (IOException e) {
            throw new IllegalArgumentException("文件不存在");
        }
    }
}
