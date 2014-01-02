/*Enconding=UTF-8*/
package netgest.bo.builder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Stack;
import java.util.zip.CRC32;

import javax.naming.InitialContext;

import netgest.bo.boConfig;
import netgest.bo.builder.boBuildRepository;
import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefViewer;
import netgest.bo.def.v1.boDefHandlerImpl;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;
import netgest.bo.system.boRepository;

import netgest.bo.transformers.*;
import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import netgest.bo.system.Logger;
/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boRefactoring 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.builder.boRefactoring");

    private static boConfig p_bcfg = new boConfig();
    private static boPathProvider pathProvider = new boPathProvider();
    private EboContext ebo;
    private boBuildRepository p_repository;
    /**
     * 
     * @since 
     */
    public boRefactoring(EboContext ebo)
    {
        this.ebo = ebo;
        p_repository = new boBuildRepository(ebo.getBoSession().getRepository());
    }

    public void toNewXMLVersion(String objName) throws boRuntimeException
    {
        try 
        {            
            File[] files;
            if(objName == null || "".equals(objName))
            {             
                files = p_repository.getXMLFilesFromDefinition();
                pathProvider.put(files);
            }
            else
            {
                File theFile = p_repository.getXMLFileFromDefinition(objName);
                pathProvider.put(theFile);
                if(theFile == null)
                {
                    logger.finest(LoggerMessageLocalizer.getMessage("NONEXISENT_OBJECT"));
                    return;
                }
                else
                {
                    files = new File[]{theFile}; 
                }
            }
            //remover o description
            for(int i = 0; i < files.length; i++)
            {
                if(files[i].canWrite())
                {
                    if(files[i].getName().toUpperCase().endsWith("$BO.XML"))                        
                        toNewXMLVersion(files[i]);
                }
                else
                {
                    logger.finest(files[i].getAbsolutePath() + " "+LoggerMessageLocalizer.getMessage("ERROR_CANT_WRITE"));
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally 
        {
        }
    }

    public int objectDelete(String objName) throws boRuntimeException 
    {
        boolean ok = false;
        javax.transaction.UserTransaction ut=null;
        try 
        {
            boolean noPermission = false;
            File bodef = new File(p_bcfg.getDefinitiondir()); 
            File[] files = p_repository.getXMLFiles();
            pathProvider.put( files);
            logger.finest(LoggerMessageLocalizer.getMessage("FILES_THAT_WILL_BE_CHANGED_BY_THIS_REFACTORING")+": ");
            File theFile = p_repository.getXMLFile( objName);
            if(theFile == null)
            {
                return -1;
            }
            if(!theFile.canWrite() || !theFile.getParentFile().canWrite())
            {
                logger.finest(theFile.getAbsolutePath() + " "+LoggerMessageLocalizer.getMessage("ERROR_CANT_WRITE") );
                noPermission = true;
            }            
            String boname= theFile.getName().substring(0, theFile.getName().indexOf("$"));
            ArrayList stackToRemove = new ArrayList();
            ArrayList stackTochange = new ArrayList();
            stackToRemove.add(boname);
            getExtendImplementsObj(objName, stackToRemove, files);
            Stack stack;
            if(stackToRemove.size() > 0)
                files = removeDeletes(stackToRemove, files);
            logger.finest(LoggerMessageLocalizer.getMessage("OBJECTS_TO_REMOVE")+": ");
            for(int i = 0; i < stackToRemove.size(); i++)
            {
                stack = new Stack();
                logger.finest((String)stackToRemove.get(i));
                referencesStack((String)stackToRemove.get(i), stack, files);
                stackTochange.add(stack);
            }
            File aux;
            ArrayList search = new ArrayList();
            logger.finest(LoggerMessageLocalizer.getMessage("OBJECTS_THAT_WILL_BE_CHANGED_BY_THIS_REFACTORING")+": ");
            for(int i = 0; i < stackTochange.size(); i++)
            {
                stack = (Stack)stackTochange.get(i);
                while(stack.size() > 0)
                {
                    aux = p_repository.getXMLFile((String)stack.pop());
                    if(!search.contains(aux))
                    {
                        if(!aux.canWrite())
                        {
                            logger.finest(aux.getAbsolutePath() + " "+LoggerMessageLocalizer.getMessage("ERROR_CANT_WRITE"));
                            noPermission = true;
                        }
                        else
                        {
                            logger.finest(aux.getName().substring(0, aux.getName().toUpperCase().indexOf("$BO.XML")));
                        }
                        search.add(aux);
                    }
                }
            }
            if(noPermission) return -4;
            File[] toChange = new File[search.size()];
            for(int i = 0; i < search.size(); i++)
            {
                toChange[i] = (File)search.get(i);
            }
            for(int i = 0; i < stackToRemove.size(); i++)
            {
                if(!objectDelete((String)stackToRemove.get(i), toChange))
                {
                    return -1;
                }
            }
            
            //Efectuar o build
            boBuilderOptions builderOptions = new boBuilderOptions();
            builderOptions.setFullBuild( true );
            boBuilder.buildAll(ebo, builderOptions, new boBuilderProgress() );
            ok = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally 
        {
            try
            {
                if(ut != null)
                {
                    if(ok) ut.commit();
                    else ut.rollback();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return 0;
    }
    public boolean objectDelete(String objName, File[] toChange) throws boRuntimeException 
    {
        try 
        {   
            File bodef = new File(p_bcfg.getDefinitiondir()); 
            File theFile = p_repository.getXMLFileFromDefinition(objName);
            boDefHandler bdef = boDefHandler.getBoDefinition(objName);
            
            //remover dos restantes xmlfiles
            for(int i = 0; i < toChange.length; i++)
            {
                deleteObjFrom(objName, toChange[i]);
            }
            
            //limpar a bd
            ArrayList bridgesTables = new ArrayList();
            boDefAttribute attributes[] = bdef.getAttributesDef();
            String name = null;
            for(int i = 0; i < attributes.length; i++)
            {
                String g = attributes[i].getName();
                if(attributes[i].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE && attributes[i].getMaxOccurs()>1)
                {
                    name = objName.toUpperCase()+ "$" + attributes[i].getName().toUpperCase();
                    if(name.length() > 30)
                    {
                        CRC32 xcrc = new CRC32();
                        String xname = name.substring(0,10);
                        xcrc.update(name.getBytes());
                        name = xname + "_" +xcrc.getValue();
                        bridgesTables.add(name);
                    }                    
                    else
                    {                        
                        bridgesTables.add(name);
                    }
                }
            }
            if(!deleteObj(ebo, objName, bdef.getBoPhisicalMasterTable(), bridgesTables.toArray()))
            {
                return false;
            }
            
            //limpar o resources
            deleteResources(new File(p_bcfg.getDeployJspDir() + File.separator + "resources" + File.separator + objName));

            //remover
            theFile.delete();
            if(theFile.getParentFile().listFiles().length == 0)
                theFile.getParentFile().delete();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;            
        }
        finally 
        {

        }
        
        return true;        
    }


    public int objectRefactoring(String fromObj, String toObj) throws boRuntimeException 
    {
        try 
        {
            File bodef = new File(p_bcfg.getDefinitiondir()); 
            File[] files = p_repository.getXMLFiles();
            pathProvider.put( files);
            logger.finest(LoggerMessageLocalizer.getMessage("FILES_THAT_WILL_BE_CHANGED_BY_THIS_REFACTORING")+": ");
            File theFile = p_repository.getXMLFile(fromObj);
            if(theFile == null)
            {
                return -1;
            }
            if(!theFile.canWrite() || !theFile.getParentFile().canWrite())
            {
                logger.finest(theFile.getAbsolutePath() + " "+LoggerMessageLocalizer.getMessage("ERROR_CANT_WRITE"));
                return -4;
            }
            logger.finest(theFile.getAbsolutePath() );            
            String boname= theFile.getName().substring(0, theFile.getName().indexOf("$"));
            Stack stack = new Stack();           
            referencesStack(fromObj, stack, files);
            File[] toChange = new File[stack.size()];
            File aux = null;            
            boolean noPermission = false;
            for(int i = 0; stack.size() > 0; i++)
            {
                aux = p_repository.getXMLFileFromDefinition((String)stack.pop());                
                if(!aux.canWrite())
                {
                    logger.finest(aux.getAbsolutePath() + " "+LoggerMessageLocalizer.getMessage("ERROR_CANT_WRITE"));
                    noPermission = true;
                }
                else
                {
                    logger.finest(aux.getAbsolutePath());
                    toChange[i] = aux;
                } 
            }
            if(noPermission) return -4;
        
            //alterar theFile
            changeFile(ebo, fromObj, toObj, theFile);            
            
            //alterar os restantes
            for(int i = 0; i < toChange.length; i++)
            {
                changeFile(ebo, fromObj, toObj, toChange[i]);
            }
            
            //remover o anterior
            theFile.delete();

            //Efectuar o build
            boBuilderOptions builderOptions = new boBuilderOptions();
            builderOptions.setFullBuild( true );
            boBuilder.buildAll(ebo, builderOptions, new boBuilderProgress() );
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally 
        {

        }
        
        return 0;        
    }


    private static boolean verifyAttribute(String lookingForboname, boDefAttribute[] a_att)
    {   
        boDefHandler[] defObj;
        for(int i=0;i<a_att.length;i++) 
        {            
            if(isObjectName(lookingForboname, a_att[i].getType())) 
            {
                return true;
            }                        
            if(a_att[i].getBridge() != null && a_att[i].getBridge().haveBridgeAttributes()) 
            {
                boDefAttribute[] b_batt = a_att[i].getBridge().getBoAttributes();
                boolean ret = verifyAttribute(lookingForboname, b_batt);
                if(ret) return true;
            }
            defObj = a_att[i].getObjects();
            if(defObj != null && defObj.length > 0)
            {
                for(int z = 0; z < defObj.length; z++)
                {
                    if(defObj[z].getBoName().equals(lookingForboname))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void getExtendImplementsObj(String lookingForboname,ArrayList stack, File[] listOfFiles) 
    {
        for(int x = 0; x < listOfFiles.length; x++)
        {
            if(listOfFiles[x].getName().toUpperCase().endsWith("$BO.XML"))
            {
                String boname = listOfFiles[x].getName().substring(0, listOfFiles[x].getName().indexOf("$"));
                boDefHandler cdef = getDefOf(boname);
                if(stack.indexOf(boname)==-1 && !boname.equals("boObject") ) 
                {            
                    boDefHandler allbo = getDefOf(boname);
                    boDefAttribute[] a_att;
                    if(allbo.getBoSuperBo()!=null && allbo.getBoSuperBo().equals(lookingForboname)) 
                    {
                        stack.add(boname);
                    }
                    else if( cdef.getClassType() == boDefHandler.TYPE_INTERFACE  && allbo.getBoImplements( lookingForboname ))
                    {
                        stack.add(boname);
                    }
                }
            }
        }
    }

    private static File[] removeDeletes(ArrayList stack, File[]listOfFiles)
    {
        String boname = null;
        ArrayList r = new ArrayList();
        for(int x = 0; x < listOfFiles.length; x++)
        {
            if(listOfFiles[x].getName().toUpperCase().endsWith("$BO.XML"))
            {
                boname = listOfFiles[x].getName().substring(0, listOfFiles[x].getName().indexOf("$"));
                boDefHandler cdef = getDefOf(boname);
                if(!stack.contains(boname))
                {
                    r.add(listOfFiles[x]);
                }
            }
        }
        File[] ret = new File[r.size()];
        for(int x = 0; x < r.size(); x++)
        {
            ret[x]=(File)r.get(x); 
        }
        return ret;
    }

    private static void referencesStack(String lookingForboname,Stack stack, File[] listOfFiles) 
    {
        boolean alreadyinStack;
        for(int x = 0; x < listOfFiles.length; x++)
        {
            alreadyinStack = false;
            if(listOfFiles[x].getName().toUpperCase().endsWith("$BO.XML"))
            {
                String boname = listOfFiles[x].getName().substring(0, listOfFiles[x].getName().indexOf("$"));
                boDefHandler cdef = getDefOf(boname);
                if(stack.indexOf(boname)==-1 && !boname.equals("boObject") ) 
                {            
                    boDefHandler allbo = getDefOf(boname);
                    boDefAttribute[] a_att;
                    if(allbo.getBoSuperBo()!=null && allbo.getBoSuperBo().equals(lookingForboname)) 
                    {
                        stack.push(boname);
                        alreadyinStack = true;
                    }
                    else if( cdef.getClassType() == boDefHandler.TYPE_INTERFACE  && allbo.getBoImplements( lookingForboname ))
                    {
                        stack.push(boname);
                        alreadyinStack = true;
                    }
                    else 
                    {
                        a_att = allbo.getBoAttributes();                    
                        boDefHandler[] defObj;
                        if(!alreadyinStack)
                        {
                            if(verifyAttribute(lookingForboname, a_att))
                            {
                                stack.push(boname);
                                alreadyinStack = true;
                            }
                        }                        
                    } 
                    if(!alreadyinStack)
                    {
                        //vou verificar nas views
                        boDefViewer[] viewers = cdef.getBoViewers();
                        for (int i = 0;viewers!=null && i < viewers.length ; i++)
                        {
                            ngtXMLHandler[] frms=viewers[i].getForms();
                            ngtXMLHandler viewer;
                            for (int j = 0;frms!=null && j <frms.length ; j++)  
                            {                                
                                viewer = frms[j];
                                ngtXMLHandler[] childs=viewer.getChildNodes();
                                if(lookfor(lookingForboname, childs))
                                {
                                    stack.push(boname);
                                    alreadyinStack = true;
                                }
                            }
                        }                        
                    }
                }
            }
        }
    }
    
    private static boolean lookfor(String boname, ngtXMLHandler[] tags)    
    {
        ngtXMLHandler tag;
        String s = null;
        for(int i=0;tags!= null && i<tags.length; i++)
        {
            tag = tags[i];
            if(tag.getNodeName().equalsIgnoreCase("img"))
            {
                s = tag.getText();                
                if(isObjectName(boname.toUpperCase(), s.toUpperCase()))
                {
                    return true;
                }
            }
            if(tag.getNodeName().equalsIgnoreCase("boql"))
            { 
                s = tag.getText();
                if(isObjectName(boname.toUpperCase(), s.toUpperCase()))
                {
                    return true;
                }
            }            
            if(tag.getNodeName().equalsIgnoreCase("action"))
            {
                s = tag.getText();
                if(isObjectName(boname.toUpperCase(), s.toUpperCase()))
                {
                    return true;
                }
            }
            else if(tag.getNodeName().equalsIgnoreCase(boname))
            {
                return true;
            }
            if(lookfor(boname, tag.getChildNodes()))
                return true;
        }
        return false;
    }

    private static void changeAttributes(String lookingForboname, String newBoname, boDefAttribute[] a_att, Node root, ArrayList path)
    {
        boDefHandler[] defObj = null;
        for(int i=0;i<a_att.length; i++) 
        {
            if(isObjectName(lookingForboname, a_att[i].getType()))
            {
                ArrayList nArr = new ArrayList();
                for(int k = 0; k < path.size(); k++)
                {
                    nArr.add((String)path.get(k));
                }
                nArr.add(a_att[i].getName());
                Node xmlAtt = getAttribute(root, nArr, 0);
                changeAttribute(xmlAtt, lookingForboname, newBoname);
            }                        
            if(a_att[i].getBridge() != null && a_att[i].getBridge().haveBridgeAttributes()) 
            {
                ArrayList nArr = new ArrayList();
                for(int k = 0; k < path.size(); k++)
                {
                    nArr.add((String)path.get(k));
                }
                nArr.add(a_att[i].getName());
                nArr.add("bridge");
                nArr.add("attributes");                
                boDefAttribute[] b_batt = a_att[i].getBridge().getBoAttributes();
                changeAttributes(lookingForboname, newBoname, b_batt, root, nArr);                
            }
            defObj = a_att[i].getObjects();
            if(defObj != null && defObj.length > 0)
            {
                for(int z = 0; z < defObj.length; z++)
                {                                
                    if(defObj[z].getBoName().equals(lookingForboname))
                    {
                        ArrayList nArr = new ArrayList();
                        for(int k = 0; k < path.size(); k++)
                        {
                            nArr.add((String)path.get(k));
                        }
                        nArr.add(a_att[i].getName());
                    
                        Node xmlObj = getAttributeObject(root, nArr);
                        changeObjects(xmlObj, lookingForboname, newBoname);
                    }
                }
            }
        }    
    }

    private static void getAttributesOfType(String type, boDefAttribute[] a_att, ArrayList path, ArrayList ret)
    {
        boDefHandler[] defObj = null;
        for(int i=0;i<a_att.length; i++) 
        {
            if(isObjectName(type, a_att[i].getType()))
            {            
                StringBuffer nArr = new StringBuffer();
                for(int k = 0; k < path.size(); k++)
                {
                    nArr.append((String)path.get(k));
                    nArr.append(".");
                }
                nArr.append(a_att[i].getName());
                ret.add(nArr.toString());
            }                        
            if(a_att[i].getBridge() != null && a_att[i].getBridge().haveBridgeAttributes()) 
            {
                ArrayList nArr = new ArrayList();
                for(int k = 0; k < path.size(); k++)
                {
                    nArr.add((String)path.get(k));
                }
                nArr.add(a_att[i].getName());
                nArr.add("bridge");
                nArr.add("attributes");                
                boDefAttribute[] b_batt = a_att[i].getBridge().getBoAttributes();
                getAttributesOfType(type, b_batt, nArr, ret);                
            }
            defObj = a_att[i].getObjects();
            if(defObj != null && defObj.length > 0)
            {
                for(int z = 0; z < defObj.length; z++)
                {                                
                    if(defObj[z].getBoName().equals(type))
                    {
                        StringBuffer nArr = new StringBuffer();
                        for(int k = 0; k < path.size(); k++)
                        {
                            nArr.append((String)path.get(k));
                            nArr.append(".");
                        }
                        nArr.append(a_att[i].getName());
                        nArr.append(".");
                        nArr.append("objects");
                        nArr.append(".");
                        nArr.append("object");
                        nArr.append(".");
                        nArr.append(type);
                        ret.add(nArr.toString());
                    }
                }
            }
        }    
    }
    
    
    private static void changeFile(EboContext eboctx, String lookingForboname, String newBoname, File theFile) throws IOException
    {         
        String ss = theFile.getAbsolutePath();
        XMLDocument doc =  ngtXMLUtils.loadXMLFile(theFile.getAbsolutePath());
        String boname = theFile.getName().substring(0, theFile.getName().indexOf("$"));
        boolean isTheFile = boname.equals(lookingForboname);
        Node root = getRoot(doc, boname);        
        boDefHandler cdef = getDefOf(boname);
        boDefHandler allbo = getDefOf(boname);
        String tableName = allbo.getBoPhisicalMasterTable();
        boDefAttribute[] a_att;
        if(allbo.getBoSuperBo()!=null && allbo.getBoSuperBo().equals(lookingForboname)) 
        {
            Node xmlDerivedFrom = getDerivedFrom(root);
            changeSuperBo(xmlDerivedFrom, lookingForboname, newBoname);
        }
        if( cdef.getClassType() == boDefHandler.TYPE_INTERFACE  && allbo.getBoImplements( lookingForboname ))
        {
            Node xmlImplements = getImplements(root);
            changeImplements(doc, lookingForboname, newBoname);
        }
    
        a_att = allbo.getBoAttributes();
        boDefHandler[] defObj;
        ArrayList arr = new ArrayList();
        arr.add("attributes");
        changeAttributes(lookingForboname, newBoname, a_att, root, arr); 
        /*
        for(int i=0;i<a_att.length; i++) 
        {
            if(isObjectName(lookingForboname, a_att[i].getType()))
            {
                Node xmlAtt = getAttribute(root, a_att[i].getName());
                changeAttribute(xmlAtt, lookingForboname, newBoname);
            }                        
            if(a_att[i].getBridge() != null && a_att[i].getBridge().haveBridgeAttributes()) 
            {
                boDefAttribute[] b_batt = a_att[i].getBridge().getBoAttributes();                            
                for(int z=0;z<b_batt.length;z++) 
                {
                    String sss = b_batt[z].getType();
                    if(b_batt[z].getType().equals(lookingForboname)) 
                    {
                        Node xmlAtt = getBridgeAttributes(root, a_att[i].getBridge().getName(), b_batt[z].getName());                                
                        changeAttribute(xmlAtt, lookingForboname, newBoname);                                
                    }
                }
            }
            defObj = a_att[i].getObjects();
            if(defObj != null && defObj.length > 0)
            {
                for(int z = 0; z < defObj.length; z++)
                {                                
                    if(defObj[z].getBoName().equals(lookingForboname))
                    {
                        Node xmlObj = getAttributeObject(root, a_att[i].getName());
                        changeObjects(xmlObj, lookingForboname, newBoname);
                    }
                }
            }
        }
        */
       
        //vou verificar nas views
        changeViews(root, lookingForboname, newBoname);
      
        if(isTheFile)
        {
            //vou escrever o update a efectuar
            Node XmlDeployInfo = getDeployInfo(root);
            String path = theFile.getParent() + File.separator + newBoname + boBuilder.TYPE_SC; 
            writeUpdate(eboctx, lookingForboname, newBoname, tableName, path);
            changeboObjectName(root, doc, lookingForboname, newBoname);
            File newFile =new File(theFile.getParentFile().getAbsolutePath() + File.separator + newBoname + "$bo.xml");
            if(!newFile.exists())
            {
                newFile.createNewFile();
            }
//            writeToFile(ngtXMLUtils.getXMLBytes(doc), newFile);
            ngtXMLUtils.saveXML(doc, newFile);
        }
        else
        {
//            writeToFile(ngtXMLUtils.getXMLBytes(doc), theFile);
            ngtXMLUtils.saveXML(doc, theFile);
        }
    }
    
    private static void writeToFile(byte[] xml, File f)
    {
        FileOutputStream fos = null;
        try
        {                 
            fos = new FileOutputStream(f);
            fos.write(xml);
            fos.flush();
        }
        catch (FileNotFoundException e)
        {
           //ignora 
        }
        catch (IOException e)
        {
            //ignora
        }
        finally
        {
            try
            {
                if(fos != null)
                {
                    fos.close();
                    fos = null;
                }
            }
            catch (Exception e)
            {
                //ignora
            }
        }
    }
    
    private static Node getRoot(XMLDocument doc, String boname)
    {
        NodeList n= doc.getChildNodes();
        Node aux;
        for(int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);            
            if(aux.getNodeName().equalsIgnoreCase(boname))
            {
                return aux;
            }
        }
        return null;
    }
    

    private static Node getGeneral(Node doc)
    {
        NodeList n= doc.getChildNodes();
        Node aux;
        for(int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);
            String s = aux.getNodeName(); 
            if(aux.getNodeName().equalsIgnoreCase("general"))
            {
                return aux;
            }
        }
        return null;
    }
    private static Node getDerivedFrom(Node doc)
    {
        NodeList n= doc.getChildNodes();
        Node aux;
        for(int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);
            String s = aux.getNodeName(); 
            if(aux.getNodeName().equalsIgnoreCase("general"))
            {
                n = aux.getChildNodes();
                for(int j = 0; j < n.getLength(); j++)
                {
                    aux = n.item(j);                    
                    if(aux.getNodeName().equalsIgnoreCase("derivedFrom"))
                    {
                        return aux;
                    }
                }
                return null;
            }
        }
        return null;
    }
 
    private static Node getDeployInfo(Node doc)
    {
        NodeList n= doc.getChildNodes();
        Node aux;
        for(int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);
            String s = aux.getNodeName(); 
            if(aux.getNodeName().equalsIgnoreCase("general"))
            {
                n = aux.getChildNodes();
                for(int j = 0; j < n.getLength(); j++)
                {
                    aux = n.item(j);                    
                    if(aux.getNodeName().equalsIgnoreCase("deploy_information"))
                    {
                        return aux;
                    }
                }
                return null;
            }
        }
        return null;
    } 
 
 
    private static Node getImplements(Node doc)
    {
        NodeList n= doc.getChildNodes();
        Node aux;
        for(int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);
            String s = aux.getNodeName(); 
            if(aux.getNodeName().equalsIgnoreCase("general"))
            {
                n = aux.getChildNodes();
                for(int j = 0; j < n.getLength(); j++)
                {
                    aux = n.item(j);                    
                    if(aux.getNodeName().equalsIgnoreCase("implements"))
                    {
                        return aux;
                    }
                }
                return null;
            }
        }
        return null;
    } 
    
//    private static Node getAttribute(Node doc, String attName)
//    {
//        NodeList n= doc.getChildNodes();
//        Node aux;
//        for(int i = 0; i < n.getLength(); i++)
//        {
//            aux = n.item(i);
//            String s = aux.getNodeName(); 
//            if(aux.getNodeName().equalsIgnoreCase("Attributes"))
//            {
//                n = aux.getChildNodes();
//                for(int j = 0; j < n.getLength(); j++)
//                {
//                    aux = n.item(j);                    
//                    if(aux.getNodeName().equalsIgnoreCase(attName))
//                    {
//                        return aux;
//                    }
//                }
//                return null;
//            }
//        }
//        return null;
//    }
    
    private static Node getAttribute(Node doc, ArrayList path, int pos)
    {
        NodeList n= doc.getChildNodes();
        Node aux;
        for(int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);
            String s = aux.getNodeName(); 
            if(aux.getNodeName().equalsIgnoreCase((String)path.get(pos)))
            {               
                if((pos + 1) == path.size())
                {
                    return aux;
                }
                else
                {
                    return getAttribute(aux, path, pos + 1);
                }       
            }
        }
        return null;
    }    
    
//    private static Node getBridgeAttributes(Node doc, String attName, String brigeAttribute)
//    {
//        Node aux = getAttribute(doc, attName);
//        NodeList n= aux.getChildNodes();
//        for(int i = 0; i < n.getLength(); i++)
//        {
//            aux = n.item(i);
//            String s = aux.getNodeName(); 
//            if(aux.getNodeName().equalsIgnoreCase("bridge"))
//            {
//                n = aux.getChildNodes();
//                for(int j = 0; j < n.getLength(); j++)
//                {
//                    aux = n.item(j);                    
//                    if(aux.getNodeName().equalsIgnoreCase("attributes"))
//                    {
//                        n = aux.getChildNodes();
//                        for(int k = 0; k < n.getLength(); k++)
//                        {
//                            aux = n.item(k);                    
//                            if(aux.getNodeName().equalsIgnoreCase(brigeAttribute))
//                            {
//                                return aux;
//                            }
//                        }
//                        return null;
//                    }
//                }
//                return null;
//            }
//        }
//        return null;
//    }    

    private static Node getAttributeObject(Node doc, ArrayList path)
    {
        Node aux = getAttribute(doc, path, 0);
        NodeList n= aux.getChildNodes();
        for(int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);
            String s = aux.getNodeName(); 
            if(aux.getNodeName().equalsIgnoreCase("objects"))
            {               
                return aux;               
            }
        }
        return null;
    }
    
    private static Node getEdit(Node doc)
    {
        NodeList n= doc.getChildNodes();
        Node aux;
        for(int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);
            String s = aux.getNodeName(); 
            if(aux.getNodeName().equalsIgnoreCase("viewers"))
            {
                n = aux.getChildNodes();
                for(int j = 0; j < n.getLength(); j++)
                {
                    aux = n.item(j);
                    
                    if(aux.getNodeName().equalsIgnoreCase("general"))
                    {        
                        n = aux.getChildNodes();
                        for(int m = 0; m < n.getLength(); m++)
                        {
                            aux = n.item(m);
                            if(aux.getNodeName().equalsIgnoreCase("forms"))
                            {
                                n = aux.getChildNodes();
                                for(int k = 0; k < n.getLength(); k++)
                                {
                                    aux = n.item(k);
                                    if(aux.getNodeName().equalsIgnoreCase("edit"))
                                    {
                                        return aux;
                                    }
                                }
                                return null;
                            }
                        }
                    }
                }
                return null;
            }
        }
        return null;
    }

    private static void changeViews(Node doc, String oldName, String newName)
    {
        NodeList n= doc.getChildNodes();
        Node aux;
        for(int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);
            String s = aux.getNodeName(); 
            if(aux.getNodeName().equalsIgnoreCase("viewers"))
            {
                n = aux.getChildNodes();
                for(int j = 0; j < n.getLength(); j++)
                {
                    aux = n.item(j);
                    
                    if(aux.getNodeName().equalsIgnoreCase("general"))
                    {        
                        n = aux.getChildNodes();
                        for(int m = 0; m < n.getLength(); m++)
                        {
                            aux = n.item(m);
                            if(aux.getNodeName().equalsIgnoreCase("forms"))
                            {
                                n = aux.getChildNodes();
                                for(int k = 0; k < n.getLength(); k++)
                                {
                                    aux = n.item(k);
                                    if(aux.getNodeType() != aux.TEXT_NODE)
                                    {
                                        String qs = aux.getNodeName();
                                        changeView(aux, oldName, newName);
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private static Node changeboObjectName(Node doc, Node parent, String oldName, String newName)
    {        
        String s = doc.getNodeName();
        Node no;
        if(doc.getNodeName().equalsIgnoreCase(oldName))
        {
            XMLElement elem =  new XMLElement(newName);
            parent.replaceChild(elem, doc);
            NodeList l = doc.getChildNodes();
            for(int j = 0; j < l.getLength(); j++)
            {
                if(l.item(j).getNodeType() != Node.TEXT_NODE)
                {
                    no = (l.item(j)).cloneNode(true);
                    elem.appendChild(no);
                }
            }
            
        }
        return null;
    }
    
    private static void changeSuperBo(Node doc, String lookingBoname, String boname)
    {   
        String s = doc.getFirstChild().getNodeValue();
        doc.getFirstChild().setNodeValue(replaceString(s, lookingBoname, boname));
    }
    private static void changeImplements(Node doc, String lookingBoname, String boname)
    {
        NodeList auxList = doc.getChildNodes();
        Node auxNode;
        for(int i=0;auxList!= null && i<auxList.getLength(); i++)
        {
            auxNode = auxList.item(i);
            if(auxNode.getNodeName().equalsIgnoreCase("type"))
            {
                String s = auxNode.getFirstChild().getNodeValue();
                auxNode.getFirstChild().setNodeValue(replaceString(s, lookingBoname, boname));
            }
        }
    }
    private static void changeAttribute(Node att, String lookingBoname, String boname)
    {
        NodeList auxList = att.getChildNodes();
        Node auxNode;
        for(int i=0;auxList!= null && i<auxList.getLength(); i++)
        {
            auxNode = auxList.item(i);
            if(auxNode.getNodeName().equalsIgnoreCase("type"))
            {
                String s = auxNode.getFirstChild().getNodeValue();
                auxNode.getFirstChild().setNodeValue(replaceString(s, lookingBoname, boname));
            }
        }
    }

    private static void changeBridgeAttribute(Node doc, String lookingBoname, String boname)
    {
        //changeSuperBo
    }
    private static void changeObjects(Node doc, String lookingBoname, String boname)
    {
        NamedNodeMap nmp = doc.getAttributes();
        Node att = nmp.getNamedItem("typeObjects");
        if(att != null)
        {
            if(lookingBoname.equalsIgnoreCase(att.getNodeValue()))
            {
                att.setNodeValue(replaceString(att.getNodeValue(), lookingBoname, boname));
            }
        }
        NodeList auxList = doc.getChildNodes();
        Node auxNode;
        for(int i=0;auxList!= null && i<auxList.getLength(); i++)
        {
            auxNode = auxList.item(i);
            if("object".equalsIgnoreCase(auxNode.getNodeName()))
            {
                String s= auxNode.getFirstChild().getNodeValue();
                if(s.equalsIgnoreCase(lookingBoname))
                {
                    auxNode.getFirstChild().setNodeValue(replaceString(s, lookingBoname, boname));
                }
            }
        }
    }    
    private static void changeView(Node edit, String lookingBoname, String boname)    
    {
        NodeList auxList = edit.getChildNodes();
        Node auxNode;
        for(int i=0;auxList!= null && i<auxList.getLength(); i++)
        {
            auxNode = auxList.item(i);
            String s;
            if(auxNode.getNodeName().equalsIgnoreCase("img"))
            {
                s = auxNode.getFirstChild().getNodeValue();                
                if(isObjectName(lookingBoname.toUpperCase(), s.toUpperCase()))
                {
                    auxNode.getFirstChild().setNodeValue(replaceString(s, lookingBoname, boname));
                }
            }
            if(auxNode.getNodeName().equalsIgnoreCase("boql"))
            { 
                s = auxNode.getFirstChild().getNodeValue();                
                if(isObjectName(lookingBoname.toUpperCase(), s.toUpperCase()))
                {
                    auxNode.getFirstChild().setNodeValue(replaceString(s, lookingBoname, boname));
                }
                s = auxNode.getFirstChild().getNodeValue();
            }
            
            if(auxNode.getNodeName().equalsIgnoreCase("action"))
            {
                s = auxNode.getFirstChild().getNodeValue();                
                if(isObjectName(lookingBoname.toUpperCase(), s.toUpperCase()))
                {
                    auxNode.getFirstChild().setNodeValue(replaceString(s, lookingBoname, boname));                    
                }
            }
            
            if(auxNode.getNodeName().equalsIgnoreCase(lookingBoname))
            {                
                XMLElement elem = new XMLElement(boname);                
                //attributes
                NamedNodeMap nnm = auxNode.getAttributes();
                for(int j = 0; j < nnm.getLength(); j++)
                {
                    elem.setAttribute(nnm.item(j).getNodeName(), nnm.item(j).getNodeValue());
                }
                
                //ChildNodes
                NodeList l = auxNode.getChildNodes();
                for(int j = 0; j < l.getLength(); j++)
                {
                    String g = l.item(j).getNodeName(); 
                    if(l.item(j).getNodeType() != Node.TEXT_NODE)                        
                        elem.appendChild((l.item(j).cloneNode(true)));
                }
                edit.replaceChild(elem, auxNode);
            }            
            changeView(auxNode, lookingBoname, boname);
        }
    }
    
    private static void writeUpdate(EboContext ctx, String fromObj, String toObj, String tableName, String path) throws IOException
    {
        XMLDocument doc;
        XMLElement scripts = null;
        Node scriptNode = null;
        File scriptFile = new File(path);
        String ebo_registryFullTableName = "";
        if(boRepository.getRepository(ctx.getApplication(), "default").getSchemaName() != null &&
            !"".equals(boRepository.getRepository(ctx.getApplication(), "default").getSchemaName()))
        {
            ebo_registryFullTableName = boRepository.getRepository(ctx.getApplication(), "default").getSchemaName() + "." ;
        }
        ebo_registryFullTableName +="Ebo_Registry";
        if(!scriptFile.exists())
        {
            scriptFile.createNewFile();
            doc = new XMLDocument();
            scripts = new XMLElement("scripts");
            doc.appendChild(scripts);
        }
        else
        {
            doc =  ngtXMLUtils.loadXMLFile(scriptFile.getAbsolutePath());
            NodeList n= doc.getChildNodes();
            Node aux;
            boolean found = false;
            for(int i = 0; i < n.getLength() && !found; i++)
            {
                aux = n.item(i);
                String s = aux.getNodeName(); 
                if(aux.getNodeName().equalsIgnoreCase("scripts"))
                {
                    scriptNode = aux;
                    found = true;
                }
            }
            if(!found)
                scripts = new XMLElement("scripts");
        }        
        XMLElement sql1 = new XMLElement("sql");
        XMLElement sql2 = new XMLElement("sql");
        XMLElement sql3 = new XMLElement("sql");
        
        //update Ebo_Registry set CLSID = 'XXXX', NAME = 'XXXX' where CLSID = 'YYYYY' 
        StringBuffer sb = new StringBuffer("update "+ ebo_registryFullTableName +" set CLSID = '");
        sb.append(toObj).append("', Name = '").append(toObj).append("' where CLSID = '");
        sb.append(fromObj).append("'");

        //update TTTTT set CLASSNAME = 'XXXX' where CLASSNAME = 'YYYYY' 
        StringBuffer sbTable = new StringBuffer("update ").append(tableName).append(" set CLASSNAME = '");
        sbTable.append(toObj).append("' where CLASSNAME = '").append(fromObj ).append("'");
        
        //update Ebo_ClsReg set NAME = 'XXXX' where NAME = 'YYYYY'
        StringBuffer sbClsreg = new StringBuffer("update Ebo_ClsReg set Name = '");
        sbClsreg.append(toObj).append("' where NAME = '").append(fromObj ).append("'");
        
        sql1.addText(sb.toString());
        sql2.addText(sbTable.toString());
        sql3.addText(sbClsreg.toString());
        XMLElement updateSQL = new XMLElement("updateSQL");            
        updateSQL.setAttribute("date", getDate());
        updateSQL.appendChild(sql1);
        updateSQL.appendChild(sql2);
        updateSQL.appendChild(sql3);
        if(scriptNode != null)
        {
            scriptNode.appendChild(updateSQL);
        }
        else
        {
            scripts.appendChild(updateSQL);
        }
//        writeToFile(ngtXMLUtils.getXMLBytes(doc), scriptFile);
        ngtXMLUtils.saveXML(doc, scriptFile);
    }
    private static String getDate() 
    {
        try 
        {
            java.util.Date d = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss");
            return formatter.format(d);
        }
        catch (Exception ex) 
        {
            return null;
        }
    }
    
    private static String replaceString(String s, String lookingBoname, String newBoname)
    {
        String ret="";
        int end = 0;
        int i = 0;
        while(end < s.length() && i >= 0)
        {
            i = s.toUpperCase().indexOf(lookingBoname.toUpperCase(), end);
            if(i >= 0)
            {
                ret = ret + s.substring(end, i);            
                end = i + lookingBoname.length();                         
                String sub = s.substring(i, end );
                if(isLowerCase(sub))
                {
                    ret = ret + newBoname.toLowerCase(); 
                }
                else
                {
                    ret = ret + newBoname;
                }
            }
            else
            {                
                ret = ret + s.substring(end);
            }
        }
        return ret;
    }
    
    private static boolean isLowerCase(String s)
    {
        char aux;
        for(int i = 0; i < s.length(); i++)
        {
            aux = s.charAt(i);
            if(!(aux >= '0' && aux <='9') && aux != '_' && (aux >= 'A' && aux <= 'Z'))
            {
                return false;
            }
        }
        return true;
    }
    
    public static synchronized final boDefHandler getDefOf(String boname) 
    {
        boDefHandler bobj;            

        String sbofile = pathProvider.getBOPath(boname);
        File xbofile = new File(sbofile);
        if(!xbofile.exists()) 
        {
            return null;
        }
                
        XMLDocument xmldoc = ngtXMLUtils.loadXMLFile(sbofile);
        bobj = new boDefHandlerImpl(xmldoc,true);
        //boBuilder.fillSystemAttributes(bobj);
        //boBuilder.buildInterfaces(bobj);               
        return bobj;
    }
 
    private static boolean isObjectName(String s, String s1)
    {
        int start;
        if( (start = s1.indexOf(s)) != -1)
        {
            int last = start + s.length();
            if(s1.length() > last)
            {
                char next = s1.charAt(last);
                if((next >= 'A' && next <='Z') || next == '_' || (next >= '0' && next <='9') )
                {
                    return false;
                }
            }
            if(start != 0)
            {
                char previous = s1.charAt(start - 1);
                if((previous >= 'A' && previous <='Z') || previous == '_' || (previous >= '0' && previous <='9') )
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
 
 /* Methods to delete object*/
    
    private static boolean deleteObj(EboContext boctx, String objName, String masterTable, Object[] bridgesTables)
    {
        Connection con = null, conDef = null;
        int size = 0;
        PreparedStatement ps = null, ps2 = null, ps3 = null;
        ResultSet rs = null, rs2 = null;
        StringBuffer sb = new StringBuffer();
        boolean tableWithBoui = false;
        String ebo_registryFullTableName = "";
        try 
        {            
            if(boRepository.getRepository(boctx.getApplication(), "default").getSchemaName() != null &&
                !"".equals(boRepository.getRepository(boctx.getApplication(), "default").getSchemaName()))
            {
                ebo_registryFullTableName = boRepository.getRepository(boctx.getApplication(), "default").getSchemaName() + "." ;
            }
            ebo_registryFullTableName +="Ebo_Registry";
            boctx.beginContainerTransaction();
            con = boctx.getConnectionData();
            //desligar as contraints
            conDef = boctx.getConnectionDef();
            ps2 = con.prepareStatement("select constraint_name,table_name from USER_CONSTRAINTS WHERE R_constraint_name=?");
            ps2.setString(1, "PK_"+objName.toUpperCase());
            rs2 = ps2.executeQuery();
            OracleDBM agf = null;
            while(rs2.next()) 
            {
                agf = new OracleDBM();
//                agf.setEnvironment( boctx ,"");
            // Exists Foreign key's referenced by this primary key.. droping foreign keys
                ps3 = conDef.prepareStatement("DELETE NGTDIC WHERE OBJECTNAME=? AND SCHEMA=? AND TABLENAME=? AND OBJECTTYPE='FK'");
                ps3.setString(1,rs2.getString(1));
                ps3.setString(2,"DATA");
                ps3.setString(3,rs2.getString(2));
                ps3.executeUpdate();
                ps3.close();
                agf.executeDDL("alter table "+rs2.getString(2)+" drop constraint "+rs2.getString(1),"DATA");
                agf.close();
            }
            rs2.close();
            ps2.close();
        
            ArrayList arr = new ArrayList();
            long iT = System.currentTimeMillis();            
            ps = con.prepareStatement("select ui$ from oebo_registry t where t.name = '" + objName +"'");
            rs = ps.executeQuery();
            while (rs.next())
            {
                arr.add(new Long(rs.getLong(1)));
            }
            ps.close();
            rs.close();
            
            ps = con.prepareStatement("select table_name from user_tables where upper(table_name) not in ('OEBO_REGISTRY', 'OEBO_CLSREG')");
            rs = ps.executeQuery();
            
            String tableName, columnName;
            long boui = -1;
            long key = -1;
            String xsql;
            while(rs.next())
            {
                tableName = rs.getString(1);
                ps2 = con.prepareStatement("select distinct(column_name) from user_tab_columns where table_name = ? and column_name like '%$' OR column_name = 'BOUI'");
                ps2.setString(1, tableName);
                rs2 = ps2.executeQuery();

                while(rs2.next())
                {
                    columnName = rs2.getString(1);
                    ps3 = con.prepareStatement("delete from " + tableName + " where " + columnName + " = ?");
//                    if("boui".equalsIgnoreCase(columnName))
//                        ps3 = con.prepareStatement("delete from " + tableName + " where " + columnName + " = ?");
//                    else
//                        ps3 = con.prepareStatement("update " + tableName + " set " + columnName + " = null where " + columnName + " = ?");
                    for(int i = 0; i < arr.size(); i++)
                    {
                        ps3.setLong(1, ((Long)arr.get(i)).longValue());
                        
                        try
                        {
                            size = size + ps3.executeUpdate();
                        }
                        catch (Exception e)
                        {
                            //vou ignorar
                            logger.finest(LoggerMessageLocalizer.getMessage("ERROR_EXECUTING_FOR_TABLE")+": " + tableName + " "+LoggerMessageLocalizer.getMessage("COLUMN")+": " + columnName);
                        }
                    }
                    ps3.close();
                }
                rs2.close();
                ps2.close();
            }
            rs.close();
            ps.close();
            
            //limpar o Ebo_Registry
            ps2 = con.prepareStatement("delete from " + ebo_registryFullTableName + " t where t.name = ?");
            ps2.setString(1, objName);
            ps2.executeUpdate();
            ps2.close();
            
            //limpar o Clsreg
            ps2 = con.prepareStatement("delete from ebo_clsreg$attributes t where t.parent$ in (select boui from ebo_clsreg a where a.name = ?)");
            ps2.setString(1, objName);
            ps2.executeUpdate();
            ps2.close();

            ps2 = con.prepareStatement("delete from ebo_clsreg$security t where t.parent$ in (select boui from ebo_clsreg a where a.name = ?)");
            ps2.setString(1, objName);
            ps2.executeUpdate();
            ps2.close();            
            
            ps2 = con.prepareStatement("delete from ebo_clsreg t where t.name = ?");
            ps2.setString(1, objName);
            ps2.executeUpdate();
            ps2.close();
             
            //drop da mastertable e das bridges            
            
            for(int i = 0; i < bridgesTables.length; i++)
            {
                ps2 = con.prepareStatement("drop table " + (String)bridgesTables[i]);
                ps2.executeUpdate();
                ps2.close();    
            }
            
            ps2 = con.prepareStatement("drop table " + masterTable);
            ps2.executeUpdate();
            ps2.close();
            
            //NGTDIC e NGTVIRTUALTABLES            
            ps2 = conDef.prepareStatement("delete from ngtdic where tablename = '" + masterTable.toUpperCase() + "'");
            ps2.executeUpdate();
            ps2.close();
/*            
            ps2 = conDef.prepareStatement("delete from ngtvirtualtables where stablename = '" + masterTable.toUpperCase() + "'");
            ps2.executeUpdate();
            ps2.close();
*/            
            
            for(int i = 0; i < bridgesTables.length; i++)
            {
                ps2 = conDef.prepareStatement("delete from ngtdic where tablename = '" + ((String)bridgesTables[i]).toUpperCase()+ "'");
                ps2.executeUpdate();
                ps2.close();
/*                
                ps2 = conDef.prepareStatement("delete from ngtvirtualtables where stablename = '" + ((String)bridgesTables[i]).toUpperCase()+ "'");
                ps2.executeUpdate();
                ps2.close();
*/                
            }

            rs.close();
            ps.close();
            boctx.commitContainerTransaction();
            sb.append(MessageLocalizer.getMessage("NUMBER_OF_DELETES")+": " + size  + "\n");
            sb.append(MessageLocalizer.getMessage("TOTAL_TIME")+": " + ((System.currentTimeMillis() - iT)/1000));
        }
        catch(Exception e)
        {            
            e.printStackTrace();
            return false;
        }
        finally 
        {
            try
            {
                if(rs != null)
                {
                    rs.close();
                }
            }catch(Exception e)
            {
            }
            try
            {
                if(ps != null)
                {
                    ps.close();
                }
            }catch(Exception e)
            {
            }            
             try
             {
                if(rs2 != null)
                {
                    rs2.close();
                }
            }catch(Exception e)
            {
            }
            try
            {
                if(ps2 != null)
                {
                    ps2.close();
                }
            }catch(Exception e)
            {
            }
            try
            {
                if(ps3 != null)
                {
                    ps3.close();
                }
            }catch(Exception e)
            {
            }
            try
            {
                if(conDef != null)
                {
                    conDef.rollback();
                    conDef.close();
                }
            }catch(Exception e)
            {
            }            
            try
            {
                if(con != null)
                {
                    boctx.rollbackContainerTransaction();
                    con.close();
                }
            }catch(Exception e)
            {
            }
        }
        return true;
    }
 
    private static void deleteResources(File f)
    {
        File[] aux = f.listFiles();
        for(int i = 0; aux != null && i < aux.length; i++)
        {
            if(aux[i].isDirectory())
            {
                deleteResources(aux[i]);
            }
            aux[i].delete();
        }
        f.delete();
    }
    
    private static void deleteObjFrom(String objName, File f) throws boRuntimeException
    {
        String ss = f.getAbsolutePath();
        XMLDocument doc =  ngtXMLUtils.loadXMLFile(f.getAbsolutePath());
        String boname = f.getName().substring(0, f.getName().indexOf("$"));
        Node root = getRoot(doc, boname);
        boDefHandler allbo = getDefOf(boname);
        String tableName = allbo.getBoPhisicalMasterTable();
        boDefAttribute[] a_att;
        //buscar a lista de atributos do tipo objname
        a_att = allbo.getBoAttributes();
        boDefHandler[] defObj;
        ArrayList arr = new ArrayList();
        ArrayList ret = new ArrayList();
        arr.add("attributes");            
        getAttributesOfType(objName, a_att, arr, ret);            
        //vou remover os atributos
        removeAttributes(root, ret);
        
        //vou verificar remover das views os atributos
        for(int i = 0; i < ret.size(); i++)
        {
            String[] attRR = ClassUtils.splitToArray((String)ret.get(i), ".");
            removeViews(root, attRR[attRR.length -1],objName);
        }
        
        //vou remover das views as chamadas aos atributos
        
//        writeToFile(ngtXMLUtils.getXMLBytes(doc), f);
        ngtXMLUtils.saveXML(doc, f);
    }
 
    private static void removeAttributes(Node root, ArrayList ret)
    {
        String[] path;
        NodeList n = null;
        for(int i = 0; i < ret.size(); i++)
        {
            path = ClassUtils.splitToArray((String)ret.get(i), ".");
            Node attNode = getAttribute(root, new ArrayList(Arrays.asList(path)), 0);
            attNode.getParentNode().removeChild(attNode);
        }
    }
 
    private static void removeViews(Node doc, String attributeName, String attType)
    {
        NodeList n= doc.getChildNodes();
        Node aux;
        for(int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);
            String s = aux.getNodeName(); 
            if(aux.getNodeName().equalsIgnoreCase("viewers"))
            {
                n = aux.getChildNodes();
                for(int j = 0; j < n.getLength(); j++)
                {
                    aux = n.item(j);
                    
                    if(aux.getNodeName().equalsIgnoreCase("general"))
                    {        
                        n = aux.getChildNodes();
                        for(int m = 0; m < n.getLength(); m++)
                        {
                            aux = n.item(m);
                            if(aux.getNodeName().equalsIgnoreCase("forms"))
                            {
                                n = aux.getChildNodes();
                                for(int k = 0; k < n.getLength(); k++)
                                {
                                    aux = n.item(k);
                                    if(aux.getNodeType() != aux.TEXT_NODE)
                                    {
                                        String qs = aux.getNodeName();
                                        removeViewNode(aux, attributeName);
                                        removeViewText(aux, attributeName, attType);
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

//    private static void removeViewNode(Node edit, String attributeName)    
//    {
//        NodeList auxList = edit.getChildNodes();
//        Node auxNode;
//        for(int i=0;auxList!= null && i<auxList.getLength(); i++)
//        {
//            auxNode = auxList.item(i);
//            String s;
//            if(auxNode.getNodeName().equalsIgnoreCase(attributeName))
//            {
//                auxNode.getParentNode().removeChild(auxNode);
//                i--;
//            }
//            if(auxNode.getNodeName().equalsIgnoreCase("attribute"))
//            { 
//                s = auxNode.getFirstChild().getNodeValue();                
//                if(isObjectName(attributeName.toUpperCase(), s.toUpperCase()))
//                {
//                    auxNode.getParentNode().removeChild(auxNode);
//                    i--;
//                }
//            }
//            removeViewNode(auxNode, attributeName);
//        }
//    }
    
    private static boolean removeViewNode(Node edit, String attributeName)    
    {
        NodeList auxList = edit.getChildNodes();
        Node auxNode;
        boolean removed = false;
        for(int i=0;auxList!= null && i<auxList.getLength(); i++)
        {
            auxNode = auxList.item(i);
            String s;
            if(auxNode.getNodeName().equalsIgnoreCase(attributeName))
            {
                auxNode.getParentNode().removeChild(auxNode);
                auxNode = null;
                removed = true;
                i--;
            }
            if(auxNode.getNodeName().equalsIgnoreCase("attribute"))
            { 
                s = auxNode.getFirstChild().getNodeValue();                
                if(isObjectName(attributeName.toUpperCase(), s.toUpperCase()))
                {
                    auxNode.getParentNode().removeChild(auxNode);
                    auxNode = null;
                    removed = true;
                    i--;
                }
            }            
            if(auxNode != null)
            {
                if(removeViewNode(auxNode, attributeName))
                {
                    if(auxNode.getNodeName().equalsIgnoreCase("col"))
                    {
                        auxNode.getParentNode().removeChild(auxNode);
                        i--;
                        removed = true;
                    }                   
                    else if(auxNode.getChildNodes().getLength() == 0)
                    {
                        auxNode.getParentNode().removeChild(auxNode);
                        i--;
                        removed = true;
                    }
                }                
            }
        }
        return removed;
    }    
    
    private static boolean removeViewText(Node edit, String attributeName, String attType)    
    {
        NodeList auxList = edit.getChildNodes();
        Node auxNode, auxNodeParent;
        int size = auxList.getLength();
        boolean removed = false;
        for(int i=0;auxList!= null && i<auxList.getLength(); i++)
        {
            auxNode = auxList.item(i);
            String s;
            if(auxNode.getNodeType() == Node.TEXT_NODE)
            {
                s = auxNode.getNodeValue();                
                if(isObjectName(attributeName.toUpperCase(), s.toUpperCase())||
                    isObjectName(attType.toUpperCase(), s.toUpperCase()))
                {
                    auxNode.getParentNode().removeChild(auxNode);
                    auxNode = null;
                    removed = true;
                    i--;
                }
                else if("".equals(s.trim()))
                {
                    auxNode.getParentNode().removeChild(auxNode);
                    auxNode = null;
                    removed = true;
                    i--;
                }
            }
            if(auxNode != null && auxNode.getNodeName().equalsIgnoreCase("boql"))
            { 
               //a pensar
            }
            
            if(auxNode != null && auxNode.getNodeName().equalsIgnoreCase("action"))
            {
                s = auxNode.getFirstChild().getNodeValue();                
                if(isObjectName(attributeName.toUpperCase(), s.toUpperCase()) ||
                   isObjectName(attType.toUpperCase(), s.toUpperCase()) )
                {
                    auxNode.getParentNode().removeChild(auxNode);
                    auxNode = null;
                    removed = true;
                    i--;
                }
            }
            if(auxNode != null)
            {
                if(removeViewText(auxNode, attributeName, attType))
                {
                    if(auxNode.getNodeName().equalsIgnoreCase("option"))
                    {
                        auxNode.getParentNode().removeChild(auxNode);
                        i--;
                        removed = true;
                    }
                    else if(auxNode.getNodeName().equalsIgnoreCase("div"))
                    {
                        auxNode.getParentNode().removeChild(auxNode);
                        i--;
                        removed = true;
                    }
                    else if(auxNode.getChildNodes().getLength() == 0)
                    {
                        auxNode.getParentNode().removeChild(auxNode);
                        i--;
                        removed = true;
                    }
                }                
            }
        }
        return removed;
    }
 
 //refactoring toNewXML
    private static void toNewXMLVersion(File f)
    {
        XMLDocument doc =  ngtXMLUtils.loadXMLFile(f.getAbsolutePath());
        String boname = f.getName().substring(0, f.getName().indexOf("$"));
        Node root = getRoot(doc, boname);
        boDefHandler allbo = getDefOf(boname);
        boDefAttribute[] a_att = allbo.getBoAttributes();
        ArrayList arr = new ArrayList();
        arr.add("attributes");
        if(!newVersion(a_att, root, arr))
        {
            removeDescriptionAndContraintsFromAttributes(a_att, root, arr);
            removeDescriptionFromCategories(root);        
            ngtXMLUtils.saveXML(doc, f);
        }
    }
    private static boolean isToRemove(String[] list, String s)
    {
        for(int i = 0; i < list.length; i++)
        {
            if(s.equals(list[i]))
            {
                return true;
            }
        }
//        return Arrays.binarySearch(list, s) >= 0;
        return false;
    }

    private static boolean newVersion(boDefAttribute[] a_att, Node root, ArrayList path)
    {
        boDefHandler[] defObj = null;
        ngtXMLHandler[] cNodes = null;
        ArrayList nArr = new ArrayList(); 
        for(int k = 0; k < path.size(); k++)
        {
            nArr.add((String)path.get(k));
        }   
        String s;
        String type;
        int nDecimals = -1;
        for(int i=0;i<a_att.length; i++) 
        {
            if(i>0)
            {
                nArr.remove(nArr.size() - 1);
            }
            nArr.add(a_att[i].getName());
            
            Node xmlAtt = getAttribute(root, nArr, 0);
            if(xmlAtt != null)
            {
                NodeList n = xmlAtt.getChildNodes();
            
                for(int j = 0; j < n.getLength(); j++)
                {
                    if("disableWhen".equalsIgnoreCase(n.item(j).getNodeName()))
                    {
                        return true;
                    }
                    else if("hiddenWhen".equalsIgnoreCase(n.item(j).getNodeName()))
                    {
                        return true;
                    }
                    else if("renderAsLov".equalsIgnoreCase(n.item(j).getNodeName()))
                    {
                        return true;
                    }
                    else if("mask".equalsIgnoreCase(n.item(j).getNodeName()))
                    {
                        return false;
                    }
                    else if("constraints".equalsIgnoreCase(n.item(j).getNodeName()))
                    {
                        return false;
                    }
                }
            }            
        }
        return false;
    }   
    
    private static void removeDescriptionAndContraintsFromAttributes(boDefAttribute[] a_att, Node root, ArrayList path)
    {
        boDefHandler[] defObj = null;
        ngtXMLHandler[] cNodes = null;
        boolean hasConstraint = false;
        String[]toRemove=new String[]{"description", "tooltip", "formulas", "formula", "gui", "mask", "transform", "values", "decimals"};
        ArrayList nArr = new ArrayList(); 
        for(int k = 0; k < path.size(); k++)
        {
            nArr.add((String)path.get(k));
        }   
        String s;
        String type;
        int nDecimals = -1;
        for(int i=0;i<a_att.length; i++) 
        {
            hasConstraint = false;
            s = a_att[i].getName();
            type = a_att[i].getType();
            nDecimals = getDecimals(type);
            if(i>0)
            {
                nArr.remove(nArr.size() - 1);
            }
            nArr.add(a_att[i].getName());
            
            Node xmlAtt = getAttribute(root, nArr, 0);
            if(xmlAtt != null)
            {
                NodeList n = xmlAtt.getChildNodes();
            
                for(int j = 0; j < n.getLength(); j++)
                {
                    String s2 = n.item(j).getNodeName();    
                    if(isToRemove(toRemove, n.item(j).getNodeName().toLowerCase()))
                    {
                        xmlAtt.removeChild(n.item(j));
                        j--;
                    }
                    else if("constraints".equalsIgnoreCase(n.item(j).getNodeName()))
                    {
                        treatConstraint(xmlAtt, n.item(j));
                        hasConstraint = true;
                        j--;
                    }
                    else if("canAlter".equalsIgnoreCase(n.item(j).getNodeName()) ||
                            "canChange".equalsIgnoreCase(n.item(j).getNodeName()))
                    {                            
                            XMLElement elem2 = new XMLElement("disableWhen");
                            if(n.item(j).getFirstChild() != null)
                            {
                                String vl =n.item(j).getFirstChild().getNodeValue();
                                if("n".equalsIgnoreCase(vl) || "no".equalsIgnoreCase(vl))
                                    elem2.addText("Y");
                                else
                                    elem2.addText("N");
                            }
                            xmlAtt.removeChild(n.item(j));
                            xmlAtt.appendChild(elem2);
                            j--;
                    }
                }                        
                if(a_att[i].getBridge() != null && a_att[i].getBridge().haveBridgeAttributes()) 
                {                
                    nArr.add("bridge");
                    nArr.add("attributes");                
                    boDefAttribute[] b_batt = a_att[i].getBridge().getBoAttributes();
                    removeDescriptionAndContraintsFromAttributes(b_batt, root, nArr);
                    nArr.remove(nArr.size() - 1);
                    nArr.remove(nArr.size() - 2);
                }
                insertNews(a_att[i], xmlAtt);
                createDefaultsDecimals(xmlAtt, nDecimals);
                if(!hasConstraint)
                {
                    createDefaults(xmlAtt, nDecimals);
                }
            }            
        }    
    }   

    private static int getDecimals(String s)
    {
        int virPos;
        if(s.startsWith("number"))
        {
            if((virPos = s.indexOf(",")) != -1)
            {
                try
                {
                    return Integer.parseInt(s.substring(virPos + 1, s.indexOf(")", virPos)).trim() );
                }
                catch (Exception e)
                {                
                }
            }
            return 0;
        }
        return -1;
    }

    private static void removeDescriptionFromCategories(Node doc)
    {
        NodeList cNodes = null;
        NodeList n= doc.getChildNodes();
        Node aux;
        for(int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);
            String s = aux.getNodeName(); 
            if(aux.getNodeName().equalsIgnoreCase("viewers"))
            {
                n = aux.getChildNodes();
                for(int j = 0; j < n.getLength(); j++)
                {
                    aux = n.item(j);
                    
                    if(aux.getNodeName().equalsIgnoreCase("general"))
                    {        
                        n = aux.getChildNodes();
                        for(int m = 0; m < n.getLength(); m++)
                        {
                            aux = n.item(m);
                            if(aux.getNodeName().equalsIgnoreCase("categories"))
                            {
                                n = aux.getChildNodes();
                                for(int h = 0; h < n.getLength(); h++)
                                {
                                    cNodes = n.item(h).getChildNodes();
                                    for(int b = 0; b < cNodes.getLength(); b++)
                                    {
                                        if("description".equalsIgnoreCase(cNodes.item(b).getNodeName()))
                                        {
                                            n.item(h).removeChild(cNodes.item(b));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void insertNews(boDefAttribute att, Node parent)
    {
        XMLElement elem = new XMLElement("formula");
        parent.appendChild(elem);
        
        XMLElement elem2 = new XMLElement("defaultValue");
        parent.appendChild(elem2);
        
        XMLElement elem4 = new XMLElement("valid");
        elem4.addText("Y");
        parent.appendChild(elem4);
    }

    private static void createDefaults(Node parent, int nDecimals)
    {
        XMLElement elem = new XMLElement("renderAsLov");
        elem.addText("N");
        parent.appendChild(elem);
        
        XMLElement elem2 = new XMLElement("hiddenWhen");
        elem2.addText("N");
        parent.appendChild(elem2);
        
        XMLElement elem3 = new XMLElement("required");
        String n = getMinOccurs(parent);
        if(n == null || "0".equals(n))
        {
            elem3.addText("N");
        }
        else
        {
            elem3.addText("Y");
        }
        parent.appendChild(elem3);
    }
    private static void createDefaultsDecimals(Node parent, int nDecimals)
    {
        if(nDecimals != -1)
        {
            XMLElement elem4 = new XMLElement("decimals");
            elem4.addText(String.valueOf(nDecimals));
            parent.appendChild(elem4);
        }
    }    
    private static void treatConstraint(Node parent, Node constraint)
    {
        //nós que passam para fora: disableWhen; hiddenWhen; required; defaultValue;
        NodeList n = constraint.getChildNodes();
        String[] values = new String[2];
        String h = parent.getNodeName();
        boolean isLov= getLovValues(constraint, values);
        for(int i = 0; i < n.getLength(); i++)
        {
            if("hiddenWhen".equalsIgnoreCase(n.item(i).getNodeName()) ||
                "required".equalsIgnoreCase(n.item(i).getNodeName())
            )
            {
                parent.appendChild(n.item(i));
            }
        }
        if(isLov)
        {
            XMLElement elem = new XMLElement("lov");
            elem.setAttribute("name", values[0]);
            if(values[1] != null)
                elem.setAttribute("editable", values[1]);
            else
                elem.setAttribute("editable", "N");
            parent.appendChild(elem);
                
            XMLElement elem2 = new XMLElement("renderAsLov");
            elem2.addText("Y");
            parent.appendChild(elem2);    
        }
        else
        {
            XMLElement elem2 = new XMLElement("renderAsLov");
            elem2.addText("N");
            parent.appendChild(elem2);
        }
        parent.removeChild(constraint);
    }
    
    private static boolean getLovValues(Node constraint, String[] values)
    {
        NodeList n = constraint.getChildNodes();
        String req = null;
        Boolean hasName = null;
        for(int i = 0; i < n.getLength(); i++)
        {
            if("lov".equalsIgnoreCase(n.item(i).getNodeName()))
            {
                n = n.item(i).getChildNodes();
                for(int j = 0; j < n.getLength(); j++)
                {
                    if("name".equalsIgnoreCase(n.item(j).getNodeName()) && n.item(j).getFirstChild() != null)
                    {
                        String g =n.item(j).getFirstChild().getNodeValue(); 
                        values[0] = n.item(j).getFirstChild().getNodeValue() != null? n.item(j).getFirstChild().getNodeValue().trim():null;
                    }
                    if("required".equalsIgnoreCase(n.item(j).getNodeName()) && n.item(j).getFirstChild() != null)
                    {
                        String g =n.item(j).getFirstChild().getNodeValue();
                        if("N".equals(g))
                        {
                            values[1] = "Y";
                        }
                        values[1] = "N";
                    }
                }
            }
        }
        return values[0] == null ? false:values[0].length() > 0;
    }
    
    private static String getMinOccurs(Node att)
    {
        NodeList n = att.getChildNodes();
        Boolean hasName = null;
        for(int i = 0; i < n.getLength(); i++)
        {
            if("minoccurs".equalsIgnoreCase(n.item(i).getNodeName()))
            {
                if(n.item(i).getFirstChild() != null &&
                    n.item(i).getFirstChild().getNodeValue() != null &&
                    n.item(i).getFirstChild().getNodeValue().trim().length() > 0
                )
                {
                    return n.item(i).getFirstChild().getNodeValue().trim();
                }
                else
                    return null;
            }
        }
        return null;
    }  
 
 
//------------------------------ Tranform to runtimeAddress

    public int transform (EboContext ctx, String fromTable, String middleTable, 
        String keyOnFromTable, String keyOnMiddleTable, String fieldToTranformOnFromTable, 
        String fieldToTranformOnMiddletTable, String transformClass)
    {
        PreparedStatement ps = null, ps2 = null;
        ResultSet rs = null, rs2 = null;
        long keyOnMidTab, bouiToTranform;
        Hashtable map = new Hashtable();
        Connection con = null;
        Connection con2 = null;
        int n;
        long newBoui;
        try
        {
            Class c = Class.forName(transformClass);
            Transformer t = (Transformer)c.newInstance();
            StringBuffer sbTest = new StringBuffer("select count(*) from ");
            sbTest.append(fromTable).append(" where ").append(keyOnFromTable)
                .append(" = ?");
                
            StringBuffer sbTest2 = new StringBuffer("select count(*) from ");
            sbTest2.append(fromTable).append(" where ").append(keyOnFromTable)
                .append(" = ? and ").append(fieldToTranformOnFromTable) 
                .append(" = ?");
        
            StringBuffer sbInsert = new StringBuffer("update ");
            sbInsert.append(fromTable).append(" set ").append(fieldToTranformOnFromTable)
                .append(" = ? where ").append(keyOnFromTable).append("=?");
            
            StringBuffer sbInsertBridge = new StringBuffer("update ");
            sbInsertBridge.append(fromTable).append(" set ").append(fieldToTranformOnFromTable)
                .append(" = ? where ").append(keyOnFromTable).append("=? and ")
                .append(fieldToTranformOnFromTable).append("=?");
            
            StringBuffer sb = new StringBuffer("select ");
            sb.append(keyOnMiddleTable).append(", ").append(fieldToTranformOnMiddletTable)
                .append(" from ").append(middleTable);
            con = ctx.getConnectionData();
            ps = con.prepareStatement(sb.toString());
            rs = ps.executeQuery();
            boObject obj, nObj;
            while(rs.next())
            {
                ctx.beginContainerTransaction();
                con2 = ctx.getConnectionData();
                keyOnMidTab = rs.getLong(1);
                bouiToTranform = rs.getLong(2);
                if(bouiToTranform > 0)
                {
                    obj = boObject.getBoManager().loadObject(ctx, bouiToTranform);
                    if(!obj.getName().equals(t.transformsTo()))
                    {
                        newBoui = t.transform(ctx, null, bouiToTranform);
                        nObj = boObject.getBoManager().loadObject(ctx, newBoui);
                        ps2 = con2.prepareStatement(sbTest.toString());
                        ps2.setLong(1, keyOnMidTab);
                        rs2 = ps2.executeQuery();                
                        rs2.next();
                        n = rs2.getInt(1);
                        rs2.close();
                        ps2.close();
                        nObj.update();
                        if(n == 1)
                        {
                            ps2 = con2.prepareStatement(sbInsert.toString());
                            ps2.setLong(1, newBoui);
                            ps2.setLong(2, keyOnMidTab);
                            ps2.executeUpdate();
                            ps2.close();
                        }
                        else if(n > 1)
                        {
                            ps2 = con2.prepareStatement(sbInsertBridge.toString());
                            ps2.setLong(1, newBoui);
                            ps2.setLong(2, keyOnMidTab);
                            ps2.setLong(3, bouiToTranform);
                            ps2.executeUpdate();
                            ps2.close();
                        }
                        else
                        {
                            logger.finest(LoggerMessageLocalizer.getMessage("UPDATE_WAS_NOT_EXECUTED")+" [" + keyOnMidTab + ", " + bouiToTranform + "]");
                        }                        
                        ctx.commitContainerTransaction();
                    }
                }
            }
            rs.close();
            ps.close();
        }
        catch(Exception e)
        {
            try
            {
                ctx.rollbackContainerTransaction();
            }
            catch (Exception _e)
            {
                
            }
            e.printStackTrace();
            logger.finest(LoggerMessageLocalizer.getMessage("FINISHED_WITH_ERRORS"));
            return -1;
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e)
                {
                    //ignore
                }
            }
            
            if(ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (Exception e)
                {
                    
                }
            }
            if(rs2 != null)
            {
                try
                {
                    rs2.close();
                }
                catch (Exception e)
                {
                    //ignore
                }
            }
            
            if(ps2 != null)
            {
                try
                {
                    ps2.close();
                }
                catch (Exception e)
                {
                    
                }
            }
        }
        logger.finest(LoggerMessageLocalizer.getMessage("FINISHED_WITHOUT_ERRORS"));
        return 0;
    }
 
    
}