/* Copyright (c) 2013 Robin Hoffmann
 *
 */
package edu.kit.aifb.IntelliCloudBench.util.persistence;

import java.io.Serializable;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import edu.kit.aifb.libIntelliCloudBench.model.Credentials;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class PersistentCredentialsObject implements Serializable {

	private static final long serialVersionUID = -7451555501182413290L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private String name;

	@Persistent(serialized = "true")
	private CredentialsFile credentialsFile;

	public PersistentCredentialsObject(String name, Map<String, Credentials> credentials) {
		credentialsFile = new CredentialsFile (name, credentials);
	}

	public Key getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public CredentialsFile getCredentialsFile() {
		return credentialsFile;
	}

}
