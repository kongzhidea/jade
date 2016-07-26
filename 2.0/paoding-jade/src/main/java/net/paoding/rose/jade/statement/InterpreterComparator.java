package net.paoding.rose.jade.statement;

import org.springframework.core.annotation.Order;

import java.util.Comparator;

/**
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * 
 */
public class InterpreterComparator implements Comparator<Interpreter> {

    @Override
    public int compare(Interpreter thees, Interpreter that) {
        Order thessOrder = thees.getClass().getAnnotation(Order.class);
        Order thatOrder = that.getClass().getAnnotation(Order.class);
        int thessValue = thessOrder == null ? 0 : thessOrder.value();
        int thatValue = thatOrder == null ? 0 : thatOrder.value();
        return thessValue - thatValue;
    }
}
