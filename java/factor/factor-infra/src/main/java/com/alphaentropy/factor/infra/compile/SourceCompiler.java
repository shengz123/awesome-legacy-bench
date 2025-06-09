package com.alphaentropy.factor.infra.compile;

import com.alphaentropy.factor.infra.domain.BuiltinFunctionCall;
import com.alphaentropy.factor.infra.domain.CompiledLine;
import com.alphaentropy.factor.infra.domain.SourceFile;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
//@Component
public class SourceCompiler {

    private static final String EQ = "=";
    private static final String COL = ":";
    private static final String DEFAULT = "default";
    private static final String FREQ = "frequency";
    private static final String FREQ_DAILY = "daily";
    private static final String FREQ_QUARTERLY = "quarterly";
    private static final String OUT = "out:";
    private static final String COMMA = ",";
    private static final String HARSH = "#";
    private static final String FUNC_PAT = "\\#(\\w+)";

    public SourceFile compile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String defaultClz = null;
            Set<String> output = new HashSet<>();
            List<CompiledLine> lines = new ArrayList<>();
            String freq = FREQ_DAILY;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.trim().isEmpty() || line.trim().startsWith(HARSH)) {
                    continue;
                }
                if (line.contains(DEFAULT)) {
                    defaultClz = line.split(COL)[1].trim();
                } else if (line.contains(FREQ)) {
                    freq = line.split(COL)[1].trim();
                } else if (line.contains(OUT)) {
                    String out = line.split(COL)[1].trim();
                    if (out.contains(EQ)) {
                        out = out.split(EQ)[0].trim();
                    }
                    output.add(out);
                }
                if (line.contains(EQ)) {
                    CompiledLine compiledLine = compileLine(line.replace(OUT, ""));
                    if (compiledLine != null) {
                        lines.add(compiledLine);
                    }
                }
            }
            return new SourceFile(file.getName(), defaultClz, output, lines, freq);
        } catch (IOException e) {
            log.error("Failed to compile {}", file.getName());
        }
        return null;
    }

    private CompiledLine compileLine(String line) {
        Pattern funcPattern = Pattern.compile(FUNC_PAT);
        Matcher m = funcPattern.matcher(line);
        while (m.find()) {
            String funcName = m.group(0).replace(HARSH, "").trim();
            String funcCall = line.split(EQ)[1].trim();
            String var = convertBiFuncToVariable(funcCall);
            String params = funcCall.substring(funcCall.indexOf("(") + 1, funcCall.lastIndexOf(")"));
            BuiltinFunctionCall functionCall = new BuiltinFunctionCall(funcName, params.split(COMMA));
            String compiled = line.split(EQ)[0] + EQ + var;
            return new CompiledLine(line, functionCall, var, compiled);
        }
        return new CompiledLine(line, null, null, line);
    }

    private String convertBiFuncToVariable(String biFunc) {
        return biFunc.replace("(", "_").replace(".", "_")
                .replace("-", "_").replace(")", "")
                .replace(",", "_").replace(" ", "")
                .replace("$", "").replace("#", "")
                .toLowerCase();
    }

    public static void main(String[] args) {
//        File file = new File("F:\\projects\\gitee\\quant\\alpha_entropy\\factor\\factor-lib\\formula\\price\\ma.txt");
//        new SourceCompiler().compile(file);
    }

}
