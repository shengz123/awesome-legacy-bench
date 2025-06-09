package com.alphaentropy.web.sina.mapper;

import com.alphaentropy.common.utils.CSVDataFrame;
import lombok.extern.slf4j.Slf4j;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import java.util.ArrayList;
import java.util.List;

import static com.alphaentropy.common.utils.BeanUtil.mapRowToBean;

@Slf4j
public abstract class SinaTableMapper {
    protected static final String YYYY_MM_DD = "yyyy-MM-dd";
    protected static final String NA = "-";

    abstract protected void cleanRow(String[] row);
    abstract protected boolean needFilter(Object row);
    abstract protected int skipRows();
    abstract protected int expectedCols();
    abstract protected String trim(String content);

    public List map(String content, Class clz, String symbol) {
        List entries = new ArrayList();
        content = trim(content);
        if (content == null) {
            return entries;
        }
        List<CSVDataFrame> list = new ArrayList<>();

        try {
            Parser parser = new Parser(content);
            NodeFilter filter = new TagNameFilter("table");
            NodeList nodeList = parser.extractAllNodesThatMatch(filter);
            TableTag table = (TableTag) nodeList.elementAt(1);
            TableRow[] rows = table.getRows();
            for (TableRow row : rows) {
                TableColumn[] columns = row.getColumns();
                for (int i = 0; i < columns.length; i++) {
                    CSVDataFrame df = CSVDataFrame.fromHTML(columns[i].getStringText().trim(), 0, 2);
                    list.add(df);
                }
            }
        } catch (Exception e) {
            log.error("Failed to create the mapper", e);
        }
        for (CSVDataFrame df : list) {
            for (int i = 0; i < df.numRows(); i++) {
                String[] row = df.getRow(i);
                cleanRow(row);
                try {
                    Object value = mapRowToBean(row, clz, YYYY_MM_DD, true, symbol, null, NA, false);
                    if (value != null && !needFilter(value)) {
                        entries.add(value);
                    }
                } catch (Exception e) {
                    log.error("Failed to create the mapper", e);
                }
            }
        }
        return entries;
    }
}
