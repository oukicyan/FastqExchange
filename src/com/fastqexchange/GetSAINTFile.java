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
import com.util.ReadFile;
import com.util.WriteFile;

public class GetSAINTFile {

	private static final Logger log = Logger.getLogger(GetSAINTFile.class);

	/**
	 * @param args
	 * @throws Exception
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws ConfigurationException,
			Exception {
		log.info("GetSAINTFile start..." + new Date());
		String inputfiles = ProfileUtil.getStringProfile("inputfile_GetSAINTFile");
		String outputfile_bait = ProfileUtil.getStringProfile("outputfile_GetSAINTFile_bait");
		String outputfile_inter = ProfileUtil.getStringProfile("outputfile_GetSAINTFile_inter");
		String outputfile_prey = ProfileUtil.getStringProfile("outputfile_GetSAINTFile_prey");
		String BaitName = ProfileUtil.getStringProfile("GetSAINTFile_BaitName");
		String T_Cs = ProfileUtil.getStringProfile("GetSAINTFile_T_C");
		String Intensity_SC = ProfileUtil.getStringProfile("GetSAINTFile_I_S");
		
		List<String> baitList = new ArrayList<String>();
		List<String> interList = new ArrayList<String>();
		List<String> preyList = new ArrayList<String>();
		
		// ¶ÁÈ¡ÎÄ¼þ
		String[] inputfileArr=inputfiles.split(";");
		String[] T_CArr=T_Cs.split(";");
		if(inputfileArr.length!=T_CArr.length){
			log.error("Number of files and number of T/C are inconsistent!files number is " + inputfileArr.length + ",T/C number is " + T_CArr.length);
			System.exit(0);
		}
		for (int k = 0; k < inputfileArr.length; k++) {
			String inputfile=inputfileArr[k];
			List<String> list = ReadFile.readFile("", new File(inputfile));
			if(list.size()>0){
				String title=list.get(0);
				String[] titleArr=title.split("\t");
				List<String> titleList=new ArrayList<String>();
				List<Integer> intensityIndexList=new ArrayList<Integer>();
				int geneIndex=-1;
				int majorProteinIndex=-1;
				for(int i=0;i<titleArr.length;i++){
					String t=titleArr[i];
					
					if ((null == Intensity_SC || "".equals(Intensity_SC)) && (t.startsWith("Intensity_"))
							|| (null != Intensity_SC && "SC".equals(Intensity_SC) && t.startsWith("SC_"))) {
						String sample = "";
						if(t.startsWith("Intensity_")){
							sample = t.replaceFirst("Intensity_", "");
						}
						if(t.startsWith("SC_")){
							sample = t.replaceFirst("SC_", "");
						}
						intensityIndexList.add(i);
						if (!titleList.contains(sample)) {
							titleList.add(sample);
						}
					}else if ("gene".equals(t.trim().toLowerCase())) {
						geneIndex = i;
					} else if ("major protein".equals(t.trim().toLowerCase())) {
						majorProteinIndex = i;
					}
				}
				
				String preyKey="";
				String interKey="";
				String interData="";
				TreeMap<String,List<String>> dataMap=new TreeMap<String,List<String>>();
				List<String> tempList=null;
				for(int i=1;i<list.size();i++){
					String dataStr=list.get(i);
					String[] dataArr=dataStr.split("\t");
					
					for(int j=0;j<intensityIndexList.size();j++){
						int intensityIndex=intensityIndexList.get(j);
						interData = dataArr[majorProteinIndex] + "\t" + dataArr[intensityIndex];
						interKey=titleList.get(j);
						tempList=dataMap.get(interKey);
						if(null==tempList){
							tempList=new ArrayList<String>();
						}
						tempList.add(interData);
						dataMap.put(interKey, tempList);
					}
					
					preyKey = dataArr[majorProteinIndex] + "\t" + dataArr[geneIndex];
					if (!preyList.contains(preyKey)) {
						preyList.add(preyKey);
					}
				}
				
				for (String s : dataMap.keySet()) {
					tempList = dataMap.get(s);
					for (String d : tempList) {
						interList.add(s + "\t" + BaitName + "\t" + d);
					}
				}
				
				for (String s : titleList) {
					baitList.add(s + "\t" + BaitName + "\t" + T_CArr[k]);
				}
			}
		}
		
		WriteFile.writeFile(new FileWriter(outputfile_bait), baitList);
		WriteFile.writeFile(new FileWriter(outputfile_inter), interList);
		WriteFile.writeFile(new FileWriter(outputfile_prey), preyList);
		
		log.info("output count:" + Integer.toString(baitList.size() - 1));
		log.info("GetSAINTFile end..." + new Date());
		System.exit(0);
	}
	
	

}
