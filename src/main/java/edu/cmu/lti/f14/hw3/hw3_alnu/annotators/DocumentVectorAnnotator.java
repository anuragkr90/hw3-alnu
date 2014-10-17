package edu.cmu.lti.f14.hw3.hw3_alnu.annotators;

import java.util.*;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f14.hw3.hw3_alnu.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_alnu.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_alnu.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			createTermFreqVector(jcas, doc);
		}

	}

	/**
   * A basic white-space tokenizer, it deliberately does not split on punctuation!
   *
	 * @param doc input text
	 * @return    a list of tokens.
	 */

	List<String> tokenize0(String doc) {
	  List<String> res = new ArrayList<String>();
	  
	  for (String s: doc.split("\\s+"))
	    res.add(s);
	  return res;
	}

	/**
	 * 
	 * @param jcas
	 * @param doc
	 */

	private void createTermFreqVector(JCas jcas, Document doc) {

		String docText = doc.getText();
		Map<String, Integer> tks = new HashMap<String, Integer>(); 
		List<String> tokns = tokenize0(docText);
		
		for (String token : tokns){
		  
		  if(!tks.containsKey(token)){
		    tks.put(token, 1);
		  }
		  else {
		    
		    Integer fq = tks.get(token);
		    fq = fq+1;
		    tks.put(token, fq);
		    
		  }
		}
		  Collection<Token> tklist = new ArrayList<Token> ();
		  for (String st : tks.keySet()) {
		    
		    Token newtk = new Token(jcas);
		    newtk.setFrequency(tks.get(st));
		    newtk.setText(st);
		    tklist.add(newtk);
		    
		  }
		  
		  doc.setTokenList(Utils.fromCollectionToFSList(jcas, tklist));
		//TO DO: construct a vector of tokens and update the tokenList in CAS
    //TO DO: use tokenize0 from above 
		

	}

}
