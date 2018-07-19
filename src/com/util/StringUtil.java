package com.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

	public static char[] convert(char[] o2Arr) {
		char[] o2ReverArr = new char[o2Arr.length];
		int index = 0;
		for (char o : o2Arr) {
			switch (o) {
			case 'A':
				o2ReverArr[index] = 'T';
				break;
			case 'T':
				o2ReverArr[index] = 'A';
				break;
			case 'C':
				o2ReverArr[index] = 'G';
				break;
			case 'G':
				o2ReverArr[index] = 'C';
				break;
			default:
			}
			index++;
		}
		return o2ReverArr;
	}

	public static String reverse(char[] o2ReverArr) {
		StringBuffer sb = new StringBuffer();
		for (int j = o2ReverArr.length - 1; j >= 0; j--) {
			sb.append(o2ReverArr[j]);
		}
		return sb.toString();
	}
	
	public static String getPostfix(String path) {
		if (path == null || Common.EMPTY.equals(path.trim())) {
			return Common.EMPTY;
		}
		if (path.contains(Common.POINT)) {
			return path.substring(path.lastIndexOf(Common.POINT) + 1, path.length());
		}
		return Common.EMPTY;
	}
	
	public static double getPeptidesCoverage(String protein, String[] aaArr) {

		if (null == protein || protein.length() == 0 || null == aaArr || aaArr.length == 0) {
			return 0;
		}
		List<Integer> proteinIndex = new ArrayList<Integer>();
		for (String aa : aaArr) {
			if(null==aa) continue;
			if (protein.contains(aa)) {
				int aaIndex = protein.indexOf(aa);
				for (int i = aaIndex; i < aaIndex + aa.length(); i++) {
					if (!proteinIndex.contains(i)) {
						proteinIndex.add(i);
					}
				}
			}
		}

		return proteinIndex.size();
	}
	
	public static void main(String[] args){
		String protein="123496075308875433599643";
		String[] aaArr={"12349","2349","7530","08875","99643"};
		System.out.println(getPeptidesCoverage(protein,aaArr));
	}
}
