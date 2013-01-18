/* Copyright (c) 2013 Robin Hoffmann
 *
 */
package edu.kit.aifb.IntelliCloudBench.util.persistence;

import java.io.Serializable;
import java.util.Map;

import edu.kit.aifb.libIntelliCloudBench.model.Credentials;

public class CredentialsFile implements Serializable {

	private static final long serialVersionUID = -7451555501182413290L;

	private String name;
	private Map<String, Credentials> credentials;

	public CredentialsFile(String name, Map<String, Credentials> credentials) {
		this.name = name;
		this.credentials = credentials;
	}

	public String getName() {
		return name;
	}

	public Map<String, Credentials> getCredentials() {
		return credentials;
	}

}
