package net.paoding.rose.jade.annotation;

/**
 * SQL类型标识。
 * <p>
 * 在使用{@link SQL}
 * 注解时，Jade将以SELECT开始的语句认为是查询类型SQL语句，其它的语句被认为是更新类型，开发者可以根据实际改变Jade的默认判断
 * ，比如SHOW语句实际应该是查询类型语句，而非更新类型语句。
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public enum SQLType {
    /**
     * 查询类型语句
     */
    READ,

    /**
     * 更新类型语句
     */
    WRITE,

    /**
     * 未知类型，将使用Jade的默认规则判断：所有以SELECT开始的语句是查询类型的，其他的是更新类型的
     */
    AUTO_DETECT,

}
