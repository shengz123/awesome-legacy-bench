package com.alphaentropy.web.hexun;

import com.alphaentropy.domain.basic.HexunCorpAction;
import com.alphaentropy.domain.basic.SharesChangeEvent;
import com.alphaentropy.web.hexun.loader.HexunCorpActionLoader;
import com.alphaentropy.web.hexun.loader.SharesChangeLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HexunLoadDriver {
    @Autowired
    private SharesChangeLoader sharesChangeLoader;

    @Autowired
    private HexunCorpActionLoader corpActionLoader;

    @Autowired
    private HexunLoadService loadService;

    public void loadDaily(List<String> symbols) {
        loadService.load(symbols, HexunCorpAction.class, corpActionLoader);
    }

    public void loadDailyByAdjust(List<String> symbols) {
        loadService.load(symbols, SharesChangeEvent.class, sharesChangeLoader);
    }
}
