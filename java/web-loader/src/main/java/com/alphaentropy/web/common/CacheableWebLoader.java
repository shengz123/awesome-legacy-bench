package com.alphaentropy.web.common;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

@Slf4j
public abstract class CacheableWebLoader {

    abstract protected String getDumpFolder();

    abstract protected String getDumpExt();

    abstract protected String url(Map<String, String> params);

    abstract protected String cacheKey(Map<String, String> params);

    abstract protected long waitTimeAfterLoad();

    abstract protected int getExpiryHours();

    abstract protected int getRetries();

    public String load(Map<String, String> params, String charset, boolean forceExpiry, boolean needFormat)
            throws Exception {
        String cacheKey = cacheKey(params);
        String content = loadFromCache(cacheKey, forceExpiry);
        if (content == null) {
            content = get(url(params), charset, needFormat);
            dumpContent(cacheKey, content);
            Thread.sleep(waitTimeAfterLoad());
        }
        return content;
    }

    protected String get(String url, String charset, boolean needFormat) {
        StringBuffer sb = new StringBuffer();
        boolean flag = true;
        int failedCnt = 0;
        while (flag) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(getStream(url, charset), charset))) {
                for (String line = in.readLine(); line != null; line = in.readLine()) {
                    sb.append(line);
                    if (needFormat) {
                        sb.append("\n");
                    }
                }
                flag = false;
            } catch (IOException e) {
                log.error("Failed with " + url, e);
                if (failedCnt++ > getRetries()) {
                    return null;
                } else {
                    sb = new StringBuffer();
                    try {
                        Thread.sleep(20000 + failedCnt * 5000);
                        log.warn("Retrying " + url);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
        return sb.toString();
    }

    protected InputStream getStream(String url, String charset) {
        InputStream result = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (charset != null) {
                connection.setRequestProperty("Accept-Charset", charset);
                connection.setRequestProperty("contentType", charset);
            }
            connection.connect();
            result = connection.getInputStream();
        } catch (Exception e) {
            log.info("Failed with " + url);
            if (e.getMessage().contains("Server returned HTTP response code: 500")) {
                log.info("Ignore 500 error");
            } else if (e instanceof FileNotFoundException) {
                log.error("No such link " + url, e);
            } else {
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }

    protected void dumpContent(String symbol, String content) throws Exception {
        String repo = getDumpFolder();
        createFolderIfNotExists(repo);
        FileWriter writer = new FileWriter(repo + "/" + symbol + "." + getDumpExt());
        writer.write(content);
        writer.close();
    }

    protected boolean isCacheExpired(File file) {
        long lastUpdated = file.lastModified();
        long now = System.currentTimeMillis();
        long diffTime = now - lastUpdated;
        int diffHours = (int)(diffTime/1000L/3600L);
        return diffHours > getExpiryHours();
    }

    protected void createFolderIfNotExists(String path) throws Exception {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    protected String loadFromCache(String fileName, boolean forceExpiry) throws Exception {
        String result = null;
        File file = new File(getDumpFolder() + "/" + fileName + "." + getDumpExt());
        if (file.exists()) {
            if (forceExpiry || isCacheExpired(file)) {
                return null;
            }
            StringBuffer sb = new StringBuffer();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
            reader.close();
            result = sb.toString();
        }
        return result;
    }

}
