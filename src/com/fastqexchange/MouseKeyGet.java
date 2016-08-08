package com.fastqexchange;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import com.util.ProfileUtil;
import com.util.ReadFile;
import com.util.WriteFile;

public class MouseKeyGet {

	private static final Logger log = Logger.getLogger(MouseKeyGet.class);

	/**
	 * @param args
	 * @throws Exception
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws ConfigurationException, Exception {
		log.info("mouse key get start..." + new Date());
		String inputfile = ProfileUtil.getStringProfile("inputfile_mouse");
		String outputfile = ProfileUtil.getStringProfile("outputfile_mouse");
		String mouse_count_flag = ProfileUtil.getStringProfile("mouse_count_flag");
		List<String> list = ReadFile.readFile("mouse", new File(inputfile));
		List<String> outList = new ArrayList<String>();
		String key = "";
		String gn = "";
		String pe = "";
		String[] spArr;
		System.out.println("input file data count:" + list.size());
		int i = 0;
		String sp="	";
		String sp1="	";
		for (String o : list) {
			if (!"1".equals(mouse_count_flag) || ("1".equals(mouse_count_flag) && i % 2 == 0)) {
				spArr = o.split(">sp\\|");
				if (spArr.length >= 2) {
					try {
						key = spArr[1].substring(0, spArr[1].indexOf("|"));
					} catch (StringIndexOutOfBoundsException e) {
						log.error("date error(key)!" + o);
					}
				}
				spArr = o.split("GN=");
				if (spArr.length >= 2) {
					try {
						gn = spArr[1].substring(0, spArr[1].indexOf(" "));
					} catch (StringIndexOutOfBoundsException e) {
						gn = spArr[1];
					}
				}
				spArr = o.split("PE=");
				if (spArr.length >= 2) {
					try {
						pe = spArr[1].substring(0, spArr[1].indexOf(" "));
					} catch (StringIndexOutOfBoundsException e) {
						pe = spArr[1];
					}
				}
			}
			if (!"1".equals(mouse_count_flag) || ("1".equals(mouse_count_flag) && i % 2 == 1)) {
				if (!"".equals(key)) {
					if("1".equals(mouse_count_flag)) {
						outList.add(key + sp + gn + sp1 + o);
					}else if("2".equals(mouse_count_flag)){
						outList.add(key + sp + gn + pe);
					}else{
						outList.add(key + sp + gn);
					}
				} else {
					log.error("date error(key or gn is blank)!" + o);
				}
			}
			i++;
		}

		WriteFile.writeFile(new FileWriter(outputfile), outList);

		log.info("mouse key get end..." + new Date());
		System.exit(0);
	}

}
