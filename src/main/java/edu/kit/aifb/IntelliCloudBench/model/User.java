/*
* This file is part of IntelliCloudBench.
*
* Copyright (c) 2012, Jan Gerlinger <jan.gerlinger@gmx.de>
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* * Neither the name of the Institute of Applied Informatics and Formal
* Description Methods (AIFB) nor the names of its contributors may be used to
* endorse or promote products derived from this software without specific prior
* written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.kit.aifb.IntelliCloudBench.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.collect.Multimap;
import com.google.gson.annotations.SerializedName;

import edu.kit.aifb.IntelliCloudBench.model.persistence.BenchmarksObject;
import edu.kit.aifb.IntelliCloudBench.model.persistence.CredentialsObject;
import edu.kit.aifb.IntelliCloudBench.model.persistence.PMF;
import edu.kit.aifb.libIntelliCloudBench.CloudBenchService;
import edu.kit.aifb.libIntelliCloudBench.model.Benchmark;
import edu.kit.aifb.libIntelliCloudBench.model.Credentials;
import edu.kit.aifb.libIntelliCloudBench.model.ICredentialsChangedListener;
import edu.kit.aifb.libIntelliCloudBench.model.InstanceType;
import edu.kit.aifb.libIntelliCloudBench.model.Provider;
import edu.kit.aifb.libIntelliCloudBench.model.xml.Result;

public class User implements Serializable, ICredentialsChangedListener {
	private static final long serialVersionUID = 1515385475851375669L;
	private static Logger log = Logger.getLogger(User.class.getName());

	private static final String KEY_CREDENTIALS = "credentials";
	private static final String KEY_RESULTS = "results";

	private String id = null;
	private String name = null;
	@SerializedName("given_name")
	private String givenName = null;
	@SerializedName("family_name")
	private String familyName = null;
	private String link = null;
	@SerializedName("picture")
	private String pictureUrl = "http://cdn.dotmed.com/images/discussions/profiles/no_profile.png";
	private String gender = null;
	private String birthday = null;
	private String locale = null;

	public UIState getUiState() {
			return ApplicationState.getUIStateForUser(this);
	}
	
	public CloudBenchService getService() {
		return ApplicationState.getCloudBenchServiceForUser(this);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public String getGivenName() {
  	return givenName;
  }

	public String getFamilyName() {
  	return familyName;
  }

	public String getLink() {
  	return link;
  }

	public String getGender() {
  	return gender;
  }

	public String getBirthday() {
  	return birthday;
  }

	public String getLocale() {
  	return locale;
  }

//	private void storeCredentialsForProviderOld() {
//		Map<String, Credentials> credentialsForProvider = new HashMap<String, Credentials>();
//		for (Provider provider : getService().getAllProviders()) {
//			credentialsForProvider.put(provider.getId(), provider.getCredentials());
//		}
//		storeObject(KEY_CREDENTIALS, credentialsForProvider);
//	}
	//TODO: Verschluesseln!
	private void storeCredentialsForProvider() {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Map<String, Credentials> credentialsForProvider = new HashMap<String, Credentials>();
		for (Provider provider : getService().getAllProviders()) {
			credentialsForProvider.put(provider.getId(), provider.getCredentials());
		}
		Key credentialsKey = KeyFactory.createKey(CredentialsObject.class.getSimpleName(), KEY_CREDENTIALS+"."+getId());
		CredentialsObject credentialsObject = new CredentialsObject(credentialsKey, credentialsForProvider);
		try {
			pm.makePersistent(credentialsObject);
			//TODO: nach Debugging entfernen!
			log.info("Succesfully stored Credentials: "+credentialsForProvider);
			log.info("Test retrieve: "+loadCredentialsForProvider());
		} finally {
			pm.close();
		}
	}

//	public Map<String, Credentials> loadCredentialsForProviderOld() {
//		@SuppressWarnings("unchecked")
//		Map<String, Credentials> credentialsForProvider = (Map<String, Credentials>) loadObject(KEY_CREDENTIALS);
//		if (credentialsForProvider == null)
//			credentialsForProvider = new HashMap<String, Credentials>();
//		return credentialsForProvider;
//	}
	
	public Map<String, Credentials> loadCredentialsForProvider() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key k = KeyFactory
				.createKey(CredentialsObject.class.getSimpleName(), KEY_CREDENTIALS+"."+getId());
		Map<String, Credentials> credentialsForProvider = null;
		try{
			CredentialsObject credentialsObject = pm.getObjectById(CredentialsObject.class, k);
			credentialsForProvider = credentialsObject.getCredentials();
		}
		catch (Exception e){
			log.info("Credentials not found, will create new Map.");
			credentialsForProvider = new HashMap<String, Credentials>();
		}
		return credentialsForProvider;
		
	}
		

//	public void storeLastBenchmarkResultsOld() {
//		Map<InstanceType, Multimap<Benchmark, Result>> resultsForAllBenchmarksForType = getService().getResultsForAllBenchmarksForType();
//		storeObject(KEY_RESULTS, resultsForAllBenchmarksForType);
//	}
	
	public void storeLastBenchmarkResults() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Map<InstanceType, Multimap<Benchmark, Result>> resultsForAllBenchmarksForType = getService().getResultsForAllBenchmarksForType();
		
		Key benchmarksKey = KeyFactory.createKey(CredentialsObject.class.getSimpleName(), KEY_RESULTS+"."+getId());
		BenchmarksObject benchmarksObject = new BenchmarksObject(benchmarksKey, resultsForAllBenchmarksForType);
		try {
			pm.makePersistent(benchmarksObject);
			//TODO: nach Debugging entfernen!
			log.info("Succesfully stored Benchmark Results: "+resultsForAllBenchmarksForType);
		} finally {
			pm.close();
		}
	}

	
//	public Map<InstanceType, Multimap<Benchmark, Result>> loadLastBenchmarkResultsOld() {
//		@SuppressWarnings("unchecked")
//		Map<InstanceType, Multimap<Benchmark, Result>> benchmarkResultsForType =
//		    (Map<InstanceType, Multimap<Benchmark, Result>>) loadObject(KEY_RESULTS);
//		return benchmarkResultsForType;
//	}
	
	public Map<InstanceType, Multimap<Benchmark, Result>> loadLastBenchmarkResults() {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Key k = KeyFactory
				.createKey(BenchmarksObject.class.getSimpleName(), KEY_RESULTS+"."+getId());
		BenchmarksObject benchmarksObject = pm.getObjectById(BenchmarksObject.class, k);
		
		Map<InstanceType, Multimap<Benchmark, Result>> benchmarkResultsForType =
		    benchmarksObject.getBenchmarks();
		
		return benchmarkResultsForType;
	}

	
//	private void storeObject(String key, Object object) {
//		try {
//			File file = new File("/tmp/" + key + "." + getId());
//			file.setExecutable(false);
//			file.setReadable(false);
//			file.setReadable(true, true);
//			file.setWritable(false);
//			file.setWritable(true, true);
//			FileOutputStream os = new FileOutputStream(file);
//			ObjectOutput output = new ObjectOutputStream(os);
//			try {
//				output.writeObject(object);
//			} finally {
//				output.close();
//			}
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	private Object loadObject(String key) {
//		Object object = null;
//		try {
//			File file = new File("/tmp/" + key + "." + getId());
//			InputStream is = new FileInputStream(file);
//			ObjectInput input = new ObjectInputStream(is);
//			try {
//				object = input.readObject();
//			} finally {
//				input.close();
//			}
//		} catch (ClassNotFoundException ex) {
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		} catch (ClassCastException ex) {
//			ex.printStackTrace();
//		}
//		return object;
//	}

	@Override
	public void notifyCredentialsChanged(Provider provider, Credentials credentials) {
		storeCredentialsForProvider();
	}
}
