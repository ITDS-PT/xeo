package netgest.io.metadata;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;

/**
 * 
 * 
 * Interface to allow the classification of documents with Metadata Items
 * 
 * @author PedroRio
 *
 */
public interface iFileClassifier 
{
	/**
	 * 
	 * Retrieves the name of the java class that implements this
	 * interface so that its no-argument constructor can be invoked
	 * 
	 * @return The name of the class implementing this interface (fully qualified)
	 */
	public String getClassName();
	
	/**
	 * 
	 * Given a document with an attribute of type binary
	 * returns a set of metadata items to associate(classify) the document
	 * 
	 * @param object The object which has the attribute
	 * @param ctx The XEO context
	 * @param attributeBinary The handler to the attribute with binary data
	 * 
	 * @return A set of Metadata items to associate
	 */
	public iMetadataItem[] classify(boObject object, 
			EboContext ctx, AttributeHandler attributeBinary);
	
	
	
}
