/*
 * 	This program is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, 
 * or (at your option) any later version. 
 * 
 * 	This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details. 
 * 	You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.meidusa.amoeba.util;

import java.io.Serializable;

/**
 * A tuple is a simple object that holds a reference to two other objects.
 * It provides hashcode and equality semantics that allow it to be used to
 * combine two objects into a single key (for hashtables, etc.).
 */
public class Tuple<L,R> implements Serializable
{
    /** The left object. */
    public L left;

    /** The right object. */
    public R right;

    /** Construct a tuple with the specified two objects. */
    public Tuple (L left, R right)
    {
        this.left = left;
        this.right = right;
    }

    /** Construct a blank tuple. */
    public Tuple ()
    {
    }

    /**
     * Returns the combined hashcode of the two elements.
     */
    public int hashCode ()
    {
        return left.hashCode() ^ right.hashCode();
    }

    /**
     * A tuple is equal to another tuple if the left and right elements
     * are equal to the left and right elements (respectively) of the
     * other tuple.
     */
    @SuppressWarnings("unchecked")
	public boolean equals (Object other)
    {
        if (other instanceof Tuple) {
            Tuple to = (Tuple)other;
            return (left.equals(to.left) && right.equals(to.right));
        } else {
            return false;
        }
    }

    /**
     * Generates a string representation of this tuple.
     */
    public String toString ()
    {
        return "[left=" + left + ", right=" + right + "]";
    }

    /** Change this if the fields or inheritance hierarchy ever changes
     * (which is extremely unlikely). We override this because I'm tired
     * of serialized crap not working depending on whether I compiled with
     * jikes or javac. */
    private static final long serialVersionUID = 1;
}
