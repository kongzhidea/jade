package com.meidusa.amoeba.sqljep.function;

import java.util.Comparator;

/**
 * 集合是否存在交集的计算
 * @author <a href=mailto:piratebase@sina.com>Struct chen</a>
 *
 */

@SuppressWarnings("unchecked")
public class Intersector {

    public static int doComparator(Comparable source,Comparable target,Comparator comparator){
        if(comparator == null){
            return source.compareTo(target);
        }else{
            return comparator.compare(source, target);
        }
    }

    private static boolean like(Comparable<?>  param1, Comparable<?>  param2){
        return Like.like(param1, param2);
    }
    public static boolean intersect(int sourceFunction,Comparable source, int targetFunction ,Comparable target,Comparator comparator) {
        switch(sourceFunction){
            case Comparative.Equivalent:{
                switch(targetFunction){
                    case Comparative.Equivalent:{
                        return doComparator(source,target,comparator) == 0;
                    }
                    case Comparative.GreaterThan:{
                        return doComparator(source,target,comparator)>0;
                    }
                    case Comparative.GreaterThanOrEqual:{
                        return doComparator(source,target,comparator)>=0;
                    }
                    case Comparative.LessThan:{
                        return doComparator(source,target,comparator)<0;
                    }
                    case Comparative.LessThanOrEqual:{
                        return doComparator(source,target,comparator)<=0;
                    }
                    case Comparative.NotEquivalent:{
                        return doComparator(source,target,comparator) !=0;
                    }
                    case Comparative.Like : {
                        return like(source,target);
                    }case Comparative.NotLike :{
                        return !like(source,target);
                    }
                }
            }
            
            case Comparative.GreaterThan:{
                switch(targetFunction){
                    case Comparative.Equivalent:{
                        return doComparator(source,target,comparator)< 0;
                    }
                    case Comparative.GreaterThan:{
                        return true;
                    }
                    case Comparative.GreaterThanOrEqual:{
                        return true;
                    }
                    case Comparative.LessThan:{
                        return doComparator(source,target,comparator)<0;
                    }
                    case Comparative.LessThanOrEqual:{
                        return doComparator(source,target,comparator)<0;
                    }
                    case Comparative.NotEquivalent:{
                        return true;
                    }
                }
            }
            
            case Comparative.GreaterThanOrEqual:{
                switch(targetFunction){
                    case Comparative.Equivalent:{
                        return doComparator(source,target,comparator)<= 0;
                    }
                    case Comparative.GreaterThan:{
                        return true;
                    }
                    case Comparative.GreaterThanOrEqual:{
                        return true;
                    }
                    case Comparative.LessThan:{
                        return doComparator(source,target,comparator)<0;
                    }
                    case Comparative.LessThanOrEqual:{
                        return doComparator(source,target,comparator)<=0;
                    }
                    case Comparative.NotEquivalent:{
                        return true;
                    }
                }
            }
            
            case Comparative.LessThan:{
                switch(targetFunction){
                    case Comparative.Equivalent:{
                        return doComparator(source,target,comparator)> 0;
                    }
                    case Comparative.GreaterThan:{
                        return doComparator(source,target,comparator)> 0;
                    }
                    case Comparative.GreaterThanOrEqual:{
                        return doComparator(source,target,comparator)> 0;
                    }
                    case Comparative.LessThan:{
                        return true;
                    }
                    case Comparative.LessThanOrEqual:{
                        return true;
                    }
                    case Comparative.NotEquivalent:{
                        return true;
                    }
                }
            }
            
            case Comparative.LessThanOrEqual:{
                switch(targetFunction){
                    case Comparative.Equivalent:{
                        return doComparator(source,target,comparator)>= 0;
                    }
                    case Comparative.GreaterThan:{
                        return doComparator(source,target,comparator)> 0;
                    }
                    case Comparative.GreaterThanOrEqual:{
                        return doComparator(source,target,comparator)>= 0;
                    }
                    case Comparative.LessThan:{
                        return true;
                    }
                    case Comparative.LessThanOrEqual:{
                        return true;
                    }
                    case Comparative.NotEquivalent:{
                        return true;
                    }
                }
            }
            
            case Comparative.NotEquivalent:{
                switch(targetFunction){
                    case Comparative.Equivalent:{
                        return doComparator(source,target,comparator)!= 0;
                    }
                    case Comparative.GreaterThan:{
                        return true;
                    }
                    case Comparative.GreaterThanOrEqual:{
                        return true;
                    }
                    case Comparative.LessThan:{
                        return true;
                    }
                    case Comparative.LessThanOrEqual:{
                        return true;
                    }
                    case Comparative.NotEquivalent:{
                        return true;
                    }
                }
            }
            case Comparative.Like:{
                switch(targetFunction){
                    case Comparative.Equivalent:{
                        return like(source,target);
                    }
                    case Comparative.Like:{
                        return like(source,target);
                    }
                    
                    case Comparative.NotLike:{
                        return !like(source,target);
                    }
                }
            }
        }
        
        return false;
    }

}
