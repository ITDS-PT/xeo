/*Enconding=UTF-8*/
package netgest.bo.builder;
import java.io.File;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Properties;

import netgest.bo.data.Driver;
import netgest.bo.def.boDefHandler;
import netgest.bo.builder.*;
import netgest.bo.system.boRepository;
/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class boBuildRepositoryOLD
{
    private boRepository p_repository;
    private boRepository p_parentRepository;
    protected boPathProvider pathProvider = new boPathProvider();
    private ArrayList p_packages=new ArrayList();

    private final static int GAP = 10;

    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public boBuildRepositoryOLD(boRepository repository)
    {
        p_repository = repository;
        p_parentRepository = repository.getParentRepository();
        refresh();
    }
    
    public void refresh()
    {
        setPathProvider();
    }

/*    
    public Connection getConnection()
    {
        return p_repository.getConnection();
    }
    public Connection getDedicatedConnection()
    {
        return p_repository.getDedicatedConnection();
    }

    public Connection getDedicatedConnectionDef()
    {
        return p_repository.getDedicatedConnectionDef();
    }
    
    public Connection getConnectionDef()
    {
        return p_repository.getConnectionDef();
    }
*/    
    public Driver getDefDriver()
    {
        return p_repository.getDefDriver();
    }
    
    
    public String getName()
    {
        return p_repository.getName();
    }

    public String getSchemaName()
    {
        return p_repository.getSchemaName();
    }
    
    public String getUserName()
    {
        return p_repository.getUserName();
    }

    public String getPassword()
    {
        return p_repository.getPassword();
    }

    public boRepository getParentRepository(  )
    {
        return p_repository.getParentRepository();
    }

    public boRepository getRepository(  )
    {
        return p_repository;
    }

    public String getDeploymentDir()
    {
        return p_repository.getDeploymentDir();
    }

    public String getDefinitionDir()
    {
        return p_repository.getDefinitionDir();
    }

//métodos para identificação dos ficheiros para deploy
    public File[] getFilesToDeploy(String objName, boolean deployAll, Hashtable interfaceDeploy, boolean buildInterfaceFlag)
    {
        int pos = 0;
        File[] deployFiles = null;
        ArrayList intfAux = new ArrayList();
        boolean globalRepositoryType = (p_parentRepository == null||"".equals(p_parentRepository)) ? true:false;
        if(deployFiles == null)
        {
            File[] deployFilesAux = getXMLFiles(new File(getDefinitionDir()));
            String boname = null;
//            boDefHandler auxDef;
            deployFiles = new File[10];
            File deployfile;
            for(int i = 0; i < deployFilesAux.length; i++)
            {
                if(deployFilesAux[i].getName().toLowerCase().endsWith( boBuilder.TYPE_BO ))
                {
                    boname = deployFilesAux[i].getName().substring(0,deployFilesAux[i].getName().toLowerCase().indexOf( boBuilder.TYPE_BO ));
                    //auxDef = boDefHandler.getBoDefinition(boname);
                    if(deployFilesAux[i].getName().toLowerCase().endsWith( boBuilder.TYPE_BO )) 
                    {
                        if(objName == null)
                        {
                            // Build file list for deploy
                            deployfile = new File(getDeploymentDir() + deployFilesAux[i].getName());
                            //contrução do pathProvider
                            //pathProvider.put( deployFilesAux[i]);
                            if(deployAll || !globalRepositoryType || !deployfile.exists() || deployfile.lastModified() < deployFilesAux[i].lastModified() ||
                             (interfaceDeploy != null && interfaceDeploy.get(boname) != null)
                            ) 
                            {
                                deployFiles = insert(deployFiles, deployFilesAux[i], pos);
                                pos++;
                            }
                        }
                        else
                        {
                            String fileName = deployFilesAux[i].getName().substring(0, deployFilesAux[i].getName().indexOf("."));
                            if(fileName.equalsIgnoreCase(objName))                        
                            {
                                deployfile = new File(getDeploymentDir() + deployFilesAux[i].getName());
                                //pathProvider.put( deployFilesAux[i]);
                                if(deployAll || !deployfile.exists() || deployfile.lastModified() < deployFilesAux[i].lastModified() ||
                             (interfaceDeploy != null && interfaceDeploy.get(boname) != null)) 
                                {
                                    deployFiles = insert(deployFiles, deployFilesAux[i], pos);
                                    pos++;
                                }
                            }
                        }
                    }                        
                }
                else
                {
                    if(deployFilesAux[i].getName().toLowerCase().endsWith(boBuilder.TYPE_STATE)
                        || deployFilesAux[i].getName().toLowerCase().endsWith(boBuilder.TYPE_LOV) 
                        || deployFilesAux[i].getName().toLowerCase().endsWith(boBuilder.TYPE_SC)
                        || deployFilesAux[i].getName().toLowerCase().endsWith(boBuilder.TYPE_WSD)
                    ) 
                    {
                        if(objName == null)
                        {
                            // Build file list for deploy
                            deployfile = new File(getDeploymentDir() + deployFilesAux[i].getName());
                            //contrução do pathProvider
                            pathProvider.put( deployFilesAux[i]);
                            if(deployAll || !deployfile.exists() || deployfile.lastModified() < deployFilesAux[i].lastModified()) 
                            {
                                deployFiles = insert(deployFiles, deployFilesAux[i], pos);
                                pos++;
                            }
                        }
                        else
                        {
                            String fileName = deployFilesAux[i].getName().substring(0, deployFilesAux[i].getName().indexOf("$"));
                            if(fileName.equalsIgnoreCase(objName))                        
                            {
                                deployfile = new File(getDeploymentDir() + deployFilesAux[i].getName());
                                pathProvider.put( deployFilesAux[i]);
                                if(deployAll || !deployfile.exists() || deployfile.lastModified() < deployFilesAux[i].lastModified()) 
                                {
                                    deployFiles = insert(deployFiles, deployFilesAux[i], pos);
                                    pos++;
                                }
                            }
                        }
                    }
                    else if((interfaceDeploy == null || interfaceDeploy.size() == 0) && 
                            deployFilesAux[i].getName().toLowerCase().endsWith( boBuilder.TYPE_INTERFACE ))
                    {
                        if(objName == null)
                        {
                            // Build file list for deploy
                            deployfile = new File(getDeploymentDir() + deployFilesAux[i].getName());
                            //contrução do pathProvider
                            pathProvider.put( deployFilesAux[i]);
                            if(deployAll || !deployfile.exists() || deployfile.lastModified() < deployFilesAux[i].lastModified()) 
                            {
                                deployFiles = insert(deployFiles, deployFilesAux[i], pos);
                                pos++;
                            }
                            else if(buildInterfaceFlag)
                            {
                                //se fôr efectuado algum deploy tem que fazer o deploy das interfaces
                                intfAux.add(deployFilesAux[i]);
                            }
                        }
                        else
                        {
                            String fileName = deployFilesAux[i].getName().substring(0, deployFilesAux[i].getName().indexOf("$"));
                            if(fileName.equalsIgnoreCase(objName))                        
                            {
                                deployfile = new File(getDeploymentDir() + deployFilesAux[i].getName());
                                pathProvider.put( deployFilesAux[i]);
                                if(deployAll || !deployfile.exists() || deployfile.lastModified() < deployFilesAux[i].lastModified()) 
                                {
                                    deployFiles = insert(deployFiles, deployFilesAux[i], pos);
                                    pos++;
                                }
                                else if(buildInterfaceFlag)
                                {
                                    //se fôr efectuado algum deploy tem que fazer o deploy das interfaces
                                    intfAux.add(deployFilesAux[i]);
                                }
                            }
                        }
                    }
                }
            }
            if(pos > 0 && buildInterfaceFlag)
            {
                for (int i = 0; i < intfAux.size(); i++) 
                {
                    deployFiles = insert(deployFiles, (File)intfAux.get(i), pos);
                    pos++;
                }
            }
            deployFiles = shrink(deployFiles, pos);
        }        
        return deployFiles;
    }

    private static File[] setNewVersion(ArrayList from, File[] to, int pos)
    {
        File auxN = null, auxO = null;
        boolean found = false;
        //ArrayList toRemove = new ArrayList(from.size());
        for(int i = 0; i < from.size(); i++)
        {
            found = false;
            auxN = (File)from.get(i);
            for(int j = pos; j >= 0 && !found; j--)
            {
                auxO = to[j];
                if(auxN.getName().equals(auxO.getName()))
                {
                    to[j] = auxN;
                    //toRemove
                    //toRemove.add(new Integer(i));
                    from.remove(i);
                    i--;
                    found = true;
                }
            }
        }
        return to;
    }

    public File getXMLFileFromDefinition(String objName)
    {
        return getLastVersionFile(new File(getDefinitionDir()), objName, boBuilder.TYPE_BO );
    }    

    public File getDataSourceFileFromDefinition(String objName)
    {
        return getLastVersionFile(new File(getDefinitionDir()), objName, boBuilder.TYPE_DS);
    }  

    public File getLovFileFromDefinition(String objName)
    {
        return getLastVersionFile(new File(getDefinitionDir()), objName, boBuilder.TYPE_LOV);
    }  

    public File getScriptFileFromDefinition(String objName)
    {
        return getLastVersionFile(new File(getDefinitionDir()), objName, boBuilder.TYPE_SC);
    }

    public File getInterfaceFileFromDefinition(String objName)
    {
        return getLastVersionFile(new File(getDefinitionDir()), objName, boBuilder.TYPE_INTERFACE);
    }

    public File getXMLFileFromDeployment(String objName)
    {
        return getLastVersionFile(new File(getDeploymentDir()), objName, boBuilder.TYPE_BO);
    }    

    public File getDataSourceFileFromDeployment(String objName)
    {
        return getLastVersionFile(new File(getDeploymentDir()), objName, boBuilder.TYPE_DS);
    }  

    public File getLovFileFromDeployment(String objName)
    {
        return getLastVersionFile(new File(getDeploymentDir()), objName, boBuilder.TYPE_LOV);
    }  

    public File getScriptFileFromDeployment(String objName)
    {
        return getLastVersionFile(new File(getDeploymentDir()), objName, boBuilder.TYPE_SC);
    }    

    public File getInterfaceFileFromDeployment(String objName)
    {
        return getLastVersionFile(new File(getDeploymentDir()), objName, boBuilder.TYPE_INTERFACE);
    }

    public File getXMLFile(String objName)
    {
        File bodefDir = new File(getDefinitionDir());
        return getLastVersionFile(bodefDir, objName, boBuilder.TYPE_BO);
    }    

    public File getDataSourceFile(String objName)
    {
        File bodefDir = new File(getDefinitionDir());
        return getLastVersionFile(bodefDir, objName, boBuilder.TYPE_DS);
    }  

    public File getLovFile(String objName)
    {
        File bodefDir = new File(getDefinitionDir());
        return getLastVersionFile(bodefDir, objName, boBuilder.TYPE_LOV);
    }
    
    public File getInterfaceFile(String objName)
    {
        File bodefDir = new File(getDefinitionDir());
        return getLastVersionFile(bodefDir, objName, boBuilder.TYPE_INTERFACE);
    }

    public boPathProvider getPathProvider()
    {
        return pathProvider;
    }
    
    public ArrayList getPackages()
    {
        return p_packages;
    }    

    public static File getXMLFile(File bodefDir, String objName)
    {
        return getLastVersionFile(bodefDir, objName, boBuilder.TYPE_BO);
    }

    public File getScriptFile(String objName)
    {
        File bodefDir = new File(getDefinitionDir());
        return getLastVersionFile(bodefDir, objName, boBuilder.TYPE_SC);
    } 
    
    public static File getLastVersionFile(File bodefDir, String objName, String type)
    {
        File[] files = bodefDir.listFiles();
        int pos = 0;
        Comparator c = new boFileComparator();
        Arrays.sort(files, c);
        File aux, aux2;
        for(int i = 0; i < files.length; i++)
        {
            aux = files[i];
            if(aux.isDirectory())
            {
                if(aux.getName().indexOf("$") != -1)
                {
                    if(hasSuperiorVersion(files, i))
                    {
                        aux2 = getLastVersionFile(files[i + 1], objName, type);
                        if(aux2 != null) return aux2;                        
                    }
                }
                aux2 = getLastVersionFile(files[i], objName, type);
                if(aux2 != null) return aux2;            
            }
            else
            {
                if(aux.getName().toLowerCase().endsWith(type) && 
                    objName.equalsIgnoreCase(aux.getName().substring(0, aux.getName().indexOf(".xeo"))))
                    return aux;
            }
        }
        return null;
    }

    private static boolean hasSuperiorVersion(File[] list, int i)
    {
        File aux = list[i];
        if(list.length > (i + 1))
        {
            File aux2 = list[i + 1];
            int x = aux.getName().indexOf("$");
            String s = aux.getName().substring(0, x);
            x = aux2.getName().indexOf("$");
            if(x == -1)
            {
                return false;
            }
            String s2 = aux2.getName().substring(0, x);
            
            if(s2.equalsIgnoreCase(s) && aux2.isDirectory())
            {
                return true;
            }
        }
        return false;
    }

    public File[] getXMLFilesFromDeployment()
    {
        File[] xmlFiles;
        xmlFiles = _getXMLFiles(new File(getDeploymentDir()), p_packages);
        return xmlFiles;
    }
    
    public File[] getXMLFilesFromDefinition()
    {
        File[] xmlFiles;
        xmlFiles = _getXMLFiles(new File(getDefinitionDir()), p_packages);
        return xmlFiles;
    }    
    
    public File[] getXMLFiles(File bodefDir)
    {
        File[] xmlFiles;
        xmlFiles = _getXMLFiles(bodefDir, p_packages);
        return xmlFiles;
    }

   private static File[] _getXMLFiles(File bodefDir, ArrayList p_package)
    {
        File[] files = bodefDir.listFiles();
        int pos = 0;
        File[] toReturn = new File[10];
        Comparator c = new boFileComparator();
        Arrays.sort(files, c);
        File aux;
        for(int i = 0; i < files.length; i++)
        {
            aux = files[i]; 
            if(aux.isDirectory())
            {                   
                if(aux.getName().indexOf("$") != -1)
                {
                    // e directorio
                    //vou verificar se existe uma versão anterior
                    addPackage(aux.getName(), p_package);
                    File[] xmlfiles = _getXMLFiles(files[i], p_package);
                    if(hasInferiorVersion(files, i))
                    {
                        ArrayList l = new ArrayList(Arrays.asList(xmlfiles));
                        toReturn = setNewVersion(l, toReturn, pos - 1);
                        if(l.size() > 0)
                        {
                            xmlfiles = toArray(l);                        
                            int[] posi = {pos};
                            toReturn = copyFromTo(xmlfiles, toReturn, posi);
                            pos = posi[0];
                        }
                    }
                    else
                    {
                        int[] posi = {pos};
                        toReturn = copyFromTo(xmlfiles, toReturn, posi);
                        pos = posi[0];
                    }
                }
                else
                {
                    int[] posi = {pos};
                    toReturn = copyFromTo(_getXMLFiles(files[i], p_package), toReturn, posi);
                    pos = posi[0];
                }
            }
            else
            {
                toReturn = insert(toReturn, aux, pos);
                pos++;
            }
        }
        return shrink(toReturn, pos);
    }

    private static final void addPackage(String name, ArrayList p_packages)
    {
      boolean found=false;
      for (int i=0;i<p_packages.size();i++)
      {
        String currName=(String)p_packages.get(i);
        if (currName.equals(name))found=true;
        
      }
      if (!found)p_packages.add(name);
    }
    private static final boolean hasInferiorVersion(File[] list, int i)
    {
        File aux = list[i];
        if((i - 1) >= 0)
        {
            File aux2 = list[i - 1];
            int x = aux.getName().indexOf("$");
            String s = aux.getName().substring(0, x);
            x = aux2.getName().indexOf("$");
            if(x == -1)
            {
                return false;
            }
            String s2 = aux2.getName().substring(0, x);
            
            if(s2.equalsIgnoreCase(s) && aux2.isDirectory())
            {
                return true;
            }
        }
        return false;
    }
    
    private static File[] copyFromTo(File[] from, File[] to, int[] toPos)
    {
        int pos = toPos[0];        
        for(int i = 0; i < from.length; i++)
        {
            to = insert(to, from[i], pos);
            pos++;
        }
        toPos[0] = pos;
        return to;
    }
    
    private static File[] insert(File[] arr, File ob, int pos)
    {
        if(pos == arr.length)
        {
            //expandCapacity
            arr = expandCapacity(arr, GAP);
        }
        arr[pos] = ob;
        return arr;
    }
    
    private static File[] expandCapacity(File[] arr, int tam) 
    {
        File[] newValue = new File[arr.length + tam];
        System.arraycopy(arr, 0, newValue, 0, arr.length);
        return newValue;
    }
    private static File[] shrink(File[] arr, int lastPos) 
    {
        File[] newValue = new File[lastPos];
        System.arraycopy(arr, 0, newValue, 0, lastPos);
        return newValue;
    }
    private static File[] toArray(ArrayList l)
    {
        File[] f = new File[l.size()];
        for(int i = 0; i < l.size(); i++)
        {
            f[i] = (File)l.get(i);
        }
        return f;
    }
    public void setPathProvider()
    {
        if( pathProvider.p_filesPath.size() == 0 )
        {
            File[] auxF = getXMLFiles(new File(getDefinitionDir()));
            for (int i = 0; i < auxF.length; i++) 
            {
                if(
                    auxF[i].getName().toLowerCase().endsWith( boBuilder.TYPE_BO )
                )
                {
                    pathProvider.put( auxF[i]  );       
                }
                else
                { 
                    //System.out.println("Ignored:" + auxF[i].getName() );
                }
            }
        }
    }
  
}