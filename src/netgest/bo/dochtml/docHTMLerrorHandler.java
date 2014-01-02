/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import java.util.*;

public final class docHTMLerrorHandler  {
    private Hashtable errorList;
    static int counter;
    private static Hashtable p_errors;
    public static final int DOCID_NOT_EXIST =1000;
    
    
     static {
            p_errors = new Hashtable();
            p_errors.put("APP-1000","Documento Referenciado já não existe");
            
    }
    
    public docHTMLerrorHandler() {
        errorList=new Hashtable();
    }

    public void empty(){
        errorList.clear();
        counter=0;
    }
    
    public void add(int errorNumber){
        String key=""+counter++;
        errorList.put(key,new appError(p_errors,errorNumber,-1));
    }
    
    public void add(String errorDescription){
        String key=""+counter++;
        errorList.put(key,new appError(errorDescription,-1));
    }

    public void add(int errorNumber,int docid){
        String key=""+counter++;
        errorList.put(key,new appError(p_errors,errorNumber,docid));
    }

    public void add(String errorDescription,int docid){
        String key=""+counter++;
        errorList.put(key,new appError(errorDescription,docid));
    }


    private class appError{
        private int p_errorNumber=0;
        private String p_errorContext="";
        private String p_errorDescription="";
        private int p_docid=-1;
    

       
        public appError(String errorDescription,int docid){
            p_errorDescription=errorDescription;
            p_docid=docid;
        }
        
        public appError(Hashtable errorList,int errorNumber,int docid){
            String key="APP-"+errorNumber;
            String error=(String)errorList.get(key);
            if(error!=null) p_errorDescription=errorNumber+" - "+error;
            else p_errorDescription="Erro "+errorNumber+" não documentado";
            p_docid=docid;
        }
    }
}