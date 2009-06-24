/*Enconding=UTF-8*/

package netgest.bo.ql.V2;

/**
 *
 * Classe muito simples que apenas armazena 3 objectos
 */
public class Triple 
{
    private Object p1;  //first object 
    private Object p2;  //second object 
    private Object p3;  //third object 
      /**Consutrutor por defeito que inicializa todos os objectos a <code>null</code>*/
    public Triple()
    {
        //default constructor
        p1=null;
        p2=null;
        p3=null;
    }
    /**Construtor que inicia os objectos com os respectivos valores
     * @param one   objecto a ser guardado na primeira posição
     * @param two   objecto a ser guardado na segunda posição
     * @param three   objecto a ser guardado na terceira posição
     */
    public Triple(Object one, Object two, Object three)
    {
        //constructor that initialized the pair with two existing objects
        p1=one;
        p2=two;
        p3=three;
    }
    /**Método de comparação
     * @param p   objecto a comparar
     * */
    public boolean equals(Triple p)
    {
        //method to allow pairs to be compared for equivalence
        if(p1.equals(p.p1)&&p2.equals(p.p2)&&p3.equals(p.p3))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    /**Retorna o primeiro objecto
     * @return objecto na primeira posição
     */
    public Object getFirst()
    {
        //returns the first object
        return p1;
    }
    /**Retorna o segundo objecto
     * @return objecto na segunda posição
     */
    public Object getSecond()
    {
        //returns the second object
        return p2;
    }
    /**Retorna o terceiro objecto
     * @return objecto na terceira posição
     */
    public Object getThird()
    {
        //returns the Third object
        return p3;
    }
    /**Define o primeiro objecto
     * @param p   objecto a ser guardado
     */
    public void setFirst(Object p)
    {
        //sets the first object in the pair
        p1=p;
    }
    /**Define o segundo objecto
     * @param p   objecto a ser guardado
     */
    public void setSecond(Object p)
    {
        //sets the second object in the pair
        p2=p;
    }
    /**Define o terceiro objecto
     * @param p   objecto a ser guardado
     */
    public void setThird(Object p)
    {
        //sets the third object in the pair
        p3=p;
    }
}