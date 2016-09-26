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

public class Panda {

	private static final Logger log = Logger.getLogger(Panda.class);

	/**
	 * @param args
	 * @throws Exception
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws ConfigurationException, Exception {
		log.info("Panda start..." + new Date());
		String inputfile_mouse = ProfileUtil.getStringProfile("inputfile_processQproteins6_mouse");
		String inputfile_QProteins = ProfileUtil.getStringProfile("inputfile_processQproteins6_QProteins1");
		String outputfile = ProfileUtil.getStringProfile("outputfile_processQproteins6");
		String Sample1 = ProfileUtil.getStringProfile("Sample1");
		String Sample2 = ProfileUtil.getStringProfile("Sample2");
		String Sample3 = ProfileUtil.getStringProfile("Sample3");
		String Sample4 = ProfileUtil.getStringProfile("Sample4");
		String Sample5 = ProfileUtil.getStringProfile("Sample5");
		int accNoFieldNo = -1;
		int uniqueFieldNo = -1;
		List<String> list_mouse = ReadFile.readFile("", new File(inputfile_mouse));
		List<String> list_QProteins = ReadFile.readFile("", new File(inputfile_QProteins));
		List<String> outList = new ArrayList<String>();

		String title_QProteins = list_QProteins.get(0);
		String[] title_QProteinsArr = title_QProteins.split("\t");
		StringBuffer titleQProteinsSb = new StringBuffer();
		List<Integer> QProteinsIndex = new ArrayList<Integer>();
		for (int i = 0; i < title_QProteinsArr.length; i++) {
			if("Protein Group accession".equals(title_QProteinsArr[i])){
				accNoFieldNo = i;
			}else if (!"Protein Group ID".equals(title_QProteinsArr[i])&&!"Protein Group accession".equals(title_QProteinsArr[i])&&!"Major Protein accession".equals(title_QProteinsArr[i])) {
				titleQProteinsSb.append(title_QProteinsArr[i]).append("\t");
				QProteinsIndex.add(i);
				if (title_QProteinsArr[i].toLowerCase().startsWith("unique")) {
					uniqueFieldNo = i;
				}
			}
		}

		String title = "Protein Group ID\tgene\tMajor Protein accession\tProtein Group accession\tlength\t"+titleQProteinsSb.toString()+"\tPeptideID_Number";
		
		if (null != Sample1 && !"".equals(Sample1)) {
			title = title.replaceAll("Sample1", Sample1).replaceAll("sample1", Sample1);
		}
		if (null != Sample2 && !"".equals(Sample2)) {
			title = title.replaceAll("Sample2", Sample2).replaceAll("sample2", Sample2);
		}
		if (null != Sample3 && !"".equals(Sample3)) {
			title = title.replaceAll("Sample3", Sample3).replaceAll("sample3", Sample3);
		}
		if (null != Sample4 && !"".equals(Sample4)) {
			title = title.replaceAll("Sample4", Sample4).replaceAll("sample4", Sample4);
		}
		if (null != Sample5 && !"".equals(Sample5)) {
			title = title.replaceAll("Sample5", Sample5).replaceAll("sample5", Sample5);
		}
				
		outList.add(title);
		list_QProteins.remove(0);
		
		Map<String, String[]> map_mouse = new HashMap<String, String[]>();
		Map<String, String> map_mouse_pe = new HashMap<String, String>();
		String[] mouseArr;
		for (String mouse : list_mouse) {
			mouseArr = mouse.split("\t");
			if (mouseArr.length != 4) {
				log.error("mouse file's field number is wrong!" + mouse);
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
		StringBuffer outputText = new StringBuffer();
		for (String qProteins : list_QProteins) {
			index++;
			qProteinsArr = qProteins.split("\t");
			try {
				accNo = qProteinsArr[accNoFieldNo];
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Major Protein accession=" + accNo + ",qProteinsArr's length=" + qProteinsArr.length);
			}

			accNo = accNo.replaceAll("sp\\|", "");

			major_protein = "";
			protein_group = "";
			accNoArr = accNo.split(";");
			if (accNoArr.length == 1) {
				major_protein = accNoArr[0].split("\\|")[0];
				protein_group = accNoArr[0].split("\\|")[0] + ";";
			} else {
				TreeMap<Integer, String> tempPeMap = new TreeMap<Integer, String>();
				String tempPeS = "";
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
				log.error("Major Protein accession not in mouse file!Major Protein accession is " + accNo + "(" + (index + 1) + ")");
				continue;
			}
			outputText = new StringBuffer();
			outputText.append(qProteinsArr[0]).append(sp).append((null==mouseArr[1]||"".equals(mouseArr[1].trim()))?major_protein:mouseArr[1]).append(sp).append(major_protein).append(sp).append(protein_group).append(sp).append(mouseArr[2]);
			String[] uniqueArr=null;
			for (int i = 0; i < QProteinsIndex.size(); i++) {
				if (uniqueFieldNo == QProteinsIndex.get(i)) {
					uniqueArr = qProteinsArr[QProteinsIndex.get(i)].split(";");
//					outputText.append(sp).append(uniqueArr.length);
				} else {
				}
				outputText.append(sp).append(qProteinsArr[QProteinsIndex.get(i)]);
			}
			outputText.append(sp).append(uniqueArr.length);
			outList.add(outputText.toString());
		}

			WriteFile.writeFile(new FileWriter(outputfile), outList);
			log.info("output count:" + Integer.toString(outList.size() - 1) + ",crap count:" + crapCount);
		log.info("Panda end..." + new Date());
		System.exit(0);
	}
	
	

}
