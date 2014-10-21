

/* First created by JCasGen Fri Oct 11 01:58:03 EDT 2013 */
package edu.cmu.lti.f14.hw3.hw3_alnu.typesystems;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Oct 11 01:58:03 EDT 2013
 * XML source: /home/diwang/ur-workspace/hw3-alnu/src/main/resources/descriptors/typesystems/VectorSpaceTypes.xml
 *  */
public class Token extends Annotation {
  /** 
   *  
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Token.class);
  /** 
   *  
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /**   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   *  */
  protected Token() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   *  */
  public Token(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /**  */
  public Token(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /**  */  
  public Token(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
   modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: text

 
  public String getText() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.cmu.lti.f14.hw3.hw3_alnu.typesystems.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_text);}
    
 
  public void setText(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.cmu.lti.f14.hw3.hw3_alnu.typesystems.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: frequency

  
  public int getFrequency() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_frequency == null)
      jcasType.jcas.throwFeatMissing("frequency", "edu.cmu.lti.f14.hw3.hw3_alnu.typesystems.Token");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Token_Type)jcasType).casFeatCode_frequency);}
    

  public void setFrequency(int v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_frequency == null)
      jcasType.jcas.throwFeatMissing("frequency", "edu.cmu.lti.f14.hw3.hw3_alnu.typesystems.Token");
    jcasType.ll_cas.ll_setIntValue(addr, ((Token_Type)jcasType).casFeatCode_frequency, v);}    
  }

    