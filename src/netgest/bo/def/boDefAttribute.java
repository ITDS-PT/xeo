/*Enconding=UTF-8*/
package netgest.bo.def;
import netgest.bo.def.boDefBridge;
import netgest.bo.runtime.boRuntimeException2;
import netgest.bo.transformers.Transformer;

import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Node;

public interface boDefAttribute
{

    public static final String ATTRIBUTE_BINARYDATA ="attributeBinaryData";
    public static final String ATTRIBUTE_BOOLEAN    ="attributeBoolean";
    public static final String ATTRIBUTE_CURRENCY   ="attributeCurrency";
    public static final String ATTRIBUTE_DATE       ="attributeDate";
    public static final String ATTRIBUTE_DATETIME   ="attributeDateTime";
    public static final String ATTRIBUTE_DURATION   ="attributeDuration";
    public static final String ATTRIBUTE_LONGTEXT   ="attributeLongText";  
    public static final String ATTRIBUTE_NUMBER     ="attributeNumber";
    public static final String ATTRIBUTE_OBJECT     ="attributeObject";
    public static final String ATTRIBUTE_OBJECTCOLLECTION = "attributeObjectCollection";
    public static final String ATTRIBUTE_SEQUENCE   ="attributeSequence";
    public static final String ATTRIBUTE_TEXT       ="attributeText";

    public static final byte TYPE_ATTRIBUTE=0;
    public static final byte TYPE_OBJECTATTRIBUTE=1;
    public static final byte TYPE_STATEATTRIBUTE=2;
    
    
    public static final byte NO_RELATION=0;
    public static final byte RELATION_1_TO_1=1;
    public static final byte RELATION_1_TO_N=2;
    public static final byte RELATION_1_TO_N_WBRIDGE=3;
    public static final byte RELATION_MULTI_VALUES=4;
    
    public static final byte RELATED_OBJECT_ORPHAN_NO=0;
    public static final byte RELATED_OBJECT_ORPHAN_YES=1;

    public static final byte CAN_READ=1;
    public static final byte CAN_WRITE=2;
    public static final byte CAN_DELETE=4;
    public static final byte CAN_ADD=8;
    
    public static final byte VALUE_UNKNOWN=0;
    public static final byte VALUE_CHAR=9;
    public static final byte VALUE_BOOLEAN=1;
    public static final byte VALUE_DURATION=2;
    public static final byte VALUE_CURRENCY=3;
    public static final byte VALUE_NUMBER=4;
    public static final byte VALUE_DATETIME=5;
    public static final byte VALUE_DATE=6;
    public static final byte VALUE_CLOB=7;
    public static final byte VALUE_BLOB=8;
    public static final byte VALUE_IFILELINK=10;
    public static final byte VALUE_SEQUENCE=11;
    
    public static final byte SET_PARENT_YES=0;
    public static final byte SET_PARENT_NO=1;
    public static final byte SET_PARENT_DEFAULT=2;
    
    public boDefMethod[] getMethods();
    
    public boDefClsEvents[] getEvents();

    public boDefClsEvents   getEvent( String name );
    
    public boDefHandler[] getObjects();

    public String[] getObjectsName();
    
    public boDefHandler[] getTransformObjects();
    public Transformer getTransformClassMap() throws boRuntimeException2;
    
    public boolean getChildIsOrphan( String relObjectName );
    
    public boolean getChildIsOrphan();
    
    public byte getSetParent();
    
    public boolean getShowLookup();
    
    public byte getRelationType();
    
    public String getName();
    
    public boolean getDbIsBinding();
    
    public String[] getDbRelationKeys();
    
    public String getBeahvior_Img();
    
    public String getBeahvior_Script();
    
    public boolean getDbIsUnique();
    
//    public boolean getDbIsFullTextIndexed();
    
    public boolean getDbIsIndexed();
    
    public boolean getDbIsTabled();
    
    public String getDbTableChildFieldName();
    
    public String getDbTableFatherFieldName();
    
    public String getDbTableName();
    
    public String getTableName();

    public String getDbName();
    
    public boolean getDbCreateConstraints();
    
    public String getType();
    
    public String[] getTypeArguments();
     
    public int getLen();
    
    public String getSizeToRender();
    
    public String getLabel();

    public String getTransformObject();

    public String getTransformClassMapName();

    public boolean hasTransformer();
    
    public boDefXeoCode getRecommend();

//    public String getMask();
    
    public boolean supportManualOperation();
    
    public boolean supportManualAdd();

    public boolean supportManualCreate();
    
    public String getTransform();

    public boolean getClock();

    public byte getAtributeType();

    public String getAtributeDeclaredType();
    
    public String getDescription();
    
    public String getTooltip();
    
    public String getReferencedObjectName();
    
    public boolean isMemberOfInterface();

    public boolean isFinder();
    
    public String[] getInterfaces( );
    
    public boolean isMemberOfInterface( String interfacename );
    
    public boDefHandler getReferencedObjectDef();

    public String getMax();
    
    public String getMin();
    
    public int getMaxOccurs();
    
    public int getMinOccurs();

    public byte getPermissions();
    
    public boDefBridge getBridge();
    
    public boDefHandler getBoDefHandler();
    
    public byte getValueType();

//    public byte getValueType( String typeName );
    
    public String getExtendsClass();
    
    public String getClassName();
    
    public String getBridgeFilter();
    
    public boDefObjectFilter[] getObjectFilter();

    public boDefObjectFilter getObjectFilter( String forObject );
    
    public int getDecimals();
    
    public boolean renderAsCheckBox();
    
    public int getMinDecimals();

    public String getGrouping();
    
    public boDefXeoCode getValid();
    
    public boDefXeoCode getDefaultValue();

    public boDefXeoCode getDisableWhen();

    public boDefXeoCode getHiddenWhen();
    
    public boDefXeoCode getOnChangeSubmit();
    
    public boDefXeoCode getTotalRefresh();
    
    public boolean renderAsLov();
    
    public boolean textIndex();
    
    public boolean indexOnlyCardId();

//    public boolean renderAsMultipleList();

    public int getRuntimeMaxOccurs();
    
    public boDefXeoCode getRequired();

    public boDefXeoCode getFormula();
    
    public String getLOVName();

    public String getLOVSql(); 

    public String getLOVSqlIdField();

    public String getLOVSqlDescField(); 
    
    public boDefXeoCode getLovCondition();
    
    public String getEditorType();
    
    public boDefXeoCode getLovEditable();
    
    public ngtXMLHandler getLovItems();
    
    public boolean getLovRetainValues();
 
    public boolean needsClass();
   
    public String className();
    
    public boolean getDbRequired();
    
    /**
     * 
     * Retrieves the definitions regarding an attributeBinaryData
     * that is stored in a Java Content Repository (JCR). If it returns
     * null, this is a regular attribute, if it returns a
     * {@link boDefDocument} instance the attribute is stored in a JCR 
     * 
     * @return A {@link boDefDocument} instance with the definitions for the
     * content repository or null if the attributes a normal attribute
     */
    public boDefDocument getECMDocumentDefinitions();
    
        //public boolean isFinder();
    
    
    // XML Direct Access...
    public abstract ngtXMLHandler getChildNode( String name );
    public abstract Node          getNode();

}