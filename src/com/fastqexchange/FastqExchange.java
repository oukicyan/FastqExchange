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
import com.util.StringUtil;
import com.util.WriteFile;

public class FastqExchange {
	
	private static final Logger log = Logger.getLogger(FastqExchange.class);

	/**
	 * @param args
	 * @throws Exception 
	 * @throws ConfigurationException 
	 */
	public static void main(String[] args) throws ConfigurationException, Exception {
 		log.info("data exchange start..." + new Date());
		String tag = ProfileUtil.getStringProfile("tag");
		String inputfile = ProfileUtil.getStringProfile("inputfile");
		String outputfile = ProfileUtil.getStringProfile("outputfile");
		String reverse = ProfileUtil.getStringProfile("reverse");
		boolean isReverrse = false;
		if (null != reverse && "1".equals(reverse)) {
			isReverrse = true;
		}
		List<String> list = ReadFile.readFile("ex",new File(inputfile));
		List<String> outList=new ArrayList<String>();
		String o1;
		String o2;
		System.out.println("input file data count:" + list.size() / 2);
		for(int i=0;i<list.size()/2;i++){
			o1=list.get(i*2);
			o2=list.get(i*2+1);
			if(o2.contains(tag)){
				outList.add(">"+o1+" = Tag: "+tag);
				if (isReverrse) {
					outList.add(StringUtil.reverse(StringUtil.convert(o2.toCharArray())));
				} else {
					outList.add(o2);
				}
			}
		}
		
		WriteFile.writeFile(new FileWriter(outputfile), outList);

		log.info("data exchange end..." + new Date());
		System.exit(0);
	}

}
