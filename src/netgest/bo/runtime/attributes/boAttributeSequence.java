package netgest.bo.runtime.attributes;
import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectUtils;
import netgest.bo.runtime.boRuntimeException;

public class boAttributeSequence extends boAttributeNumber
{

    public boAttributeSequence( boObject parent, boDefAttribute def ) 
    {
        super( parent, def );
    }

    public boAttributeSequence(boObject parent, String name)
    {
        super( parent, name );
    }

    public String formula() throws boRuntimeException
    {
        return boObjectUtils.initializeSequenceField( this );
    }
    
}