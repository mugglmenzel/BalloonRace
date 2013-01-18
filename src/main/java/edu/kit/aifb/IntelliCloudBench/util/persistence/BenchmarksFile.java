package edu.kit.aifb.IntelliCloudBench.util.persistence;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Multimap;

import edu.kit.aifb.libIntelliCloudBench.model.Benchmark;
import edu.kit.aifb.libIntelliCloudBench.model.InstanceType;
import edu.kit.aifb.libIntelliCloudBench.model.xml.Result;

public class BenchmarksFile implements Serializable {

	private static final long serialVersionUID = 6603879708745198720L;

	private String name;

	private Map<InstanceType, Multimap<Benchmark, Result>> resultsForAllBenchmarksForType;

	public BenchmarksFile(
			String name,
			Map<InstanceType, Multimap<Benchmark, Result>> resultsForAllBenchmarksForType) {

		this.name = name;
		this.resultsForAllBenchmarksForType = resultsForAllBenchmarksForType;

	}

	public String getName() {
		return name;
	}

	public Map<InstanceType, Multimap<Benchmark, Result>> getBenchmarks() {
		return resultsForAllBenchmarksForType;
	}

}
