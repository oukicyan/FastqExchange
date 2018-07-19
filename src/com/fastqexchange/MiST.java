package com.fastqexchange;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import com.util.ProfileUtil;
import com.util.ReadFile;
import com.util.WriteFile;

public class MiST {
	private static final Logger log = Logger.getLogger(MiST.class);
	public static void main(String[] args) throws ConfigurationException, Exception {
		log.info("MiST start..." + new Date());
		String inputfiles = ProfileUtil.getStringProfile("inputfile_MiST");
		String outputfile_inter = ProfileUtil.getStringProfile("outputfile_MiST");
		String BaitNames = ProfileUtil.getStringProfile("MiST_BaitName");
		String T_Cs = ProfileUtil.getStringProfile("MiST_T_C");
		String Intensity_SC = ProfileUtil.getStringProfile("MiST_I_S");
		
		List<String> interList = new ArrayList<String>();
		
		// ¶ÁÈ¡ÎÄ¼þ
		String[] inputfileArr=inputfiles.split(";");
		String[] T_CArr=T_Cs.split(";");
		String[] BaitNameArr=BaitNames.split(";");
		if(inputfileArr.length!=T_CArr.length || inputfileArr.length!=BaitNameArr.length){
			log.error("Number of files and number of T/C and BaitName are inconsistent!files number is " + inputfileArr.length + ",T/C number is " + T_CArr.length+ ",BaitName number is " + BaitNameArr.length);
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
				List<Integer> scIndexList=new ArrayList<Integer>();
				int geneIndex=-1;
				int majorProteinIndex=-1;
				int lengthIndex=-1;
				int PeptideID_NumberIndex=-1;
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
					} else if (t.trim().toLowerCase().startsWith("major protein")) {
						majorProteinIndex = i;
					}else if ("length".equals(t.trim().toLowerCase())) {
						lengthIndex = i;
					}else if (t.trim().toLowerCase().startsWith("peptideid_number")) {
						PeptideID_NumberIndex = i;
					}else if(t.startsWith("SC_")){
						String sample = "";
						if(t.startsWith("SC_")){
							sample = t.replaceFirst("SC_", "");
						}
						scIndexList.add(i);
						if (!titleList.contains(sample)) {
							titleList.add(sample);
						}
					}
				}
				intensityIndexList.addAll(scIndexList);
				
				String interKey="";
				String interData="";
				TreeMap<String,List<String>> dataMap=new TreeMap<String,List<String>>();
				List<String> tempList=null;
				for(int i=1;i<list.size();i++){
					String dataStr=list.get(i);
					String[] dataArr=dataStr.split("\t");
					
					for(int j=0;j<intensityIndexList.size();j++){
						int intensityIndex=intensityIndexList.get(j);
						interData = dataArr[majorProteinIndex] + "\t" + dataArr[intensityIndex]+ "\t" + dataArr[lengthIndex]+ "\t" + dataArr[PeptideID_NumberIndex];
						interKey=titleList.get(j);
						tempList=dataMap.get(interKey);
						if(null==tempList){
							tempList=new ArrayList<String>();
						}
						tempList.add(interData);
						dataMap.put(interKey, tempList);
					}
				}
				
				for (String s : dataMap.keySet()) {
					tempList = dataMap.get(s);
					for (String d : tempList) {
						interList.add(s + "\t" + BaitNameArr[k] + "\t" + d);
					}
				}
				
			}
		}
		
		WriteFile.writeFile(new FileWriter(outputfile_inter), interList);
		
		log.info("output count:" + Integer.toString(interList.size() - 1));
		log.info("MiST end..." + new Date());
		System.exit(0);
	}

}
