/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.symbol;
import java.util.ArrayList;
import netgest.bo.parser.modifiers.Modifier;
import netgest.bo.parser.modifiers.PrimitiveModifier;
/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class ModifierClause implements ClauseDeclarationSymbol {
    ArrayList classes = new ArrayList();
  /**
   * Constructor
   */
  public ModifierClause() {
  }

  public void setModifier(String e){
    if(e.equals("abstract"))
        classes.add(PrimitiveModifier.ABSTRACT);
    else if(e.equals("final"))
        classes.add(PrimitiveModifier.FINAL);
    else if(e.equals("interface"))
        classes.add(PrimitiveModifier.INTERFACE);
    else if(e.equals("native"))
        classes.add(PrimitiveModifier.NATIVE);
    else if(e.equals("private"))
        classes.add(PrimitiveModifier.PRIVATE);
    else if(e.equals("protected"))
        classes.add(PrimitiveModifier.PROTECTED);
    else if(e.equals("public"))
        classes.add(PrimitiveModifier.PUBLIC);
    else if(e.equals("static"))
        classes.add(PrimitiveModifier.STATIC);
    else if(e.equals("strict"))
        classes.add(PrimitiveModifier.STRICT);
    else if(e.equals("synchronized"))
        classes.add(PrimitiveModifier.SYNCHRONIZED);
    else if(e.equals("transient"))
        classes.add(PrimitiveModifier.TRANSIENT);
    else if(e.equals("volatile"))
        classes.add(PrimitiveModifier.VOLATILE);
  }

  public void setModifier(Modifier e){
    classes.add(e);
  }

  public ArrayList getModifiers(){
    return classes;
  }

  public int getNumModifier(){
    return classes.size();
  }

  public Modifier getModifier(int i){
    return (Modifier)classes.get(i);
  }

  public String toJAVA(){
    StringBuffer sb = new StringBuffer();

    for(int i = 0; i < classes.size(); i ++)
        sb.append(((Modifier)classes.get(i)).getName()).append(" ");


    return sb.toString();
  }
}


