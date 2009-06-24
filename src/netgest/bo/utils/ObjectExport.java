/*Enconding=UTF-8*/
package netgest.bo.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.io.*;
import netgest.io.FSiFile;
import netgest.io.iFile;
import netgest.io.iFilePermissionDenied;
import netgest.io.iFileServer;
import netgest.utils.ngtXMLHandler;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;



/**
 * <p>Title: ObjectExport </p>
 * <p>Description: exporta os dados de um boObject para XML ou CSV</p>  
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Luís Eduardo Moscoso Barreira
 * @version 1.0
 */
public final class ObjectExport  implements Serializable {
    
    //vector com os dados de todos os atributos do boObject
    private Vector data  = new Vector();
    //vector com os dados formatados para exportação de csv
    private Vector csvData = new Vector();
    //nome do boObjecto
    private String objName;
    //boui do boObjecto
    private long boui;
    //stringBuffer com o texto a ser exportado para o ficheiro
    private StringBuffer sb;
    //documento xmlpara exportar
    private XMLDocument xmldoc;
    //defenição do objecto a esportar
    private boDefHandler bodef;
    //indicador que é para fazer uma exportação completa do objecto
    private boolean allAtt;
    
    /**
     * Constroi um objecto para se poder exportar os dados
     *
     * @param   object     o boObject a ser exportado.
     * @param   attrNames   o array de nomes dos atributos a considerar.
     * @param   allAtt   indica se é para exportar todos os atributos do objecto
     */ 
    public ObjectExport(boObject object, String[] attrNames , boolean allAtt) throws boRuntimeException
    {
        getObject(object,attrNames,allAtt,null);           
    }
    /**
     * Constroi um objecto para se poder exportar os dados
     * @param   object     o boObject a ser exportado.
     * @param   attrNames   o array de nomes dos atributos a considerar.
     * @param   allAtt   indica se é para exportar todos os atributos do objecto
     * @param   booleanPattern   pattern relativo ao atributo do tipo boolean.
     */ 
    public ObjectExport(boObject object, String[] attrNames , boolean allAtt, Hashtable booleanPattern) throws boRuntimeException
    {    
        getObject(object,attrNames,allAtt,booleanPattern);
    }
    private void getObject(boObject object, String[] attrNames , boolean allAtt, Hashtable booleanPattern) throws boRuntimeException
    {
        bodef = object.getBoDefinition();
        objName = bodef.getLabel();
        boui = object.getBoui();
        this.allAtt = allAtt;
        
       //contem todos os atributos do objecto
       Enumeration attrArray = object.getAllAttributes().elements();
       //irá conter os dados acerca de um atributo
       attrData attrVec; 
       //objecto que contem os dados do attributo
       AttributeHandler attr;
       //vector auxiliar para pesquesar attributos dos tipo objecto e bridge
       Vector boAux = new Vector();
       //arrayu de objectos para se poder pesquisar os seus attributos atraves do metodo attrFill
       boObject[] boArr;
        
        while(attrArray.hasMoreElements()){
            attr =  (AttributeHandler)attrArray.nextElement();
            //testa se o atributo está defionido para ser exportado
            if((allAtt || isValid(attr.getName(), attrNames)) && !attr.getName().equals("PARENT")){
                //preencher os generico do atributo
                attrVec = new attrData();
                attrVec.boui = boui;
                attrVec.label = attr.getDefAttribute().getLabel();
                attrVec.name = attr.getName();
                attrVec.pathName = attr.getName();
                attrVec.pathLabel = attr.getDefAttribute().getLabel();
                attrVec.namePar = attr.getParent().getName();
                attrVec.labelPar = attr.getParent().getBoDefinition().getLabel();
                //caso em que o attributo é do tipo objecto
                if(attr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE){
                    attrVec.dataStr = null;
                    //se é de relação 0-1 ou 1-1, só existe máximo um objecto
                    if(attr.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1){
                        attrVec.type = attrData.OBJECT_TYPE;
                        boArr = new boObject[1];
                        boArr[0] = attr.getObject();
                        if("CREATOR".equalsIgnoreCase(attrVec.name) && attr.getObject() == null)
                            boArr[0] = object.getObject(object.getEboContext().getBoSession().getPerformerBoui());    
                        attrVec.dataObj = attrFill(boArr,"","",true);
                    }
                    //caso contrário pode existir vários objectos
                    else{
                        attrVec.type = attrData.BRIDGE_TYPE;                                        
                        boAux.removeAllElements();
                        bridgeHandler auxb=attr.getParent().getBridge(attr.getName());
                        if (auxb!=null)
                        {
                          auxb.beforeFirst();
                          //retirar todos os objectos e colocalos num vector
                          while(attr.getParent().getBridge(attr.getName()).next())
                              boAux.addElement(attr.getParent().getBridge(attr.getName()).getObject());
                          boArr = new boObject[boAux.size()];
                          //retirar todos os objectos do vector e colocá-los num array de boObjects
                          for (int i = 0; i < boAux.size(); i++)
                              boArr[i] = (boObject)boAux.elementAt(i);                        
                          attrVec.dataObj = attrFill(boArr,attrVec.pathName + ".", attrVec.pathLabel + ".", false);
                        }
                    }
                    data.addElement(attrVec);
                }
                else
                { //caso em que é do tipo string
                    attrVec.type = attrData.STRING_TYPE;
                    attrVec.dataObj = null;
                    //para o caso dos booleanos que podem ser 0 ou 1
                    if(attr.getDefAttribute().getType().equals("boolean"))
                    {
                        if(booleanPattern != null)
                        {
                            attrVec.dataStr = (String)booleanPattern.get(attr.getValueString());
                        }
                        else
                        {
                            if(attr.getValueString().equalsIgnoreCase("1"))
                              attrVec.dataStr = "Sim";
                            else
                              attrVec.dataStr = "Não";                            
                        }
                    }
                    else
                    {
                        attrVec.dataStr = attr.getValueString();   
                    }                                            
                    data.addElement(attrVec);
                }
            }
        }
    }

     /**
     * Percorre um array de boObjects para retirar os atributos a ser exportados
     * chama-se a si recursivamente aquando encontra atributos que são objects/bridges
     *
     * @param   object     o array de boObjects.
     * @param   nameBr   string com o nome do caminho para o boObject.
     * @param   labelBr  string com o nome do caminho para o boObject(usando labels).
     * @param   rel_1_1  indica se se o objecto unico do array é ou não de uma bridge
     * 
     * @return  retorna um array de vectores(um vector por cada objecto), contendo objectos do tipo
     *          attrData.          
     */    
    private Vector[] attrFill(boObject[] object, String nameBr, String labelBr, boolean rel_1_1) throws boRuntimeException{
        //array de retorno, contem um vector por cada objecto
        Vector ret[] = new Vector[object.length];
        //ira conter os dados de um atributo
        attrData attr;
        //serve para retirar o nome do atributo definido no XML
        String attrName = null;
        //servirá para pesquisar bridges, auxiliar, serve apenas para tranportar os objectos para um array 
       Vector boAux = new Vector();
       //irá conter todos os atributos do objecto
       boDefAttribute attsDefs[];
       //irá conter o array de objectos de uma bridge( ou um unico se for um atributo do tipo objecto)
       boObject[] boArr;       
        
        //pesquisar todos os objectos
        for (int i = 0; i < object.length; i++){
            if(object[i]==null)
              continue;
            ret[i] = new Vector(); 
            //adicionar os atributos da bridge se for o caso
            if(!nameBr.equalsIgnoreCase(""))
                ret[i].addAll(addBridgeAttr(object[i], nameBr, labelBr,i+1));
            
            
            //retirar todos os atributos do objecto, ou só o boui
            if(allAtt)
            {
              if(object[i].getBoDefinition().getBoCanBeOrphan())
              {
                attsDefs = new boDefAttribute[1];
                attsDefs[0] = object[i].getAttribute("BOUI").getDefAttribute();
              }
              else
              {
                attsDefs = object[i].getBoDefinition().getAttributesDef();
              }
            }
            //retira todas as colunas do doc XML
            else
            {
                try
                {
                  //irá conter o array atributos que estão definidos no XML
                  ngtXMLHandler[] cols = object[i].getBoDefinition().getViewer("general").getForm("list").getChildNode("grid").getChildNode("cols").getChildNodes();
                  attsDefs = new boDefAttribute[cols.length];
                  for (int j = 0; j < cols.length; j++)
                  {
                    attrName = cols[j].getChildNode("attribute").getText();
                    if(attrName!=null)
                      attsDefs[j] = object[i].getAttribute(attrName).getDefAttribute();
                  }
                  
                }
                catch (Exception e){continue;}              
            }
            
            //para cada atributo definido no xml, retirar os seus dados
            for (int j = 0; j < attsDefs.length; j++){
                if(attsDefs[j]==null || attsDefs[j].getName().equals("PARENT"))
                  continue;
                
                attr = new attrData();

                //nome do atributo tal como está difinido
                attrName = attsDefs[j].getName();
                
                attr.boui = object[i].getBoui();
                attr.label = attsDefs[j].getLabel();
                attr.namePar = object[i].getName();    
                attr.labelPar = object[i].getBoDefinition().getLabel();
                
                //actualiza o nome dos attributos para se saber que estes pertencem a objectos de uma bridge
                //no caso de pertencerem a uma bridge é necessário incluir o nome/label do objecto
                if(rel_1_1){
                    attr.pathName =  nameBr + object[i].getAttribute(attrName).getName();
                    attr.pathLabel = labelBr + attsDefs[j].getLabel();
                }
                else{
                    attr.pathName =  nameBr +object[i].getName() + "." + object[i].getAttribute(attrName).getName();
                    attr.pathLabel =  labelBr +object[i].getBoDefinition().getLabel() + "." + attsDefs[j].getLabel();
                }
                attr.name =object[i].getAttribute(attrName).getName();
                
                //caso em que o atributo é do tipo objecto
                if(attsDefs[j].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE){
                    attr.dataStr = null;
                    //caso em que é um siples objecto
                    if(attsDefs[j].getRelationType() == boDefAttribute.RELATION_1_TO_1){
                        attr.type = attrData.OBJECT_TYPE;
                        boArr = new boObject[1];
                        boArr[0] = object[i].getAttribute(attrName).getObject();
                        attr.dataObj = attrFill(boArr, nameBr, labelBr, true);
                    }//caso em que é uma bridge
                    else{
                        attr.type = attrData.BRIDGE_TYPE;      
                        //no caso de bridge é necessário retirar todos os objectos para colocálos num array
                        boAux.removeAllElements();
                        object[i].getAttribute(attrName).getParent().getBridge(object[i].getAttribute(attrName).getName()).beforeFirst();
                        while(object[i].getAttribute(attrName).getParent().getBridge(object[i].getAttribute(attrName).getName()).next())
                            boAux.addElement(object[i].getAttribute(attrName).getParent().getBridge(object[i].getAttribute(attrName).getName()).getObject());
                        boArr = new boObject[boAux.size()];
                        for (int x = 0; x < boAux.size(); x++) 
                            boArr[i] = (boObject)boAux.elementAt(i);
                        attr.dataObj = attrFill((boObject[])boAux.toArray(new boObject[boAux.size()]), nameBr + ".", labelBr + ".", false);
                    }
                    ret[i].addElement(attr);
                }//caso em que o atributo é siples
                else{
                    attr.type = attrData.STRING_TYPE;
                    attr.dataObj = null;
                    //é necessário testar se o atributo é um booleano, para se substituir os valores
                    if(object[i].getAttribute(attrName).getDefAttribute().getType().equals("boolean"))
                    {
                        if(object[i].getAttribute(attrName).getValueString().equalsIgnoreCase("1"))
                            attr.dataStr = "Sim";
                        else
                            attr.dataStr = "Não";
                    }
                    else
                        attr.dataStr = object[i].getAttribute(attrName).getValueString();                    
                    ret[i].addElement(attr);
                }

            }
        }
        
        return ret;       
    }

    
    /**
     * Retira os atributos de uma bridge
     *
     * @param   object     o boObject pertencente à bridge do qual se quer os tributos.
     * @param   nameBr   string com o nome do caminho para o boObject.
     * @param   labelBr  string com o nome do caminho para o boObject(usando labels). 
     * @param   boNum   posição do objecto na bridge, necessário pis getParentBridge() retorna bridge com todos atributos
     * 
     * @return  retorna vector com objectos do tipo attrData
     */    
    private Vector addBridgeAttr(boObject object, String nameBr, String labelBr, int boNum) throws boRuntimeException{
        //vector de retorno
        Vector ret = new Vector();
       //irá conter os dados acerca de um atributo
       attrData attrVec;
       //objecto que contem os dados do attributo
       AttributeHandler attr;
       //arrayu de objectos para se poder pesquisar os seus attributos atraves do metodo attrFill
       boObject[] boArr;        
        bridgeHandler bh = null;
        try{  bh = object.getParentBridgeRow().getBridge();}
            catch (Exception e){return ret;}
        if(bh == null)
            return ret;
        
        boAttributesArray boAttrArr = bh.getAllAttributes();
        if(boAttrArr == null)
            return ret;
            
        //contem todos os atributos do objecto    
        Enumeration attrArray = boAttrArr.elements();
        if(attrArray == null)
            return ret;
            
        while(attrArray.hasMoreElements()){
            attr =  (AttributeHandler)attrArray.nextElement();
            
            //o atributo LIN é de sistema
            if((attr.getName().indexOf("LIN") != -1) || (!attr.getName().endsWith(""+boNum)))
                continue;
            
            //preencher os generico do atributo
            attrVec = new attrData();
            attrVec.boui = object.getBoui();
            attrVec.label = attr.getDefAttribute().getLabel();
            attrVec.name = attr.getName().substring(attr.getName().indexOf(".")+1,attr.getName().lastIndexOf("."));
            attrVec.pathName = attr.getName().substring(0,attr.getName().lastIndexOf("."));
            attrVec.namePar = object.getName();
            attrVec.labelPar = object.getBoDefinition().getLabel();
            attrVec.pathLabel = labelBr + attrVec.label;
            attrVec.isInBridge = true;
            
            //caso em que o attributo é do tipo objecto
            if(attr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE){
                //se é de relação 0-1 ou 1-1, só existe máximo um objecto
                if(attr.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1){
                    attrVec.dataStr = null;
                    attrVec.type = attrData.OBJECT_TYPE;
                    boArr = new boObject[1];
                    boArr[0] = attr.getObject();
                    attrVec.dataObj = attrFill(boArr,nameBr,labelBr , true);
                    ret.addElement(attrVec);                
                }
            }
            //caso em que é do tipo string
            else {
                attrVec.type = attrData.STRING_TYPE;
                attrVec.dataObj = null;
                if(attr.getDefAttribute().getType().equals("boolean"))
                {                
                    if(attr.getValueString().equalsIgnoreCase("1"))
                        attrVec.dataStr = "Sim";
                    else 
                        attrVec.dataStr = "Não";
                }
                else
                    attrVec.dataStr = attr.getValueString();                                    
                ret.addElement(attrVec);
            }
        }
        return ret;
    }

    
    /**
     * Testa se um attributo é válido para ser exportado
     *
     * @param   name     nome do atributo.
     * @param   names   o array de nomes dos atributos a considerar.
     * 
     * @return  retorna se é ou não válido 
     */    
    private boolean isValid(String name, String[] names){
        
        if (names == null)
            return true;
        
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase(name))
                return true;
        }
        return false;     
        
    }
    
    /**
     * Calcula a posição de um certo atributo no vector de csv
     *
     * @param   name     o nome do atributo.
     * 
     * @return retorna a posição no vector
     *          -1 se não encontra
     */    
    private int csvPos(String name)
    { 
        for (int i = 0; i < csvData.size(); i++){
            if(((dataCSV) csvData.elementAt(i)).name.equalsIgnoreCase(name))
                return i;
        }
        return -1;
    
    }


    
    /**
     * Prenche o documento XML
     * chama-se a si recursivamente para poder percorrer todos os dados
     *
     * @param   dat     vector de atributos.
     * @param   xmlPar  elemento pai de XML
     */    
    private void getXMLElement(Vector dat, XMLElement xmlPar){
        attrData attr;
        XMLElement xmlelem = null;
        for (int i = 0; i < dat.size(); i++){
            attr = (attrData)dat.elementAt(i);
            if(attr.name.equals("BOUI"))
              continue;
            if (attr.type == attrData.STRING_TYPE)
              xmlelem = (XMLElement)xmldoc.createElement("Attribute");
            else
              xmlelem = (XMLElement)xmldoc.createElement("ObjectAttribute");
            xmlelem.setAttribute("name",attr.name);
            xmlelem.setAttribute("description",attr.label);
            if(attr.isInBridge)
                xmlelem.setAttribute("isInBridge","yes");
            else
                xmlelem.setAttribute("isInBridge","no");
            
            //caso em que o atributo é um tipo simples
            if (attr.type == attrData.STRING_TYPE){
                xmlelem.addText(attr.dataStr);
            }//caso em que é um objecto
            else if (attr.type == attrData.OBJECT_TYPE){
                XMLElement xmlelem2 = null;
                for (int j = 0; j < attr.dataObj.length; j++)
                {
                    if(attr.dataObj[j] == null)
                      continue;
                    xmlelem2=(XMLElement)xmldoc.createElement("Object");
                    xmlelem2.setAttribute("boui",""+((attrData) attr.dataObj[j].elementAt(0)).boui);
                    xmlelem2.setAttribute("name",((attrData) attr.dataObj[j].elementAt(0)).namePar);
                    xmlelem2.setAttribute("description",((attrData) attr.dataObj[j].elementAt(0)).labelPar);
                    getXMLElement(attr.dataObj[j], xmlelem2);
                }
                if (xmlelem2!=null)xmlelem.appendChild(xmlelem2);
            }//caso em que é uma bridge
            else{
                //no caso de uma bridge é necessário colocar o tipo de objecto   
                if (attr.dataObj!=null)
                {
                  for (int j = 0; j < attr.dataObj.length; j++){
                      XMLElement xmlelem2 = (XMLElement)xmldoc.createElement("Object");
                      xmlelem2.setAttribute("boui",""+((attrData) attr.dataObj[j].elementAt(0)).boui);
                      xmlelem2.setAttribute("name",((attrData) attr.dataObj[j].elementAt(0)).namePar);
                      xmlelem2.setAttribute("description",((attrData) attr.dataObj[j].elementAt(0)).labelPar);
                      getXMLElement(attr.dataObj[j], xmlelem2);
                      xmlelem.appendChild( xmlelem2 );
                  }
                }
            }
            xmlPar.appendChild(xmlelem);
        }

    }
    
/*
     private void getXMLElement(Vector dat, XMLElement xmlPar){
        attrData attr;
        XMLElement xmlelem = null;
        for (int i = 0; i < dat.size(); i++){
            attr = (attrData)dat.elementAt(i);
            xmlelem = (XMLElement)xmldoc.createElement(attr.label);
            //caso em que o atributo é um tipo simples
            if (attr.type == attrData.STRING_TYPE){
                xmlelem.addText(attr.dataStr);
            }//caso em que é um objecto
            else if (attr.type == attrData.OBJECT_TYPE){
                for (int j = 0; j < attr.dataObj.length; j++)
                {
                    if(attr.dataObj[j] == null)
                      continue;
                    getXMLElement(attr.dataObj[j], xmlelem);
                }
            }//caso em que é uma bridge
            else{
                //no caso de uma bridge é necessário colocar o tipo de objecto
                for (int j = 0; j < attr.dataObj.length; j++){
                    XMLElement xmlelem2 = (XMLElement)xmldoc.createElement(((attrData) attr.dataObj[j].elementAt(0)).labelPar);
                    getXMLElement(attr.dataObj[j], xmlelem);
                    xmlelem.appendChild( xmlelem2 );
                }
            }
            xmlPar.appendChild(xmlelem);
        }

    }
 */
    
    
     /**
     * Insere um dado de um atributo no vector na su posição correcta
     *
     * @param   v     vectro para o qual se vai inserir.
     * @param   s   string a inserir.
     * @param   csvNivel   nivel correcto do atributo.
     */    
    private void insertAt(Vector v, String s, int csvNivel){
        for (int i = 0; i <csvNivel ; i++) {
            if(i>=v.size())//se ainda não for o nivel do atributo inserir string vazia
               v.addElement("");  
        }
        v.addElement(s);        
    }
    
     /**
     * Constroi o vector com os dados dos atributos para exportação em csv
     * chama-se a si recursivamente para poder apanhar todos os atributos
     *
     * @param   dat     vector com os dados dos atributos.
     * @param   par   indicação se os atributos pertencem ao boObject principal.
     * @param   nivel   indica o nivel em csv do atributo.
     */    
    private void getCSV(Vector dat, boolean par, int nivel){
        attrData attr;
        dataCSV csv;
        for (int i = 0; i < dat.size(); i++){
            attr = (attrData)dat.elementAt(i);
            if (attr.type == attrData.STRING_TYPE){
                //se o atributo é de um tipo ainda não considerado criar uma entrada para ele
                if(csvPos(attr.pathName) == -1){
                    csv = new dataCSV();
                    csv.name = attr.pathName;
                    csv.label = attr.pathLabel;
                    csv.parent = par;
                    insertAt(csv.data,attr.dataStr, nivel);
                    csvData.addElement(csv);
                }
                else{
                    csv = (dataCSV) csvData.elementAt(csvPos(attr.pathName));
                    csv.parent = par;
                    insertAt(csv.data,attr.dataStr,nivel);
                }
            }
            else if(attr.type == attrData.OBJECT_TYPE){
                for (int j = 0; j < attr.dataObj.length; j++)
                {
                    if(attr.dataObj[j] == null)
                      continue;                
                    getCSV(attr.dataObj[j], par, nivel);
                }
            }
            else {
                //quando é um atributo bride, sobe-se de nivel, e deixa de ser parent
                for (int j = 0; j < attr.dataObj.length; j++)
                    getCSV(attr.dataObj[j], false, nivel+j+1);
            }
            
        }
    }

     /**
     * Escreve csv e prenche os campos que faltam
     *
     * @param   div   String com divisão entre atributos.
     * 
     */    
    private void writeCSV(String div){
        dataCSV csv;
        int nivel=0;
        
        //contar os niveis
        csv = (dataCSV) csvData.elementAt(0);
        nivel= csv.data.size();

        for (int i = 1; i < csvData.size(); i++){ 
            csv = (dataCSV) csvData.elementAt(i);
            if(csv.data.size() > nivel){
                nivel = csv.data.size();
            }
        }
        
        //preencher os capos que faltam, para ficarem todos com o mesmo numero de niveis
        for (int i = 0; i < csvData.size(); i++){
            
            csv = (dataCSV) csvData.elementAt(i);
            for (int j = csv.data.size(); j < nivel; j++){
                if(csv.parent)//se for do tipo parent é repetido para todos os niveis
                    csv.data.addElement(csv.data.elementAt(0));
                else//caso contrário e colocado uma string vazia
                    csv.data.addElement("");
            }
        }
       
        //escrever, apagar a primeira linha se tiver masi niveis,pois esta não irá ser necessária
        String data;
        int i;
        if(nivel>1)
            i = 1;
        else
            i =0;
        for (; i < nivel; i++){        
            
            for (int j = 0; j < csvData.size(); j++){
                csv = (dataCSV) csvData.elementAt(j);
                data = (String) csv.data.elementAt(i);
                sb.append("\"").append(data).append("\"").append(div);
            }
            sb.append("\n");
        }
    }
   
    
     /**
     * Exporta os dados para XML
     */    
   public XMLDocument saveXML(String user) throws boRuntimeException{
        xmldoc = new XMLDocument();
        xmldoc.setEncoding("ISO-8859-1");
        xmldoc.setVersion("1.0");


        //escrever as tags principais
//      XMLElement xmlelem = (XMLElement)xmldoc.createElement(objName); 
        XMLElement xmlelemP = (XMLElement)xmldoc.createElement("XEO");
        xmlelemP.setAttribute("date",(new Date()).toString());
        xmlelemP.setAttribute("action","export");
        xmlelemP.setAttribute("type","file");        
        if(user!=null)
          xmlelemP.setAttribute("creator",user);
        XMLElement xmlelem = (XMLElement)xmldoc.createElement("Object");
        xmlelem.setAttribute("boui",""+boui);
        xmlelem.setAttribute("name",bodef.getName());
        xmlelem.setAttribute("description",objName);
//        

        //preencher o xml
        getXMLElement(data, xmlelem);
        
//      xmldoc.appendChild(xmlelem);
        xmlelemP.appendChild(xmlelem);
        xmldoc.appendChild(xmlelemP);
//        
        return xmldoc;
   }
   
     /**
     * Exporta os dados para CSV
     *
     * @param   label     verdaeiro se for para usar as label dos atributos como titulo, falso se for para usar os nomes.
     * @param   div     String com divisão entre campos.
     * 
     */    
   public StringBuffer saveCSV(boolean label, String div) throws boRuntimeException{
        sb = new StringBuffer();
        
        //construir o vectro com os dados
        getCSV(data, true,0);
        //imprimir os nomes dos atributos
        if(label){
            for (int i = 0; i < csvData.size(); i++)
                sb.append("\"").append(((dataCSV) csvData.elementAt(i)).label).append("\"").append(div);
        }
        else{
            for (int i = 0; i < csvData.size(); i++)
                sb.append("\"").append(((dataCSV) csvData.elementAt(i)).name).append("\"").append(div);
        }        
        sb.append("\n");        
        
        //escrever os dados csv
        writeCSV(div);
        
        return sb;
   }
   
    /**
     * <p>Title: attrData </p>
     * <p>Description: Guarda os dados referentes a um atributo</p> 
     */
    private  class attrData  implements Serializable {
        public String label;
        public String name;
        public String pathName; //indica o caminho para o atributo, usando nomes dos objectos/atributos
        public String pathLabel;//indica o caminho para o atributo, usando labels dos objectos/atributos
        public String namePar;
        public String labelPar;
        public long boui;
        public byte type; //tipo de attributo
        public boolean isInBridge=false; //indica se o atributo de um objecto filho pertence a bridge desse objecto
        public String dataStr; //caso em que é uma string
        public Vector[] dataObj; //caso em que é um objecto ou bridge
        public static final byte STRING_TYPE = 0;
        public static final byte OBJECT_TYPE = 1;
        public static final byte BRIDGE_TYPE = 2;
    
        public attrData(){
        }
 
    }   
   
    /**
     * <p>Title: dataCSV </p>
     * <p>Description: Guarda os dados referentes a atributos, em formato para csv</p> 
     */
    private  class dataCSV  implements Serializable {
        public String name;
        public String label;
        public Vector data;
        public boolean parent;
    
        public dataCSV(){
            name = null;
            data = new Vector();
        }
        
    }   



   
}