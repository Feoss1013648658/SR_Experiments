package org.insight_centre.aceis.utils.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.insight_centre.aceis.io.streams.cqels.CQELSSensorStream;
import org.insight_centre.aceis.io.streams.csparql.CSPARQLSensorStream;
import org.insight_centre.citybench.main.SRJCityBench;
import org.insight_centre.citybench.reasoning.TSReasonerSensorStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvWriter;

public class PerformanceMonitor implements Runnable {
	private Map<String, String> qMap;
	private long duration;
	private int duplicates;
	private String resultName;
	private long start = 0;
	private ConcurrentHashMap<String, List<Long>> latencyMap = new ConcurrentHashMap<String, List<Long>>();
	private List<Double> memoryList = new ArrayList<Double>();;
	private ConcurrentHashMap<String, Long> resultCntMap = new ConcurrentHashMap<String, Long>();
	private CsvWriter cw;
	private long resultInitTime = 0, lastCheckPoint = 0, globalInit = 0;
	private boolean stop = false;
	private List<String> qList;
	private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitor.class);
	
	
	private long lubm_cnt=0, lubm_latency=0;
	private boolean lubm=false;

	public PerformanceMonitor(Map<String, String> queryMap, long duration, int duplicates, String resultName)
			throws Exception {
		qMap = queryMap;
		this.duration = duration;
		this.resultName = resultName;
		this.duplicates = duplicates;
		File outputFile = new File("result_log" + File.separator + resultName + "_" + new Date(System.currentTimeMillis()) + ".csv");
		if (outputFile.exists())
			throw new Exception("Result log file already exists.");
		cw = new CsvWriter(new FileWriter(outputFile, true), ',');
		cw.write("");
		qList = new ArrayList(this.qMap.keySet());
		Collections.sort(qList);

		for (String qid : qList) {
//			logger.info("qid " + qid);
			latencyMap.put(qid, new ArrayList<Long>());
			resultCntMap.put(qid, (long) 0);
			cw.write("latency-" + qid);
		}
//		logger.info("latencyMap = " + latencyMap.toString());
		// for (String qid : qList) {
		// cw.write("cnt-" + qid);
		// }
		cw.write("memory");
		cw.endRecord();
		// cw.flush();
		// cw.
		this.globalInit = System.currentTimeMillis();
	}
	
	

	public boolean isLubm() {
		return lubm;
	}



	public void setLubm(boolean lubm) {
		this.lubm = lubm;
	}



	public void run() {
		int minuteCnt = 0;
		if(duration != 0){
			
			
			while (!stop) {
				try {
					if (((System.currentTimeMillis() - this.globalInit) > 1.5 * duration)
							|| (duration != 0 && resultInitTime != 0 && (System.currentTimeMillis() - this.resultInitTime) > (30000 + duration))) {
						this.cw.flush();
						this.cw.close();
						logger.info("Stopping after " + (System.currentTimeMillis() - this.globalInit) + " ms.");
						this.cleanup();
						logger.info("Experimment stopped.");
						System.exit(0);
					}

					if (this.lastCheckPoint != 0 && (System.currentTimeMillis() - this.lastCheckPoint) >= 60000) {
						minuteCnt += 1;
						
							

							this.lastCheckPoint = System.currentTimeMillis();
							cw.write(minuteCnt + "");
							for (String qid : this.qList) {
								double latency = 0.0;
								for (long l : this.latencyMap.get(qid))
									latency += l;
								latency = (latency + 0.0) / (this.latencyMap.get(qid).size() + 0.0);
								cw.write(latency + "");

							}
							// for (String qid : this.qList)
							// cw.write((this.resultCntMap.get(qid) / (this.duplicates + 0.0)) + "");
							double memory = 0.0;
							for (double m : this.memoryList)
								memory += m;
							memory = memory / (this.memoryList.size() + 0.0);
							cw.write(memory + "");
							cw.endRecord();
							cw.flush();
							logger.info("Results logged.");

							// empty memory and latency lists
							this.memoryList.clear();
							for (Entry<String, List<Long>> en : this.latencyMap.entrySet()) {
								en.getValue().clear();
							}
						}
						
						
					

					Map<String, Double> currentLatency = new HashMap<String, Double>();
					for (String qid : this.qList) {
						double latency = 0.0;
						for (long l : this.latencyMap.get(qid))
							latency += l;
						latency = (latency + 0.0) / (this.latencyMap.get(qid).size() + 0.0);
						currentLatency.put(qid, latency);
					}

					
					System.gc();
					Runtime rt = Runtime.getRuntime();
					double usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024.0 / 1024.0;
					// double overhead = (obMapBytes + listerObIdListBytes + listenerResultListBytes) / 1024.0 / 1024.0;
					this.memoryList.add(usedMB);
					logger.info("Current performance: L - " + currentLatency + ", Cnt: " + this.resultCntMap + ", Mem - "
							+ usedMB);// + ", monitoring overhead - " + overhead);
					Thread.sleep(5000);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			//static setting
			
			long t = System.currentTimeMillis();
			boolean flag = false;
			while(!flag){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					
					for (String qid : this.qList) {
						double latency = 0.0;
						for (long l : this.latencyMap.get(qid))
							latency += l;
//						logger.info("latency = " + latency);
						if(latency != 0.0) flag =true;
						latency = (latency + 0.0) / (this.latencyMap.get(qid).size() + 0.0);
						
						if(flag){
							cw.write("");
							cw.write(latency + "");
						}
						

					}
					if(flag){
						// empty memory and latency lists
						System.gc();
						Runtime rt = Runtime.getRuntime();
						double usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024.0 / 1024.0;
						cw.write(usedMB + "");
						cw.endRecord();
						cw.flush();
						logger.info("Results logged.");

					
						
//						logger.info("Current performance: L - " + currentLatency + ", Cnt: " + this.resultCntMap + ", Mem - "
//								+ usedMB);// + ", monitoring overhead - " + overhead);
				
					}
					
					
						
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			} 
			
			this.cw.close();
			logger.info("Experimment stopped.");
			
		}
		
		
		
		System.exit(0);
	}

	private void cleanup() {
		if (SRJCityBench.csparqlEngine != null) {
			// CityBench.csparqlEngine.destroy();
			for (Object css : SRJCityBench.startedStreamObjects) {
				((CSPARQLSensorStream) css).stop();
			}
		} else if (SRJCityBench.cqelsContext != null){
			// CityBench.cqelsContext.engine().
			for (Object css : SRJCityBench.startedStreamObjects) {
				((CQELSSensorStream) css).stop();
			}
		} else {
			for (Object css : SRJCityBench.startedStreamObjects) {
				((TSReasonerSensorStream) css).stop();
			}
		}
		this.stop = true;
		System.gc();

	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public synchronized void addResults(String qid, Map<String, Long> results, int cnt) {
		if (this.resultInitTime == 0) {
			this.resultInitTime = System.currentTimeMillis();
			this.lastCheckPoint = System.currentTimeMillis();
		}
		
//		logger.info("qid " + qid);
		qid = qid.split("-")[0];
//		logger.info("qid " + qid);
		for (Entry en : results.entrySet()) {
			String obid = en.getKey().toString();
			long delay = (long) en.getValue();
//			logger.info("obid : " + obid);
//			logger.info("delay : " + delay);
//			logger.info("qList = " + (new ArrayList(this.qMap.keySet())).toString());
			this.latencyMap.get(qid).add(delay);
		}
		this.resultCntMap.put(qid, this.resultCntMap.get(qid) + cnt);
	}

	public void addResults(String id, long cnt, long latency) {
		
		this.lubm_cnt = cnt;
		this.lubm_latency = latency;
	}


}
