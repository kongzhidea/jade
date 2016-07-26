package com.meidusa.datasource;

/**
 * 实现一个简单拆分查询语句的工具, 该工具不考虑子查询等特殊情况。
 * 
 * @author han.liao
 */
public class SqlTokenizer {

    // 拆分的  SQL 语句
    protected final String sql;

    protected final int length;

    // 分拆的位置
    protected int offset = 0;

    // 分拆的设置
    protected boolean unwrapBracket;

    protected boolean wantedComparison;

    protected boolean wantedOperator;

    protected boolean wantedBracket;

    protected boolean wantedComma;

    /**
     * SqlTokenizer 对象。
     * 
     * @param sql - 拆分的语句
     */
    public SqlTokenizer(String sql) {

        this.sql = sql;

        length = sql.length();
    }

    /**
     * SqlTokenizer 对象。
     * 
     * @param sql - 拆分的语句
     * 
     * @param offset - 拆分的起始位置
     */
    public SqlTokenizer(String sql, int offset) {

        this.sql = sql;

        length = sql.length();
    }

    /**
     * 设置拆分括号内容。
     * 
     * @param unwrapBracket - 拆分括号内容
     */
    public void setUnwrapBracket(boolean unwrapBracket) {
        this.unwrapBracket = unwrapBracket;
    }

    /**
     * 设置返回比较符号。
     * 
     * @param unwrapBracket - 返回比较符号
     */
    public void setWantedComparison(boolean wantedComparison) {
        this.wantedComparison = wantedComparison;
    }

    /**
     * 设置返回运算符。
     * 
     * @param unwrapBracket - 返回运算符
     */
    public void setWantedOperator(boolean wantedOperator) {
        this.wantedOperator = wantedOperator;
    }

    /**
     * 设置返回括号字符。
     * 
     * @param unwrapBracket - 返回括号字符
     */
    public void setWantedBracket(boolean wantedBracket) {
        this.wantedBracket = wantedBracket;
    }

    /**
     * 设置返回逗号字符。
     * 
     * @param wantedComma - 返回逗号字符
     */
    public void setWantedComma(boolean wantedComma) {
        this.wantedComma = wantedComma;
    }

    /**
     * 返回当前解析的语句。
     * 
     * @return 当前解析的语句
     */
    public String getSql() {

        return sql;
    }

    /**
     * 返回当前的解析位置。
     * 
     * @return 当前的解析位置
     */
    public int getOffset() {

        return offset;
    }

    /**
     * 检查是否剩下更多内容。
     * 
     * @return 是否剩下更多内容
     */
    public boolean moreTokens() {

        char ch;

        while (offset < length) {

            ch = sql.charAt(offset);

            if (!Character.isWhitespace(ch) // NL
                    // 如果需要返回逗号
                    && (wantedComma || (ch != COMMA))
                    // 如果需要返回操作符
                    && (wantedOperator || !isOperator(ch))
                    // 如果需要返回比较符
                    && (wantedComparison || !isComparison(ch))
                    // 如果需要返回括号内容或字符
                    && (!unwrapBracket || wantedBracket || (ch != LEFT_BRACKET))
                    // 如果需要返回括号括号字符
                    && (wantedBracket || (ch != RIGHT_BRACKET))) {

                return true;
            }

            offset++;
        }

        return false;
    }

    /**
     * 从语句中检查下一个内容。
     * 
     * <P>
     * 如果没有下一条内容，返回值是 <code>null</code>. 如果事先调用 moreTokens() 返回
     * <code>true</code> 就不会出现这种情况。
     * 
     * @return 下一个内容
     */
    public String nextToken() {

        int begin = offset;

        char ch;

        while (offset < length) {

            ch = sql.charAt(offset);

            if (Character.isWhitespace(ch)) {

                if (begin < offset) {

                    // 返回空白之前的部分
                    return sql.substring(begin, offset);

                } else {

                    offset++;

                    // 忽略这一段空白
                    moreTokens();

                    begin = offset;

                    continue;
                }

            } else if (isOperator(ch)) {

                if (begin < offset) {

                    // 返回操作符之前的部分
                    return sql.substring(begin, offset);

                } else if (wantedOperator) {

                    offset++;

                    // 返回单个操作符
                    return String.valueOf(ch);

                } else {

                    offset++;

                    // 忽略这一段空白
                    moreTokens();

                    begin = offset;

                    continue;
                }

            } else if (isComparison(ch)) {

                if (begin < offset) {

                    // 返回比较符号之前的部分
                    return sql.substring(begin, offset);

                } else if (wantedComparison) {

                    offset++;

                    // 返回全部比较符
                    nextComparison();

                    return sql.substring(begin, offset);

                } else {

                    offset++;

                    // 忽略这一段空白
                    moreTokens();

                    begin = offset;

                    continue;
                }

            } else if (ch == LEFT_BRACKET) {

                if (begin < offset) {

                    // 返回括号之前的部分
                    return sql.substring(begin, offset);

                } else if (!unwrapBracket) {

                    offset++;

                    // 返回全部的括号内容
                    nextBracket(LEFT_BRACKET, RIGHT_BRACKET);

                    return sql.substring(begin, offset);

                } else if (wantedBracket) {

                    offset++;

                    // 返回单个括号
                    return String.valueOf(ch);

                } else {

                    offset++;

                    // 忽略括号后面的空白
                    moreTokens();

                    begin = offset;

                    continue;
                }

            } else if (ch == RIGHT_BRACKET) {

                if (begin < offset) {

                    // 返回括号之前的部分
                    return sql.substring(begin, offset);

                } else if (wantedBracket) {

                    offset++;

                    // 返回单个括号
                    return String.valueOf(ch);

                } else {

                    offset++;

                    // 忽略括号后面的空白
                    moreTokens();

                    begin = offset;

                    continue;
                }

            } else if (ch == COMMA) {

                if (begin < offset) {

                    // 返回括号之前的部分
                    return sql.substring(begin, offset);

                } else if (wantedComma) {

                    offset++;

                    // 返回单个逗号
                    return String.valueOf(ch);

                } else {

                    offset++;

                    // 忽略逗号后面的空白
                    moreTokens();

                    begin = offset;

                    continue;
                }

            } else if (ch == QUOTE) {

                if (begin < offset) {

                    // 返回空白之前的部分
                    return sql.substring(begin, offset);

                } else {

                    offset++;

                    // 返回引用的内容
                    nextQuote(QUOTE);

                    return sql.substring(begin, offset);
                }

            } else if (ch == DOUBLE_QUOTE) {

                if (begin < offset) {

                    // 返回空白之前的部分
                    return sql.substring(begin, offset);

                } else {

                    offset++;

                    // 返回引用的内容
                    nextQuote(DOUBLE_QUOTE);

                    return sql.substring(begin, offset);
                }
            }

            offset++;
        }

        if (begin < offset) {

            // 返回空白之前的部分。
            return sql.substring(begin, offset);
        }

        return null;
    }

    /**
     * 从语句中查找下一个内容。
     * 
     * @param keyword - 查找的内容
     * 
     * @return 是否找到内容
     */
    public boolean findToken(String keyword) {

        while (moreTokens()) {

            String token = nextToken();

            // 语句的关键字是大小写不敏感的。
            if (keyword.equalsIgnoreCase(token)) {

                return true;
            }
        }

        return false;
    }

    /**
     * 检查语句中的引号。
     * 
     * @param quoteChar - 作为引号的标记
     */
    protected void nextQuote(char quoteChar) {

        char ch;

        while (offset < length) {

            ch = sql.charAt(offset);

            if (ch == '\\') {

                offset++; // 忽略后面的字符

            } else if (ch == quoteChar) {

                offset++;

                break; // 找到边界, 终止循环
            }

            offset++;
        }
    }

    /**
     * 检查语句中的括号，包括其中的嵌套括号。
     * 
     * @param bracketLeft - 左侧括号
     * @param bracketRight - 右侧括号
     */
    protected void nextBracket(char bracketLeft, char bracketRight) {

        char ch;

        int nesting = 0;

        while (offset < length) {

            ch = sql.charAt(offset);

            if (ch == QUOTE) {

                offset++;

                nextQuote(QUOTE);

                continue;

            } else if (ch == DOUBLE_QUOTE) {

                offset++;

                nextQuote(DOUBLE_QUOTE);

                continue;

            } else if (ch == bracketLeft) {

                nesting++; // 增加嵌套

            } else if (ch == bracketRight) {

                if (nesting == 0) {

                    offset++;

                    break; // 结束括号
                }

                nesting--;
            }

            offset++;
        }
    }

    /**
     * 检查语句中的比较符号。
     */
    protected void nextComparison() {

        char ch;

        while (offset < length) {

            ch = sql.charAt(offset);

            if (!isComparison(ch)) {
                break;
            }

            offset++;
        }
    }

    // 逗号字符
    private static final char COMMA = ',';

    // 单引号字符
    private static final char QUOTE = '\'';

    // 双引号字符
    private static final char DOUBLE_QUOTE = '\"';

    // 左括号字符
    private static final char LEFT_BRACKET = '(';

    // 右括号字符
    private static final char RIGHT_BRACKET = ')';

    /**
     * 检查指定的字符是不是运算符号。
     * 
     * @param ch - 指定的字符
     * 
     * @return 字符是不是运算符号
     */
    private static boolean isOperator(char ch) {

        return (ch == '.') || (ch == '+') || (ch == '-') || (ch == '*') || (ch == '/');
    }

    /**
     * 检查指定的字符是不是比较符号。
     * 
     * @param ch - 指定的字符
     * 
     * @return 字符是不是比较符号
     */
    private static boolean isComparison(char ch) {

        return (ch == '<') || (ch == '>') || (ch == '=');
    }
}
