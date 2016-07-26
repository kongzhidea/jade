/**
 * <pre>
 * 	This program is free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * 	This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 	You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * </pre>
 */
package com.meidusa.amoeba.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.HashMap;


/**
 * <pre>
 * The invoker is used to invoke self-contained units of code on an
 * invoking thread. Each invoker is associated with its own thread and
 * that thread is used to invoke all of the units posted to that invoker
 * in the order in which they were posted. The invoker also provides a
 * convenient mechanism for processing the result of an invocation back on
 * the main thread.
 * &lt;p&gt; The invoker is a useful tool for services that need to block and
 * therefore cannot be run on the main thread. For example, an interactive
 * application might provide an invoker on which to run database queries.
 * &lt;p&gt; Bear in mind that each invoker instance runs units on its own
 * thread and care must be taken to ensure that code running on separate
 * invokers properly synchronizes access to shared information. Where
 * possible, complete isolation of the services provided by a particular
 * invoker is desirable.
 * </pre>
 */
public class Invoker extends LoopingThread {

    private static Log log = LogFactory.getLog(Invoker.class);

    /** The invoker's queue of units to be executed. */
    protected Queue<Unit> _queue = new Queue<Unit>();

    /** The result receiver with which we're working. */
    protected RunQueue _receiver;

    /** Tracks the counts of invocations by unit's class. */
    protected HashMap<Object, UnitProfile> _tracker = new HashMap<Object, UnitProfile>();

    /** The total number of invoker units run since the last report. */
    protected int _unitsRun;

    /**
     * The duration of time after which we consider a unit to be delinquent and log a warning.
     */
    protected long _longThreshold = 500L;

    /** Whether or not to track invoker unit performance. */
    protected static final boolean PERF_TRACK = true;

    /**
     * <pre>
     * The unit encapsulates a unit of executable code that will be run on
     * the invoker thread. It also provides facilities for additional code
     * to be run on the main thread once the primary code has completed on
     * the invoker thread.
     * </pre>
     */
    public static abstract class Unit implements Runnable {

        /** The time at which this unit was placed on the queue. */
        public long queueStamp;

        /** The default constructor. */
        public Unit() {
        }

        /**
         * Creates an invoker unit which will report the supplied name in {@link #toString}.
         */
        public Unit(String name) {
            _name = name;
        }

        /**
         * <pre>
         * This method is called on the invoker thread and should be used
         * to perform the primary function of the unit. It can return true
         * to cause the &lt;code&gt;handleResult&lt;/code&gt; method to be
         * subsequently invoked on the dobjmgr thread (generally to allow
         * the results of the invocation to be acted upon back in the
         * context of the regular world) or false to indicate that no
         * further processing should be performed.
         * &#064;return true if the &lt;code&gt;handleResult&lt;/code&gt; method should be
         * invoked on the main thread, false if not.
         * </pre>
         */
        public abstract boolean invoke();

        /**
         * <pre>
         * Invocation unit implementations can implement this function to
         * perform any post-unit-invocation processing back on the main
         * thread. It will be invoked if &lt;code&gt;invoke&lt;/code&gt; returns true.
         * </pre>
         */
        public void handleResult() {
            // do nothing by default
        }

        // we want to be a runnable to make the receiver interface simple,
        // but we'd like for invocation unit implementations to be able to
        // put their result handling code into an aptly named method
        public void run() {
            handleResult();
        }

        /** Returns the name of this invoker. */
        public String toString() {
            return _name;
        }

        protected String _name = "Unknown";
    }

    /**
     * Creates an invoker that will post results to the supplied result receiver.
     */
    public Invoker(String name, RunQueue resultReceiver) {
        super(name);
        _receiver = resultReceiver;
    }

    /**
     * <pre>
     * Configures the duration after which an invoker unit will be considered
     * &quot;long&quot;. When they complete, long units have a warning message
     * logged. The default long threshold is 500 milliseconds.
     * </pre>
     */
    public void setLongThresholds(long longThreshold) {
        _longThreshold = longThreshold;
    }

    /**
     * Posts a unit to this invoker for subsequent invocation on the invoker's thread.
     */
    public void postUnit(Unit unit) {
        // note the time
        unit.queueStamp = System.currentTimeMillis();
        // and append it to the queue
        _queue.append(unit);
    }

    // documentation inherited
    public void iterate() {
        // pop the next item off of the queue
        Unit unit = _queue.get();

        long start;
        if (PERF_TRACK) {
            // record the time spent on the queue as a special unit
            start = System.currentTimeMillis();
            synchronized (this) {
                _unitsRun++;
            }
            // record the time spent on the queue as a special unit
            recordMetrics("queue_wait_time", start - unit.queueStamp);
        }

        try {
            willInvokeUnit(unit, start);
            if (unit.invoke()) {
                // if it returned true, we post it to the receiver thread
                // to invoke the result processing
                _receiver.postRunnable(unit);
            }
            didInvokeUnit(unit, start);

        } catch (Throwable t) {
            log.warn("Invocation unit failed [unit=" + unit + "].", t);
        }
    }

    /**
     * <pre>
     * Shuts down the invoker thread by queueing up a unit that will cause
     * the thread to exit after all currently queued units are processed.
     * </pre>
     */
    public void shutdown() {
        _queue.append(new Unit() {

            public boolean invoke() {
                _running = false;
                return false;
            }
        });
    }

    /**
     * Called before we process an invoker unit.
     *
     * @param unit the unit about to be invoked.
     * @param start a timestamp recorded immediately before invocation if {@link #PERF_TRACK} is enabled, 0L otherwise.
     */
    protected void willInvokeUnit(Unit unit, long start) {
    }

    /**
     * Called before we process an invoker unit.
     *
     * @param unit the unit about to be invoked.
     * @param start a timestamp recorded immediately before invocation if {@link #PERF_TRACK} is enabled, 0L otherwise.
     */
    protected void didInvokeUnit(Unit unit, long start) {
        // track some performance metrics
        if (PERF_TRACK) {
            long duration = System.currentTimeMillis() - start;
            Object key = unit.getClass();
            recordMetrics(key, duration);

            // report long runners
            if (duration > _longThreshold) {
                String howLong = (duration >= 10 * _longThreshold) ? "Really long" : "Long";
                log.warn(howLong + " invoker unit [unit=" + unit + " (" + key + "), time=" + duration + "ms].");
            }
        }
    }

    protected void recordMetrics(Object key, long duration) {
        UnitProfile prof = _tracker.get(key);
        if (prof == null) {
            _tracker.put(key, prof = new UnitProfile());
        }
        prof.record(duration);
    }

    /** Used to track profile information on invoked units. */
    protected static class UnitProfile {

        public void record(long duration) {
            _totalElapsed += duration;
            _histo.addValue((int) duration);
        }

        public void clear() {
            _totalElapsed = 0L;
            _histo.clear();
        }

        public String toString() {
            int count = _histo.size();
            return _totalElapsed + "ms/" + count + " = " + (_totalElapsed / count) + "ms avg " + Arrays.toString(_histo.getBuckets());
        }

        // track in buckets of 50ms up to 500ms
        protected Histogram _histo = new Histogram(0, 50, 10);
        protected long _totalElapsed;
    }

    public int size() {
        return _queue.size();
    }

}
