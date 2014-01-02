/*Enconding=UTF-8*/
package netgest.bo.utils;

import java.util.Enumeration;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;




/**
 * <p>Title: DifferenceHelper </p>
 * <p>Description: Compara todos os elementos associados ao boObject
 * (atributos, bridges, bridges atributos, multivalues)</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public final class DifferenceHelper {

   /**
    * Devolve um objecto com as diferenças entre dois boObjects.
    *
    * @param obj1 boObject Source
    * @param obj2 boObject Destination
    * @return diffContainer com as diferenças entre os dois boObjects.
    */
    public static final DifferenceContainer showDifferences(boObject obj1, boObject obj2)
    {
        DifferenceContainer diffContainer = null;
        if(obj1 != null && obj2 != null){
            if(obj1.getBoDefinition().getName().equals(obj2.getBoDefinition().getName()))
            {
                // Repositório onde são guardadas as diferenças entre os atributos.
                diffContainer = new DifferenceContainer(obj1.getBoDefinition().getName(),obj1.getBoui(),obj2.getBoui());
                try
                {
                  compareObjects(diffContainer,obj1, obj2, null,obj1.getBoDefinition().getName());
                }
                catch (boRuntimeException e)
                {
                }
            }
        }
        return diffContainer;
    }

   /**
    * Compara os tipos de atributos (TYPE_ATTRIBUTE,TYPE_OBJECTATTRIBUTE, ...)
    * e relações(RELATION_1_TO_1,RELATION_MULTI_VALUES,..)
    *
    * @param diffContainer Repositório das diferenças
    * @param obj1 boObject Source
    * @param obj2 boObject Destination
    * @param bridge bridge name, null caso não estejamos no ambito de uma bridge.
    */
    private static final void compareObjects(DifferenceContainer diffContainer,boObject obj1, boObject obj2, String bridge, String boObjectName) throws boRuntimeException
    {
        AttributeHandler attHandler = null;
        Enumeration oEnum = obj1.getAttributes().elements();
        while( oEnum.hasMoreElements()  )
        {
            attHandler = (AttributeHandler)oEnum.nextElement();
            if(attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_OBJECTATTRIBUTE)
            {
                // Só faz as bridges do boObjecto avaliado
                if(bridge == null){
                    if(attHandler.getDefAttribute().getRelationType() == attHandler.getDefAttribute().RELATION_1_TO_1)
                    {
                        if(attHandler.getDefAttribute().getChildIsOrphan())
                        {
                            if(!compareAttributes(obj1.getAttribute(attHandler.getName()) ,obj2.getAttribute(attHandler.getName())))
                            {
                                diffContainer.registerAttDifference(new DifferenceElement(boObjectName,attHandler.getName(),obj1,obj2,bridge));
                            }
                        }
                        else
                        {
                            boObject ch_obj1 = (boObject)obj1.getAttribute(attHandler.getName()).getObject();
                            boObject ch_obj2 = (boObject)obj2.getAttribute(attHandler.getName()).getObject();
                            if(ch_obj1==null && ch_obj2!=null)
                            {
                              try
                              {
                                ch_obj1 = boObject.getBoManager().createObject(obj1.getEboContext(),ch_obj2.getName());
                                ch_obj1.setBoui(ch_obj2.getBoui());
                                obj1.getAttribute(attHandler.getName()).setValueLong(ch_obj1.getBoui());
                              }
                              catch (boRuntimeException e)
                              {
                               ch_obj1=null;
                              }
                            }

                            DifferenceContainer listDif  = DifferenceHelper.showDifferences(ch_obj1,ch_obj2);
                            if(listDif != null && !listDif.isEmpty()){
                                diffContainer.registerChild(listDif);
                            }
                        }
                    }
                    else if(attHandler.getDefAttribute().getRelationType() == attHandler.getDefAttribute().RELATION_MULTI_VALUES)
                    {
                        compareMultiValues(diffContainer,attHandler,obj2.getAttribute(attHandler.getName()),boObjectName);
                    }
                    else
                    {
                        // Src -> Dst
                        compareBridge(diffContainer,obj1,obj2,attHandler.getName(),true,boObjectName);
                        // Dst -> Src
                        compareBridge(diffContainer,obj2,obj1,attHandler.getName(),false,boObjectName);
                     }
                }
            }
            else if(attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_ATTRIBUTE)
            {
               if(!compareAttributes(obj1.getAttribute(attHandler.getName()) ,obj2.getAttribute(attHandler.getName())))
               {
                    if(bridge == null)
                    {
                        // Atributo fora da bridge
                        diffContainer.registerAttDifference(new DifferenceElement(boObjectName,attHandler.getName(),obj1,obj2,bridge));
                    }
                    else
                    {
                        // Atributo dentro da bridge
                        diffContainer.registerBridgeDifference(new DifferenceElement(boObjectName,attHandler.getName(),obj1,obj2,bridge,obj1.getBoui(),false));
                    }

               }
            }
        }
    }
   /**
    * Compara os atributos MultiValues e regista as suas diferenças.
    *
    * @param diffContainer Repositório das diferenças
    * @param srcAttHandler AttributeHandler Source
    * @param dstAttHandler AttributeHandler Destination
    */
    private static final void compareMultiValues(DifferenceContainer diffContainer,AttributeHandler srcAttHandler , AttributeHandler dstAttHandler, String boObjectName) throws boRuntimeException
    {
        boObject bo = null;
        boObject[] srcAttBoObjects = srcAttHandler.getObjects();
        boObject[] dstAttBoObjects = dstAttHandler.getObjects();
        long boui;
        if(srcAttBoObjects != null && dstAttBoObjects != null) // Comparar os valores
        {
            for(int i = 0 ; i < srcAttBoObjects.length ; i++)
            {
                bo = srcAttBoObjects[i];
                boui = bo.getBoui();
                if(!existMultiValue(dstAttBoObjects , boui))
                {
                    // Regista a diferença do multivalue
                    diffContainer.registerBridgeDifference(new DifferenceElement(boObjectName,srcAttHandler.getName(),bo,null,true));
                }
            }

            for(int i = 0 ; i < dstAttBoObjects.length ; i++)
            {
                bo = dstAttBoObjects[i];
                boui = bo.getBoui();
                if(!existMultiValue(srcAttBoObjects , boui))
                {
                    // Regista a diferença do multivalue
                    diffContainer.registerBridgeDifference(new DifferenceElement(boObjectName,dstAttHandler.getName(),null,bo,true));
                }
            }
        }
        else if(dstAttBoObjects != null && srcAttBoObjects == null)
        {
            for(int i = 0 ; i < dstAttBoObjects.length ; i++)
            {
                bo = dstAttBoObjects[i];
                // Regista a diferença do multivalue
                diffContainer.registerBridgeDifference(new DifferenceElement(boObjectName,dstAttHandler.getName(),null,bo,true));
            }
        }
        else if(dstAttBoObjects == null && srcAttBoObjects != null)
        {
            for(int i = 0 ; i < srcAttBoObjects.length ; i++)
            {
                bo = srcAttBoObjects[i];
                // Regista a diferença do multivalue
                diffContainer.registerBridgeDifference(new DifferenceElement(boObjectName,srcAttHandler.getName(),bo,null,true));
            }
        }
    }
   /**
    * Verifica a existencia de um MultiValue na lista dada.
    *
    * @param attBoObjects boObject[] lista a pesquisar
    * @param boui do boObject a pesquisar
    * @return True caso exista, False caso contrário.
    */
    public static boolean existMultiValue(boObject[] attBoObjects, long boui)
    {
        // Alterar tem de comparar o objecto
        boolean result = false;
        for(int i = 0 ; i < attBoObjects.length ; i++)
        {
            if(attBoObjects[i].getBoui() == boui)
            {
                return true;
            }
        }
        return result;
    }

   /**
    * Compara os elementos das Bridges e regista as suas diferenças.
    *
    * @param diffContainer Repositório das diferenças
    * @param srcObj boObject Source
    * @param dstObj boObject Destination
    * @param bridgeName nome da bridge
    * @param src indicação do sentido da comparação, True caso o srcObj seja relamente da source, False caso contrário
    */
    private static final void compareBridge(DifferenceContainer diffContainer, boObject srcObj , boObject dstObj, String bridgeName,boolean src, String boObjectName) throws boRuntimeException
    {
        bridgeHandler bHandler = srcObj.getBridge(bridgeName);
        bHandler.beforeFirst();
        while ( bHandler.next() )
        {
             boObject objHandler = bHandler.getObject();
             long boui = objHandler.getBoui();
             if(dstObj.getBridge(bridgeName).haveBoui(boui) && src)
             {
                compareObjects(diffContainer,objHandler ,dstObj.getBridge(bridgeName).getObject(boui),bridgeName,boObjectName);
                compareBridgeAttributes(diffContainer,bHandler,dstObj.getBridge(bridgeName).getObject(boui).getParentBridgeRow().getBridge(),boui,boObjectName);
             }
             else if(src)
             {
                  diffContainer.registerBridgeSrcDiff(new DifferenceElement(boObjectName,boui,bridgeName));
             }
             else if(!dstObj.getBridge(bridgeName).haveBoui(boui) && !src)
             {
                  if(!srcObj.getBridge(bridgeName).isEmpty())
                  {
                    diffContainer.registerBridgeDstDiff(new DifferenceElement(boObjectName,boui,bridgeName));
                  }
             }
          }

    }
   /**
    * Compara os atributos de uma  Bridges e regista as suas diferenças.
    *
    * @param diffContainer Repositório das diferenças
    * @param srcBridgeHandler bridgeHandler Source
    * @param dstBridgeHandler bridgeHandler Destination
    * @param boui Boui do boObject da bridge
    */
    private static final void compareBridgeAttributes(DifferenceContainer diffContainer, bridgeHandler srcBridgeHandler, bridgeHandler dstBridgeHandler,long boui, String boObjectName)  throws boRuntimeException
    {
        Enumeration oEnum = srcBridgeHandler.getAllAttributes().elements();
        AttributeHandler attHandler = null;
        AttributeHandler attHandlerDst = null;
        String name = null;

        while( oEnum.hasMoreElements()  )
        {
            attHandler = (AttributeHandler)oEnum.nextElement();
            name = attHandler.getName();
            attHandlerDst = dstBridgeHandler.getAttribute(attHandler.getDefAttribute().getName());
            if(!compareAttributes(attHandler,attHandlerDst))
            {
                // Regista a diferença do atributo da bridge
                diffContainer.registerBridgeDifference(new DifferenceElement(boObjectName,attHandler.getName(),attHandler,attHandlerDst,srcBridgeHandler.getName(),boui,true));
            }
        }
    }
   /**
    * Compara atributos.
    *
    * @param attHandler1 AttributeHandler
    * @param attHandler2 AttributeHandler
    * @return True caso sejam iguais, False caso contrário.
    */
    private static boolean compareAttributes(AttributeHandler attHandler1, AttributeHandler attHandler2) throws boRuntimeException
    {
        boolean result = false;
        if(attHandler1.getValueObject() != null && attHandler2.getValueObject() != null)
        {
            result = attHandler1.getValueObject().equals(attHandler2.getValueObject());
        }
        else if(attHandler1.getValueObject() == null && attHandler2.getValueObject() == null)
        {
            result =  true;
        }
        return result;
    }
}