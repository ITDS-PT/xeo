/*Enconding=UTF-8*/
package netgest.bo.plugins;
import netgest.bo.builder.boBuildRepository;
import netgest.bo.builder.boBuilder;
import netgest.bo.def.boDefHandler;
import netgest.bo.plugins.IDataManager;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectFinder;
import netgest.bo.runtime.boRuntimeException;

public interface IDataPlugin 
{
    /**
     * 
     * @return 
     * @param bodef
     */
    public IDataManager     getDataManager( boDefHandler bodef );
    /**
     * 
     * @return 
     * @param bodef
     * @param builder
     */
    public IDataBuilderDB   getBuilderDB( boBuildRepository builder, boDefHandler bodef );
    /**
     * 
     * @return 
     * @param bodef
     */
    public String           getDataTableName( boBuildRepository repos,  boDefHandler bodef );
    /**
     * 
     * @return 
     * @param bodef
     */
    public String           getXeoTableName(  boDefHandler bodef );
    
    public boObject         lookByPrimaryKey( EboContext ctx, String objectName, Object[] keys ) throws boRuntimeException;
    
    public boObjectFinder[] getFinders( boDefHandler bodef ) throws boRuntimeException;
}