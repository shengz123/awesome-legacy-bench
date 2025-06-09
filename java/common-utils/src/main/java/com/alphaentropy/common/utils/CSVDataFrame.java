package com.alphaentropy.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import java.io.*;
import java.util.*;

@Slf4j
public class CSVDataFrame {
    private String[][] data;

    public static CSVDataFrame fromString(String content, String delimiter) {
        return new CSVDataFrame(content, delimiter);
    }

    public static CSVDataFrame fromHTML(String htmlTable, int skippedHeader, int expectedCount) {
        return new CSVDataFrame(htmlTable, skippedHeader, expectedCount);
    }

    public static CSVDataFrame fromFile(File file, String delimiter, int headerLines, int expectedColumns) {
        return new CSVDataFrame(file, delimiter, headerLines, expectedColumns);
    }

    CSVDataFrame(String content, String delimiter) {
        try {
            this.data = parseContent(content, delimiter);
        } catch (IOException e) {
            log.error("Failed to parse the content " + content);
        }
    }

    CSVDataFrame(String htmlTable, int skippedHeader, int expectedCount) {
        List<String[]> list = new ArrayList<>();
        try {
            Parser parser = new Parser(htmlTable);
            NodeFilter filter = new TagNameFilter("tr");
            NodeList nodeList = parser.extractAllNodesThatMatch(filter);

            SimpleNodeIterator children = nodeList.elements();
            int count = 0;

            while (children.hasMoreNodes()) {
                TableRow row = (TableRow) children.nextNode();
                count++;
                if (count < skippedHeader) {
                    continue;
                }
                TableColumn[] columns = row.getColumns();
                if (columns.length < expectedCount) {
                    continue;
                }
                String[] resultRow = new String[columns.length];
                for (int i = 0; i < columns.length; i++) {
                    resultRow[i] = columns[i].getStringText().trim();
                }
                list.add(resultRow);
            }
        } catch (Exception e) {
            log.error("Failed to parse HTML\n" + htmlTable, e);
        }
        this.data = listToMatrix(list);
    }

    CSVDataFrame(File file, String delimiter, int headerLines, int expectedColumns) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            this.data = parseContent(reader, delimiter, headerLines, expectedColumns);
        } catch (IOException e) {
            log.error("Failed to parse the file " + file.getName());
        }
    }

    CSVDataFrame(String[][] data) {
        this.data = data;
    }

    private String[][] parseContent(BufferedReader reader, String delimiter,
                                    int headerLines, int expectedColumns) throws IOException {
        List<String[]> dataFrame = new ArrayList<>();
        // skip header
        for (int i = 0; i < headerLines; i++) {
            reader.readLine();
        }
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            String[] row = line.split(delimiter);
            if (expectedColumns > 0 && row.length < expectedColumns) {
                continue;
            }
            dataFrame.add(row);
        }
        reader.close();
        return listToMatrix(dataFrame);
    }

    private String[][] listToMatrix(List<String[]> list) {
        String[][] matrix = new String[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            matrix[i] = list.get(i);
        }
        return matrix;
    }

    private String[][] parseContent(String content, String delimiter) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(content));
        return parseContent(reader, delimiter, 0, -1);
    }

    public List<CSVDataFrame> split(int splitterLen, String[] matchSplitter) {
        List<CSVDataFrame> result = new ArrayList<>();
        List<String[]> list = new ArrayList<>();
        for (String[] row : data) {
            if (row.length == splitterLen && matchSplitter(row, matchSplitter)) {
                result.add(new CSVDataFrame(listToMatrix(list)));
                list = new ArrayList<>();
            } else {
                list.add(row);
            }
        }
        if (!list.isEmpty()) {
            result.add(new CSVDataFrame(listToMatrix(list)));
        }
        return result;
    }

    private boolean matchSplitter(String[] row, String[] matchSplitter) {
        if (row.length != matchSplitter.length) {
            return false;
        }
        for (int i = 0; i < row.length; i++) {
            if (!row[i].equals(matchSplitter[i])) {
                return false;
            }
        }
        return true;
    }

    public Map<String, CSVDataFrame> splitAsMap(int splitterLen) {
        Map<String, CSVDataFrame> result = new HashMap<>();
        List<String[]> list = new ArrayList<>();
        String key = null;
        for (String[] row : data) {
            if (row.length == splitterLen) {
                if (!list.isEmpty()) {
                    result.put(key, new CSVDataFrame(listToMatrix(list)));
                    list = new ArrayList<>();
                }
                key = row[0];
            } else {
                list.add(row);
            }
        }
        if (!list.isEmpty()) {
            result.put(key, new CSVDataFrame(listToMatrix(list)));
        }
        return result;
    }

    public CSVDataFrame sub(int startRow, int endRow, int startCol, int endCol) {
        String[][] subData = Arrays.copyOfRange(data, startRow, endRow);
        List<String[]> tmp = new ArrayList<>();
        for (String[] row : subData) {
            int end = Math.min(row.length, endCol);
            tmp.add(Arrays.copyOfRange(row, startCol, end));
        }
        return new CSVDataFrame(listToMatrix(tmp));
    }

    public CSVDataFrame sub(int startRow) {
        return sub(startRow, data.length, 0, data[0].length);
    }

    public boolean isEmpty() {
        return data.length == 0 || data[0].length == 0;
    }

    public int numCols() {
        return data[0].length;
    }

    public int numRows() {
        return data.length;
    }

    public String getCell(int rowNo, int colNo) {
        return data[rowNo][colNo];
    }

    public String[] getColumn(int index) {
        String[] column = new String[data.length];
        for(int i = 0; i < column.length; i++){
            if (data[i].length == data[0].length) {
                column[i] = data[i][index];
            }
        }
        return column;
    }

    public String[] getRow(int index) {
        return data[index];
    }

    public String[] getSlice(int index, int axis) {
        if (axis == 0) {
            return getRow(index);
        } else if (axis == 1) {
            return getColumn(index);
        }
        return null;
    }

    public int numSlices(int axis) {
        if (axis == 0) {
            return numRows();
        } else if (axis == 1) {
            return numCols();
        }
        return 0;
    }

}
