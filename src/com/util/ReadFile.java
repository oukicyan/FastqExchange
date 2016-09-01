package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ReadFile {
	private static final Logger log = Logger.getLogger(ReadFile.class);

	public static List<String> readFile(String type, File file) throws IOException {
		List<String> list = new ArrayList<String>();
		if (!file.exists()) {
			log.error("error:file isn't existed!file path:" + file.getAbsolutePath());
			return list;
		}
		log.info("info:file read start.file path is " + file.getAbsolutePath());

		String encoding = "GBK";
		InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// ¿¼ÂÇµ½±àÂë¸ñÊ½
		BufferedReader bufferedReader = new BufferedReader(read);
		String lineTxt = null;
		boolean flag = true;
		int index=0;
//		StringBuffer mouseSb=new StringBuffer();
		int mouseLength=0;
		while ((lineTxt = bufferedReader.readLine()) != null) {
			index++;
//			if ("ex".equals(type) && lineTxt.startsWith("@")) {
//				if(lineTxt.startsWith("@")&&!lineTxt.contains(":")){
//					flag = true;
//					continue;
//				}
//				flag = false;
//			}
//			if ("ex".equals(type) && flag) {
//				flag = false;
////				System.out.println(lineTxt);
//				continue;
//			}
//			if ("ex".equals(type) && lineTxt.startsWith("+")) {
//				flag = true;
//				continue;
//			}
			if("ex".equals(type)){
				if(index%4==3||index%4==0){
					continue;
				}
			}else if("mouse".equals(type)){
				if(lineTxt.startsWith(">sp")){
					flag = false;
					if(0!=mouseLength){
						list.add(Integer.toString(mouseLength));
					}
				}
				if(!flag){
					list.add(lineTxt);
					flag = true;
					mouseLength=0;
				}
				if(flag){
					mouseLength+=lineTxt.length();
				}
			}else{
				list.add(lineTxt);
			}
		}
		if(mouseLength!=0){
			list.add(Integer.toString(mouseLength));
		}
		read.close();

		return list;
	}

}
