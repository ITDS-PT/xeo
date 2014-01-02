package netgest.bo.impl.document.merge.gestemp;

import java.util.ArrayList;

import netgest.bo.impl.document.merge.Tabela;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;


public class GtCampoNJava extends GtCampo {
    private String queryName = null;

    //Campos Java
    ArrayList campos;

    public GtCampoNJava(GtCampo parentCampo) {
        super(parentCampo);
        campos = new ArrayList();
    }

    public GtCampoNJava(GtTemplate template, GtQuery query) {
        super(template, query);
        campos = new ArrayList();
    }

    public void addCampo(GtCampo newValue) {
        campos.add(newValue);
    }

    public String getHeaderName() {
        if (getQueryName() != null) {
            return getQueryName() + "__" + getNome();
        }

        return getNome();
    }

    public String getHTMLFieldName() {
        if (getQueryName() != null) {
            return getQueryName() + "__" + getNome();
        }

        return getNome();
    }

    public String getHTMLFieldID() {
        if (getQueryName() != null) {
            return "tblLook" + getQueryName() + "__" + getNome();
        }

        return "tblLook" + getNome();
    }

    public static GtCampoNJava getCampo(GtCampo parentObj, boObject campo)
        throws boRuntimeException {
        GtCampoNJava newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoNJava(parentObj.getTemplate(),
                    parentObj.getQuery());
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    public static GtCampoNJava getCampo(GtTemplate template, GtQuery query,
        boObject campo) throws boRuntimeException {
        GtCampoNJava newCampo = null;

        if (campo != null) {
            newCampo = new GtCampoNJava(template, query);
            setCampoValues(newCampo, campo);
        }

        return newCampo;
    }

    public static GtCampoNJava setCampoValues(GtCampoNJava newCampo,
        boObject campo) throws boRuntimeException {
        if (campo != null) {
            newCampo.setNome(campo.getAttribute("nome").getValueString());
            newCampo.setDescricao(campo.getAttribute("descricao")
                                       .getValueString());
            newCampo.setFormula(campo.getAttribute("formula").getValueString());
            newCampo.setParametro(campo.getAttribute("parametro").getValueLong());
            newCampo.setTipo(campo.getAttribute("tipo").getValueString());
            newCampo.setTags(campo.getBridge("tags"));

            newCampo.setTipoSeleccao(campo.getAttribute("tipoSeleccao")
                                          .getValueString());

            //campos
            boBridgeIterator bit = campo.getBridge("campos").iterator();
            bit.beforeFirst();

            boObject aux = null;
            GtCampoJava cj = null;
            GtCampoNJava cjn = null;

            while (bit.next()) {
                aux = bit.currentRow().getObject();

                if ("GESTEMP_CampoJava".equals(aux.getName())) {
                    cj = GtCampoJava.getCampo(newCampo, aux);
                    newCampo.addCampo(cj);
                }
            }
            //é usado no modelo
            newCampo.setReferenceByTemplate(Helper.referencedByTemplate(newCampo.getQuery(), campo, newCampo.getTemplate().getTemplateBookmarks(), true));
        }

        return newCampo;
    }

    public void setData(EboContext boctx, Tabela tab, byte templateType)
        throws boRuntimeException 
    {
        
        GtCampo[] aAllCampos = getAllCampos();
        if( aAllCampos.length > 0 )
        {
            GtValue oAuxValue = aAllCampos[ 0 ].getValue();
            if( oAuxValue != null && oAuxValue.getValues() != null )
            {
                for (int i = 0; i < oAuxValue.getValues().size(); i++) {
                    tab.startEditingLine();
                    for (int z = 0; z < aAllCampos.length ; z++) 
                    {
                        
                        ArrayList valores = aAllCampos[ z ].getValue().getValues();
                        
                        Object valor = valores.get(i);
                        Object returnedObj = Helper.getReturnObject(boctx,
                                Integer.parseInt(aAllCampos[ z ].getTipo()), valor, templateType,
                getTags()); 
                        
                        String hName =  aAllCampos[ z ].getNome();
                        // Retirar o nome da Query, não 
                        
                        tab.insert(returnedObj, hName,
                            Helper.getSqlTypeFromGesDocTipoCampoLov(
                                        Integer.parseInt(aAllCampos[ z ].getTipo()), templateType, returnedObj
                                    )
                            );
                    }
                    tab.endEditingline();
                }
            }
        }

    }

    public GtCampo[] getAllCampos() {
        return (GtCampo[]) campos.toArray(new GtCampo[campos.size()]);
    }
    
    public void setReferencias(boObject generatedObj)   throws boRuntimeException
    {
        GtValue v = getValue();
        if(v != null && v.getValue() != null)
        {
            ArrayList valores = (ArrayList)v.getValues();
            Long auxL;
            for (int i = 0; i < valores.size(); i++) 
            {
                auxL = (Long)valores.get(i);
                boObject o = boObject.getBoManager().loadObject(generatedObj.getEboContext(), auxL.longValue());
                if(!generatedObj.getBridge("objReferences").haveBoui(o.getBoui()))
                {
                    generatedObj.getBridge("objReferences").add(o.getBoui());
                }
                //Martelada
                if("Peritagem".equals(o.getName()))
                {
                    o = o.getAttribute("sinistro").getObject();
                    if(o != null && !generatedObj.getBridge("objReferences").haveBoui(o.getBoui()))
                    {
                        generatedObj.getBridge("objReferences").add(o.getBoui());
                    }
                }
            }
        }
    }
}
