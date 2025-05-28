package com.alphaentropy.web.hexun.mapper;

import com.alphaentropy.domain.basic.HexunCorpAction;
import com.alphaentropy.domain.basic.SharesChangeEvent;
import org.springframework.stereotype.Component;

@Component
public class HexunMapperFactory {
    public HexunTableMapper getMapper(Class clz) {
        if (clz.equals(SharesChangeEvent.class)) {
            return new SharesChangeMapper();
        } else if (clz.equals(HexunCorpAction.class)) {
            return new CorpActionMapper();
        }
        return null;
    }
}
