package com.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ProfileUtil {

	public static String getStringProfile(String proper) throws Exception, ConfigurationException {

		String superManage = new String();
		Configuration config = new PropertiesConfiguration("application.properties");
		if (config.containsKey(proper)) {
			superManage = config.getString(proper);
		}

		return superManage;
	}

}
