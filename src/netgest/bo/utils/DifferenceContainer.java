/*Enconding=UTF-8*/
package netgest.bo.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title: DifferenceContainer </p>
 * <p>Description: Repositório de diferenças entre boObjects </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public final class DifferenceContainer 
{
    /**
     * Nome do objecto do tipo do objecto comparado
     */
    private String objectName;
    /**
     * Boui do objecto source
     */
    private long bouiSrc;
    /**
     * Boui do objecto destination
     */    
    private long bouiDst;
    /**
     * Repositório onde são guardadas as diferenças entre os atributos.
     */
    private List attDifList = new ArrayList();
    
    /**
     * Repositório onde são guardadas as diferenças dos attributos das bridges e multivalues.
     */
    private List bridgeDiff = new ArrayList();
    
    /**
     * Repositório onde são guardados, os objectos apagados das bridges.
     */
    private List bridgeSrcDiff = new ArrayList();
    
    /**
     * Repositório onde são guardados, os objectos adicionados as bridges.
     */
    private List bridgeDstDiff = new ArrayList();
    /**
     * Repositório onde são guardados, os filhos.
     */
    private List diffContainerChilds = new ArrayList();
    
    public DifferenceContainer(String objectName, long bouiSrc, long bouiDst)
    {
        this.objectName = objectName;
        this.bouiSrc = bouiSrc;
        this.bouiDst = bouiDst;
    }
    /**
     * Regista um novo objecto no repositório com as alterações dos atributos.    
     * @param obj Objecto identificativo
     */
    public final void registerAttDifference(Object obj) 
    {    
        attDifList.add(obj);
    }
    /**
     * Regista um novo objecto no repositório com as alterações dos atributos das bridges.    
     * @param obj Objecto identificativo 
     */
    public final void registerBridgeDifference(Object obj) 
    {    
        bridgeDiff.add(obj);
    }            
    /**
     * Regista uma novo objecto apagado de uma bridge.     
     * @param obj Objecto identificativo
     */
    public final void registerBridgeSrcDiff(Object obj) 
    {    
        bridgeSrcDiff.add(obj);
    }
    
    /**
     * Regista um novo objecto adicionado a uma bridge.     
     * @param obj Objecto identificativo
     */
    public final void registerBridgeDstDiff(Object obj) 
    {
        bridgeDstDiff.add(obj);
    }
   
    /**
     * Regista um novo filho.     
     * @param obj Objecto identificativo
     */
    public final void registerChild(Object obj) 
    {
        diffContainerChilds.add(obj);
    }    
    /**
     * Devolve o um objecto respeitante as diferenças de um determinado atributo
     * @deprecated usar com boui
     * @param attName Nome do Atributo
     * @return objecto com as diferenças, null caso este atributo não tenha diferenças.
     */    
    public DifferenceElement getDifferenceElement(String attName)
    {
        DifferenceElement diffElem = null;
        for (Iterator objects = attDifList.iterator(); objects.hasNext() ;) {
            diffElem = (DifferenceElement)objects.next();
            if(attName.equals(diffElem.getAttributeName()) && diffElem.getBridgeName() == null)
            {
                return diffElem;
            }
        }
        return null;
    }
    /**
     * Devolve o um objecto respeitante as diferenças de um determinado atributo e boui
     * @param attName Nome do Atributo
     * @param boui Boui do boObject, do Atributo 
     * @return objecto com as diferenças, null caso este atributo não tenha diferenças.
     */    
    public DifferenceElement getDifferenceElement(String attName, long boui)
    {
        DifferenceElement diffElem = null;
        if(this.bouiSrc == boui){
            for (Iterator objects = attDifList.iterator(); objects.hasNext() ;) {
                diffElem = (DifferenceElement)objects.next();
                if(attName.equals(diffElem.getAttributeName()) && diffElem.getBridgeName() == null)
                {
                    return diffElem;
                }
            }
        }
        else
        {
            DifferenceContainer diffContainer = null;
            for (Iterator objects = diffContainerChilds.iterator(); objects.hasNext() ;) {
                diffContainer = (DifferenceContainer)objects.next();
                diffElem = diffContainer.getDifferenceElement(attName,boui);
                if(diffElem != null) return diffElem;
            }            
        }
        return null;
    }    
    /**
     * Devolve o um objecto respeitante as diferenças de um determinado atributo de um boObject numa determinada bridge,
     * no próprio <i>container</i> 
     * @param attName Nome do Atributo
     * @param bridgeName Nome da Bridge
     * @param boui Identificador do Objecto 
     * @return objecto com as diferenças, null caso este atributo não tenha diferenças.
     */        
    public DifferenceElement getLocalBridgeDiffElement(String attName,String bridgeName,long boui)
    {
        DifferenceElement diffElem = null;
        for (Iterator objects = bridgeDiff.iterator(); objects.hasNext() ;) {
            diffElem = (DifferenceElement)objects.next(); 
            if(attName.equals(diffElem.getAttributeName()) && bridgeName.equals(diffElem.getBridgeName()) && boui == diffElem.getBoui() )
            {
                return diffElem;
            }
        }
        return null;
    }
    /**
     * Devolve o um objecto respeitante as diferenças de um determinado atributo de um boObject numa determinada bridge,
     * no próprio <i>container</i> e nos filhos
     * @param attName Nome do Atributo
     * @param bridgeName Nome da Bridge
     * @param boui Identificador do Objecto 
     * @return objecto com as diferenças, null caso este atributo não tenha diferenças.
     */        
    public DifferenceElement getBridgeDiffElement(String attName,String bridgeName,long boui)
    {
        DifferenceElement diffElem = getLocalBridgeDiffElement(attName,bridgeName,boui);
        DifferenceContainer diffContainer = null;
        for (Iterator objects = diffContainerChilds.iterator(); objects.hasNext() ;) {
            diffContainer = (DifferenceContainer)objects.next();
            diffElem = diffContainer.getLocalBridgeDiffElement(attName,bridgeName,boui);
            if(diffElem != null) return diffElem;                        
        } 
        return null;
    }    
    /**
     * Devolve o um objecto respeitante as diferenças de um determinado atributo numa determinada bridge
     * @param attName Nome do Atributo
     * @param bridgeName Nome da Bridge
     * @param boui Identificador do Objecto
     * @param relation True se pertence a relação da bridge, False caso contrário 
     * @return objecto com as diferenças, null caso este atributo não tenha diferenças.
     */            
    public DifferenceElement getBridgeDiffElement(String attName,String bridgeName,long boui, boolean relation)
    {
        DifferenceElement diffElem = null;
        for (Iterator objects = bridgeDiff.iterator(); objects.hasNext() ;) {
            diffElem = (DifferenceElement)objects.next(); 
            if(attName.equals(diffElem.getAttributeName()) && bridgeName.equals(diffElem.getBridgeName()) && boui == diffElem.getBoui() && relation == diffElem.isRelation())
            {
                return diffElem;
            }
        }
        return null;
    }
    /**
     * Devolve o um objecto respeitante aos objectos adicionados a uma determinada bridge.
     * @param bridgeName Nome da Bridge
     * @param boui Identificador do Objecto 
     * @return objecto com as diferenças, null caso este atributo não tenha diferenças.
     */    
    public DifferenceElement getLocalBridgeDstDiffElem(String bridgeName, long boui)
    {
        DifferenceElement diffElem = null;
        for (Iterator objects = bridgeDstDiff.iterator(); objects.hasNext() ;) {
            diffElem = (DifferenceElement)objects.next();
            if(bridgeName.equals(diffElem.getBridgeName()) && boui == diffElem.getBoui())
            {
                return diffElem;
            }
        }
        return null;
    }    
    /**
     * Devolve o um objecto respeitante aos objectos adicionados a uma determinada bridge, nos filhos tambem.
     * @param bridgeName Nome da Bridge
     * @param boui Identificador do Objecto 
     * @return objecto com as diferenças, null caso este atributo não tenha diferenças.
     */    
    public DifferenceElement getBridgeDstDiffElem(String bridgeName, long boui)
    {
        DifferenceElement diffElem = getLocalBridgeDstDiffElem(bridgeName,boui);
        DifferenceContainer diffContainer = null;
        for (Iterator objects = diffContainerChilds.iterator(); objects.hasNext() ;) {
            diffContainer = (DifferenceContainer)objects.next();
            diffElem = diffContainer.getLocalBridgeDstDiffElem(bridgeName,boui);
            if(diffElem != null) return diffElem;                        
        }         
        return diffElem;
    }     
    /**
     * Devolve o número de elementos da lista de elementos diferentes existentes no objecto destino
     * @return numero de elementos
     */    
    private int getLocalBridgeDstDiffSize()
    {
        return bridgeDstDiff.size();
    }
    /**
     * Devolve o número de elementos de todas as lista de elementos diferentes existentes no objecto destino
     * @return numero de elementos, 0 se não existirem elementos.
     */    
    public int getBridgeDstDiffSize()
    {
        int size = getLocalBridgeDstDiffSize();
        DifferenceContainer diffContainer = null;
        for (Iterator objects = diffContainerChilds.iterator(); objects.hasNext() ;) {
            diffContainer = (DifferenceContainer)objects.next();
            size += diffContainer.getLocalBridgeDstDiffSize();
        }        
        return size;
    }          
    /**
     * Devolve o número de elementos de uma determinada bridge que foram adicionados.
     * @param bridgeName Nome da Bridge     
     * @return numero de elementos
     */    
    public int getBridgeDstDiffSizeByBridgeName(String bridgeName)
    {
        int count = 0;
        DifferenceElement diffElem = null;
        for (Iterator objects = bridgeDstDiff.iterator(); objects.hasNext() ;) {
            diffElem = (DifferenceElement)objects.next();
            if(bridgeName.equals(diffElem.getBridgeName()))
            {
                count ++;
            }
        }
        return count;
    }
    /**
     * Indica se existem ou não diferenças registadas, bo próprio e nos filhos.     
     * @return True se não existir diferenças, False no caso contrário
     */    
    public boolean isEmpty()
    {
        boolean result = isEmptyLocal();
        DifferenceContainer diffContainer = null;
        for (Iterator objects = diffContainerChilds.iterator(); objects.hasNext() ;) {
            diffContainer = (DifferenceContainer)objects.next();
            if(!diffContainer.isEmptyLocal()) return false;
        } 
        return result;
    }    
    /**
     * Indica se existem ou não diferenças registadas      
     * @return True se não existir diferenças, False no caso contrário
     */    
    public boolean isEmptyLocal()
    {
        boolean result = true;
        if(!attDifList.isEmpty()) return false;        
        if(!bridgeDiff.isEmpty()) return false;        
        if(!bridgeDstDiff.isEmpty()) return false;        
        if(!bridgeSrcDiff.isEmpty()) return false;
        return result;
    }
    
    public Iterator getAttributeDiffIterator()    
    {
        return attDifList.iterator();
    }    
    public Iterator getBridgeDiffIterator()
    {
        return bridgeDiff.iterator();
    }        
    public Iterator getBridgeSrcDiffIterator()
    {
        return bridgeSrcDiff.iterator();
    }                
    public Iterator getBridgeDstDiffIterator()
    {
        return bridgeDstDiff.iterator();
    }        
    public Iterator getDiffChildsIterator()
    {
        return diffContainerChilds.iterator();
    }

    public void setBouiSrc(long bouiSrc)
    {
        this.bouiSrc = bouiSrc;
    }


    public long getBouiSrc()
    {
        return bouiSrc;
    }


    public void setBouiDst(long bouiDst)
    {
        this.bouiDst = bouiDst;
    }


    public long getBouiDst()
    {
        return bouiDst;
    }
}