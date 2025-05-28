package com.alphaentropy.domain.release;


import com.alphaentropy.domain.annotation.Cached;
import com.alphaentropy.domain.annotation.MySQLDateKey;
import com.alphaentropy.domain.annotation.MySQLSymbolKey;
import com.alphaentropy.domain.annotation.MySQLTable;
import lombok.*;

import java.util.Date;

@Data
@Builder
@Setter
@Getter
@MySQLTable
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseDate {
    @MySQLSymbolKey
    private String securityCode;

    private Date firstAppointDate;

    private Date actualPublishDate;

    @MySQLDateKey
    private Date reportDate;

    private Date appointPublishDate;
}
