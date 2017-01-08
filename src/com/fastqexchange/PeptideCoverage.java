package com.fastqexchange;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import com.util.ProfileUtil;
import com.util.ReadFile;
import com.util.StringUtil;
import com.util.WriteFile;

public class PeptideCoverage {

	private static final Logger log = Logger.getLogger(PeptideCoverage.class);

	/**
	 * @param args
	 * @throws Exception
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws ConfigurationException,
			Exception {
		log.info("PeptideCoverage start..." + new Date());
		String inputfile_mouse = ProfileUtil.getStringProfile("inputfile_PeptideCoverage_mouse");
		String inputfile = ProfileUtil.getStringProfile("inputfile_PeptideCoverage");
		String inputfile_Peptides = ProfileUtil.getStringProfile("inputfile_PeptideCoverage_Peptides");
		String outputfile = ProfileUtil.getStringProfile("outputfile_PeptideCoverage");
		List<String> list_mouse = ReadFile.readFile("", new File(inputfile_mouse));
		List<String> list_inputfile = ReadFile.readFile("", new File(inputfile));
		List<String> list_Peptides = ReadFile.readFile("", new File(inputfile_Peptides));
		List<String> outList = new ArrayList<String>();
		
		Map<String,String> PeptidesMap=new HashMap<String,String>();
		list_Peptides.remove(0);
		String[] PeptidesArr;
		for(String Peptides:list_Peptides){
			PeptidesArr=Peptides.split("\t");
			PeptidesMap.put(PeptidesArr[0], PeptidesArr[1]);
		}
		
		Map<String, String> map_mouse = new HashMap<String, String>();
		String[] mouseArr;
		StringBuffer mouseSb=new StringBuffer();
		String mouseKey="";
		for (String mouse : list_mouse) {
			mouseArr = mouse.split("\\|");
			if(mouseArr.length>1){
				if(null!=mouseKey&&!"".equals(mouseKey)){
					map_mouse.put(mouseKey, mouseSb.toString());
					mouseSb=new StringBuffer();
				}
				mouseKey=mouseArr[1];
			}else{
				mouseSb.append(mouse);
			}
		}
		map_mouse.put(mouseKey, mouseSb.toString());

		String title_inputfile = list_inputfile.get(0);
		String[] title_inputfileArr = title_inputfile.split("\t");
		int peptideIDIndex=-1;
		int majorProteinIndex=-1;
		int lengthIndex=-1;
		for (int i = 0; i < title_inputfileArr.length; i++) {
			if (title_inputfileArr[i].equals("PeptideID")) {
				peptideIDIndex = i;
			}else if (title_inputfileArr[i].equals("Major Protein accession")) {
				majorProteinIndex = i;
			}else if (title_inputfileArr[i].equals("length")) {
				lengthIndex = i;
			}
		}
		
		if(peptideIDIndex==-1||majorProteinIndex==-1||lengthIndex==-1){
			log.error("inputfile title error!");
			System.exit(0);
		}
		
		outList.add(title_inputfile+"Peptides Length\tPeptides Coverage");
		list_inputfile.remove(0);

		System.out.println("input file data count:" + list_inputfile.size());
		StringBuffer outputText = new StringBuffer();
		String PeptideID;
		String[] inputdataArr;
		String[] PeptideIDArr;
		int index=1;
		String mouseData;
		double PeptidesCount;
		
		for (String data : list_inputfile) {
			inputdataArr=data.split("\t");
			PeptideID=inputdataArr[peptideIDIndex];
			PeptideIDArr=PeptideID.split(";");
			PeptidesArr=new String[PeptideIDArr.length];
			for(int i=0;i<PeptideIDArr.length;i++){
				if(PeptidesMap.containsKey(PeptideIDArr[i])){
				PeptidesArr[i]=PeptidesMap.get(PeptideIDArr[i]);
				}else{
					log.error("PeptideID not in Peptides file!PeptideID is " + PeptideIDArr[i] + "(" + index + ")");
				}
			}

			mouseData = map_mouse.get(inputdataArr[majorProteinIndex]);
			if (null == mouseData) {
				log.error("MajorProtein not in mouse file!MajorProtein is " + inputdataArr[majorProteinIndex] + "(" + index + ")");
				continue;
			}
			outputText = new StringBuffer();
			PeptidesCount=StringUtil.getPeptidesCoverage(mouseData, PeptidesArr);
			outputText.append(data).append("\t").append(PeptidesCount)
					.append("\t").append(PeptidesCount/Double.parseDouble(inputdataArr[lengthIndex]));
			outList.add(outputText.toString());

			WriteFile.writeFile(new FileWriter(outputfile), outList);
			log.info("output count:" + Integer.toString(outList.size() - 1));
		}
		log.info("PeptideCoverage end..." + new Date());
		System.exit(0);
	}
}
