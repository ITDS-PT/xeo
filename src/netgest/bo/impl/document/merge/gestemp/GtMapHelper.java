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
        boObject tempAtrib = mapObj.getAttribute("atributo").getObject(); //$NON-NLS-1$
        boObject clf = mapObj.getAttribute("classificacao").getObject(); //$NON-NLS-1$
        boObject clfAtrib = mapObj.getAttribute("atribClf").getObject(); //$NON-NLS-1$
        
        //têm que estar preenchidos todos preenchidos
        if(temp == null || tempAtrib == null || clf == null || clfAtrib == null)
        {
            mapObj.addErrorMessage(Messages.getString("GtMapHelper.3")); //$NON-NLS-1$
            return false;
        }
        
        //vou validar se o atributo é campo do template
        if("GESTEMP_Query".equals(tempAtrib.getName())||  //$NON-NLS-1$
            "GESTEMP_JavaQuery".equals(tempAtrib.getName())) //$NON-NLS-1$
        {
            boBridgeIterator bit = temp.getBridge("queries").iterator(); //$NON-NLS-1$
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
                mapObj.addErrorMessage(Messages.getString("GtMapHelper.7")); //$NON-NLS-1$
                return false;
            }
        }
        else if("GESTEMP_CampoManual".equals(tempAtrib.getName()) || //$NON-NLS-1$
               "GESTEMP_CampoNManual".equals(tempAtrib.getName()) //$NON-NLS-1$
        )
        {
            boBridgeIterator bit = temp.getBridge("camposManuais").iterator(); //$NON-NLS-1$
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
                mapObj.addErrorMessage(Messages.getString("GtMapHelper.7")); //$NON-NLS-1$
                return false;
            }
        }
        else
        {
            String helper = mapObj.getAttribute("helper").getValueString(); //$NON-NLS-1$
            if(helper != null && !"".equals(helper)) //$NON-NLS-1$
            {
                String path[] = helper.split("\\."); //$NON-NLS-1$
                if(path.length == 2 || path.length == 3)
                {
                    boBridgeIterator bit = temp.getBridge("queries").iterator(); //$NON-NLS-1$
                    bit.beforeFirst();
                    boolean foundQ = false, foundC = false;
                    while(bit.next() && !foundQ)
                    {
                        boObject querie = bit.currentRow().getObject();
                        if(path[0].equals(querie.getAttribute("nome").getValueString())) //$NON-NLS-1$
                        {
                            foundQ = true;
                            //agora vou andar nos campos da querie
                            boBridgeIterator bit2 = querie.getBridge("campos").iterator(); //$NON-NLS-1$
                            bit2.beforeFirst();
                            while(bit2.next() && !foundC)
                            {
                                boObject campo = bit2.currentRow().getObject();
                                String s = campo.getAttribute("nome").getValueString(); //$NON-NLS-1$
                                if(path[1].equals(campo.getAttribute("nome").getValueString())) //$NON-NLS-1$
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
                                        boBridgeIterator bit3 = campo.getBridge("campos").iterator(); //$NON-NLS-1$
                                        bit3.beforeFirst();
                                        while(bit3.next() && !foundC)
                                        {
                                            campo = bit3.currentRow().getObject();
                                            s = campo.getAttribute("nome").getValueString(); //$NON-NLS-1$
                                            if(path[2].equals(campo.getAttribute("nome").getValueString())) //$NON-NLS-1$
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
                        mapObj.addErrorMessage(Messages.getString("GtMapHelper.23")); //$NON-NLS-1$
                        return false;
                    }
                }
                else
                {
                    mapObj.addErrorMessage(Messages.getString("GtMapHelper.24")); //$NON-NLS-1$
                    return false;
                }
            }
            else
            {
                mapObj.addErrorMessage(Messages.getString("GtMapHelper.24")); //$NON-NLS-1$
                return false;
            }
        }
        return true;
    }
    
    private static String validType(boObject clfAtrib, boObject tempAtrib) throws boRuntimeException
    {
        if("GESDocClfNumber".equals(clfAtrib.getName())) //$NON-NLS-1$
        {
            //moeda
            boolean moeda = "1".equals(clfAtrib.getAttribute("currency").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            if("GESTEMP_CampoFormula".equals(tempAtrib.getName()) ||  //$NON-NLS-1$
               "GESTEMP_CampoNFormula".equals(tempAtrib.getName()) || //$NON-NLS-1$
               "GESTEMP_CampoJava".equals(tempAtrib.getName()) || //$NON-NLS-1$
               "GESTEMP_CampoManual".equals(tempAtrib.getName())) //$NON-NLS-1$
            {
                String tipo = tempAtrib.getAttribute("tipo").getValueString(); //$NON-NLS-1$
                if(moeda)
                {
                    if(!"2".equals(tipo)) //$NON-NLS-1$
                    {
                        return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
                    }
                }
                else
                {
                    if(!"3".equals(tipo)) //$NON-NLS-1$
                    {
                        return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
                    }
                }
            }
            else if("GESTEMP_CampoObjecto".equals(tempAtrib.getName())|| //$NON-NLS-1$
                "GESTEMP_CampoNObjecto".equals(tempAtrib.getName())) //$NON-NLS-1$
            {
                boObject att = tempAtrib.getAttribute("atributo").getObject(); //$NON-NLS-1$
                String defName = att.getAttribute("clsReg").getObject().getAttribute("name").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                String attName = att.getAttribute("name").getValueString(); //$NON-NLS-1$
                boDefHandler def = boDefHandler.getBoDefinition(defName);
                boDefAttribute attDef = def.getAttributeRef(attName);
                if(moeda)
                {
                    if(attDef.getValueType() != boDefAttribute.VALUE_CURRENCY)
                    {
                        return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
                    }
                }
                else
                {
                    if(attDef.getValueType() != boDefAttribute.VALUE_NUMBER)
                    {
                        return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
                    }
                }
            }
            else if("GESTEMP_Query".equals(tempAtrib.getName()) ||  //$NON-NLS-1$
                    "GESTEMP_JavaQuery".equals(tempAtrib.getName())|| //$NON-NLS-1$
                    "GESTEMP_CampoNJava".equals(tempAtrib.getName()) //$NON-NLS-1$
                )
            {
                return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
            }
        }
        else if("GESDocClfText".equals(clfAtrib.getName())) //$NON-NLS-1$
        {
            //se fôr texto vai aceitar, pq vai converter            
            return null;
        }
        else if("GESDocClfObject".equals(clfAtrib.getName())) //$NON-NLS-1$
        {
            //só pode aceitar boObject do mesmo tipo
            String defName = clfAtrib.getAttribute("objecto").getObject().getAttribute("name").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
            if("GESTEMP_CampoObjecto".equals(tempAtrib.getName())|| //$NON-NLS-1$
              "GESTEMP_CampoNObjecto".equals(tempAtrib.getName())) //$NON-NLS-1$
            {
                boObject att = tempAtrib.getAttribute("atributo").getObject(); //$NON-NLS-1$
                String defName2 = att.getAttribute("clsReg").getObject().getAttribute("name").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                String attName =  att.getAttribute("name").getValueString(); //$NON-NLS-1$
                boDefHandler def = boDefHandler.getBoDefinition(defName2);
                boDefAttribute attDef = def.getAttributeRef(attName);
                
                if(def.getAttributeType(attName) != boDefAttribute.TYPE_OBJECTATTRIBUTE)
                {
                    return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
                }                
                if(!defName.equals(attDef.getReferencedObjectName()))
                {
                    return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
                }
            }
            else if("GESTEMP_Query".equals(tempAtrib.getName()) ||  //$NON-NLS-1$
                    "GESTEMP_JavaQuery".equals(tempAtrib.getName())|| //$NON-NLS-1$
                    "GESTEMP_CampoNJava".equals(tempAtrib.getName()) //$NON-NLS-1$
                )
            {
                boObject parametro = tempAtrib.getAttribute("parametro").getObject(); //$NON-NLS-1$
                String defName2 = parametro.getAttribute("objecto").getObject().getAttribute("name").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                if(!defName.equals(defName2))
                {
                    return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
                }
            }
            else
            {
                return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
            }
        }
        else if("GESDocClfDate".equals(clfAtrib.getName())) //$NON-NLS-1$
        {
            //data e tempo
            if("GESTEMP_CampoFormula".equals(tempAtrib.getName()) || //$NON-NLS-1$
               "GESTEMP_CampoNFormula".equals(tempAtrib.getName()) || //$NON-NLS-1$
               "GESTEMP_CampoJava".equals(tempAtrib.getName()) || //$NON-NLS-1$
               "GESTEMP_CampoManual".equals(tempAtrib.getName())) //$NON-NLS-1$
            {
                String tipo = tempAtrib.getAttribute("tipo").getValueString(); //$NON-NLS-1$
                if(!"6".equals(tipo) && !"4".equals(tipo)) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
                }
            }
            else if("GESTEMP_CampoObjecto".equals(tempAtrib.getName())|| //$NON-NLS-1$
                    "GESTEMP_CampoNObjecto".equals(tempAtrib.getName())) //$NON-NLS-1$
            {
                boObject att = tempAtrib.getAttribute("atributo").getObject(); //$NON-NLS-1$
                String defName = att.getAttribute("clsReg").getObject().getAttribute("name").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                String attName = att.getAttribute("name").getValueString(); //$NON-NLS-1$
                boDefHandler def = boDefHandler.getBoDefinition(defName);
                boDefAttribute attDef = def.getAttributeRef(attName);
                if(attDef.getValueType() != boDefAttribute.VALUE_DATE && 
                    attDef.getValueType() != boDefAttribute.VALUE_DATETIME)
                {
                    return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
                }
            }
            else if("GESTEMP_Query".equals(tempAtrib.getName()) ||  //$NON-NLS-1$
                    "GESTEMP_JavaQuery".equals(tempAtrib.getName())|| //$NON-NLS-1$
                    "GESTEMP_CampoNJava".equals(tempAtrib.getName()) //$NON-NLS-1$
                )
            {
                return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
            }
        }
        else if("GESDocClfLov".equals(clfAtrib.getName())) //$NON-NLS-1$
        {
            boObject lov = clfAtrib.getAttribute("lov").getObject(); //$NON-NLS-1$
            if("GESTEMP_CampoFormula".equals(tempAtrib.getName()) || //$NON-NLS-1$
               "GESTEMP_CampoNFormula".equals(tempAtrib.getName()) || //$NON-NLS-1$
               "GESTEMP_CampoJava".equals(tempAtrib.getName()) || //$NON-NLS-1$
               "GESTEMP_CampoManual".equals(tempAtrib.getName())) //$NON-NLS-1$
            {
                String tipo = tempAtrib.getAttribute("tipo").getValueString(); //$NON-NLS-1$
                if(!"7".equals(tipo) && !"1".equals(tipo)) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
                }
            }
            else if("GESTEMP_CampoObjecto".equals(tempAtrib.getName())|| //$NON-NLS-1$
                    "GESTEMP_CampoNObjecto".equals(tempAtrib.getName())) //$NON-NLS-1$
            {
                boObject att = tempAtrib.getAttribute("atributo").getObject(); //$NON-NLS-1$
                String defName = att.getAttribute("clsReg").getObject().getAttribute("name").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                String attName = att.getAttribute("name").getValueString(); //$NON-NLS-1$
                boDefHandler def = boDefHandler.getBoDefinition(defName);
                boDefAttribute attDef = def.getAttributeRef(attName);
                if(attDef.getValueType() != boDefAttribute.VALUE_CHAR)
                {
                    return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
                }
            }
            else if("GESTEMP_Query".equals(tempAtrib.getName()) ||  //$NON-NLS-1$
                    "GESTEMP_JavaQuery".equals(tempAtrib.getName()) || //$NON-NLS-1$
                    "GESTEMP_CampoNJava".equals(tempAtrib.getName()) //$NON-NLS-1$
                 )
            {
                return Messages.getString("GtMapHelper.35"); //$NON-NLS-1$
            }
        }
        return null;
    }
    
    public static boolean validBridge(boEvent event, boObject obj) throws boRuntimeException
    {
        boBridgeIterator bit = obj.getBridge("mapeamentos").iterator(); //$NON-NLS-1$
        bit.beforeFirst();
        while(bit.next())
        {
            if(!onBeforeSave(bit.currentRow().getObject()))
            {
                obj.addErrorMessage(Messages.getString("GtMapHelper.112")); //$NON-NLS-1$
                return false;
            }
        }
        return true;
    }
}