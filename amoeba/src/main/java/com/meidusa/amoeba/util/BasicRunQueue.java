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

/**
 * A very basic implementation of RunQueue for general purpose use.
 */
public class BasicRunQueue extends LoopingThread implements RunQueue {

    /** The queue of things to run. */
    protected Queue<Runnable> _queue;

    /**
     * Construct a BasicRunQueue with a default Queue implementation.
     */
    public BasicRunQueue(){
        super("RunQueue");
        _queue = new Queue<Runnable>();
    }

    public BasicRunQueue(String queueName){
        super(queueName);
        _queue = new Queue<Runnable>();
    }

    // documentation inherited from interface
    public void postRunnable(Runnable r) {
        _queue.append(r);
    }

    // documentation inherited from interface
    public boolean isDispatchThread() {
        return Thread.currentThread() == this;
    }

    // documentation inherited
    protected void iterate() {
        Runnable r = _queue.get();
        try {
            r.run();
        } catch (Throwable t) {
            // TODO log Runnable posted to RunQueue barfed
        }
    }

    // documentation inherited
    protected void kick() {
        postRunnable(new Runnable() {

            public void run() {
                // nothing
            }
        });
    }

    public int size() {
        return _queue.size();
    }

}
