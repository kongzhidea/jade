/**
 * <pre>
 *  This program is free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * </pre>
 */
package com.meidusa.amoeba.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 该类作为线程提供了循环的基本功能，能够处理一个简单的循环。 最大特点是每一次通过循环，事件处理线程可以轻易地安排处理类似事件队列。
 */
public class LoopingThread extends Thread {

    private static Log log = LogFactory.getLog(LoopingThread.class);

    protected boolean _running = true;

    public LoopingThread() {
    }

    /**
     * 指定一个特殊的命名
     */
    public LoopingThread(String name) {
        super(name);
    }

    /**
     * 允许关闭该线程，如果此次请求并发本线程发出，则需要清理相关的事情。
     */
    public synchronized void shutdown() {
        _running = false;

        // only kick the thread if it's not requesting it's own shutdown
        if (this != Thread.currentThread()) {
            kick();
        }
    }

    /**
     * 循环
     */
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug(this.getName() + " LoopingThread willStart....");
        }
        try {
            willStart();

            while (isRunning()) {
                try {
                    iterate();
                } catch (Exception e) {
                    handleIterateFailure(e);
                }
            }
        } finally {
            didShutdown();
        }
    }

    /**
     * 检查线程是否处于运行状态。如果该方法返回false，此时线程如果处于iterate调用，将会退出循环。 这个方法作为循环线程的一部分 {@link #run}
     */
    public synchronized boolean isRunning() {
        return _running;
    }

    /**
     * 当线程在退出的时候，需要清理内部环境或者进行其他操作
     */
    protected void kick() {
        // nothing doing by default
    }

    /**
     * 线程在开始执行的时候，可以在这个方法里面做一些初始化的动作
     */
    protected void willStart() {
    }

    protected void iterate() {
        throw new RuntimeException("Derived class must implement iterate().");
    }

    protected void handleIterateFailure(Exception e) {
        // log the exception

        // and shut the thread down
        log.error("error:", e);
        shutdown();
    }

    /**
     * 完成shutdown动作以后。即将退出整个线程的运行。
     */
    protected void didShutdown() {
    }

}
