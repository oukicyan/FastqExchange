package com.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class WriteFile {
	private static final Logger log = Logger.getLogger(WriteFile.class);

	public static void writeFile(FileWriter file, List<String> list)
			throws IOException {
		BufferedWriter br = new BufferedWriter(file);// 输出的结果文件

		for (String o : list) {
			br.write(o + "\n");
		}
		log.info("info:output file count is " + list.size());
		br.flush();
		br.close();
	}
	
	public static void writeSataFile(FileWriter file, Map<String, Integer> map)
			throws IOException {
		BufferedWriter br = new BufferedWriter(file);// 输出的结果文件

		for (String o : map.keySet()) {
			br.write(o +" : "+map.get(o)+ "\n");
		}
		log.info("info:output file count is " + map.keySet().size());
		br.flush();
		br.close();
	}
}
