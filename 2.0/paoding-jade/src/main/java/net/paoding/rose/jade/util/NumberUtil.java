package net.paoding.rose.jade.util;

import org.apache.commons.lang.ClassUtils;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberUtil {

    public static Number convertNumberToTargetClass(Number number, Class targetClass) throws IllegalArgumentException {
        targetClass = ClassUtils.primitiveToWrapper(targetClass);
        Assert.notNull(number, "Number must not be null");
        Assert.notNull(targetClass, "Target class must not be null");
        if (targetClass.isInstance(number)) {
            return number;
        } else {
            long value;
            if (targetClass.equals(Byte.class)) {
                value = number.longValue();
                if (value < -128L || value > 127L) {
                    raiseOverflowException(number, targetClass);
                }

                return new Byte(number.byteValue());
            } else if (targetClass.equals(Short.class)) {
                value = number.longValue();
                if (value < -32768L || value > 32767L) {
                    raiseOverflowException(number, targetClass);
                }

                return new Short(number.shortValue());
            } else if (!targetClass.equals(Integer.class)) {
                if (targetClass.equals(Long.class)) {
                    return new Long(number.longValue());
                } else if (targetClass.equals(BigInteger.class)) {
                    return number instanceof BigDecimal ? ((BigDecimal) number).toBigInteger() : BigInteger.valueOf(number.longValue());
                } else if (targetClass.equals(Float.class)) {
                    return new Float(number.floatValue());
                } else if (targetClass.equals(Double.class)) {
                    return new Double(number.doubleValue());
                } else if (targetClass.equals(BigDecimal.class)) {
                    return new BigDecimal(number.toString());
                } else {
                    throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to unknown target class [" + targetClass.getName() + "]");
                }
            } else {
                value = number.longValue();
                if (value < -2147483648L || value > 2147483647L) {
                    raiseOverflowException(number, targetClass);
                }

                return new Integer(number.intValue());
            }
        }
    }

    private static void raiseOverflowException(Number number, Class targetClass) {
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
    }

}
