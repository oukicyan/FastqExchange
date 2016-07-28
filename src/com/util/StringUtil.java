package com.util;

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
}
