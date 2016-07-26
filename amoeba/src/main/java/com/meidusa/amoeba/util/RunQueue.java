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
 * An interface for a service that queues up execution of Runnables.
 */
public interface RunQueue {

    /**
     * Post the specified Runnable to be run on the RunQueue.
     */
    public void postRunnable(Runnable r);

    /**
     * @return true if the calling thread is the RunQueue dispatch thread.
     */
    public boolean isDispatchThread();

    public int size();
}
