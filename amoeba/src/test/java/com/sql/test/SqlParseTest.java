package com.sql.test;

import com.meidusa.amoeba.parser.dbobject.Column;
import com.meidusa.amoeba.parser.dbobject.Table;
import com.meidusa.datasource.SQLParseInfo;
import com.meidusa.datasource.SqlRewriter;
import org.junit.Test;

public class SqlParseTest {

    @Test
    public void testSQL() {
        String sql = "select id,name,pwd from eby_user  u, eby_role as t  where id = ? and name=?";
        SQLParseInfo parseInfo = SQLParseInfo.getParseInfo(sql);

        System.out.println(parseInfo);

        Table table = parseInfo.getTables()[0];

        Column idCol = new Column();
        idCol.setName("name");
        idCol.setTable(table);
        System.out.println(parseInfo.getColumnIndex(idCol)); // 1


        // 替换表名
        System.out.println(SqlRewriter.rewriteSqlTable(sql, "eby_role", "r"));
    }
}
