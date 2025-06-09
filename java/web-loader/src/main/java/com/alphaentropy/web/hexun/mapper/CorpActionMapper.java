package com.alphaentropy.web.hexun.mapper;

public class CorpActionMapper extends HexunTableMapper {
    private static final String START_STRING = "<div id=\"zaiyaocontent\">";
    private static final String END_STRING = "<!-- zaiyaocontent end -->";

    @Override
    protected void cleanRow(String[] row) {
        for (int i = 0; i < row.length; i++) {
            String cell = row[i];
            row[i] = cell.substring(cell.indexOf(">") + 1, cell.lastIndexOf("<")).trim();
        }
    }

    @Override
    protected boolean needFilter(Object row) {
        return false;
    }

    @Override
    protected int skipRows() {
        return 3;
    }

    @Override
    protected int expectedCols() {
        return 7;
    }

    @Override
    protected String trim(String content) {
        int startIdx = content.indexOf(START_STRING);
        if (startIdx < 0) {
            return null;
        }
        int endIdx = content.indexOf(END_STRING);
        if (endIdx < 0) {
            return null;
        }
        content = content.substring(startIdx, endIdx);
        startIdx = content.indexOf("<table");
        endIdx = content.indexOf("</table>");
        return content.substring(startIdx, endIdx + 8);
    }
}
