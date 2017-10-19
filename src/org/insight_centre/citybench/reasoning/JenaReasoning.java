package org.insight_centre.citybench.reasoning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import com.csvreader.CsvWriter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

public class JenaReasoning {

	public static void main(String[] args) throws MalformedURLException {
		
		
		
		
		CsvWriter cw;
		String outputFileStr = "jena_lubm";
		try {
			File outputFile = new File("result_log" + File.separator + outputFileStr + "_" + new Date(System.currentTimeMillis()) + ".csv");
			cw = new CsvWriter(new FileWriter(outputFile, true), ',');
			cw.write("winSize");
			cw.write("latency");
			cw.write("memory");
			cw.endRecord();
			
			
			int[] winSizes = new int[]{5000, 10000, 30000, 60000, 100000};
//			int[] winSizes = new int[]{60000};
			
			String ruleFile = "ruleset/lubm_jena.txt";
			for(int w : winSizes){
				
				System.out.println("Testing w = " + w);
				String dataFile = "dataset/jena_lubm_" + w + ".rdf";
				jenaCreateDataset("streams/Universities-1.stream",w, dataFile);
				
				cw.write(w + "");
				long st = System.currentTimeMillis();
				jenaReasoning(dataFile, ruleFile);
				long et = System.currentTimeMillis();
				
				cw.write((et-st) + "");
			
				Runtime rt = Runtime.getRuntime();
				double usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024.0 / 1024.0;
				
				cw.write(usedMB + "");
				cw.endRecord();
				System.gc();
				System.out.println("DONE TEST " + w);
			}
			
			cw.close();
			System.out.println("DONE TESTS!!!");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
				

	

	}
	

	
	public static void jenaReasoning(String fileName, String ruleFile) throws MalformedURLException{
		Model model = ModelFactory.createDefaultModel();
		model.read( new File(fileName).toURL().toString(),"N-TRIPLES");
//		model.write(System.out, "N3");
		
//		System.out.println("\n\n");
		
		Reasoner reasoner = new GenericRuleReasoner( Rule.rulesFromURL( ruleFile ) );
		reasoner.setDerivationLogging(true);
		
		InfModel infModel = ModelFactory.createInfModel( reasoner, model );
		
		infModel.write(System.out, "N3");
 

							
	}
	
	public static void jenaCreateDataset(String fileName, int size, String outputFile){
		BufferedReader br = null;
		FileReader fr = null;
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		Model model = ModelFactory.createDefaultModel();

		
		model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		model.setNsPrefix("uniben", "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#");
		model.setNsPrefix("lubm", "http://lubm_example.org#");

		try {
			
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			
			
//			fw = new FileWriter(outputFile);
//			bw = new BufferedWriter(fw);

			String sCurrentLine;
			int count = 0;
	    
			
			while ((sCurrentLine = br.readLine()) != null ) {
				
				
				if(sCurrentLine.contains("<http://www.w3.org/2002/07/owl#Ontology>") ||
						sCurrentLine.contains("<http://www.w3.org/2002/07/owl#imports>"))
						continue;
					
		
					
					
					if(model.listStatements().toList().size() > size) break;
					
					
					String[] spo = sCurrentLine.split(" ");
					String s = spo[0].replace("<", "").replace(">", "");
					String p =  spo[1].replace("<", "").replace(">", "");
					String o =  spo[2].replace("<", "").replace(">", "").replaceAll("\"", "");
					
							

//					Resource rs=model.createResource(s); 
					Property rp=model.createProperty(p); 
					if(spo[2].contains("<")){
						Resource ro=model.createResource(o); 
						model.createResource(s).addProperty(rp, ro);
						count ++;
					}else{
//						rs.addProperty(rp, o);
						
						model.createResource(s).addProperty(rp, o);
						count ++;
					}
					
					
					
					
			         
					
//					bw.write(sCurrentLine + "\n");
//				
//					bw.flush();
	
			}
			System.out.println("COUNT = " + count);
//			 model.write(new FileOutputStream(outputFile), "RDF/XML");
			 model.write(new FileOutputStream(outputFile), "N-TRIPLES");
			
//			// list the statements in the Model
//			StmtIterator iter = model.listStatements();
//
//			// print out the predicate, subject and object of each statement
//			while (iter.hasNext()) {
//			    Statement stmt      = iter.nextStatement();  // get next statement
//			    Resource  subject   = stmt.getSubject();     // get the subject
//			    Property  predicate = stmt.getPredicate();   // get the predicate
//			    RDFNode   object    = stmt.getObject();      // get the object
//
//			    System.out.print(subject.toString());
//			    System.out.print(" " + predicate.toString() + " ");
//			    if (object instanceof Resource) {
//			       System.out.print(object.toString());
//			    } else {
//			        // object is a literal
//			        System.out.print(" \"" + object.toString() + "\"");
//			    }
//
//			    System.out.println(" .");
//			} 
			
//			model.write(System.out, "RDF/XML");


		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();
//				
//				if (bw != null)
//					bw.close();
//
//				if (fw != null)
//					fw.close();
				
				
				

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
		
//		return model;
	
			
	}

}
