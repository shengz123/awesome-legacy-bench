package com.alphaentropy.tdx.daily;

import com.alphaentropy.common.utils.SymbolUtil;
import com.alphaentropy.domain.basic.IpoDate;
import com.alphaentropy.store.application.MySQLStatement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class IPOChecker {

	@Value("${daily_file_path}")
	private String FOLDER;

	@Autowired
	private MySQLStatement statement;

	public void checkIPODates() {
		try {
			Map<String, Date> ipoDates = statement.getMostRecent(IpoDate.class);
			checkIPODates(ipoDates);
		} catch (Exception e) {
			log.error("Failed to check ipo dates", e);
		}
	}

	private void checkIPODates(Map<String, Date> ipoDates) throws Exception {
		File[] files = new File(FOLDER).listFiles();
		ExecutorService symbolExecutorService = Executors.newFixedThreadPool(20);
		for (final File file : files) {
			final String fileName = file.getName();
			if (fileName.endsWith(".txt") && fileName.length() == 10) {
				symbolExecutorService.execute(new Runnable() {
					@Override
					public void run() {
						String symbol = fileName.substring(0, 6);
						if (!SymbolUtil.isAShare(symbol)) {
							return;
						}
						checkIPODate(symbol, file, ipoDates.get(symbol));
					}
				});
			}
		}
		// Destroy only when all the files are processed.
		symbolExecutorService.shutdown();
		symbolExecutorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.HOURS);
	}

	private void checkIPODate(String symbol, File priceFile, Date existDate) {
		try {
			Date ipoDate = get1stDate(priceFile.getAbsolutePath());
			if (ipoDate != null && existDate == null) {
				statement.insert(new IpoDate(symbol, ipoDate), IpoDate.class);
			}
		} catch (Exception e) {
			log.error("Failed to insert ipo date for {}", symbol, e);
		}
	}

	private Date get1stDate(String fileName) throws Exception {
		Map<Date, BigDecimal> result = new HashMap<Date, BigDecimal>();

		SimpleDateFormat MMddyyyyStr = new SimpleDateFormat("MM/dd/yyyy");
		BufferedReader reader = new BufferedReader(new FileReader(fileName));

		// skip the first 2 lines
		reader.readLine();
		reader.readLine();
		String line = reader.readLine();
		reader.close();

		String[] row = line.split("\t");

		if (row.length < 7) {
			return null;
		}

		String dateStr = row[0];
		return MMddyyyyStr.parse(dateStr);
	}

}
