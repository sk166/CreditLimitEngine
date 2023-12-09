package com.kaya.credit.datastructures.portfolio.sector;

import java.util.HashSet;
import java.util.Set;

public class TradeSector {
    Set<String> arrlist;
    public static String NOTSECTOR="";
    public TradeSector()
    {
        arrlist=new HashSet<>();
    }
    public TradeSector(HashSet<String> initialValues)
    {
        arrlist=initialValues;
    }
    public void init(HashSet<String> initialValues)
    {
        arrlist=initialValues;
    }
    public Set<String> getValues()
    {
        return arrlist;
    }
    public boolean contains(String sector)
    {
        if(arrlist.contains(sector))
        {
            return true;
        }else {
            return false;
        }
    }
}
