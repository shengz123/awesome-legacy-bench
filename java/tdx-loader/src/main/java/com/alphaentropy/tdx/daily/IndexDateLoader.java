package com.alphaentropy.tdx.daily;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class IndexDateLoader {

    @Value("${daily_file_path}")
    private String FOLDER;

    public List<Date> getAllDatesSince(Date day1) {
        try {
            List<Date> ret = new ArrayList<>();
            List<Date> dates = getAllDates(getIndexFile());
            for (Date d : dates) {
                if (!d.before(day1)) {
                    ret.add(d);
                }
            }
            return ret;
        } catch (Exception e) {
            log.error("Failed to check ipo dates", e);
        }
        return null;
    }

    private File getIndexFile() {
        String path = FOLDER + "/999999.txt";
        return new File(path);
    }

    private List<Date> getAllDates(File file) throws Exception {
        List<Date> ret = new ArrayList<>();
        SimpleDateFormat MMddyyyyStr = new SimpleDateFormat("MM/dd/yyyy");
        BufferedReader reader = new BufferedReader(new FileReader(file));

        // skip the first 2 lines
        reader.readLine();
        reader.readLine();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            String[] row = line.split("\t");
            if (row.length < 7) {
                continue;
            }
            ret.add(MMddyyyyStr.parse(row[0]));
        }
        reader.close();

        return ret;
    }

}
