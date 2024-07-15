package org.example;

import org.apache.commons.lang3.tuple.Pair;
import org.example.model.*;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Character.isDigit;

public class Parser {
    private static final String SYMBOL_CHARS = "_.$:";
    private static final int CONSTANT_MAX = 32767;
    private final BufferedReader br;

    public Parser(BufferedReader br) {
        this.br = br;
    }

    public List<Instruction> parse() {
        return br.lines().map(this::parseLine).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Instruction parseLine(String line) {
        if (line.isBlank()) {
            return null;
        }
        int i = 0;
        Pair<Integer, Character> pair = nextNonSpace(i, line);
        if (pair == null) {
            return null;
        }
        i = pair.getLeft();
        Character fstChar = pair.getRight();
        Instruction instruction;
        if (fstChar.equals('@')) {
            // parse AInstruction
            Pair<Integer, Instruction> instructionPair = parseAInstruction(i - 1, line);
            instruction = instructionPair.getRight();
            i = instructionPair.getLeft();
        } else if (fstChar.equals('(')) {
            // parse Location
            Pair<Integer, Instruction> instructionPair = parseLocationDef(i - 1, line);
            i = instructionPair.getLeft();
            instruction = instructionPair.getRight();
        } else if (fstChar.equals('/')) {
            // parse comment
            parseComment(i - 1, line);
            return null;
        } else {
            // parse CInstruction
            Pair<Integer, Instruction> instructionPair = parseCInstruction(i - 1, line);
            i = instructionPair.getLeft();
            instruction = instructionPair.getRight();
        }

        // after instruction can only be comments
        Pair<Integer, Character> sndPair = nextNonSpace(i, line);
        if (sndPair == null) {
            return instruction;
        }

        i = sndPair.getLeft();
        if (sndPair.getRight().equals('/')) {
            parseComment(i - 1, line);
            return instruction;
        }

        throw new IllegalArgumentException("文件格式错误");
    }

    private Pair<Integer, Instruction> parseCInstruction(int i, String line) {
        Dest dest;
        Comp comp;
        Jump jump;
        int idx = i;
        int fstEqualIdx = line.indexOf('=', idx);
        if (fstEqualIdx == -1) {
            dest = new Dest("");
            Pair<Integer, Comp> compPair = parseComp(idx, line);
            idx = compPair.getLeft();
            comp = compPair.getRight();
        } else {
            Pair<Integer, Dest> destPair = parseDest(idx, line);
            idx = destPair.getLeft();
            dest = destPair.getRight();
            Pair<Integer, Comp> compPair = parseComp(idx, line);
            idx = compPair.getLeft();
            comp = compPair.getRight();
        }
        int semicolonIdx = line.indexOf(';', idx);
        if (semicolonIdx == -1) {
            jump = new Jump("");
        } else {
            Pair<Integer, Jump> jumpPair = parseJump(idx, line);
            idx = jumpPair.getLeft();
            jump = jumpPair.getRight();
        }
        CInstruction instruction = new CInstruction(dest, comp, jump);
        return Pair.of(idx, instruction);
    }

    private Pair<Integer, Jump> parseJump(int i, String line) {
        int idx = i;
        idx = consume(';', idx, line);
        List<Pair<Integer, Character>> pairList = new ArrayList<>();
        Pair<Integer, Character> pair;
        for (int count = 0; count != 3; count++) {
            pair = nextNonSpace(idx, line);
            if (pair == null) {
                idx = line.length();
                break;
            }
            idx = pair.getLeft();
            pairList.add(pair);
        }

        String str = toString(pairList);
        if (str.isEmpty()) {
            return Pair.of(idx, new Jump(""));
        } else {
            if (str.length() != 3 && !Jump.isJump(str)) {
                throw new IllegalArgumentException("C指令格式错误");
            }
            return Pair.of(pairList.get(2).getLeft(), new Jump(str));
        }
    }

    private String toString(List<Pair<Integer, Character>> pairList) {
        StringBuilder builder = new StringBuilder();
        for (Pair<Integer, Character> pair : pairList) {
            builder.append(pair.getRight());
        }
        return builder.toString();
    }

    private Pair<Integer, Dest> parseDest(int i, String line) {
        List<Pair<Integer, Character>> pairList = new ArrayList<>();
        int idx = i;
        Pair<Integer, Character> pair;
        for (int count = 0; count != 3; count++) {
            pair = nextNonSpace(idx, line);
            if (pair == null) {
                idx = line.length();
                break;
            }
            idx = pair.getLeft();
            pairList.add(pair);
        }

        String str = toString(pairList);
        int len = str.length();
        for (; len != 0; len--) {
            if (Dest.isDest(str.substring(0, len))) {
                break;
            }
        }

        if (str.startsWith("=")) {
            idx = consume('=', i, line);
            return Pair.of(idx, new Dest(""));
        }

        if (len == 0) {
            throw new IllegalArgumentException("C指令格式错误");
        }
        idx = consume('=', pairList.get(len - 1).getLeft(), line);
        return Pair.of(idx, new Dest(str.substring(0, len)));
    }

    private Pair<Integer, Comp> parseComp(int i, String line) {
        List<Pair<Integer, Character>> pairList = new ArrayList<>();
        int idx = i;
        Pair<Integer, Character> pair;
        for (int count = 0; count != 3; count++) {
            pair = nextNonSpace(idx, line);
            if (pair == null) {
                idx = line.length();
                break;
            }
            idx = pair.getLeft();
            pairList.add(pair);
        }

        String str = toString(pairList);
        int len = str.length();
        for (; len != 0; len--) {
            if (Comp.isComp(str.substring(0, len))) {
                break;
            }
        }

        if (len == 0) {
            throw new IllegalArgumentException("C指令格式错误");
        }

        return Pair.of(pairList.get(len - 1).getLeft(), new Comp(str.substring(0, len)));
    }

    private Pair<Integer, Instruction> parseLocationDef(int i, String line) {
        int idx = i;
        idx = consume('(', idx, line);
        Pair<Integer, String> symbolPair = parseSymbol(idx, line);
        idx = symbolPair.getLeft();
        idx = consume(')', idx, line);
        return Pair.of(idx, new LocationDef(symbolPair.getRight()));
    }

    private Pair<Integer, Instruction> parseAInstruction(int i, String line) {
        int idx = consume('@', i, line);
        Pair<Integer, Character> pair = nextNonSpace(idx, line);
        if (pair == null || !isSymbolChar(pair.getRight())) {
            throw new IllegalArgumentException("A指令格式错误");
        }
        if (isDigit(pair.getRight())){
            Pair<Integer, Integer> idxAndConstant = parseConstant(idx, line);
            return Pair.of(idxAndConstant.getLeft(),
                    new AInstruction(idxAndConstant.getRight()));
        } else {
            Pair<Integer, String> idxAndSymbol = parseSymbol(idx, line);
            return Pair.of(idxAndSymbol.getLeft(),
                    new AInstruction(idxAndSymbol.getRight()));
        }
    }

    private Pair<Integer, Integer> parseConstant(int i, String line) {
        int result = 0;
        int idx = i;
        Pair<Integer, Character> pair;
        while ((pair = nextNonSpace(idx, line)) != null
        && isDigit(pair.getRight())) {
            result = result * 10 + (pair.getRight() - '0');
            if (result > CONSTANT_MAX) {
                throw new IllegalArgumentException("常数过大");
            }
            idx = pair.getLeft();
        }

        if (pair == null) {
            return Pair.of(line.length(), result);
        } else {
            return Pair.of(idx - 1, result);
        }
    }

    private Pair<Integer, String> parseSymbol(int i, String line) {
        int idx = i;
        Pair<Integer, Character> pair = nextNonSpace(idx, line);
        if (pair == null || !isSymbolChar(pair.getRight()) || isDigit(pair.getRight())) {
            throw new IllegalArgumentException("标识符格式错误");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(pair.getRight());
        while ((idx = pair.getLeft()) != line.length()
                && (pair = nextNonSpace(idx, line)) != null
                && isSymbolChar(pair.getRight())) {
            builder.append(pair.getRight());
        }
        if (idx == line.length() || pair == null) {
            return Pair.of(line.length(), builder.toString());
        } else {
            return Pair.of(pair.getLeft() - 1, builder.toString());
        }
    }

    private boolean isSymbolChar(Character c) {
        return Character.isLetterOrDigit(c) || SYMBOL_CHARS.contains(c.toString());
    }


    private void parseComment(int i, String line) {
        int idx = i;
        idx = consume('/', idx, line);
        consume('/', idx, line);
    }

    private int consume(char c, int i, String line) {
        Pair<Integer, Character> pair = nextNonSpace(i, line);
        if (pair == null || !pair.getRight().equals(c)) {
            throw new IllegalArgumentException("文件格式错误");
        }
        return pair.getLeft();
    }


    private Pair<Integer, Character> nextNonSpace(int i, String line) {
        while (i < line.length() && line.charAt(i) == ' ') {
            i++;
        }
        return i == line.length() ? null : Pair.of(i + 1, line.charAt(i));
    }
}
