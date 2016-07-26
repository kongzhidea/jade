package com.meidusa.amoeba.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@SuppressWarnings("unchecked")
public class SoftCache extends AbstractMap
{
    private class Entry
        implements java.util.Map.Entry
    {
        private java.util.Map.Entry ent;
        private Object value;

        Entry(java.util.Map.Entry entry, Object obj)
        {
            ent = entry;
            value = obj;
        }
        
        public Object getKey()
        {
            return ent.getKey();
        }

        public Object getValue()
        {
            return value;
        }

        public Object setValue(Object obj)
        {
            return ent.setValue(ValueCell.create(ent.getKey(), obj, queue));
        }

        public boolean equals(Object obj)
        {
            if(!(obj instanceof java.util.Map.Entry))
            {
                return false;
            } else
            {
                java.util.Map.Entry entry = (java.util.Map.Entry)obj;
                return SoftCache.valEquals(ent.getKey(), entry.getKey()) && SoftCache.valEquals(value, entry.getValue());
            }
        }

        public int hashCode()
        {
            Object obj;
            return ((obj = getKey()) != null ? obj.hashCode() : 0) ^ (value != null ? value.hashCode() : 0);
        }

    }

    private class EntrySet extends AbstractSet
    {

        public Iterator iterator()
        {
            return new Iterator() {

                Iterator hashIterator;
                Entry next;
                {
                    hashIterator = hashEntries.iterator();
                    next = null;
                }
                
                public boolean hasNext()
                {
                    while(hashIterator.hasNext()) 
                    {
                        java.util.Map.Entry entry = (java.util.Map.Entry)hashIterator.next();
                        ValueCell valuecell = (ValueCell)entry.getValue();
                        Object obj = null;
                        if(valuecell == null || (obj = valuecell.get()) != null)
                        {
                            next = new Entry(entry, obj);
                            return true;
                        }
                    }
                    return false;
                }

                public Object next()
                {
                    if(next == null && !hasNext())
                    {
                        throw new NoSuchElementException();
                    } else
                    {
                        Entry entry = next;
                        next = null;
                        return entry;
                    }
                }

                public void remove()
                {
                    hashIterator.remove();
                }

            };
        }

        public boolean isEmpty()
        {
            return !iterator().hasNext();
        }

        public int size()
        {
            int i = 0;
            for(Iterator iterator1 = iterator(); iterator1.hasNext(); iterator1.next())
                i++;

            return i;
        }

        public boolean remove(Object obj)
        {
            processQueue();
            if(obj instanceof Entry)
                return hashEntries.remove(((Entry)obj).ent);
            else
                return false;
        }

        Set hashEntries;

        private EntrySet()
        {
            hashEntries = hash.entrySet();
        }

    }

    private static class ValueCell extends SoftReference
    {

        private static ValueCell create(Object obj, Object obj1, ReferenceQueue referencequeue)
        {
            if(obj1 == null)
                return null;
            else
                return new ValueCell(obj, obj1, referencequeue);
        }

        private static Object strip(Object obj, boolean flag)
        {
            if(obj == null)
                return null;
            ValueCell valuecell = (ValueCell)obj;
            Object obj1 = valuecell.get();
            if(flag)
                valuecell.drop();
            return obj1;
        }

        private boolean isValid()
        {
            return key != INVALID_KEY;
        }

        private void drop()
        {
            super.clear();
            key = INVALID_KEY;
            dropped++;
        }

        private static Object INVALID_KEY = new Object();
        private static int dropped = 0;
        private Object key;

        private ValueCell(Object obj, Object obj1, ReferenceQueue referencequeue)
        {
            super(obj1, referencequeue);
            key = obj;
        }
    }


    private void processQueue()
    {
        ValueCell valuecell;
        while((valuecell = (ValueCell)queue.poll()) != null) 
            if(valuecell.isValid())
                hash.remove(valuecell.key);
            else
                ValueCell.dropped--;
    }

    public SoftCache(int i, float f)
    {
        queue = new ReferenceQueue();
        entrySet = null;
        hash = new HashMap(i, f);
    }

    public SoftCache(int i)
    {
        queue = new ReferenceQueue();
        entrySet = null;
        hash = new HashMap(i);
    }

    public SoftCache()
    {
        queue = new ReferenceQueue();
        entrySet = null;
        hash = new HashMap();
    }

    public int size()
    {
        return entrySet().size();
    }

    public boolean isEmpty()
    {
        return entrySet().isEmpty();
    }

    public boolean containsKey(Object obj)
    {
        return ValueCell.strip(hash.get(obj), false) != null;
    }

    protected Object fill(Object obj)
    {
        return null;
    }

    public Object get(Object obj)
    {
        processQueue();
        Object obj1 = hash.get(obj);
        if(obj1 == null)
        {
            obj1 = fill(obj);
            if(obj1 != null)
            {
                hash.put(obj, ValueCell.create(obj, obj1, queue));
                return obj1;
            }
        }
        return ValueCell.strip(obj1, false);
    }

    public Object put(Object obj, Object obj1)
    {
        processQueue();
        ValueCell valuecell = ValueCell.create(obj, obj1, queue);
        return ValueCell.strip(hash.put(obj, valuecell), true);
    }

    public Object remove(Object obj)
    {
        processQueue();
        return ValueCell.strip(hash.remove(obj), true);
    }

    public void clear()
    {
        processQueue();
        hash.clear();
    }

    private static boolean valEquals(Object obj, Object obj1)
    {
        return obj != null ? obj.equals(obj1) : obj1 == null;
    }

    public Set entrySet()
    {
        if(entrySet == null)
            entrySet = new EntrySet();
        return entrySet;
    }

    private Map hash;
    private ReferenceQueue queue;
    private Set entrySet;




}
