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

	class ValueComparator implements Comparator<Map.Entry<String, Integer>> {
		public int compare(Map.Entry<String, Integer> m,
				Map.Entry<String, Integer> n) {
			return n.getValue() - m.getValue();
		}
	}

	public TopApp(Map<String, Integer> app_fre) {
		this.app_fre = app_fre;
	}

	private Map<String, Integer> app_fre = new HashMap<String, Integer>();
	private ValueComparator vc = new ValueComparator();
	/**
	 * 通过FilterList进行过滤
	 */
	public List<Map.Entry<String, Integer>> toApp(Map<String, Integer> filter) {
		List<Map.Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>();
		for (Iterator<Entry<String, Integer>> iterator = app_fre.entrySet()
				.iterator(); iterator.hasNext();) {
			Entry<String, Integer> e = iterator.next();
			String c = e.getKey();
			if (!filter.containsKey(c)) {//即没被过滤
				list.add(e);
			}
		}
//		
//		
//		for (Iterator<Entry<String, Integer>> iterator = filter.entrySet()
//				.iterator(); iterator.hasNext();) {
//			Entry<String, Integer> e = iterator.next();
//			if (null != app_fre.get(e.getKey())) {
//				e.setValue(app_fre.get(e.getKey()));
//			}
//			app_fre.remove(e.getKey());
//		}
//		list.addAll(app_fre.entrySet());
//		for (Iterator<Entry<String, Integer>> iterator = filter.entrySet()
//				.iterator(); iterator.hasNext();) {
//			Entry<String, Integer> e = iterator.next();
//			app_fre.put(e.getKey(), e.getValue());
//		}
		Collections.sort(list, vc);
		return list;
	}
}