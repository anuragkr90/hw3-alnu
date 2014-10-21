package edu.cmu.lti.f14.hw3.hw3_alnu.casconsumers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UIMARuntimeException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f14.hw3.hw3_alnu.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_alnu.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_alnu.utils.Utils;


public class RetrievalEvaluator extends CasConsumer_ImplBase {

  /*
   * Storing Cosine Similarity, Document text, relevance value, qid and rank of each query in array lists
   * Implemented cosine similarity function two compute cosine similarity between query and documents
   * Implemented mrr computation function
   * Implemented other similarity and distance function
   */
  
  
	/** query id number **/
	public ArrayList<Integer> qIdList;

	/** query and text relevant values **/
	public ArrayList<Integer> relList;

	public ArrayList<String> docList;
	
	public ArrayList<Map<String,Integer>> fslList;
	
	public ArrayList<Double> cosSim;
	
	public ArrayList<Integer> ListRank;
	public String flwrt ;
	
	public void initialize() throws ResourceInitializationException {

		qIdList = new ArrayList<Integer>();

		relList = new ArrayList<Integer>();
		
		docList = new ArrayList<String>();
		fslList = new ArrayList<Map<String,Integer>>();
		cosSim = new ArrayList<Double>();
		ListRank = new ArrayList<Integer>();
		
		flwrt = (String)getConfigParameterValue("outputfilename");
		
		
	}

	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas =aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();
	
		if (it.hasNext()) {
			Document doc = (Document) it.next();

			//Make sure that your previous annotators have populated this in CAS
			FSList fsTokenList = doc.getTokenList();
			//ArrayList<Token>tokenList=Utils.fromFSListToCollection(fsTokenList, Token.class);

			qIdList.add(doc.getQueryID());
			relList.add(doc.getRelevanceValue());
			
			docList.add(doc.getText());
			
			ArrayList<Token> querytk = Utils.fromFSListToCollection(doc.getTokenList(), Token.class);
			/*
			 * Storing query and frequency as hashmaps
			 */
			Map<String, Integer> querymap = MakeMap(querytk);
			//Map<String, Double> queryl1 = l1norm(querymap);
			fslList.add(querymap);
			//Do something useful here

		}

	}

	/**
	 * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2.
	 * Compute the MRR metric
	 */
	private Map<String, Integer> MakeMap(Collection<Token> qToken){
	  
	  Map<String, Integer> queryret = new HashMap<String,Integer>();
	  for(Token tk : qToken){
	    
	    queryret.put(tk.getText(),tk.getFrequency());
	  }
	  return queryret;
	}
	
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);

		int id = 0;		
		for(id = 0 ; id < qIdList.size();){
		  
		  Map<String, Integer> qmap = fslList.get(id);
		  cosSim.add(1.0);
		  int nxt = id+1;
		  
		  for(nxt=id+1; nxt < qIdList.size();){
		    
		      if(qIdList.get(nxt)==qIdList.get(id)){
		        Map<String, Integer> dmap = fslList.get(nxt);
            double cosmeas = computeCosineSimilarity(qmap, dmap);
		        //double cosmeas = computeEuclidean(qmap, dmap);
            //double cosmeas = computeJacard(qmap, dmap);
		        //double cosmeas = computeSimilarity(qmap, dmap);
            cosSim.add(cosmeas);
            nxt++; 
		      }
		      else{
		        break;
		      }
		    }
		  
		  id = nxt;
		}
		
		id = 0;
		for(id=0;id < qIdList.size();) {
		ArrayList<Integer> newrklist = new ArrayList<Integer>();
		int nxt = id + 1;
		for(nxt=id+1;nxt < qIdList.size();){
		  
		  if(qIdList.get(nxt)==qIdList.get(id)){
		    newrklist.add(nxt);
		    nxt++;
		  }
		  else
		    break;
		}
		Collections.sort(newrklist, new Comparator<Integer>() {
		public int compare(Integer i1, Integer i2) {
		return cosSim.get(i1).compareTo(cosSim.get(i2));
		}
		});
		//Comparatorx<Integer> newcomp = new Comparatorx<Integer>();
		//Collections.sort(newrklist,newcomp);
		
		Collections.reverse(newrklist);
		for(int k = 0; k < newrklist.size(); k++) {
		if(relList.get(newrklist.get(k)) == 1) {
		ListRank.add(k + 1);
		break;
		}
		}
		id = nxt;
		}
		//System.out.println(ListRank);
		//System.out.println(cosSim);
		//System.out.println(cosSim.size());
		double metric_mrr = compute_mrr();
		System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
		//System.out.println(cosSim.size());
		//System.out.println(fslList);
		//System.out.println(ListRank.size());
		//System.out.println(qIdList);
		//System.out.println(docList);
		//System.out.println(docList.size());
		
		ArrayList<Double> finalcos;
		finalcos = new ArrayList<Double>();
		
		ArrayList<Integer> finalqid;
    finalqid = new ArrayList<Integer>();
    
    ArrayList<Integer> finalrel;
    finalrel = new ArrayList<Integer>();
    
    ArrayList<String> finalstr;
    finalstr = new ArrayList<String>();
    
		for(int i=0;i<relList.size();i++){
		  
		  if(relList.get(i)==1){
		   
		    finalcos.add(cosSim.get(i));
		    finalqid.add(qIdList.get(i));
		    finalrel.add(relList.get(i));
		    finalstr.add(docList.get(i));
		  }
		}
		
		//System.out.println("Start");
		//for(int i=0;i<cosSim.size();i++){
		  
		//  System.out.println(String.format("%.4f",cosSim.get(i)) + " " + qIdList.get(i) + " "+ relList.get(i) + " " + docList.get(i));
		//}
		
		
		//System.out.println(finalcos);
		//System.out.println(finalrel);
		//System.out.println(finalqid);
		//System.out.println(finalstr);
		FileWriter wrtf = null;
		try {
		  wrtf = new FileWriter(flwrt, false);
		  } catch (IOException e) {
		  throw new UIMARuntimeException(e);
		  }
		
		  for(int i=0;i < finalcos.size();i++){
		   
		    wrtf.write("cosine="+String.format("%.4f",finalcos.get(i))+ "\t"+"rank="+ListRank.get(i)+"\t"+"qid="+finalqid.get(i)+"\t"+"rel="+finalrel.get(i)+"\t"+finalstr.get(i)+"\n");
		    
		  }
		  wrtf.write(String.format("MRR=%.4f\n", metric_mrr));
		  wrtf.close();
		  
		
		
	}

	/**
	 * 
	 * @return cosine_similarity
	 */
	//private class Comparatorx<Integer> {
	//  public int compare(Integer i1, Integer i2){
	//    return cosSim.get((java.lang.Integer) i1).compareTo(cosSim.get((java.lang.Integer) i2));
	//  }
	//}
	private double computeCosineSimilarity(Map<String, Integer> queryVector,
			Map<String, Integer> docVector) {
		double cosine_similarity=0.0;

		/*
		 * Cosine Similarity function
		 */
		
		// TODO :: compute cosine similarity between two sentences
		Map<String,Double> queryl1 = new HashMap<String,Double>();
		Map<String,Double> docl1 = new HashMap<String,Double>();
		
		Map<String,Double> docl2 = new HashMap<String,Double>();
		Map<String,Double> queryl2 = new HashMap<String,Double>();
		
		queryl1 = l1norm(queryVector);
		docl1 = l1norm(docVector);
		
		queryl2 = l2norm(queryl1);
		docl2 = l2norm(docl1);
		
		//queryl2 = l2normint(queryVector);
		//docl2 = l2normint(docVector);
		
		double currq = 0.0;
		for(String st:queryl2.keySet()){
		  
		  currq = queryl2.get(st);
		  if(docl2.containsKey(st)){
		    cosine_similarity = cosine_similarity + currq * (docl2.get(st));
		  }
		  
		}
		//cosine_similarity = Math.round((cosine_similarity * 10000))/10000.0;
		return cosine_similarity;
	}

	
	 private double computeJacard(Map<String, Integer> queryVector,
	         Map<String, Integer> docVector) {
	       double cosine_similarity=0.0;

	       /*
	        *Jacard Similarity
	        */
	       
	       // TODO :: compute cosine similarity between two sentences
	       Map<String,Double> queryl1 = new HashMap<String,Double>();
	       Map<String,Double> docl1 = new HashMap<String,Double>();
	       
	       Map<String,Double> docl2 = new HashMap<String,Double>();
	       Map<String,Double> queryl2 = new HashMap<String,Double>();
	       
	       queryl1 = l1norm(queryVector);
	       docl1 = l1norm(docVector);
	       
	       docl2 = docl1;
	       queryl2 = queryl1;
	       
	       //queryl2 = l2norm(queryl1);
	       //docl2 = l2norm(docl1);
	       
	       //queryl2 = l2normint(queryVector);
	       //docl2 = l2normint(docVector);
	       double nume = 0.0;
	       double denom = 0.0;
	       for(String st:queryl2.keySet()){
	         
	         double currq = queryl2.get(st);
	         if(docl2.containsKey(st)){
	           nume = nume + Math.min(currq, docl2.get(st));
	           denom = denom + Math.max(currq, docl2.get(st));
	         }
	         
	       }
	       if(denom!=0.0){
	         cosine_similarity = nume/denom;
	       }
	       
	       return cosine_similarity;
	     }
	
	 private double computeSimilarity(Map<String, Integer> queryVector,
           Map<String, Integer> docVector) {
         double cosine_similarity=0.0;

         /*
          * Histogram Similarity-- similar to jacard 
          * 
          */
         
         // TODO :: compute cosine similarity between two sentences
         Map<String,Double> queryl1 = new HashMap<String,Double>();
         Map<String,Double> docl1 = new HashMap<String,Double>();
         
         Map<String,Double> docl2 = new HashMap<String,Double>();
         Map<String,Double> queryl2 = new HashMap<String,Double>();
         
         queryl1 = l1norm(queryVector);
         docl1 = l1norm(docVector);
         
         docl2 = docl1;
         queryl2 = queryl1;
         
         //queryl2 = l2norm(queryl1);
         //docl2 = l2norm(docl1);
         
         //queryl2 = l2normint(queryVector);
         //docl2 = l2normint(docVector);
         
         for(String st:queryl2.keySet()){
           
           double currq = queryl2.get(st);
           if(docl2.containsKey(st)){
             cosine_similarity = cosine_similarity + Math.min(currq, docl2.get(st));
           }
           
         } 
         return cosine_similarity;
       }
	
	private double computeEuclidean(Map<String, Integer> queryVector,
	        Map<String, Integer> docVector) {
	      double cosine_similarity=0.0;

	      /*
	       * Euclidean distance -- since this is a distance shorting has to be done in ascending order
	       * 
	       */
	      
	      // TODO :: compute cosine similarity between two sentences
	      Map<String,Double> queryl1 = new HashMap<String,Double>();
	      Map<String,Double> docl1 = new HashMap<String,Double>();
	      
	      Map<String,Double> docl2 = new HashMap<String,Double>();
	      Map<String,Double> queryl2 = new HashMap<String,Double>();
	      
	      queryl1 = l1norm(queryVector);
	      docl1 = l1norm(docVector);
	      
	      queryl2 = queryl1;
	      docl2 = docl1;
	      
	      //queryl2 = l2normint(queryVector);
	      //docl2 = l2normint(docVector);
	      
	      double currq = 0.0;
	      for(String st:queryl2.keySet()){
	        
	        currq = queryl2.get(st);
	        if(docl2.containsKey(st)){
	          cosine_similarity = cosine_similarity + (currq-(docl2.get(st)))*(currq-(docl2.get(st)));
	          //cosine_similarity = cosine_similarity + ((currq-(docl2.get(st)))*(currq-(docl2.get(st))))/(currq+(docl2.get(st)));
	        }
	        
	      }
	      //cosine_similarity = Math.round((cosine_similarity * 10000))/10000.0;
	      return cosine_similarity;
	    }


	private Map<String,Double> l1norm(Map<String, Integer> queryVector){
	  /*
	   * function for L1 normalizing of vector
	   */
	  Double fsum =0.0;
	  Map<String,Double> finalret = new HashMap<String,Double>();
	  
	  for(String tk : queryVector.keySet()){
	    
	    fsum = fsum + queryVector.get(tk); 
	    
	  }
	  
	  for (String tk : queryVector.keySet()){
	    
	    Integer fqnorm = queryVector.get(tk);
	    finalret.put(tk, fqnorm/fsum);
	  }
	 return finalret; 
	}
	 private Map<String,Double> l2norm(Map<String, Double> queryVector){
	   /*
	    * function for L1 normalizing of vector
	    */
	    Double fsum =0.0;
	    Map<String,Double> finalret = new HashMap<String,Double>();
	    
	    for(String tk : queryVector.keySet()){
	      
	      fsum = fsum + (queryVector.get(tk)*queryVector.get(tk)); 
	      
	    }
	    
	    for (String tk : queryVector.keySet()){
	      
	      Double fqnorm = queryVector.get(tk);
	      finalret.put(tk, fqnorm/Math.sqrt(fsum));
	    }
	   return finalret; 
	  }
	 
   private Map<String,Double> l2normint(Map<String, Integer> queryVector){
     
     Double fsum =0.0;
     Map<String,Double> finalret = new HashMap<String,Double>();
     
     for(String tk : queryVector.keySet()){
       
       fsum = fsum + (queryVector.get(tk)*queryVector.get(tk)); 
       
     }
     
     for (String tk : queryVector.keySet()){
       
       int fqnorm = queryVector.get(tk);
       finalret.put(tk, fqnorm/Math.sqrt(fsum));
     }
    return finalret; 
   }
	/**
	 * 
	 * @return mrr
	 */
	private double compute_mrr() {
		double metric_mrr=0.0;

		/*
		 * MRR computation function
		 */
		
		// TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
		int ln = ListRank.size() ;
		double invrank = 0.0;
    for(int i = 0; i < ln ; i++) {
    invrank += 1.0/ListRank.get(i);
    }
    metric_mrr = (1.0/ln)*invrank;
    // TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
    //metric_mrr = Math.round(metric_mrr*10000)/10000.0;
    return metric_mrr;
		
	}

}
