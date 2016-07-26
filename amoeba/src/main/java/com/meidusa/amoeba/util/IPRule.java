package com.meidusa.amoeba.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPRule {
    private static final Map<String,Pattern> ipPattern = new HashMap<String,Pattern>();
    
    /**
     * IP Pattern
     * @author struct
     *
     */
    static interface IPPattern{
        boolean isMatch(String ip);
    }
    
    /**
     * 数字。只能小于256
     * @author struct
     *
     */
    static class NumPattern implements IPPattern{
        int source;
        
        public NumPattern(String string){
            source = Integer.valueOf(string);
        }
        public boolean isMatch(String ip){
            return source == Integer.valueOf(ip);
        }
    }
    
    /**
     * 只能包含一个"-"符号，并且在前后都是小于256，前面数字比后面数字小
     * @author struct
     *
     */
    static class SegmentPattern implements IPPattern{
        int begin;
        int end;
        public SegmentPattern(String string){
            String[] temp = StringUtil.split(string,'-');
            begin = Integer.parseInt(temp[0]);
            end = Integer.parseInt(temp[1]);
        }
        
        public boolean isMatch(String ip){
            int ippart = Integer.valueOf(ip);
            return begin <= ippart && ippart <= end;
        }
    }
    
    /**
     * IP Pattern 只能包含一个*并且在结尾。 
     * @author struct
     *
     */
    static class StarPattern implements IPPattern{
        String starString;
        
        public StarPattern(String ipRegexString){
            starString = StringUtil.replace(ipRegexString,"*","");
        }
        
        public boolean isMatch(String ip){
            return ip.startsWith(starString);
        }
    }
    
    static class IPRegex implements IPPattern{
        IPPattern[] patterns = new IPPattern[4];
        public IPRegex(String ip){
            String[] tmps = StringUtil.split(ip,'.');
            for(int i=0;i< tmps.length;i++){
                if(tmps[i].contains("-")){
                    patterns[i] = new SegmentPattern(tmps[i]);
                }else if(tmps[i].contains("*")){
                    patterns[i] = new StarPattern(tmps[i]);
                }else{
                    patterns[i] = new NumPattern(tmps[i]);
                }
            }
        }
        
        public boolean isMatch(String ip) {
            String tmps[] = StringUtil.split(ip,'.');
            for(int i=0;i<tmps.length;i++){
                if(!patterns[i].isMatch(tmps[i])){
                    return false;
                }
            }
            return true;
        }
        
    }
    
    static class Regex{
        static boolean IsMatch(String ip,String ipRegexString){
            Pattern pattern = ipPattern.get(ipRegexString);
            if(pattern == null){
                pattern = Pattern.compile(ipRegexString);
                ipPattern.put(ipRegexString, pattern);
            }
            Matcher m = pattern.matcher(ip);
            return m.matches();
        }
    }
    /// <summary>
    /// 判断指定的IP是否在指定的 规则下允许的(三个特殊符号 -?*）
    /// rule[192.*.1.236-239:yes;192.*.1.226:no;218.85.*.*:no]最后一个不要加";"分号
    /// 前面的规则优先级高
    /// 注意，规则中的 * - ? 不能同时存在于同一个段内 如: 192.168.*?.123 会出错
    /// *号在同一段内只能有一个, 如 192.16*.1.*,  192.1**.1.1 是错误的，可以用 ?号代替
    /// </summary>
    /// <param name="rule">(192.*.1.236-239:yes;192.*.1.226:no;218.85.*.*:no) 最后一个规则不要再多加";"分号</param>
    /// <param name="ip">192.168.1.237(不正确的IP会出错)</param>
    /// <returns></returns>
    public static boolean IsAllowIP(String rule, String ip) throws Exception
    {
        String[] ruleArray = StringUtil.split(rule,";");
        return isAllowIP(ruleArray,ip);
    }
    
    public static boolean isAllowIP(String[]ruleArray ,String ip) throws Exception{
        //IP正则表达式
        String ipRegexString = "^((2[0-4][0-9]|25[0-5]|[01]?[0-9][0-9]?).){3}(2[0-4][0-9]|25[0-5]|[01]?[0-9][0-9]?)$";
        //如果IP地址是错的，禁止
        if (!Regex.IsMatch(ip, ipRegexString))
        {
            throw new Exception("参数ip错误：错误的IP地址" + ip);
        }
        
        //分离规则
        String[] ipdata = StringUtil.split(ip,".");
        boolean retValue = false;//默认返回值

        //遍历规则并验证
        for(String s : ruleArray){
            boolean IsFind = false;
            String[] data = StringUtil.split(s,':');
            //如果没有用:分开
            if (data.length != 2) { throw new Exception("请用:分开 如:192.168.1.1:yes"); }

            String ruleIp = data[0];//得到 192.168.20-60.*:yes 中的 [192.168.20-60.*]部分
            retValue = data[1].equalsIgnoreCase("yes") ? true : false;


            String[] ruleIpArray = StringUtil.split(ruleIp,'.');
            if (ruleIpArray.length != 4) { throw new Exception("IP部分错误！"); }

            //region
            for (int i = 0; i < 4; i++)
            {
                boolean AA = ruleIpArray[i].contains("*");
                boolean BB = ruleIpArray[i].contains("-");
                boolean CC = ruleIpArray[i].contains("?");
                if ((AA && BB) || (AA && CC) || (BB && CC) || (AA && BB && CC))
                {
                    throw new Exception("这样的格式是错误的,192.168.15-20*,*与-不能包含在同一个部分! ");
                }
                else if (!AA && !BB && !CC) //没有包含 * 与 - 及 ?
                {
                    if (!Regex.IsMatch(ruleIpArray[i], "^2[0-4][0-9]|25[0-5]|[01]?[0-9][0-9]?$"))
                    {
                        throw new Exception("IP段错误应该在1~255之间:" + ruleIpArray[i]);
                    }
                    else
                    {
                        //region 这里判断 111111111111
                        if (ruleIpArray[i].equalsIgnoreCase(ipdata[i]))
                        {
                            IsFind = true;
                        }
                        else
                        {
                            IsFind = false;
                            break;
                        }
                        //endregion
                    }
                }
                else if (AA && !BB && !CC) //包含 [*] 的
                {
                    if (!ruleIpArray[i].equalsIgnoreCase("*"))
                    {
                        if (ruleIpArray[i].startsWith("*") || !ruleIpArray[i].endsWith("*") || ruleIpArray[i].contains("**"))
                        {
                            throw new Exception("IP中的*部分：不能以*开头，不能有两个**，只能以*结尾");
                        }
                    }
                    else
                    {
                        //region 这里判断22222222222222
                        if (ipdata[i].startsWith(ruleIpArray[i].replace("*", "")))
                        {
                            IsFind = true;
                        }
                        else
                        {
                            IsFind = false;
                            break;
                        }
                        //endregion
                    }
                }
                else if (BB && !AA && !CC) //包含 [-] 的
                {

                    String[] temp = StringUtil.split(ruleIpArray[i],'-');
                    if (temp.length != 2)
                    {
                        throw new Exception("IP段错误, 如:23-50,在1~255之间");
                    }
                    else
                    {
                        int[] nums = {Integer.parseInt(temp[0]),Integer.parseInt(temp[1])};
                        if (nums[0] < 1 || nums[1]  > 255)
                        {
                            throw new Exception("IP段错误, 如:23-50,在1~255之间");
                        }
                        else
                        {
                            int ipNum = Integer.parseInt(ipdata[i]);
                            if (ipNum >= nums[0] && ipNum <= nums[1])
                            {
                                IsFind = true;
                            }
                            else
                            {
                                IsFind = false;
                                break;
                            }
                        }
                    }
                }
                else if (CC && !AA & !BB) //包含 [?] 的
                {
                    //去掉问号后 
                    String temp = ruleIpArray[i].replace("?", "");
                    if (!Regex.IsMatch(temp,"^[0-9][0-9]?$") || temp.length() > 2)
                    {
                        throw new Exception("IP段错误:" + ruleIpArray[i]);
                    }
                    else
                    {
                        if (ruleIpArray[i].length() != ipdata[i].length())
                        {
                            IsFind = false;
                            break;
                        }
                        else
                        {
                            String tempRegstring = "^" + ruleIpArray[i].replace("?", "([0-9])*") + "$";
                            if (Regex.IsMatch(ipdata[i],tempRegstring))
                            {
                                IsFind = true;
                            }
                            else
                            {
                                IsFind = false;
                                break;
                            }
                        }
                        //endregion
                    }
                }
                else
                {
                    IsFind = false;
                    break;
                }


            }
            //endregion
            if (IsFind)
            {
                return retValue;//IP规则中 :后面的 yes/no 对应的  true false
            }
        }
        return false;
    }
    
    public static void main(String[] args){
        try {
            long start = System.currentTimeMillis();
            for(int i=0;i<1;i++){
                IPRule.IsAllowIP("192.*.1.236-239:yes","192.84.1.226");
            }
            System.out.println(System.currentTimeMillis()-start);
            
            start = System.currentTimeMillis();
            IPRegex regex = new IPRegex("192.1*.1.236-239");
            for(int i=0;i<1000000;i++){
                System.out.println(regex.isMatch("192.181.1.236"));
                //regex.isMatch("192.181.1.236");
            }
            System.out.println(System.currentTimeMillis()-start);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
