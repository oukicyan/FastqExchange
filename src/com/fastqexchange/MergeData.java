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

public class MergeData {

	private static final Logger log = Logger.getLogger(MergeData.class);

	/**
	 * @param args
	 * @throws Exception
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws ConfigurationException, Exception {
		log.info("MergeData start..." + new Date());
		String inputfiles = ProfileUtil.getStringProfile("inputfile_MergeDataFile");
		String outputfile = ProfileUtil.getStringProfile("outputfile_MergeDataFile");

		Map<String, String[]> map = new HashMap<String, String[]>();
		List<String> datalist = new ArrayList<String>();

		// 读取文件
		String[] inputfileArr = inputfiles.split(";");
		String[] outtitleArr = new String[5 + 3 * (inputfileArr.length)];
		outtitleArr[0] = "gene";
		outtitleArr[1] = "major protein";
		for (int k = 0; k < inputfileArr.length; k++) {
			outtitleArr[2 + k] = "file" + (k + 1);
		}

		int fileindex = -1;

		for (int k = 0; k < inputfileArr.length; k++) {
			String inputfile = inputfileArr[k];
			List<String> list = ReadFile.readFile("", new File(inputfile));
			if (list.size() > 0) {
				// 处理title
				String title = list.get(0);
				String[] titleArr = title.split("\t");

				outtitleArr[2 + inputfileArr.length + 3 * k] = titleArr[5];
				outtitleArr[2 + inputfileArr.length + 3 * k + 1] = titleArr[6];
				outtitleArr[2 + inputfileArr.length + 3 * k + 2] = titleArr[7];

				for (int i = 1; i < list.size(); i++) {
					String dataStr = list.get(i);
					String[] dataArr = dataStr.split("\t");
					String[] outdataArr = new String[5 + 3 * (inputfileArr.length)];
					for (int l = 2; l < 2 + inputfileArr.length; l++) {
						outdataArr[l] = "0";
					}
					for (int l = 2 + inputfileArr.length; l < outdataArr.length; l++) {
						outdataArr[l] = "N/A";
					}

					if (map.containsKey(dataArr[1])) {
						if (k == fileindex) {
							System.out.println("!!k=" + k + ":" + dataArr[1]);
						}
						outdataArr = map.get(dataArr[1]);
					} else {
						outdataArr[0] = dataArr[0];
						outdataArr[1] = dataArr[1];
					}

					outdataArr[2 + k] = "1";
					outdataArr[2 + inputfileArr.length + 3 * k] = dataArr[5];
					outdataArr[2 + inputfileArr.length + 3 * k + 1] = dataArr[6];
					outdataArr[2 + inputfileArr.length + 3 * k + 2] = dataArr[7];

					map.put(dataArr[1], outdataArr);
				}

			}
			fileindex = k;
		}

		StringBuffer dataSb = new StringBuffer();
		for (int j = 0; j < 5; j++) {
			dataSb.append(outtitleArr[j]).append("\t");
		}
		for (int j = 0; j < inputfileArr.length; j++) {
			dataSb.append(outtitleArr[5 + j]).append("\t");
			dataSb.append(outtitleArr[5 + j + 3]).append("\t");
			dataSb.append(outtitleArr[5 + j + 6]).append("\t");
		}
		datalist.add(dataSb.toString());

		for (String s : map.keySet()) {
			String[] outdataArr = map.get(s);
			dataSb = new StringBuffer();
			for (int j = 0; j < 5; j++) {
				dataSb.append(outdataArr[j]).append("\t");
			}
			for (int j = 0; j < inputfileArr.length; j++) {
				dataSb.append(outdataArr[5 + j]).append("\t");
				dataSb.append(outdataArr[5 + j + 3]).append("\t");
				dataSb.append(outdataArr[5 + j + 6]).append("\t");
			}
			datalist.add(dataSb.toString());
		}

		WriteFile.writeFile(new FileWriter(outputfile), datalist);

		log.info("output count:" + Integer.toString(datalist.size() - 1));
		log.info("MergeData end..." + new Date());
		System.exit(0);
	}

}
