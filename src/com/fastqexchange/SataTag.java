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

public class SataTag {
	
	private static final Logger log = Logger.getLogger(SataTag.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws ConfigurationException,
			Exception {
		log.info("sata tag start..." + new Date());
		String tag = ProfileUtil.getStringProfile("tag");
		String inputfile = ProfileUtil.getStringProfile("satainputfile");
		String outputfile = ProfileUtil.getStringProfile("sataoutputfile");
		String outputfile1 = ProfileUtil.getStringProfile("sataoutputfile1");
		String reverse = ProfileUtil.getStringProfile("reverse");
		boolean isReverrse = false;
		if (null != reverse && "1".equals(reverse)) {
			isReverrse = true;
		}
		String countS = ProfileUtil.getStringProfile("count");
		int countKey = 0;
		try {
			countKey = Integer.parseInt(countS);
		} catch (NumberFormatException e) {
		}
		List<String> list = ReadFile.readFile("sata", new File(inputfile));
		List<String> outList = new ArrayList<String>();
		String o1;
		String o2;
		List<String> keyList = new ArrayList<String>();
		Map<String, Integer> sataMap = new HashMap<String, Integer>();
		String key="";
		for (int i = 0; i < list.size() / 2; i++) {
			o1 = list.get(i * 2);
			o2 = list.get(i * 2 + 1);
			if (o2.length() < o2.indexOf(tag) + tag.length() + countKey) {
				key = tag + o2.substring(o2.indexOf(tag) + tag.length());
			} else {
				key = tag + o2.substring(o2.indexOf(tag) + tag.length(), o2.indexOf(tag) + tag.length() + countKey);
			}
			if (sataMap.containsKey(key)) {
				sataMap.put(key, sataMap.get(key) + 1);
			} else {
				sataMap.put(key, 1);
				outList.add(o1);
				if (isReverrse) {
					outList.add(StringUtil.reverse(StringUtil.convert(o2.toCharArray())));
				} else {
					outList.add(o2);
				}
				keyList.add(key);
			}
		}

		WriteFile.writeFile(new FileWriter(outputfile), outList);
		WriteFile.writeSataFile(new FileWriter(outputfile1), sataMap);

		log.info("data exchange end..." + new Date());
		System.exit(0);
	}

}
