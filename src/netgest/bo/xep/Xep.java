/*Enconding=UTF-8*/
package netgest.bo.xep;

import bsh.Primitive;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boEvent;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;

import netgest.xwf.common.xwfBoManager;
import netgest.xwf.core.xwfECMAevaluator;
import netgest.xwf.core.xwfECMAparser;

public class Xep 
{
  private String xep_code;
  private xwfECMAevaluator xep_eval;
  
  public Xep()
  {
    xep_code = "";
    xep_eval = new xwfECMAevaluator();
  }
  
  public void addCode(String code)
  {
    xep_code = code + "\n";
  }
  
  public void addVariable(String var_name, Class var_class, Object var_value, String xep_type) throws boRuntimeException
  {
    xep_eval.setVariable(var_name, var_class, var_value, xep_type);
  }
  
  public void addImportClass(String class_path)
  {
    xep_eval.addImportClass(class_path);
  }
  
  public void addImportPackage(String package_path)
  {
    xep_eval.addImportClass(package_path);
  }
  
  public void addIntVariable(String var_name, int var_value)throws boRuntimeException
  {
    
    addVariable(var_name, null, new Primitive(var_value), xwfECMAparser.TYPE_INT);
  }
  
  public void addLongVariable(String var_name, long var_value)throws boRuntimeException
  {
    addVariable(var_name, null, new Primitive((int)var_value), xwfECMAparser.TYPE_INT);
  }
  
  public void addDoubleVariable(String var_name, double var_value)throws boRuntimeException
  {
    addVariable(var_name, null, new Primitive(var_value), xwfECMAparser.TYPE_DOUBLE);
  }
  
  public void addBoolVariable(String var_name, boolean var_value)throws boRuntimeException
  {
    addVariable(var_name, null, new Primitive(var_value), xwfECMAparser.TYPE_BOOL);
  }
  
  public void addBoObjectVariable(String var_name, boObject var_value)throws boRuntimeException
  {
    addVariable(var_name, boObject.class, var_value, var_value.getName());
  }
  
  public Object eval(EboContext ctx)throws boRuntimeException
  {
    xwfBoManager xm = new xwfBoManager(ctx, null);
    return xep_eval.eval(xm, getCode());
  }
  
  public String evalToString(EboContext ctx)throws boRuntimeException
  {
    Object xo = eval(ctx);
    if(xo != null)
      return xo.toString();
    else
      return null;
  }
  
  public Object getVariable( String name )
  {
    return xep_eval.getVariable( name );
  }
  
  public String getCode()
  {
    return xep_code;
  }
  
  public void clearCode()
  {
    xep_code = "";
  }
  
}