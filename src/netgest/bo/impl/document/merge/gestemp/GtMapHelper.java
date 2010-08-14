package netgest.bo.impl.document.merge.gestemp;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boEvent;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
public class GtMapHelper 
{
    public GtMapHelper()
    {
    }
    public static boolean onBeforeSave(boObject mapObj) throws boRuntimeException
    {
        boObject temp = mapObj.getParent();
        boObject tempAtrib = mapObj.getAttribute("atributo").getObject();
        boObject clf = mapObj.getAttribute("classificacao").getObject();
        boObject clfAtrib = mapObj.getAttribute("atribClf").getObject();
        
        //têm que estar preenchidos todos preenchidos
        if(temp == null || tempAtrib == null || clf == null || clfAtrib == null)
        {
            mapObj.addErrorMessage(Messages.getString("GtMapHelper.3"));
            return false;
        }
        
        //vou validar se o atributo é campo do template
        if("GESTEMP_Query".equals(tempAtrib.getName())|| 
            "GESTEMP_JavaQuery".equals(tempAtrib.getName()))
        {
            boBridgeIterator bit = temp.getBridge("queries").iterator();
            bit.beforeFirst();
            boolean found = false;
            while(bit.next() && !found)
            {
                if(bit.currentRow().getValueLong() == tempAtrib.getBoui())
                {
                    found = true;
                    String aux = null;
                    if((aux = validType(clfAtrib, tempAtrib)) != null)
                    {
                        mapObj.addErrorMessage(aux);
                        return false;
                    }
                }
            }
            if(!found)
            {
                mapObj.addErrorMessage(Messages.getString("GtMapHelper.7"));
                return false;
            }
        }
        else if("GESTEMP_CampoManual".equals(tempAtrib.getName()) ||
               "GESTEMP_CampoNManual".equals(tempAtrib.getName())
        )
        {
            boBridgeIterator bit = temp.getBridge("camposManuais").iterator();
            bit.beforeFirst();
            boolean found = false;
            while(bit.next() && !found)
            {
                if(bit.currentRow().getValueLong() == tempAtrib.getBoui())
                {
                    found = true;
                    String aux = null;
                    if((aux = validType(clfAtrib, tempAtrib)) != null)
                    {
                        mapObj.addErrorMessage(aux);
                        return false;
                    }
                }
            }
            if(!found)
            {
                mapObj.addErrorMessage(Messages.getString("GtMapHelper.7"));
                return false;
            }
        }
        else
        {
            String helper = mapObj.getAttribute("helper").getValueString();
            if(helper != null && !"".equals(helper))
            {
                String path[] = helper.split("\\.");
                if(path.length == 2 || path.length == 3)
                {
                    boBridgeIterator bit = temp.getBridge("queries").iterator();
                    bit.beforeFirst();
                    boolean foundQ = false, foundC = false;
                    while(bit.next() && !foundQ)
                    {
                        boObject querie = bit.currentRow().getObject();
                        if(path[0].equals(querie.getAttribute("nome").getValueString()))
                        {
                            foundQ = true;
                            //agora vou andar nos campos da querie
                            boBridgeIterator bit2 = querie.getBridge("campos").iterator();
                            bit2.beforeFirst();
                            while(bit2.next() && !foundC)
                            {
                                boObject campo = bit2.currentRow().getObject();
                                String s = campo.getAttribute("nome").getValueString();
                                if(path[1].equals(campo.getAttribute("nome").getValueString()))
                                {
                                    if(path.length == 2)
                                    {
                                        foundC = true;
                                        String aux = null;
                                        if((aux = validType(clfAtrib, tempAtrib)) != null)
                                        {
                                            mapObj.addErrorMessage(aux);
                                            return false;
                                        }
                                    }
                                    else
                                    {
                                        boBridgeIterator bit3 = campo.getBridge("campos").iterator();
                                        bit3.beforeFirst();
                                        while(bit3.next() && !foundC)
                                        {
                                            campo = bit3.currentRow().getObject();
                                            s = campo.getAttribute("nome").getValueString();
                                            if(path[2].equals(campo.getAttribute("nome").getValueString()))
                                            {
                                                foundC = true;
                                                String aux = null;
                                                if((aux = validType(clfAtrib, tempAtrib)) != null)
                                                {
                                                    mapObj.addErrorMessage(aux);
                                                    return false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(!foundQ || !foundC)
                    {
                        mapObj.addErrorMessage(Messages.getString("GtMapHelper.23"));
                        return false;
                    }
                }
                else
                {
                    mapObj.addErrorMessage(Messages.getString("GtMapHelper.24"));
                    return false;
                }
            }
            else
            {
                mapObj.addErrorMessage(Messages.getString("GtMapHelper.24"));
                return false;
            }
        }
        return true;
    }
    
    private static String validType(boObject clfAtrib, boObject tempAtrib) throws boRuntimeException
    {
        if("GESDocClfNumber".equals(clfAtrib.getName()))
        {
            //moeda
            boolean moeda = "1".equals(clfAtrib.getAttribute("currency").getValueString());
            if("GESTEMP_CampoFormula".equals(tempAtrib.getName()) || 
               "GESTEMP_CampoNFormula".equals(tempAtrib.getName()) ||
               "GESTEMP_CampoJava".equals(tempAtrib.getName()) ||
               "GESTEMP_CampoManual".equals(tempAtrib.getName()))
            {
                String tipo = tempAtrib.getAttribute("tipo").getValueString();
                if(moeda)
                {
                    if(!"2".equals(tipo))
                    {
                        return Messages.getString("GtMapHelper.35");
                    }
                }
                else
                {
                    if(!"3".equals(tipo))
                    {
                        return Messages.getString("GtMapHelper.35");
                    }
                }
            }
            else if("GESTEMP_CampoObjecto".equals(tempAtrib.getName())||
                "GESTEMP_CampoNObjecto".equals(tempAtrib.getName()))
            {
                boObject att = tempAtrib.getAttribute("atributo").getObject();
                String defName = att.getAttribute("clsReg").getObject().getAttribute("name").getValueString();
                String attName = att.getAttribute("name").getValueString();
                boDefHandler def = boDefHandler.getBoDefinition(defName);
                boDefAttribute attDef = def.getAttributeRef(attName);
                if(moeda)
                {
                    if(attDef.getValueType() != boDefAttribute.VALUE_CURRENCY)
                    {
                        return Messages.getString("GtMapHelper.35");
                    }
                }
                else
                {
                    if(attDef.getValueType() != boDefAttribute.VALUE_NUMBER)
                    {
                        return Messages.getString("GtMapHelper.35");
                    }
                }
            }
            else if("GESTEMP_Query".equals(tempAtrib.getName()) || 
                    "GESTEMP_JavaQuery".equals(tempAtrib.getName())||
                    "GESTEMP_CampoNJava".equals(tempAtrib.getName())
                )
            {
                return Messages.getString("GtMapHelper.35");
            }
        }
        else if("GESDocClfText".equals(clfAtrib.getName()))
        {
            //se fôr texto vai aceitar, pq vai converter            
            return null;
        }
        else if("GESDocClfObject".equals(clfAtrib.getName()))
        {
            //só pode aceitar boObject do mesmo tipo
            String defName = clfAtrib.getAttribute("objecto").getObject().getAttribute("name").getValueString();
            if("GESTEMP_CampoObjecto".equals(tempAtrib.getName())||
              "GESTEMP_CampoNObjecto".equals(tempAtrib.getName()))
            {
                boObject att = tempAtrib.getAttribute("atributo").getObject();
                String defName2 = att.getAttribute("clsReg").getObject().getAttribute("name").getValueString();
                String attName =  att.getAttribute("name").getValueString();
                boDefHandler def = boDefHandler.getBoDefinition(defName2);
                boDefAttribute attDef = def.getAttributeRef(attName);
                
                if(def.getAttributeType(attName) != boDefAttribute.TYPE_OBJECTATTRIBUTE)
                {
                    return Messages.getString("GtMapHelper.35");
                }                
                if(!defName.equals(attDef.getReferencedObjectName()))
                {
                    return Messages.getString("GtMapHelper.35");
                }
            }
            else if("GESTEMP_Query".equals(tempAtrib.getName()) || 
                    "GESTEMP_JavaQuery".equals(tempAtrib.getName())||
                    "GESTEMP_CampoNJava".equals(tempAtrib.getName())
                )
            {
                boObject parametro = tempAtrib.getAttribute("parametro").getObject();
                String defName2 = parametro.getAttribute("objecto").getObject().getAttribute("name").getValueString();
                if(!defName.equals(defName2))
                {
                    return Messages.getString("GtMapHelper.35");
                }
            }
            else
            {
                return Messages.getString("GtMapHelper.35");
            }
        }
        else if("GESDocClfDate".equals(clfAtrib.getName()))
        {
            //data e tempo
            if("GESTEMP_CampoFormula".equals(tempAtrib.getName()) ||
               "GESTEMP_CampoNFormula".equals(tempAtrib.getName()) ||
               "GESTEMP_CampoJava".equals(tempAtrib.getName()) ||
               "GESTEMP_CampoManual".equals(tempAtrib.getName()))
            {
                String tipo = tempAtrib.getAttribute("tipo").getValueString();
                if(!"6".equals(tipo) && !"4".equals(tipo))
                {
                    return Messages.getString("GtMapHelper.35");
                }
            }
            else if("GESTEMP_CampoObjecto".equals(tempAtrib.getName())||
                    "GESTEMP_CampoNObjecto".equals(tempAtrib.getName()))
            {
                boObject att = tempAtrib.getAttribute("atributo").getObject();
                String defName = att.getAttribute("clsReg").getObject().getAttribute("name").getValueString();
                String attName = att.getAttribute("name").getValueString();
                boDefHandler def = boDefHandler.getBoDefinition(defName);
                boDefAttribute attDef = def.getAttributeRef(attName);
                if(attDef.getValueType() != boDefAttribute.VALUE_DATE && 
                    attDef.getValueType() != boDefAttribute.VALUE_DATETIME)
                {
                    return Messages.getString("GtMapHelper.35");
                }
            }
            else if("GESTEMP_Query".equals(tempAtrib.getName()) || 
                    "GESTEMP_JavaQuery".equals(tempAtrib.getName())||
                    "GESTEMP_CampoNJava".equals(tempAtrib.getName())
                )
            {
                return Messages.getString("GtMapHelper.35");
            }
        }
        else if("GESDocClfLov".equals(clfAtrib.getName()))
        {
            boObject lov = clfAtrib.getAttribute("lov").getObject();
            if("GESTEMP_CampoFormula".equals(tempAtrib.getName()) ||
               "GESTEMP_CampoNFormula".equals(tempAtrib.getName()) ||
               "GESTEMP_CampoJava".equals(tempAtrib.getName()) ||
               "GESTEMP_CampoManual".equals(tempAtrib.getName()))
            {
                String tipo = tempAtrib.getAttribute("tipo").getValueString();
                if(!"7".equals(tipo) && !"1".equals(tipo))
                {
                    return Messages.getString("GtMapHelper.35");
                }
            }
            else if("GESTEMP_CampoObjecto".equals(tempAtrib.getName())||
                    "GESTEMP_CampoNObjecto".equals(tempAtrib.getName()))
            {
                boObject att = tempAtrib.getAttribute("atributo").getObject();
                String defName = att.getAttribute("clsReg").getObject().getAttribute("name").getValueString();
                String attName = att.getAttribute("name").getValueString();
                boDefHandler def = boDefHandler.getBoDefinition(defName);
                boDefAttribute attDef = def.getAttributeRef(attName);
                if(attDef.getValueType() != boDefAttribute.VALUE_CHAR)
                {
                    return Messages.getString("GtMapHelper.35");
                }
            }
            else if("GESTEMP_Query".equals(tempAtrib.getName()) || 
                    "GESTEMP_JavaQuery".equals(tempAtrib.getName()) ||
                    "GESTEMP_CampoNJava".equals(tempAtrib.getName())
                 )
            {
                return Messages.getString("GtMapHelper.35");
            }
        }
        return null;
    }
    
    public static boolean validBridge(boEvent event, boObject obj) throws boRuntimeException
    {
        boBridgeIterator bit = obj.getBridge("mapeamentos").iterator();
        bit.beforeFirst();
        while(bit.next())
        {
            if(!onBeforeSave(bit.currentRow().getObject()))
            {
                obj.addErrorMessage(Messages.getString("GtMapHelper.112"));
                return false;
            }
        }
        return true;
    }
}