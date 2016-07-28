package com.fastqexchange;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import com.util.ProfileUtil;
import com.util.ReadExcel;
import com.util.ReadFile;
import com.util.WriteFile;

public class MergeExcel {

	private static final Logger log = Logger.getLogger(MergeExcel.class);

	/**
	 * 将Excel中的2个sheet进行合并。2016/07/27
	 * 跟EXCEL的VLOOKUP语句差不多
	 * 把EBOV_VP30_Flag_Bio1和EBOV_VP30_Flag整合成一张表表~，直接整合成一张表表就行啦
	 * 这两张表里的基因有区别，要一个合集
	 * 重复的就弄到一行，不重复的就都设置成0
	 * 有可能两组的protein group和Peptide_IDs不一样
	 * 前两列是group_Bio1和group_Bio2+3，第三列是group
	 * Peptide_IDs也这样
	 * @param args
	 * @throws Exception
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws ConfigurationException, Exception {
		log.info("MergeExcel start..." + new Date());
		String inputfiles = ProfileUtil.getStringProfile("MergeExcelFile_inputfile");
		String outputfile = ProfileUtil.getStringProfile("MergeExcelFile_outputfile");
		String key = ProfileUtil.getStringProfile("MergeExcelFile_key");
		String[] keyList=key.split(";");

		Map<String, String[]> map = new HashMap<String, String[]>();
		List<String> datalist = new ArrayList<String>();

		// 读取文件
		Map<String, List<Object[]>> inputdata_map = ReadExcel.readExcel(inputfiles);
		Map<String,String> titleMap=new HashMap<String,String>();
		TreeMap<String, Integer> titleTempMap = new TreeMap<String, Integer>();
		List<String> titleList=new ArrayList<String>();
		Map<String,String[]> dataMap=new HashMap<String,String[]>();
		List<String> titleOrderList=new ArrayList<String>();
		
		// 处理title
		for (String sheetName : inputdata_map.keySet()) {
			List<Object[]> dataRowList = inputdata_map.get(sheetName);
			if (null != dataRowList && dataRowList.size() > 0) {
				Object[] data1st = dataRowList.get(0);
				int i = 0;
				for (Object data : data1st) {
					String title = data.toString();
					if(title.endsWith("1")){
						titleOrderList.add(title.substring(0, title.length()-1));
					}
					if ("protein group".equals(title.toLowerCase()) || "peptide_ids".equals(title.toLowerCase())) {
//						titleList.add(title + "_" + sheetName);
						titleTempMap.put(title + "_" + sheetName, i);
						if (!titleTempMap.containsKey(title + "_collSet")) {
							titleTempMap.put(title + "_collSet", i);
						}
						if (!titleTempMap.containsKey(title + "_diffSet")) {
							titleTempMap.put(title + "_diffSet", i);
						}
					} else if (!titleTempMap.containsKey(title.toString())) {
						titleTempMap.put(title, i);
					}
					i++;
				}
			}
		}
		
		// title排序
		titleList.add("gene");
		titleList.add("major protein");
		for (String title : titleTempMap.keySet()) {
			if (title.startsWith("protein group")) {
				titleList.add(title);
			}
		}
		titleList.add("length");
		for (String titleOrder : titleOrderList) {
			for (String title : titleTempMap.keySet()) {
				if (title.startsWith(titleOrder)) {
					titleList.add(title);
				}
			}
		}
		for (String title : titleTempMap.keySet()) {
			if (title.toLowerCase().startsWith("peptide_ids")) {
				titleList.add(title);
			}
		}
		
		for (String sheetName : inputdata_map.keySet()) {
			List<Object[]> dataRowList = inputdata_map.get(sheetName);
			if (null != dataRowList && dataRowList.size() > 0) {
				Object[] data1st = dataRowList.get(0);
				int i = 0;
				for (Object data : data1st) {
					
				}
			}
		}
		
//		String[] inputfileArr = inputfiles.split(";");
//		String[] outtitleArr = new String[5 + 3 * (inputfileArr.length)];
//		outtitleArr[0] = "gene";
//		outtitleArr[1] = "major protein";
//		for (int k = 0; k < inputfileArr.length; k++) {
//			outtitleArr[2 + k] = "file" + (k + 1);
//		}
//
//		int fileindex = -1;
//
//		for (int k = 0; k < inputfileArr.length; k++) {
//			String inputfile = inputfileArr[k];
//			List<String> list = ReadFile.readFile("", new File(inputfile));
//			if (list.size() > 0) {
//				// 处理title
//				String title = list.get(0);
//				String[] titleArr = title.split("\t");
//
//				outtitleArr[2 + inputfileArr.length + 3 * k] = titleArr[5];
//				outtitleArr[2 + inputfileArr.length + 3 * k + 1] = titleArr[6];
//				outtitleArr[2 + inputfileArr.length + 3 * k + 2] = titleArr[7];
//
//				for (int i = 1; i < list.size(); i++) {
//					String dataStr = list.get(i);
//					String[] dataArr = dataStr.split("\t");
//					String[] outdataArr = new String[5 + 3 * (inputfileArr.length)];
//					for (int l = 2; l < 2 + inputfileArr.length; l++) {
//						outdataArr[l] = "0";
//					}
//					for (int l = 2 + inputfileArr.length; l < outdataArr.length; l++) {
//						outdataArr[l] = "N/A";
//					}
//
//					if (map.containsKey(dataArr[1])) {
//						if (k == fileindex) {
//							System.out.println("!!k=" + k + ":" + dataArr[1]);
//						}
//						outdataArr = map.get(dataArr[1]);
//					} else {
//						outdataArr[0] = dataArr[0];
//						outdataArr[1] = dataArr[1];
//					}
//
//					outdataArr[2 + k] = "1";
//					outdataArr[2 + inputfileArr.length + 3 * k] = dataArr[5];
//					outdataArr[2 + inputfileArr.length + 3 * k + 1] = dataArr[6];
//					outdataArr[2 + inputfileArr.length + 3 * k + 2] = dataArr[7];
//
//					map.put(dataArr[1], outdataArr);
//				}
//
//			}
//			fileindex = k;
//		}

		StringBuffer dataSb = new StringBuffer();
//		for (int j = 0; j < 5; j++) {
//			dataSb.append(outtitleArr[j]).append("\t");
//		}
//		for (int j = 0; j < inputfileArr.length; j++) {
//			dataSb.append(outtitleArr[5 + j]).append("\t");
//			dataSb.append(outtitleArr[5 + j + 3]).append("\t");
//			dataSb.append(outtitleArr[5 + j + 6]).append("\t");
//		}
//		datalist.add(dataSb.toString());
//
//		for (String s : map.keySet()) {
//			String[] outdataArr = map.get(s);
//			dataSb = new StringBuffer();
//			for (int j = 0; j < 5; j++) {
//				dataSb.append(outdataArr[j]).append("\t");
//			}
//			for (int j = 0; j < inputfileArr.length; j++) {
//				dataSb.append(outdataArr[5 + j]).append("\t");
//				dataSb.append(outdataArr[5 + j + 3]).append("\t");
//				dataSb.append(outdataArr[5 + j + 6]).append("\t");
//			}
//			datalist.add(dataSb.toString());
//		}
		
		// 输出title
		for(String title:titleList){
			dataSb.append(title).append("\t");
		}
		datalist.add(dataSb.toString());

		WriteFile.writeFile(new FileWriter(outputfile), datalist);

		log.info("output count:" + Integer.toString(datalist.size() - 1));
		log.info("MergeExcel end..." + new Date());
		System.exit(0);
	}

}
