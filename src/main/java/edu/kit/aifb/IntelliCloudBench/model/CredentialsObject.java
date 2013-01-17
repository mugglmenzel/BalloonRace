/* Copyright (c) 2013 Robin Hoffmann
*
*/
package edu.kit.aifb.IntelliCloudBench.model;

import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import edu.kit.aifb.libIntelliCloudBench.model.Credentials;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class CredentialsObject {
 @PrimaryKey
 @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
 private Key key;

 @Persistent
 private String name;
 
 @Persistent
 private Map<String, Credentials> credentials;

 public CredentialsObject(String name, Map<String, Credentials> credentials) {

   this.name = name;
   this.credentials = credentials;

 }

 public Key getKey() {
   return key;
 }
 
 public String getName() {
	   return name;
	 }
 
 public Map<String, Credentials> getCredentials() {
	    return credentials;
	  }

}

