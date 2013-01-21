/* Copyright (c) 2013 Robin Hoffmann
 *
 */
package edu.kit.aifb.IntelliCloudBench.util.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import edu.kit.aifb.libIntelliCloudBench.model.Credentials;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class CredentialsObject  {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent(serialized = "true", defaultFetchGroup = "true")
	private Map<String, Credentials> credentials = new HashMap<String, Credentials>();

	public CredentialsObject(Key key, Map<String, Credentials> credentials) {
		this.key = key;
		this.credentials = credentials;
	}

	public Key getKey() {
		return key;
	}

	public Map<String, Credentials> getCredentials() {
		return credentials;
	}

}
