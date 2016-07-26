/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License i distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.paoding.rose.jade.provider.jdbctemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * 实现返回新生成主键的 {@link PreparedStatementCallback} 实现。
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public class PreparedStatementCallbackReturnId implements PreparedStatementCallback {

    private PreparedStatementSetter setter;

    public PreparedStatementCallbackReturnId() {
    }

    public PreparedStatementCallbackReturnId(PreparedStatementSetter setter) {
        this.setter = setter;
    }

    @Override
    public Object doInPreparedStatement(PreparedStatement ps) throws SQLException,
            DataAccessException {

        if (setter != null) {
            setter.setValues(ps);
        }

        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        if (keys != null) {

            try {
                RowMapperResultSetExtractor extractor = new RowMapperResultSetExtractor(
                        new SingleColumnRowMapper(Number.class), 1);
                return DataAccessUtils.requiredSingleResult((List<?>) extractor.extractData(keys));
            } finally {
                JdbcUtils.closeResultSet(keys);
            }
        }

        return null;
    }
}
