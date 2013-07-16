/* Copyright (c) 2013 Robin Hoffmann
 *
 */
package edu.kit.aifb.IntelliCloudBench.model.persistence;

import java.util.Map;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.common.collect.Multimap;

import edu.kit.aifb.libIntelliCloudBench.model.Benchmark;
import edu.kit.aifb.libIntelliCloudBench.model.InstanceType;
import edu.kit.aifb.libIntelliCloudBench.model.xml.Result;
 
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BenchmarksObject {
	@PrimaryKey
	@Persistent
	private String key;

	@Persistent(serialized = "true", defaultFetchGroup = "true")
	private Map<InstanceType, Multimap<Benchmark, Result>> resultsForAllBenchmarksForType;

	public BenchmarksObject(
			String key,
			Map<InstanceType, Multimap<Benchmark, Result>> resultsForAllBenchmarksForType) {

		this.key = key;
		this.resultsForAllBenchmarksForType = resultsForAllBenchmarksForType;

	}

	public String getKey() {
		return key;
	}

	public void setBenchmarks(
			Map<InstanceType, Multimap<Benchmark, Result>> resultsForAllBenchmarksForType) {
		this.resultsForAllBenchmarksForType = resultsForAllBenchmarksForType;
	}

	public Map<InstanceType, Multimap<Benchmark, Result>> getBenchmarks() {
		return resultsForAllBenchmarksForType;
	}

}
