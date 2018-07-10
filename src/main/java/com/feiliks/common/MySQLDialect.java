package com.feiliks.common;

import org.hibernate.dialect.MySQL5InnoDBDialect;

public class MySQLDialect extends MySQL5InnoDBDialect {

    @Override
    public String getTableTypeString() {
        return " CHARACTER SET utf8 COLLATE utf8_bin ENGINE InnoDB";
    }

}
