package com.qihoo.huangmabisheng.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TopApp {

	private static class ValueComparator implements
			Comparator<Map.Entry<String, Integer>> {
		public int compare(Map.Entry<String, Integer> m,
				Map.Entry<String, Integer> n) {
			return n.getValue() - m.getValue();
		}
	}
    public TopApp(Map<String, Integer> app_fre){
    	this.app_fre = app_fre;
    }
	private Map<String, Integer> app_fre = new HashMap<String, Integer>();
	List<Map.Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>();

	public List<Map.Entry<String, Integer>> toApp(Map<String, Integer> filter) {
		for (Iterator<Entry<String, Integer>>iterator= filter.entrySet().iterator();iterator.hasNext();) {
			Entry<String, Integer> e = iterator.next();
			if(null != app_fre.get(e.getKey())){
				e.setValue(app_fre.get(e.getKey()));
				}
			app_fre.remove(e.getKey());
		}
		list.addAll(app_fre.entrySet());
		for (Iterator<Entry<String, Integer>>iterator= filter.entrySet().iterator();iterator.hasNext();) {
			Entry<String, Integer> e = iterator.next();
			app_fre.put(e.getKey(),e.getValue());
		}
		ValueComparator vc = new ValueComparator();
		Collections.sort(list, vc);
		return list;
	}
}