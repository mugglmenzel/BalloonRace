package edu.kit.aifb.IntelliCloudBench.model;

import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.common.collect.Multimap;

import edu.kit.aifb.libIntelliCloudBench.model.Benchmark;
import edu.kit.aifb.libIntelliCloudBench.model.InstanceType;
import edu.kit.aifb.libIntelliCloudBench.model.xml.Result;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BenchmarksObject {
 @PrimaryKey
 @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
 private Key key;

 @Persistent
 private String name;
 
 @Persistent
private Map<InstanceType, Multimap<Benchmark, Result>> resultsForAllBenchmarksForType;

 public BenchmarksObject(String name, Map<InstanceType, Multimap<Benchmark, Result>> resultsForAllBenchmarksForType) {

   this.name = name;
   this.resultsForAllBenchmarksForType = resultsForAllBenchmarksForType;

 }

 public Key getKey() {
   return key;
 }
 
 public String getName() {
	   return name;
	 }
 
 public Map<InstanceType, Multimap<Benchmark, Result>> getBenchmarks() {
	    return resultsForAllBenchmarksForType;
	  }

}
