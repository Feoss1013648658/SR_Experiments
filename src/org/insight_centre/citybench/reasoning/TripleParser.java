package org.insight_centre.citybench.reasoning;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import org.insight_centre.aceis.io.rdf.RDFFileManager;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

public class TripleParser {

	public static void main(String[] args) {
		String fileTriplesName = RDFFileManager.datasetDirectory + "SensorRepository.n3";
		String fileAtomsName = RDFFileManager.datasetDirectory + "SensorRepository.lp";
		parseTriplesToAtoms(fileTriplesName, fileAtomsName);
//		
		
//		String fileTriplesName1 = RDFFileManager.datasetDirectory + "AarhusLibraryEvents.n3";
//		String fileAtomsName1 = RDFFileManager.datasetDirectory + "AarhusLibraryEvents.lp";
//		parseTriplesToAtoms(fileTriplesName1, fileAtomsName1);
//		
//		String fileTriplesName2 = RDFFileManager.datasetDirectory + "AarhusCulturalEvents.n3";
//		String fileAtomsName2 = RDFFileManager.datasetDirectory + "AarhusCulturalEvents.lp";
//		parseTriplesToAtoms(fileTriplesName2, fileAtomsName2);
		

	}
	
	
	public static void parseTriplesToAtoms(String fileTriplesName, String fileAtomsName){
		Model model = FileManager.get().loadModel(fileTriplesName);
		SimpleSelector selector = new SimpleSelector(null, null, (RDFNode)null) {
		    public boolean selects(Statement s)
		        { return true; }
		};
		
		
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			fw = new FileWriter(fileAtomsName);
			bw = new BufferedWriter(fw);
			
			StmtIterator iter = model.listStatements(selector);
			while(iter.hasNext()) {
			   Statement stmt = iter.nextStatement();
			   String p = replacePrefixtoUrl(stmt.getPredicate().toString());
			   String s = replacePrefixtoUrl(stmt.getSubject().toString());
			   String o = replacePrefixtoUrl(stmt.getObject().toString());
			   System.out.print( s + "\t");
			   System.out.print(p + "\t");
			   System.out.println(o);
			   
			   StringBuilder sb = new StringBuilder();
			   sb.append(p.toString()).append("(\"").append(s).append("\",\"").append(o).append("\").");
			   
			   
//			   if(check(sb.toString())){
				   System.out.println ("Atom : " + sb.toString());
				   bw.write(sb.toString() + "\n");
//			   }
			   
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}
		

		
	}
	
	private static boolean check(String p) {
		if (p.contains("type") && p.contains("CongestionLevel")){
			return true;
		}else if(p.contains("observedBy") || p.contains("observedProperty") || p.contains("hasValue")) 
			return true;
		return false;
	}


	public static String replacePrefixtoUrl(String p){
		String temp=null;
		for (Entry<String, String> entry : RDFFileManager.prefixMap.entrySet())
		{
		   temp = p.replace(entry.getValue(), entry.getKey()+"_");
		   if(!temp.equals(p)) break;
		}
		
		//fix bug if p has " 
		if(temp.contains("\"")){
			temp = temp.replaceAll("\"", "%%%");
		}
		return temp;
	}

}
