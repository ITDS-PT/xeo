/*Enconding=UTF-8*/
package netgest.bo.mapConstructor;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.jsp.*;
import netgest.bo.runtime.EboContext;
import netgest.bo.def.boDefHandler;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class MapConstructor 
{
    private StringBuffer sb = null;
    private boDefHandler handlers[] = null;
    int position = 0;
    /**
     * 
     * @since 
     */
    public MapConstructor()
    {
        sb = new StringBuffer();
    }
    
    public String contruct(String objName, EboContext xeboctx){
        try
        {            
            if(objName != null && !"".equals(objName.trim())){
                boDefHandler handler = boDefHandler.getBoDefinition(objName);
                if(handler == null){
                    //objecto inexistente                    
                    HTMLBuilder.writeInvalidObject(sb);
                }
                else{
                    //escreve arvore
                    escreveArvore(handler);
                }
            }
            else{
                //todos os objectos
                handlers = boDefHandler.listBoDefinitions();
                boDefHandler nextHandler = null;
                while((nextHandler = getNext()) != null){
                    escreveArvore(nextHandler);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
    private boDefHandler getNext(){
        boDefHandler aux = null;
        boolean s = true;
        for(; (position < handlers.length && handlers[position] != null);){
            aux = handlers[position];
            if(!aux.getBoIsSubBo()){
                position++;
                return aux;
            }
            position++;
        }
        return null;
    }
    private void remove (int i){
        int size = handlers.length - (i + 1);
        boDefHandler[] newList = new boDefHandler[size];
        
        for(int j = 0; size != 0 && i < (handlers.length - 1); i++, j++){
            newList[j] = handlers[i + 1];  
        }
        handlers = newList;
    }
    
    public void escreveArvore(boDefHandler handler) throws IOException{
        String nodeName = handler.getBoName();
        String imageURL = handler.getSrcForIcon16();
        String description = null;
        String label = null;
        try{
            description = handler.getDescription();
            label = handler.getLabel();
        }catch(Exception e){
            try{
                if(description == null){
                    label = handler.getLabel();
                }
            }catch(Exception _e){        
            }
        }
        description = description == null ? "":description;
        label = label == null ? "":label;
        
        boDefHandler[] sons = null;
        imageURL = imageURL == null ? handler.getSrcForIcon32() : imageURL;
        if(imageURL == null){
            HTMLBuilder.writeNode(sb, nodeName);
        }
        else{            
            HTMLBuilder.writeNode(sb, nodeName, imageURL, description, label);            
        }
        if((sons = handler.getBoSubClasses()).length > 0){
            HTMLBuilder.startSons(sb);
            for(int i = 0; i < sons.length; i++){
                escreveArvore(sons[i]);
            }
            HTMLBuilder.endSons(sb);
        }
    }
    
}