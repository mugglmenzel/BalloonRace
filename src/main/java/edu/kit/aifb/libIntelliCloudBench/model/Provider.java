/*
 * This file is part of libIntelliCloudBench.
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

package edu.kit.aifb.libIntelliCloudBench.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.domain.Location;
import org.jclouds.providers.ProviderMetadata;

import edu.kit.aifb.libIntelliCloudBench.CloudBenchService;

public class Provider extends Observable implements Serializable {
	private static final long serialVersionUID = 7347235110052708192L;

	private String id;
	private String name;
	private Credentials credentials;
	private Collection<Region> allRegions = new TreeSet<Region>();
	private boolean loadedAllRegions = false;
	private Collection<HardwareType> allHardwareTypes = new TreeSet<HardwareType>();
	private boolean loadedAllHardwareTypes = false;

	private static Map<Provider, List<ICredentialsChangedListener>> credentialsListener = new HashMap<Provider, List<ICredentialsChangedListener>>();

	public Provider(ProviderMetadata provider) {
		this.id = provider.getId();
		this.name = provider.getName();
		this.credentials = new Credentials();
		credentialsListener.put(this,
				new ArrayList<ICredentialsChangedListener>());
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public boolean areCredentialsSetup() {
		return !(credentials.getKey().equals("") && credentials.getSecret()
				.equals(""));
	}

	public void credentialsChanged() {
		synchronized (allRegions) {
			loadedAllRegions = false;
			allRegions.clear();
		}
		Logger.getAnonymousLogger().info("Credentials now: " + credentials);
		for (ICredentialsChangedListener listener : credentialsListener
				.get(this))
			if (listener != null)
				listener.notifyCredentialsChanged(this, credentials);

	}

	public void registerCredentialsChangedListener(
			ICredentialsChangedListener listener) {
		credentialsListener.get(this).add(listener);
	}

	public Iterable<Region> getAllRegions(CloudBenchService service,
			Observer observer) throws NotReadyException {
		if (allRegions.isEmpty()) {
			updateAllRegions(getComputeServiceContext(service), observer);
		}
		if (loadedAllRegions) {
			return allRegions;
		} else {
			throw new NotReadyException("allRegions");
		}
	}

	public Iterable<HardwareType> getAllHardwareTypes(
			CloudBenchService service, Observer observer)
			throws NotReadyException {
		if (allHardwareTypes.isEmpty()) {
			updateAllHardwareTypes(service.getContext(this), observer);
		}
		if (loadedAllHardwareTypes) {
			return allHardwareTypes;
		} else {
			throw new NotReadyException("allHardwareTypes");
		}
	}

	public void updateAllRegions(final ComputeServiceContext context,
			final Observer observer) {

		loadedAllRegions = false;
		allRegions.clear();
		for (Location location : Collections.unmodifiableSet(context
				.getComputeService().listAssignableLocations())) {
			Region region = new Region(location);
			allRegions.add(region);
		}
		loadedAllRegions = true;

		observer.update(Provider.this, allRegions);
	}

	public void updateAllHardwareTypes(final ComputeServiceContext context,
			final Observer observer) {

		loadedAllHardwareTypes = false;
		allHardwareTypes.clear();
		for (Hardware hardware : Collections.unmodifiableSet(context
				.getComputeService().listHardwareProfiles())) {
			HardwareType hardwareType = new HardwareType(hardware);
			allHardwareTypes.add(hardwareType);
		}
		loadedAllHardwareTypes = true;

		observer.update(Provider.this, allHardwareTypes);
	}

	private ComputeServiceContext getComputeServiceContext(
			CloudBenchService service) {
		return service.getContext(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Provider other = (Provider) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 5;
		return "Provider ["
				+ (id != null ? "id=" + id + ", " : "")
				+ (name != null ? "name=" + name + ", " : "")
				+ (credentials != null ? "credentials=" + credentials + ", "
						: "")
				+ (allRegions != null ? "allRegions="
						+ toString(allRegions, maxLen) + ", " : "")
				+ "loadedAllRegions="
				+ loadedAllRegions
				+ ", "
				+ (allHardwareTypes != null ? "allHardwareTypes="
						+ toString(allHardwareTypes, maxLen) + ", " : "")
				+ "loadedAllHardwareTypes=" + loadedAllHardwareTypes + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

}
