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
package com.meidusa.amoeba.parser.dbobject;

import com.meidusa.amoeba.util.StringUtil;

/**
 * @author <a href=mailto:piratebase@sina.com>Struct chen</a>
 */
public class Table implements DBObjectBase {

    private Schema schema;
    private String name;
    private String alias;

    public String getSql() {
        return (schema == null) ? name : (schema.getSql() + "." + this.getName());
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtil.isEmpty(name) ? null : name.trim();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean equals(Object object) {
        boolean isMatched = true;
        if (object instanceof Table) {
            Table other = (Table) object;

            if (schema == null) {
                isMatched = isMatched && (other.schema == null);
            } else {
                isMatched = isMatched && (schema.equals(other.schema));
            }
            isMatched = isMatched && StringUtil.equalsIgnoreCase(((Table) object).name, name);
            return isMatched;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return 211 + (name == null ? 0 : name.toLowerCase().hashCode()) + (schema == null ? 0 : schema.hashCode());
    }

    public String toString() {
        return getSql();
    }
}
