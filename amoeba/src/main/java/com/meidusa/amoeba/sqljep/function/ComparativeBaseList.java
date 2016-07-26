package com.meidusa.amoeba.sqljep.function;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ComparativeBaseList extends Comparative {

    protected List<Comparative> list = new ArrayList<Comparative>();

    public ComparativeBaseList(int function, Comparable<?> value){
        super(function, value);
        list.add(new Comparative(function, value));
    }

    protected ComparativeBaseList(){
        super();
    }

    public ComparativeBaseList(Comparative item){
        super(item.getComparison(), item.getValue());
        list.add(item);
    }

    public List<Comparative> getList() {
        return list;
    }

    public void addComparative(Comparative item) {
        this.list.add(item);
    }

    @SuppressWarnings("unchecked")
    public abstract boolean intersect(int function, Comparable other, Comparator comparator);

    public Object clone() {
        try {
            Constructor<? extends ComparativeBaseList> con = this.getClass().getConstructor((Class[]) null);
            ComparativeBaseList compList = con.newInstance((Object[]) null);
            for (Comparative com : list) {
                compList.addComparative((Comparative) com.clone());
            }
            compList.setComparison(this.getComparison());
            compList.setValue(this.getValue());
            return compList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
