package net.paoding.rose.jade.statement;

import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * 
 */
public class DefaultInterpreterFactory implements InterpreterFactory {

    private Interpreter[] interpreters = new Interpreter[] { new SystemInterpreter() };

    public DefaultInterpreterFactory() {
    }

    public DefaultInterpreterFactory(Interpreter[] interpreters) {
        for (Interpreter interpreter : interpreters) {
            this.addInterpreter(interpreter);
        }
    }

    public DefaultInterpreterFactory(List<Interpreter> interpreters) {
        for (Interpreter interpreter : interpreters) {
            this.addInterpreter(interpreter);
        }
    }

    public synchronized void addInterpreter(Interpreter interpreter) {
        if (!ArrayUtils.contains(this.interpreters, interpreter)) {
            Interpreter[] interpreters = Arrays.copyOf(this.interpreters,
                    this.interpreters.length + 1);
            interpreters[this.interpreters.length] = interpreter;
            Arrays.sort(interpreters, new InterpreterComparator());
            this.interpreters = interpreters;
        }
    }

    @Override
    public Interpreter[] getInterpreters(StatementMetaData metaData) {
        return interpreters;
    }

}
