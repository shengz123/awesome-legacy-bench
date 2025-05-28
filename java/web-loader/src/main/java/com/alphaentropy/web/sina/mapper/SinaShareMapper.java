package com.alphaentropy.web.sina.mapper;

public class SinaShareMapper extends SinaTableMapper {

    private static final String START_STRING = "<!--股本结构历史begin-->";
    private static final String END_STRING = "<!--股本结构历史end-->";

    private static final String UNIT_TRIM = "万股";

    @Override
    protected void cleanRow(String[] row) {
        for (int i = 0; i < row.length; i++) {
            String cell = row[i];
            row[i] = cell.substring(cell.indexOf(">") + 1, cell.lastIndexOf("<")).trim().replace(UNIT_TRIM, "");
        }
    }

    @Override
    protected boolean needFilter(Object row) {
        return false;
    }

    @Override
    protected int skipRows() {
        return 1;
    }

    @Override
    protected int expectedCols() {
        return 0;
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
        endIdx = content.lastIndexOf("</table>");
        return content.substring(startIdx, endIdx + "</table>".length());
    }
}
