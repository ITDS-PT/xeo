/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge;
import java.sql.ResultSet;
import netgest.bo.impl.document.merge.resultSet.MergeResultSetTable;
import netgest.bo.impl.document.merge.resultSet.MergeResultSetBoObject;
import netgest.bo.runtime.*;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class MergeResultSetFactory 
{
   // Não se pode construit o objecto
    private MergeResultSetFactory() {}
    
    
    /**
	 * ResultSet do Tipo Tabela.
	 */
	public static ResultSet getResultSet(Tabela tab)
	{
		return new MergeResultSetTable(tab);
	}
    
    /**
	 * ResultSet do Tipo Tabela (com prefixo).
	 */
	public static ResultSet getResultSet(String prefix, Tabela tab)
	{
		return new MergeResultSetTable(prefix, tab);
	}
    
    /**
	 * ResultSet do Tipo boObject.
	 */
	public static ResultSet getResultSet(boObject obj)
	{
		return new MergeResultSetBoObject(obj);
	}
    /**
	 * ResultSet do Tipo boObject (c/ prefixo).
	 */
	public static ResultSet getResultSet(String prefix, boObject obj)
	{
		return new MergeResultSetBoObject(prefix, obj);
	}
    /**
	 * ResultSet do Tipo boObject (bridge).
	 */
	public static ResultSet getResultSet(boObject obj, String bridgeName)
	{
		return new MergeResultSetBoObject(obj, bridgeName);
	}
}