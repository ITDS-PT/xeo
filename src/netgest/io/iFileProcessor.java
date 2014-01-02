package netgest.io;

import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import netgest.bo.boConfig;
import netgest.bo.configUtils.FileNodeConfig;
import netgest.bo.configUtils.FolderNodeConfig;
import netgest.bo.configUtils.RepositoryConfig;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.io.metadata.iMetadataItem;

public class iFileProcessor {

	private Session session;
	
	public iFileProcessor(Session session)
	{
		this.session = session;
	}
	
	
	public void updateFile(boObject object, AttributeHandler handler, 
			String repositoryName) throws boRuntimeException, iFileException
	{
		//Get the repository configurations
		RepositoryConfig config = boConfig.getApplicationConfig().getDefaultFileRepositoryConfiguration();
		
		//Get the File and Folder configurations from the attribute
		//TODO: The same with the metadata
		FileNodeConfig attFileConfig = handler.getDefAttribute().
			getECMDocumentDefinitions().getFileNodeConfig();
		FolderNodeConfig attFolderConfig = handler.getDefAttribute().
			getECMDocumentDefinitions().getFolderNodeConfig();
		
		//Get the default configuration nodes
		FileNodeConfig configFiles = config.getFileConfig();
		FolderNodeConfig configFolder = config.getFolderConfig();
		
		///Check which of the configurations will be used
		if (attFileConfig != null)
			configFiles = attFileConfig;
		if (attFolderConfig != null)
			configFolder = attFolderConfig;
		
		//Buscar o file em questão
		
		iFile oFileToSave = handler.getValueiFile();
		
		if (oFileToSave.exists())
		{	//Se já existe então tem de se actualizar os dados
			
		}
		else
		{	//Se não existe então é preciso criar novos nós
			String path = oFileToSave.getPath();
			String[] parts = path.split("/");
			
			
			//Lidar com a metainformação (criar os nós correctos)
			List<iMetadataItem> oMetadataLst = oFileToSave.getAllMetadata();
			Iterator<iMetadataItem> it = oMetadataLst.iterator();
			while (it.hasNext()) {
				iMetadataItem oMetadata = (iMetadataItem) it.next();
				
			}
		}
		
		
	}
	
	
	public Node createNodesFromIFile(
			iFile file, 
			FileNodeConfig fileConfig, 
			FolderNodeConfig folderConfig)
	{
		
		return null;
	}
	
}
