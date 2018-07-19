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
import com.util.WriteFile;

public class VarMods {

	private static final Logger log = Logger.getLogger(VarMods.class);

	/**
	 * @param args
	 * @throws Exception
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws ConfigurationException,
			Exception {
		log.info("VarMods start..." + new Date());
		// 1、 PANDA或Coverage生成的列表
		String inputfile_list = ProfileUtil.getStringProfile("inputfile_VarMods_list");
		// 2、 生成Coverage时的Peptides.txt文件
		String inputfile_Peptides = ProfileUtil.getStringProfile("inputfile_VarMods_Peptides");
		// 3、 QC文件（.PepList文件，可能存在多个文件）
		String inputfile_PepLists = ProfileUtil.getStringProfile("inputfile_VarMods_PepList");
		String outputfile = ProfileUtil.getStringProfile("outputfile_VarMods");

		List<String> list_data = ReadFile.readFile("", new File(inputfile_list));
		List<String> list_Peptides = ReadFile.readFile("", new File(inputfile_Peptides));
		Map<String, String> map_Peptides = new HashMap<String, String>();
		for (int i = 1; i < list_Peptides.size(); i++) {
			String data[] = list_Peptides.get(i).split("\t");
			if (map_Peptides.containsKey(data[1])) {
				log.error("input Peptides file's sequence has more than on PeptideID !");
				//System.exit(0);
			} else {
				map_Peptides.put(data[1], data[0]);
			}
		}

		Map<String, String[]> map_PepLists = new HashMap<String, String[]>();
		for (String filename : inputfile_PepLists.split(";")) {
			int ReadableModIndex = -1;
			int peptideIndex = -1;
			int VarModsStrIndex = -1;
			int SpectrumIndex = -1;
			int index = 0;
			List<String> list_temp = ReadFile.readFile("", new File(filename));
			for (String field : list_temp.get(0).split("\t")) {
				if ("ReadableMod".equals(field)) {
					ReadableModIndex = index;
				} else if ("peptide".equals(field)) {
					peptideIndex = index;
				} else if (("VarModsStr").equals(field)) {
					VarModsStrIndex = index;
				} else if ("spectrum".equals(field)) {
					SpectrumIndex = index;
				}
				index++;
			}
			if (ReadableModIndex == -1 || peptideIndex == -1 || SpectrumIndex==-1||VarModsStrIndex == -1 ) {
				log.error("input file " + filename + " not found ReadableMod or peptide or VarModsStr or Spectrum field!");
			} else {
				for (int i = 1; i < list_temp.size(); i++) {
					String data = list_temp.get(i);
					String[] dataArr = data.split("\t");
					String ReadableMod = dataArr[ReadableModIndex];
					if (null != ReadableMod && !"".equals(ReadableMod.trim())) {
						String peptide = dataArr[peptideIndex];
						if (map_Peptides.containsKey(peptide)) {
							// TODO:重複警告？
							String[] outdataArr = new String[4];
							if (map_PepLists.containsKey(map_Peptides.get(peptide))) {
								outdataArr=map_PepLists.get(map_Peptides.get(peptide));
								outdataArr[3] += dataArr[SpectrumIndex]+"|";
							} else {
								outdataArr[0] = peptide;
								outdataArr[1] = dataArr[VarModsStrIndex];
								outdataArr[2] = dataArr[ReadableModIndex];
								outdataArr[3] = dataArr[SpectrumIndex]+"|";
							}
							map_PepLists.put(map_Peptides.get(peptide), outdataArr);
						} else {
							log.error("input file " + filename + " 's data " + peptide + " not in Peptides file!");
						}
					}
				}
			}
		}
		List<String> outList = new ArrayList<String>();
		String[] titleArr = list_data.get(0).split("\t");
		int PeptideIDIndex = -1;
		int index = 0;
		for (String title : titleArr) {
			if ("PeptideID".equals(title)) {
				PeptideIDIndex = index;
				break;
			}
			index++;
		}
		StringBuffer titleSb = new StringBuffer(list_data.get(0));
		titleSb.append("VarMods_peptide\tVarModsStr\tReadableMod(VarMods)\tSpectrum\tVarMod_PeptideID");
		outList.add(titleSb.toString());
		list_data.remove(0);
		for (String data : list_data) {
			StringBuffer outputText = new StringBuffer();
			String[] dataArr = data.split("\t");
			String[] keyArr = dataArr[PeptideIDIndex].split(";");
			String QCArr0="",QCArr1="",QCArr2="",QCArr3="",keys="";
			for(String key:keyArr){
				
			
			if (map_PepLists.containsKey(key)) {
				String[] QCArr = map_PepLists.get(key);
				QCArr0+=QCArr[0]+";";
				QCArr1+=QCArr[1]+";";
				QCArr2+=QCArr[2]+";";
				QCArr3+=QCArr[3]+";";
				keys+=key+";";
			} else {
				log.error("input PANDA or Coverage file 's data : " + key + " not in  QC file!");
			}
			}
			outputText.append(data).append("\t").append(QCArr0).append("\t").append(QCArr1).append("\t")
			.append(QCArr2).append("\t").append(QCArr3).append("\t").append(keys);
			outList.add(outputText.toString());
		}

		WriteFile.writeFile(new FileWriter(outputfile), outList);
		log.info("output count:" + Integer.toString(outList.size() - 1));
		log.info("VarMods end..." + new Date());
		System.exit(0);
	}

}
