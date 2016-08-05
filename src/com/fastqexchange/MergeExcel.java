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
	 */
	public static void main(String[] args) throws ConfigurationException, Exception {
		log.info("MergeExcel start..." + new Date());
		String inputfiles = ProfileUtil.getStringProfile("MergeExcelFile_inputfile");
		String outputfile = ProfileUtil.getStringProfile("MergeExcelFile_outputfile");

		List<String> datalist = new ArrayList<String>();

		// 读取文件
		Map<String, List<Object[]>> inputdata_map = null;
		if (inputfiles.contains("xls") || inputfiles.contains("xlsx")) {
			inputdata_map = ReadExcel.readExcel(inputfiles);
		} else {
			inputdata_map = new HashMap<String, List<Object[]>>();
			String[] inputfileArr = inputfiles.split(";");
			for (int k = 0; k < inputfileArr.length; k++) {
				String inputfile = inputfileArr[k];
				List<String> list = ReadFile.readFile("", new File(inputfile));
				List<Object[]> templist = new ArrayList<Object[]>();
				for (String s : list) {
					templist.add(s.split("\t"));
				}
				inputdata_map.put(inputfile, templist);
			}
		}
		
		TreeMap<String, Integer> titleTempMap = new TreeMap<String, Integer>();
		List<String> titleList=new ArrayList<String>();
		Map<String,String[]> dataMap=new HashMap<String,String[]>();
		// 输出文件的title
		List<String> titleOrderList=new ArrayList<String>();
		// 各Sheet的title顺序
		Map<String,Map<String,Integer>> titleOrderMap=new HashMap<String,Map<String,Integer>>();
		
		// 处理title
		for (String sheetName : inputdata_map.keySet()) {
			List<Object[]> dataRowList = inputdata_map.get(sheetName);
			if (null != dataRowList && dataRowList.size() > 0) {
				Object[] data1st = dataRowList.get(0);
				int i = 0;
				Map<String,Integer> titleOrderTempMap=new HashMap<String,Integer>();
				for (Object data : data1st) {
					String title = data.toString();
					if(title.endsWith("1")){
						titleOrderList.add(title.substring(0, title.length()-1));
					}
					if ("protein group".equals(title.toLowerCase()) || "peptide_ids".equals(title.toLowerCase())) {
						titleTempMap.put(title + "_" + sheetName, i);
						titleOrderTempMap.put(title + "_" + sheetName, i);
						if (!titleTempMap.containsKey(title + "_collSet")) {
							titleTempMap.put(title + "_collSet", i);
						}
						if (!titleTempMap.containsKey(title + "_diffSet")) {
							titleTempMap.put(title + "_diffSet", i);
						}
					} else if (!titleTempMap.containsKey(title.toString())) {
						titleTempMap.put(title, i);
						titleOrderTempMap.put(title, i);
					}else{
						titleOrderTempMap.put(title, i);
					}
					i++;
				}
				titleOrderMap.put(sheetName, titleOrderTempMap);
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
			Map<String,Integer> titleOrderTempMap=titleOrderMap.get(sheetName);
			if (null != dataRowList && dataRowList.size() > 0) {
				for (int i = 1; i < dataRowList.size(); i++) {
					Object[] data = dataRowList.get(i);
					String datakey=data[0].toString();
					if(null==datakey||"".equals(datakey)){
						continue;
					}
					String[] outputData=dataMap.get(datakey);
					if(null==outputData){
						outputData = new String[titleList.size()];
					}
					for (int j = 0; j < titleList.size(); j++) {
						String title = titleList.get(j);
						if (titleOrderTempMap.containsKey(title)) {
							int index = titleOrderTempMap.get(title);
							outputData[j] = data[index].toString();
						} else {
							if (null == outputData[j] && !"0".equals(outputData[j])) {
								outputData[j] = "0";
							}
						}
					}
					dataMap.put(datakey, outputData);
				}
			}
		}
		
		StringBuffer dataSb = new StringBuffer();
		// 输出title
		for(String title:titleList){
			dataSb.append(title).append("\t");
		}
		datalist.add(dataSb.toString());
		for(String data:dataMap.keySet()){
			dataSb = new StringBuffer();
			for(int i=0;i<dataMap.get(data).length;i++){
				String title=titleList.get(i);
				if(title.endsWith("collSet")){
					List<String> list=new ArrayList<String>();
					list.add(dataMap.get(data)[i-2]);
					list.add(dataMap.get(data)[i-1]);
					List<String> result=getCollDiffSet(list);
					
					dataSb.append(result.get(0)).append("\t").append(result.get(1)).append("\t");
					i++;
				}else{
					String s=dataMap.get(data)[i];
					dataSb.append(s).append("\t");
				}
			}
			datalist.add(dataSb.toString());
		}

		WriteFile.writeFile(new FileWriter(outputfile), datalist);

		log.info("output count:" + Integer.toString(datalist.size() - 1));
		log.info("MergeExcel end..." + new Date());
		System.exit(0);
	}
	
	static List<String> getCollDiffSet(List<String> list) {
		List<String> result = new ArrayList<String>();
		List<String> collSet = new ArrayList<String>();
		List<String> diffSet = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			String[] dataArr = list.get(i).split(";");
			for (String s : dataArr) {
				if (i != 0) {
					if (collSet.contains(s)) {
						diffSet.add(s);
					}
				}
				if (!collSet.contains(s)) {
					collSet.add(s);
				}
			}
		}
		StringBuffer collSb = new StringBuffer();
		for (String s : collSet) {
			collSb.append(s).append(";");
		}
		StringBuffer diffSb = new StringBuffer();
		if (diffSet.size() > 0) {
			//交集or差集
			collSet.removeAll(diffSet);
			diffSet=collSet;
			for (String s : diffSet) {
				diffSb.append(s).append(";");
			}
		} else {
			diffSb.append("0");
		}
		result.add(collSb.toString());
		result.add(diffSb.toString());
		return result;
	}

}
