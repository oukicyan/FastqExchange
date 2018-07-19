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

public class GetCRAPomeFile {

	private static final Logger log = Logger.getLogger(GetCRAPomeFile.class);

	/**
	 * @param args
	 * @throws Exception
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws ConfigurationException,
			Exception {
		log.info("GetCRAPomeFile start..." + new Date());
		String inputfiles = ProfileUtil.getStringProfile("inputfile_GetCRAPomeFile");
		String outputfile = ProfileUtil.getStringProfile("outputfile_GetCRAPomeFile");
		String BaitNames = ProfileUtil.getStringProfile("GetCRAPomeFile_BaitName");
		String T_Cs = ProfileUtil.getStringProfile("GetCRAPomeFile_T_C");
		String Intensity_SC = ProfileUtil.getStringProfile("GetCRAPomeFile_I_S");
		String APNames=ProfileUtil.getStringProfile("GetCRAPomeFile_APName");
		
//		List<String> baitList = new ArrayList<String>();
		List<String> interList = new ArrayList<String>();
//		List<String> preyList = new ArrayList<String>();
		
		// ¶ÁÈ¡ÎÄ¼þ
		String[] inputfileArr=inputfiles.split(";");
		String[] T_CArr=T_Cs.split(";");
		String[] BaitNameArr=BaitNames.split(";");
		String[] APNameArr=APNames.split(";");
		if (inputfileArr.length != T_CArr.length
				|| inputfileArr.length != BaitNameArr.length
				|| (null != APNameArr && APNameArr.length != inputfileArr.length)) {
			log.error("Number of files and number of T/C and BaitName are inconsistent!files number is "
					+ inputfileArr.length
					+ ",T/C number is "
					+ T_CArr.length
					+ ",BaitName number is " + BaitNameArr.length + ",APName number is " + APNameArr.length);
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
					} else if (t.trim().toLowerCase().startsWith("major protein")) {
						majorProteinIndex = i;
					}
				}
				
//				String preyKey="";
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
					
//					preyKey = dataArr[majorProteinIndex] + "\t" + dataArr[geneIndex];
//					if (!preyList.contains(preyKey)) {
//						preyList.add(preyKey);
//					}
				}
				
				for (String s : dataMap.keySet()) {
					tempList = dataMap.get(s);
					for (String d : tempList) {
						if(null!=APNameArr&&APNameArr.length>0){
							interList.add(APNameArr[k] + "\t" + BaitNameArr[k] + "\t" + d);
						}else{
							interList.add(s + "\t" + BaitNameArr[k] + "\t" + d);
						}
					}
				}
				
//				for (String s : titleList) {
//					baitList.add(s + "\t" + BaitNameArr[k] + "\t" + T_CArr[k]);
//				}
			}
		}
		
		List<String> outList = new ArrayList<String>();
		String outString="";
		String[] interArr=null;
		for(String s : interList){
			interArr=s.split("\t");
			outString=interArr[1]+","+interArr[0]+","+interArr[2]+","+interArr[3];
			outList.add(outString);
		}
		
		WriteFile.writeFile(new FileWriter(outputfile), outList);
		
		log.info("output count:" + Integer.toString(outList.size() - 1));
		log.info("GetCRAPomeFile end..." + new Date());
		System.exit(0);
	}
	
	

}
