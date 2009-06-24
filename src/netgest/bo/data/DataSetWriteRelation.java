/*Enconding=UTF-8*/
package netgest.bo.data;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class DataSetWriteRelation extends DataSetReadRelation
{
    /**
     *
     * @since
     */
    private String[] p_localColumns;
    private String[] p_remoteColumns;
    private String[] p_icnFields;
    private String p_objectName;
    private String p_schemaName;
    private String p_xmlObjectName;

    public DataSetWriteRelation(String name, String dataSource,
        String xmlObjectName, String objectName, String schemaName,
        String[] icnFields, String[] parentFields, String[] childFields,
        String[] localColumns, String[] remoteColumns)
    {
        super(name, dataSource, schemaName, null, parentFields, childFields);
        p_localColumns = localColumns;
        p_remoteColumns = remoteColumns;
        p_schemaName = schemaName;
        p_objectName = objectName;
        p_icnFields = icnFields;
        p_xmlObjectName = xmlObjectName;
    }

    public String[] getLocalColumns()
    {
        return p_localColumns;
    }

    public String[] getRemoteColumns()
    {
        return p_remoteColumns;
    }

    public String[] getICNFields()
    {
        return p_icnFields;
    }

    public String getObjectName()
    {
        return p_objectName;
    }

    public String getXMLObjectName()
    {
        return p_xmlObjectName;
    }

    public String getSchemaName()
    {
        return p_schemaName;
    }

    public void setSchemaName(String newValue)
    {
        p_schemaName = newValue;
    }
}
