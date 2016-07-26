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

import java.util.logging.Level;

/**
 * @author <a href=mailto:piratebase@sina.com>Struct chen</a>
 */
public interface Reporter {

    /**
     * @param buffer 组装report信息的 StringBuilder
     * @param now report 任务开始产生report的当时时间，单位ms
     * @param sinceLast 上一次report的时间
     * @param reset 是否需要重新统计
     */
    public void appendReport(StringBuilder buffer, long now, long sinceLast, boolean reset, Level level);

    // public void alertStartReport();

    public static interface SubReporter {

        public void appendReport(StringBuilder buffer, long now, long sinceLast, boolean reset, Level level);
    }
}
