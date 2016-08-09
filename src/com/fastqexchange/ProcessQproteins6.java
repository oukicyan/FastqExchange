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

public class ProcessQproteins6 {

	private static final Logger log = Logger.getLogger(ProcessQproteins6.class);

	/**
	 * @param args
	 * @throws Exception
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws ConfigurationException, Exception {
		log.info("processQproteins6 start..." + new Date());
		String inputfile_mouse = ProfileUtil.getStringProfile("inputfile_processQproteins6_mouse");
		String inputfile_QProteins = ProfileUtil.getStringProfile("inputfile_processQproteins6_QProteins1");
		String inputfile_QProteins_SC = ProfileUtil.getStringProfile("inputfile_processQproteins6_QProteins2");
		String inputfile_QProteins_ionscore = ProfileUtil.getStringProfile("inputfile_processQproteins6_QProteins3");
		String outputfile = ProfileUtil.getStringProfile("outputfile_processQproteins6");
		String Sample1 = ProfileUtil.getStringProfile("Sample1");
		String Sample2 = ProfileUtil.getStringProfile("Sample2");
		String Sample3 = ProfileUtil.getStringProfile("Sample3");
		String Sample4 = ProfileUtil.getStringProfile("Sample4");
		String Sample5 = ProfileUtil.getStringProfile("Sample5");
		int accNoFieldNo = -1;
		List<String> list_mouse = ReadFile.readFile("", new File(inputfile_mouse));
		List<String> list_QProteins = ReadFile.readFile("", new File(inputfile_QProteins));
		List<String> list_QProteins_SC = ReadFile.readFile("", new File(inputfile_QProteins_SC));
		List<String> list_QProteins_ionscore = ReadFile.readFile("", new File(inputfile_QProteins_ionscore));
		List<String> outList = new ArrayList<String>();

		if (list_QProteins.size() != list_QProteins_SC.size() || list_QProteins.size() != list_QProteins_ionscore.size()) {
			log.error("QProteins,SC,ionscore files records count not equal! QProteins:" + list_QProteins.size()
					+ ",SC:" + list_QProteins_SC.size() + ",ionscore:" + list_QProteins_ionscore.size());
		} else {

				String title_SC = list_QProteins_SC.get(0);
				String[] title_SCArr = title_SC.split("\t");
				StringBuffer titleSb = new StringBuffer();
				List<Integer> scIndex = new ArrayList<Integer>();
				String titleTemp = "";
				for (int i = 0; i < title_SCArr.length; i++) {
					if (title_SCArr[i].startsWith("Intensity_")) {
						titleTemp = title_SCArr[i].replaceAll("Intensity_", "SC_");
						titleSb.append(titleTemp).append("\t");
						scIndex.add(i);
					}
				}

				String title_ionscore = list_QProteins_ionscore.get(0);
				String[] title_ionscoreArr = title_ionscore.split("\t");
				StringBuffer titleionscoreSb = new StringBuffer();
				List<Integer> ionscoreIndex = new ArrayList<Integer>();
				for (int i = 0; i < title_ionscoreArr.length; i++) {
					if (title_ionscoreArr[i].startsWith("Intensity_")) {
						titleTemp = title_ionscoreArr[i].replaceAll( "Intensity_", "ionscore_");
						titleionscoreSb.append(titleTemp).append("\t");
						ionscoreIndex.add(i);
					}
				}
				
				String title_QProteins = list_QProteins.get(0);
				String[] title_QProteinsArr = title_QProteins.split("\t");
				StringBuffer titleQProteinsSb = new StringBuffer();
				List<Integer> QProteinsIndex = new ArrayList<Integer>();
				int Peptide_IDsIndex=-1;
				for (int i = 0; i < title_ionscoreArr.length; i++) {
					if (!title_QProteinsArr[i].contains("/")&&!"AccNo".equals(title_QProteinsArr[i])) {
//						titleTemp = title_ionscoreArr[i].replaceAll( "Intensity_", "ionscore_");
						if("Peptide_IDs".equals(title_QProteinsArr[i])){
							Peptide_IDsIndex=i;
						}else{
							titleQProteinsSb.append(title_QProteinsArr[i]).append("\t");
							QProteinsIndex.add(i);
						}
					}
				}

				String title="gene\tmajor protein\tprotein group\tlength\t" + titleQProteinsSb.toString() + titleSb.toString() + titleionscoreSb.toString()+"Peptide_IDs";
				if (null != Sample1 && !"".equals(Sample1)) {
					title=title.replaceAll("Sample1", Sample1);
				}
				if (null != Sample2 && !"".equals(Sample2)) {
					title=title.replaceAll("Sample2", Sample2);
				}
				if (null != Sample3 && !"".equals(Sample3)) {
					title=title.replaceAll("Sample3", Sample3);
				}
				if (null != Sample4 && !"".equals(Sample4)) {
					title=title.replaceAll("Sample4", Sample4);
				}
				if (null != Sample5 && !"".equals(Sample5)) {
					title=title.replaceAll("Sample5", Sample5);
				}
				
				outList.add(title);
				String[] titleArr = list_QProteins.get(0).split("\t");
				for (int i = 0; i < titleArr.length; i++) {
					if ("accno".equals(titleArr[i].toLowerCase()) || "protein".equals(titleArr[i].toLowerCase())) {
						accNoFieldNo = i;
						break;
					}
				}
				list_QProteins.remove(0);
			Map<String, String[]> map_mouse = new HashMap<String, String[]>();
			Map<String, String> map_mouse_pe = new HashMap<String, String>();
			String[] mouseArr;
			for (String mouse : list_mouse) {
				mouseArr = mouse.split("\t");
				if (mouseArr.length != 4) {
					log.error("mouse file's field number is wrong!" + list_mouse);
				} else {
					map_mouse.put(mouseArr[0], mouseArr);
					map_mouse_pe.put(mouseArr[0], mouseArr[3]);
				}
			}

			System.out.println("input file data count:" + list_QProteins.size());
			int crapCount = 0;
			String sp = "	";
			String[] qProteinsArr;
			String accNo = "";
			String[] accNoArr;
			String major_protein = "";
			String protein_group = "";
			int index = 0;
			StringBuffer outputText=new StringBuffer();
			for (String qProteins : list_QProteins) {
				index++;
				qProteinsArr = qProteins.split("\t");
				try {
					accNo = qProteinsArr[accNoFieldNo];
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("accNo=" + accNo + ",qProteinsArr's length=" + qProteinsArr.length);
				}
				
				accNo = accNo.replaceAll("sp\\|", "");
//				accNo = accNo.replaceAll("\\|;", ";");
//				accNo = accNo.replaceAll("\\|", ";");

				major_protein="";
				protein_group="";
				accNoArr = accNo.split(";");
				if (accNoArr.length == 1) {
					major_protein = accNoArr[0].split("\\|")[0];
					protein_group = accNoArr[0].split("\\|")[0]+";";
				} else {
					TreeMap<Integer, String> tempPeMap = new TreeMap<Integer, String>();
					String tempPeS="";
					int tempPe = 9999;
					for (int i = 0; i < accNoArr.length; i++) {
						String s = accNoArr[i];
						
						if ("".equals(major_protein) && s.startsWith("crap")) {
							major_protein = "crap";
						} else {
							if ("".equals(major_protein) || major_protein.startsWith("crap")) {
								major_protein = s.split("\\|")[0];
							}
							if (true) {
								protein_group += s.split("\\|")[0] + ";";
							}
						}
						
						if (map_mouse_pe.containsKey(s)) {
							tempPeS = map_mouse_pe.get(s);
							try {
								tempPe = Integer.parseInt(tempPeS);

								if (!tempPeMap.containsKey(tempPe)) {
									tempPeMap.put(tempPe, s);
								}

							} catch (NumberFormatException e) {
								tempPe = 9999;
								log.error("PE change error! PE=" + tempPeS + "(" + s + ")");
							}
							
						} else {
							log.error("Couldn't found PE error! (" + s + ")");
						}
					}
					for (Integer i : tempPeMap.keySet()) {
						major_protein = tempPeMap.get(i);
						break;
					}
				}

				if (major_protein.startsWith("crap")) {
					crapCount++;
					continue;
				}

				mouseArr = map_mouse.get(major_protein);
				if (null == mouseArr) {
					log.error("accNo not in mouse file!accNo is " + accNo + "(" + (index+1) + ")");
					continue;
				}
				outputText=new StringBuffer();
				outputText.append((null==mouseArr[1]||"".equals(mouseArr[1].trim()))?major_protein:mouseArr[1]).append(sp).append(major_protein).append(sp).append(protein_group).append(sp).append(mouseArr[2]);
//				String qProteins=list_QProteins.get(index);
//				String[] scArr=sc.split("\t");
				for(int i=0;i<QProteinsIndex.size();i++){
					outputText.append(sp).append(qProteinsArr[QProteinsIndex.get(i)]);
				}
				String sc=list_QProteins_SC.get(index);
				String[] scArr=sc.split("\t");
				for(int i=0;i<scIndex.size();i++){
					outputText.append(sp).append(scArr[scIndex.get(i)]);
				}
				String ionscore=list_QProteins_ionscore.get(index);
				String[] ionscoreArr=ionscore.split("\t");
				for(int i=0;i<ionscoreIndex.size();i++){
					outputText.append(sp).append(ionscoreArr[ionscoreIndex.get(i)]);
				}
				outputText.append("\t").append(qProteinsArr[Peptide_IDsIndex]);
				outList.add(outputText.toString());
			}

			WriteFile.writeFile(new FileWriter(outputfile), outList);
			log.info("output count:" + Integer.toString(outList.size() - 1) + ",crap count:" + crapCount);
		}
		log.info("processQproteins6 end..." + new Date());
		System.exit(0);
	}
	
	

}
