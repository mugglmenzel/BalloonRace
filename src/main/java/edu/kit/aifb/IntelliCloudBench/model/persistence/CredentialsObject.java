/* Copyright (c) 2013 Robin Hoffmann
 *
 */
package edu.kit.aifb.IntelliCloudBench.model.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import edu.kit.aifb.libIntelliCloudBench.model.Credentials;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class CredentialsObject  {
	@PrimaryKey
	@Persistent
	private String key;

	@Persistent(serialized = "true", defaultFetchGroup = "true")
	private Map<String, Credentials> credentials = new HashMap<String, Credentials>();

	public CredentialsObject(String key, Map<String, Credentials> credentials) {
		this.key = key;
		this.credentials = credentials;
	}

	public String getKey() {
		return key;
	}

	public void setCredentials(Map<String, Credentials> credentials) {
		this.credentials = credentials;
	}
	
	public Map<String, Credentials> getCredentials() {
		return credentials;
	}

}
