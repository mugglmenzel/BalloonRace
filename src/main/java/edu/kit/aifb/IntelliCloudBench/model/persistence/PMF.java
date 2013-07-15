/* Copyright (c) 2013 Robin Hoffmann
 *
 */
package edu.kit.aifb.IntelliCloudBench.model.persistence;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;


public final class PMF {
	//TODO: Let Beanstalk take care of this data, see below
	private static PersistenceManagerFactory pmfInstance;

//Beanstalk specific data
//	private String dbName = System.getProperty("RDS_DB_NAME"); 
//	private String userName = System.getProperty("RDS_USERNAME"); 
//	private String password = System.getProperty("RDS_PASSWORD"); 
//	private String hostname = System.getProperty("RDS_HOSTNAME");
//	private String port = System.getProperty("RDS_PORT");

  public static PersistenceManagerFactory get() {
//	  String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
//	  
//	  properties.setProperty("javax.jdo.PersistenceManagerFactoryClass",
//	                  "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
//	  properties.setProperty("javax.jdo.option.ConnectionDriverName","com.mysql.jdbc.Driver");
//	  properties.setProperty("javax.jdo.option.ConnectionURL", jdbcUrl);
//	  properties.setProperty("javax.jdo.option.ConnectionUserName", userName);
//	  properties.setProperty("javax.jdo.option.ConnectionPassword", password);
//	  properties.setProperty("datanucleus.autoCreateTables", "true");
//	  pmfInstance = JDOHelper.getPersistenceManagerFactory(properties);
	  
	  pmfInstance = JDOHelper
				.getPersistenceManagerFactory("datanucleus.properties");
	  
    return pmfInstance;
  }
}
