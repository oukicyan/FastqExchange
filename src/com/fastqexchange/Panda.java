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
		String Sample6 = ProfileUtil.getStringProfile("Sample6");
		String Sample7 = ProfileUtil.getStringProfile("Sample7");
		String Sample8 = ProfileUtil.getStringProfile("Sample8");
		String Sample9 = ProfileUtil.getStringProfile("Sample9");
		String Sample10 = ProfileUtil.getStringProfile("Sample10");
		String Label = ProfileUtil.getStringProfile("Label");

		int accNoFieldNo = -1;
//		int uniqueFieldNo = -1;
		List<String> counterFieldName=new ArrayList<String>();
		List<Integer> counterFieldNo=new ArrayList<Integer>();
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
				if (title_QProteinsArr[i].toLowerCase().startsWith("unique")||title_QProteinsArr[i].equals("PeptideID")) {
					counterFieldName.add(title_QProteinsArr[i]);
					counterFieldNo.add(i);
//					uniqueFieldNo = i;
				}
			}
		}

		String title = "Protein Group ID\tgene\tMajor Protein accession\tProtein Group accession\tlength\t"+titleQProteinsSb.toString();
		for(String s:counterFieldName){
			title+=s+"_Number\t";
		}
		if (null != Label && !"".equals(Label)) {
			title = title.replaceAll("Intensity1", Label + "plex113_Intensity");
			title = title.replaceAll("Intensity2", Label + "plex114_Intensity");
			title = title.replaceAll("Intensity3", Label + "plex115_Intensity");
			title = title.replaceAll("Intensity4", Label + "plex116_Intensity");
			title = title.replaceAll("Intensity5", Label + "plex117_Intensity");
			title = title.replaceAll("Intensity6", Label + "plex118_Intensity");
			title = title.replaceAll("Intensity7", Label + "plex119_Intensity");
			title = title.replaceAll("Intensity8", Label + "plex121_Intensity");
		} else {
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
			if (null != Sample6 && !"".equals(Sample6)) {
				title = title.replaceAll("Sample6", Sample6).replaceAll("sample6", Sample6);
			}
			if (null != Sample7 && !"".equals(Sample7)) {
				title = title.replaceAll("Sample7", Sample7).replaceAll("sample7", Sample7);
			}
			if (null != Sample8 && !"".equals(Sample8)) {
				title = title.replaceAll("Sample8", Sample8).replaceAll("sample8", Sample8);
			}
			if (null != Sample9 && !"".equals(Sample9)) {
				title = title.replaceAll("Sample9", Sample9).replaceAll("sample9", Sample9);
			}
			if (null != Sample10 && !"".equals(Sample10)) {
				title = title.replaceAll("Sample10", Sample10).replaceAll("sample10", Sample10);
			}
		}
		title = title + "MW";

		outList.add(title);
		list_QProteins.remove(0);
		
		Map<String, String[]> map_mouse = new HashMap<String, String[]>();
		Map<String, String> map_mouse_pe = new HashMap<String, String>();
		String[] mouseArr;
		for (String mouse : list_mouse) {
			mouseArr = mouse.split(",");
//			if (mouseArr.length != 4) {
//				log.error("mouse file's field number is wrong!" + mouse);
//			} else {
				map_mouse.put(mouseArr[0], mouseArr);
				if("NA".equals(mouseArr[3])){
					map_mouse_pe.put(mouseArr[0], "9999");
				}else{
					map_mouse_pe.put(mouseArr[0], mouseArr[3]);
				}
//			}
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
			boolean isSp=false;
			boolean isTr=false;
			index++;
			qProteinsArr = qProteins.split("\t");
			try {
				accNo = qProteinsArr[accNoFieldNo];
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Major Protein accession=" + accNo + ",qProteinsArr's length=" + qProteinsArr.length);
			}

//			if(accNo.contains("sp|")){
//				accNo = accNo.replaceAll("sp\\|", "");
//				isSp=true;
//			}
//			if(accNo.contains("tr|")){
//				accNo = accNo.replaceAll("tr\\|", "");
//				isTr=true;
//			}

			major_protein = "";
			protein_group = "";
			accNoArr = accNo.split(";");
			if (accNoArr.length == 1) {
				if (accNoArr[0].split("\\|").length > 1) {
					major_protein = accNoArr[0].split("\\|")[1];
					protein_group = accNoArr[0].split("\\|")[0] + "|" + accNoArr[0].split("\\|")[1] + ";";
				} else {
					major_protein = accNoArr[0];
					protein_group = accNoArr[0];
				}
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
							if (s.split("\\|").length > 1) {
								major_protein = s.split("\\|")[1];
							} else {
								major_protein = s;
							}
						}
						if (true) {
							if (s.split("\\|").length > 1) {
								protein_group += s.split("\\|")[0] + "|" + s.split("\\|")[1] + ";";
							} else {
								protein_group += s + ";";
							}
						}
					}

					String s1=s.replaceAll("sp\\|", "").replaceAll("tr\\|", "");
					
					if (map_mouse_pe.containsKey(s1.split("\\|")[0])) {
						tempPeS = map_mouse_pe.get(s1.split("\\|")[0]);
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
					if(tempPeMap.get(i).split("\\|").length>1){
						major_protein = tempPeMap.get(i).split("\\|")[1];
					}else{
						major_protein = tempPeMap.get(i);
					}
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
			List<String> counterContentList=new ArrayList<String>();
			for (int i = 0; i < QProteinsIndex.size(); i++) {
				if (counterFieldNo.contains(QProteinsIndex.get(i))) {
					uniqueArr = qProteinsArr[QProteinsIndex.get(i)].split(";");
//					outputText.append(sp).append(uniqueArr.length);
					
					StringBuffer sb=new StringBuffer();
					for(String s:uniqueArr){
						try{
							//20170107_PeptideID和Unique开头字段数据取消做+1处理
							//sb.append(Integer.parseInt(s)+1).append(";");
							sb.append(Integer.parseInt(s)).append(";");
						}catch(NumberFormatException e){
							if(null==s||"".equals(s)){
								sb.append(s);
							}else{
								sb.append(s).append(";");
							}
						}
					}
					outputText.append(sp).append(sb.toString());
					if(null==qProteinsArr[QProteinsIndex.get(i)]||"".equals(qProteinsArr[QProteinsIndex.get(i)].trim())){
						counterContentList.add("0");
					}else{
						counterContentList.add(uniqueArr.length+"");
					}
				} else {
					outputText.append(sp).append(qProteinsArr[QProteinsIndex.get(i)]);
				}
			}
			for(String s:counterContentList){
				outputText.append(sp).append(s);
			}
			outputText.append(sp).append(mouseArr[4].trim());
			outList.add(outputText.toString());
		}

			WriteFile.writeFile(new FileWriter(outputfile), outList);
			log.info("output count:" + Integer.toString(outList.size() - 1) + ",crap count:" + crapCount);
		log.info("Panda end..." + new Date());
		System.exit(0);
	}
	
	

}
