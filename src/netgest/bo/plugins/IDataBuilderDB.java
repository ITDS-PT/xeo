/*Enconding=UTF-8*/
package netgest.bo.plugins;
import java.util.ArrayList;
import java.util.Hashtable;
import netgest.bo.builder.boBuildDB;
import netgest.bo.builder.boBuildRepository;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;

/**
 * Interface que deverá ser implementado para a construção de tabelas/repositórios persitentes para guardar os
 * dados de um objecto.
 * 
 * Este Interface deve ser utilizado seguindo as seguintes linhas básicas:
 *  <ol>
 *      <li> Deverá retornar um nome diferente da tabela fisica caso esta não seja igual <li>
 *      <li> Poderá dizer ao builder que determinados atributos não devem ser criados
 *      através da Hastable que é retornada pelo método getExternalAttributes();<li>
 * 
 * 
 */
public interface IDataBuilderDB 
{
    /**
     * Metodo chamado quando é inicializada a class todas a vezes que é iniciado
     * um build de objectos na mesma tabela.
     * 
     * @param repository    Objecto com o repositorio  actual da base de dados
     * @param dbbuilder     boBuildDB actual
     * @param ctx           EboContext  actual;
     */
    public void initialize(EboContext ctx, boBuildDB dbbuilder, boBuildRepository repository, 
                            int mode, 
                            Hashtable   objectInterfaceMap,  
                            boolean     createdFwdMethods
                        );

    /**
     * Evento chamado após o build de um objecto. Neste momento já se encontra todos os objectos criados 
     * na base de dados mediante a fase do build. Quando está na fase de Atributos só tabelas e campos é que estão criados
     * na fase das Contsraints as contraints e na fase das viewes as viewes.
     * @param boDef
     */
    public void     afterDataBaseScript( boDefHandler boDef );
    
    
    /**
     * Método que devolve o nome da tabela que vai ser criada na base de dados
     * se o nome da tabela for indiferente para o caso deverá devolver null
     * @return Nome da tabela que vai ser criado.
     * @param boDef definição do objecto principal
     */
    public String   getPhisicalTableName(  boDefHandler boDef  );
    
    /**
     * Evento que é chamado depois de analisar os objectos de definir quais os campos/contraints/viewes necessárias
     * mas antes de correr os script's para efectivar a criação dos objectos
     * @param boDef Definição do Objecto que está ser deployed
     */
    public void     beforeDataBaseScripts( boDefHandler boDef );
    
    
    /**
     * Evento para procurar atributos que existam noutros objectos que partilhem a mesmas tabelas.
     * @param boDef Objecto que está a ser analisado.
     */
    public void     inheritObject( boDefHandler boDef  );

    public void     beforeInheritViewes( boDefHandler boDef  );

    public void     afterInheritViewes( boDefHandler boDef  );
    
    public void     addViewFields( ArrayList flds, boDefHandler boDef  );
}