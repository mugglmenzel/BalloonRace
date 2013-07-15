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
import javax.jdo.Transaction;

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
	private static final String KEY_CREDENTIALS = "credentials";
	private static final String KEY_RESULTS = "results";
	private static Logger log = Logger.getLogger(User.class.getName());

	private String id = null;
	private String name = null;
	@SerializedName("given_name")
	private String givenName = null;
	@SerializedName("family_name")
	private String familyName = null;
	private String link = null;
	@SerializedName("picture")
	private String pictureUrl = null;
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

	private void storeCredentialsForProvider(Provider provider,
			Credentials credentials) {

		PersistenceManager pm = PMF.get().getPersistenceManager();

		Map<String, Credentials> credentialsForProvider = new HashMap<String, Credentials>();
		for (Provider p : getService().getAllProviders()) {
			if (p.equals(provider))
				credentialsForProvider.put(provider.getId(), credentials);
			else
				credentialsForProvider.put(p.getId(), p.getCredentials());
		}

		String credentialsKey = CredentialsObject.class.getSimpleName()+"."+KEY_CREDENTIALS + "."
				+ getId();

		CredentialsObject credentialsObject = null;

		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			try{
				credentialsObject = pm.getObjectById(CredentialsObject.class, credentialsKey);
				credentialsObject.setCredentials(credentialsForProvider);
			}
			catch (Exception e) {
				credentialsObject =	new CredentialsObject(credentialsKey, credentialsForProvider);
			}
			pm.makePersistent(credentialsObject);

			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}


	// public Map<String, Credentials> loadCredentialsForProviderOld() {
	// @SuppressWarnings("unchecked")
	// Map<String, Credentials> credentialsForProvider = (Map<String,
	// Credentials>) loadObject(KEY_CREDENTIALS);
	// if (credentialsForProvider == null)
	// credentialsForProvider = new HashMap<String, Credentials>();
	// return credentialsForProvider;
	// }

	public Map<String, Credentials> loadCredentialsForProvider() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String k = CredentialsObject.class.getSimpleName()+"."+
				KEY_CREDENTIALS + "." + getId();
		Map<String, Credentials> credentialsForProvider = null;
		Transaction tx = pm.currentTransaction();

		try {
			tx.begin();
			CredentialsObject credentialsObject = pm.getObjectById(
					CredentialsObject.class, k);
			credentialsForProvider = credentialsObject.getCredentials();
			tx.commit();
		}
		catch (Exception e) {
			credentialsForProvider = new HashMap<String, Credentials>();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		} 
		return credentialsForProvider;

	}

	// public void storeLastBenchmarkResultsOld() {
	// Map<InstanceType, Multimap<Benchmark, Result>>
	// resultsForAllBenchmarksForType =
	// getService().getResultsForAllBenchmarksForType();
	// storeObject(KEY_RESULTS, resultsForAllBenchmarksForType);
	// }

	public void storeLastBenchmarkResults() {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		//TODO: Anscheinend werden Ergebnisse nicht korrekt weitergegeben
		Map<InstanceType, Multimap<Benchmark, Result>> resultsForAllBenchmarksForType = getService()
				.getResultsForAllBenchmarksForType();

		String benchmarksKey = BenchmarksObject.class.getSimpleName()+"."+KEY_RESULTS + "."
				+ getId();

		BenchmarksObject benchmarksObject = null;

		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			try{
				benchmarksObject = pm.getObjectById(BenchmarksObject.class, benchmarksKey);
				benchmarksObject.setBenchmarks(resultsForAllBenchmarksForType);
			}
			catch (Exception e) {
				benchmarksObject =	new BenchmarksObject(benchmarksKey, resultsForAllBenchmarksForType);
			}
			pm.makePersistent(benchmarksObject);
			tx.commit();
			// TODO: nach Debugging entfernen!
			log.info("Succesfully stored Benchmark Results for Key "+benchmarksKey+": "
					+ resultsForAllBenchmarksForType);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}

	// public Map<InstanceType, Multimap<Benchmark, Result>>
	// loadLastBenchmarkResultsOld() {
	// @SuppressWarnings("unchecked")
	// Map<InstanceType, Multimap<Benchmark, Result>> benchmarkResultsForType =
	// (Map<InstanceType, Multimap<Benchmark, Result>>) loadObject(KEY_RESULTS);
	// return benchmarkResultsForType;
	// }

	public Map<InstanceType, Multimap<Benchmark, Result>> loadLastBenchmarkResults() {

		PersistenceManager pm = PMF.get().getPersistenceManager();

		String k = BenchmarksObject.class.getSimpleName()+"."+KEY_RESULTS + "." + getId();
		Map<InstanceType, Multimap<Benchmark, Result>> benchmarkResultsForType = null;
		Transaction tx = pm.currentTransaction();

		try {
			tx.begin();
			BenchmarksObject benchmarksObject = pm.getObjectById(
					BenchmarksObject.class, k);
			benchmarkResultsForType = benchmarksObject.getBenchmarks();
			tx.commit();
		}
		catch (Exception e) {
			benchmarkResultsForType = new HashMap<InstanceType, Multimap<Benchmark, Result>>();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		} 
		return benchmarkResultsForType;
	}

	@Override
	public void notifyCredentialsChanged(Provider provider, Credentials credentials) {
		storeCredentialsForProvider(provider, credentials);
	}
}
