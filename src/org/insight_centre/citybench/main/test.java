package org.insight_centre.citybench.main;
import java.io.File;
import java.net.MalformedURLException;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.reasoner.rulesys.*;
public class test {

	public static void main(String[] args) {
		 // Create an empty model.
        Model model = ModelFactory.createDefaultModel();

        // Read the RDF/XML on standard in.
        try {
			model.read(new File("dataset/jena_lubm10.n3").toURL().toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Create a simple RDFS++ Reasoner.
        StringBuilder sb = new StringBuilder();
        sb.append("[rdfs2:   (?x ?p ?y), (?p rdfs:domain ?c) -> (?x rdf:type ?c)] ");
        sb.append("[rdfs3:   (?x ?p ?y), (?p rdfs:range ?c) -> (?y rdf:type ?c)] ");

        sb.append("[rdfs6:   (?a ?p ?b), (?p rdfs:subPropertyOf ?q) -> (?a ?q ?b)] ");
        sb.append("[rdfs5:   (?x rdfs:subPropertyOf ?y), (?y rdfs:subPropertyOf ?z) -> (?x rdfs:subPropertyOf ?z)] ");

        sb.append("[rdfs9:   (?x rdfs:subClassOf ?y), (?a rdf:type ?x) -> (?a rdf:type ?y)] ");
        sb.append("[rdfs11:  (?x rdfs:subClassOf ?y), (?y rdfs:subClassOf ?z) -> (?x rdfs:subClassOf ?z)] ");

        sb.append("[owlinv:  (?x ?p ?y), (?p owl:inverseOf ?q) -> (?y ?q ?x)] ");
        sb.append("[owlinv2: (?p owl:inverseOf ?q) -> (?q owl:inverseOf ?p)] ");

        sb.append("[owltra:  (?x ?p ?y), (?y ?p ?z), (?p rdf:type owl:TransitiveProperty) -> (?x ?p ?z)] ");

        sb.append("[owlsam:  (?x ?p ?y), (?x owl:sameAs ?z) -> (?z ?p ?y)] ");
        sb.append("[owlsam2: (?x owl:sameAs ?y) -> (?y owl:sameAs ?x)] ");

        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(sb.toString()));

        // Create inferred model using the reasoner and write it out.
        InfModel inf = ModelFactory.createInfModel(reasoner, model);
        inf.write(System.out);

	}
	
	

}
