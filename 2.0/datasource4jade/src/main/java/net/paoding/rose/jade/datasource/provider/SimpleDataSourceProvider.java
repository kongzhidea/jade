package net.paoding.rose.jade.datasource.provider;

import net.paoding.rose.jade.datasource.DataSourceProvider;
import net.paoding.rose.jade.datasource.cache.LocalCache;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.util.*;

/**
 * 实际工程中 需要做成 从远程服务获取 DataSource或者connection需要的url，密码等信息
 */
public class SimpleDataSourceProvider implements DataSourceProvider {
    private Log logger = LogFactory.getLog(SimpleDataSourceProvider.class);

    private static int CACHE_EXPIRE_S = 60 * 60; // 1小时

    public static int DB_InitialSize = 2;
    public static int DB_MaxActive = 20;
    public static boolean DB_DefaultAutoCommit = true;
    public static int DB_TimeBetweenEvictionRunsMillis = 3600000;
    public static int DB_MinEvictableIdleTimeMillis = 3600000;

    private LocalCache masterCached = new LocalCache();

    private LocalCache slavesCached = new LocalCache();

    @Override
    public DataSource getReadDataSource(String catalog, String pattern) {
        String url = getReadUrl(catalog, pattern);
        String key = catalog + url + pattern;
        DataSource dataSource = (DataSource) slavesCached.getValue(key);
        if (dataSource == null) {
            synchronized (slavesCached) {
                if (dataSource == null) {
                    dataSource = createDataSource(url);
                    slavesCached.putValue(key, dataSource, CACHE_EXPIRE_S);
                }
            }
        }
        return (DataSource) slavesCached.getValue(key);
    }

    @Override
    public DataSource getWriteDataSource(String catalog, String pattern) {
        String key = catalog + pattern;

        DataSource dataSource = (DataSource) masterCached.getValue(key);
        if (dataSource == null) {
            synchronized (masterCached) {
                if (dataSource == null) {
                    String url = getWriteUrl(catalog, pattern);
                    dataSource = createDataSource(url);
                    masterCached.putValue(key, dataSource, CACHE_EXPIRE_S);
                }
            }
        }
        return (DataSource) masterCached.getValue(key);
    }

    private String getReadUrl(String catalog, String pattern) {
        if ("test".equals(catalog)) {
            String url1 = "jdbc:mysql://localhost:3306/test?user=root&password=&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true";
            String url2 = "jdbc:mysql://localhost:3306/kk?user=root&password=&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true";
            List<String> list = new ArrayList<String>();
            list.add(url1);
            list.add(url2);
            return random(list);
        }
        if ("catalog_blog".equals(catalog)) {
            String url;
            if (pattern == null || "".equals(pattern)) {
                url = "jdbc:mysql://localhost:3306/blog?user=root&password=&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true";
            } else {
                url = "jdbc:mysql://localhost:3306/" + pattern + "?user=root&password=&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true";
            }
            List<String> list = new ArrayList<String>();
            list.add(url);
            return random(list);
        }
        return null;
    }


    private String getWriteUrl(String catalog, String pattern) {
        if ("test".equals(catalog)) {
            return "jdbc:mysql://localhost:3306/test?user=root&password=&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true";
        }
        if ("catalog_blog".equals(catalog)) {
            String url;
            if (pattern == null || "".equals(pattern)) {
                url = "jdbc:mysql://localhost:3306/blog?user=root&password=&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true";
            } else {
                url = "jdbc:mysql://localhost:3306/" + pattern + "?user=root&password=&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true";
            }
            return url;
        }
        return null;
    }

    public static <K> K random(List<K> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }


    // user,password 设置到url中
    private DataSource createDataSource(String url) {
        logger.info("创建数据源:" + url);
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
//        dataSource.setUsername();
//        dataSource.setPassword();
        dataSource.setInitialSize(DB_InitialSize);
        dataSource.setMaxActive(DB_MaxActive);
        dataSource.setDefaultAutoCommit(DB_DefaultAutoCommit);
        dataSource.setTimeBetweenEvictionRunsMillis(DB_TimeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(DB_MinEvictableIdleTimeMillis);
        return dataSource;
    }

}
