/*Enconding=UTF-8*/
package netgest.bo.runtime;
/**
 * Um listener dos atributos que vão ser clonados.
 *
 * This interface is a member of the 
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public interface CloneListener 
{
    /**
     * Passa o objecto que está a ser clonado tal como atributo que vai ser clonado.
     * Retorna o valor que este atributo deverá ter no novo objecto.
     * 
     * @param clonningObject objecto que se encontra a ser clonado.
     * @param att atributo que se encontra a ser clonado.
     * @return o valor que este atributo deverá ter no novo objecto. Valores possíveis: boObject, BigDecimal, Long. 
     * @throws boRuntimeException em caso de erro.
     */
    public Object getValue(boObject clonningObject, AttributeHandler att) throws boRuntimeException;
    
    /**
     * Passa o objecto que está a ser clonado tal como atributo da bridge que se encontra a ser clonado.
     * Retorna o valor que este atributo deverá ter no novo objecto.
     * 
     * @param clonningObject objecto que se encontra a ser clonado.
     * @param bh bridge que se encontra a ser clonado.
     * @param line número da linha que se encontra a ser clonada.
     * @param att atributo que se encontra a ser clonado.
     * @return o valor que este atributo deverá ter no novo objecto. Valores possíveis: boObject, BigDecimal, Long. 
     * @throws boRuntimeException em caso de erro.
     */
    public Object getValue(boObject clonningObject, bridgeHandler bh, int line, AttributeHandler att) throws boRuntimeException;
    
    /**
     * Passa o objecto que está a ser clonado tal como objecto referenciado pela bridge que se encontra a ser clonada.
     * Retorna o valor que este atributo deverá ter no novo objecto.
     * 
     * @param clonningObject objecto que se encontra a ser clonado.
     * @param bh bridge que se encontra a ser clonado.
     * @param line número da linha que se encontra a ser clonada.
     * @param lineValueObject valor da linha que se encontra a ser colanda.
     * @return o valor que este atributo deverá ter no novo objecto. Valores possíveis: boObject, BigDecimal, Long. 
     * @throws boRuntimeException em caso de erro.
     */
    public Object getBridgeValue(boObject clonningObject, bridgeHandler bh, int line, boObject lineValueObject) throws boRuntimeException;
    
}