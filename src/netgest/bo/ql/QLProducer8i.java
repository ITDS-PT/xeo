/*Enconding=UTF-8*/

package netgest.bo.ql;
import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
import netgest.bo.runtime.*;
import netgest.utils.*;
import netgest.bo.def.*;
import netgest.bo.*;
import netgest.bo.security.*;
/**
 *  Classe derivada do QLProducer especifica para a versão 8i da Oracle
 * 
*/
public class QLProducer8i extends QLProducer{

    
    /**Produtor por defeito que realiza todas as inicializaçõe necessárias*/
    private QLProducer8i()
    {
        super(null,null,true);
        selectRules = new Vector();
        whereRules = new Vector();
        joinRules = new Vector();
        stateRules = new Vector();
        fromTree = new Vector();
        orderRules = new Vector();
        groupRules = new Vector();
        havingRules = new Vector();
        keyRules = new Vector();
    }
    /**Produtor por que define qual o Parser, o contexto e se o sistema de seguranças está activo
    *  @param qp   parser que chamou o o objecto QLProducer
    *  @param xeboctx    contexto de execução da quer
    *  @param sec    activa ou desactiva o sistema de seguranças
    *  */
    public QLProducer8i(QLParser qp, EboContext xeboctx, boolean sec)
    {
        super(null,null,sec);
        parser = qp;
        ctx = xeboctx;
        this.security = sec;
        selectRules = new Vector();
        whereRules = new Vector();
        joinRules = new Vector();
        stateRules = new Vector();
        fromTree = new Vector();
        orderRules = new Vector();
        groupRules = new Vector();
        havingRules = new Vector();
        keyRules = new Vector();
    }
    
    
   
    protected void prodFrom()
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)fromTree.get(0);  //buscar a raiz da árvore
        String s, midtable="", parent, base_tab, parent_tab;
        fromC = null;
        String root = getRoot(0);
        if(root!=null && root.equalsIgnoreCase(objTable) || !existsInFromTree(objTable)) //se o obejcto seleccionado não existir na árvore incluir na clausula from
        {
            fromC = "FROM "+objTable+" ";
            base_tab = objTable;
        }
        else
        {
            fromC = "FROM "+(String)node.getUserObject();   //senão por a tabela que está à cabeça
            base_tab = (String)node.getUserObject();
        }
        if(node!=null)
            node = node.getNextNode();  //próximo nó
        while(node!=null)
        {
            needWhere=true; //se temos mais nós isto irá significar a necessidade de termos clausula WHERE 
            Triple att_t = (Triple)node.getUserObject();    //os nós da árvore são Pares (atributo, boolean) em que o booleano representa se queremos que ele conste
            boDefAttribute att = (boDefAttribute)att_t.getFirst();
            s = att.getReferencedObjectName();
            boDefHandler bo_obj_att = boDefHandler.getBoDefinition(s);
            s = bo_obj_att.getBoMasterTable();
            DefaultMutableTreeNode nodePai = (DefaultMutableTreeNode)node.getParent();    //nó pai
            if(nodePai.getParent()==null) //se o avô falhar é porque o pai é a raiz, que é um obj especial
            {
                parent = ((String)nodePai.getUserObject()).substring(1);                                         //nome do pai
                parent_tab = base_tab;
            }
            else
            {    
                parent = ((boDefAttribute)((Triple)nodePai.getUserObject()).getFirst()).getReferencedObjectName(); //nome do pai
                if(!((Boolean)((Triple)nodePai.getUserObject()).getSecond()).booleanValue())
                  parent_tab = midtable;
                else
                  if(boDefHandler.getBoDefinition(parent).getAttributeRef(att.getName())!=null)
                    parent_tab = boDefHandler.getBoDefinition(parent).getBoMasterTable();
                  else
                    parent_tab = midtable;
            }
            
         
            byte rel = att.getRelationType();
            boolean inn;
            if(exists_or)
             inn = ((Boolean)att_t.getThird()).booleanValue();
            else
              inn = true;
            //conforme o tipo de relação, temos diferentes joins a fazer
            if(!s.equalsIgnoreCase("OboObject") || !((Boolean)att_t.getSecond()).booleanValue())
            {
                switch(rel)
                {
                    case boDefAttribute.RELATION_1_TO_1:
                    {
                      
                        //if(att.getRequired().equalsIgnoreCase("Y") || att.getRequired().equalsIgnoreCase("YES") || inn)   //se for required não precisamos de fazer um outer join
                        //    joinRules.add(parent_tab+"."+att.getName()+"$="+s+".BOUI");
                        //else
                        joinRules.add(parent_tab+"."+att.getName()+"$="+s+".BOUI(+)");
                        break;
                    }
                    case boDefAttribute.RELATION_1_TO_N:
                    {   //midtable terá o valor da tabela que faz a relação entre os dois objectos
                        
                        midtable = att.getBridge().getBoMasterTable();
                        String attlig = ".BOUI";
                        if(nodePai.getParent()!=null && !((Boolean)((Triple)nodePai.getUserObject()).getSecond()).booleanValue())
                          attlig = ".CHILD$";
                        if(fromC!=null)
                        {
                          if(!existsInFrom(fromC, midtable))
                            fromC += ", "+midtable;
                        }
                        else
                            fromC = "FROM "+midtable;
                        //adicionar a condição de join com outer join
                        if(inn)
                        {
                          joinRules.add(midtable+"."+"PARENT$="+parent_tab+attlig);
                          if(((Boolean)att_t.getSecond()).booleanValue()) //quando não nos ineressa a tabela filho o campo boolean fica falso
                          {                                               //para que agoras não escolhamos fazer a condição de ligação á tabela filho
                              joinRules.add(midtable+"."+"CHILD$="+s+".BOUI");
                          }
                        }
                        else
                        {
                          joinRules.add(midtable+"."+"PARENT$(+)="+parent_tab+attlig);
                          if(((Boolean)att_t.getSecond()).booleanValue()) //quando não nos ineressa a tabela filho o campo boolean fica falso
                          {                                               //para que agoras não escolhamos fazer a condição de ligação á tabela filho
                              joinRules.add(midtable+"."+"CHILD$="+s+".BOUI(+)");
                          }
                        }
                        break;
                    }
                    
                    case boDefAttribute.RELATION_1_TO_N_WBRIDGE:
                    {   //midtable terá o valor da tabela que faz a relação entre os dois objectos
                        
                        midtable = att.getBridge().getBoMasterTable();
                        String attlig = ".BOUI";
                        if(nodePai.getParent()!=null && !((Boolean)((Triple)nodePai.getUserObject()).getSecond()).booleanValue())
                          attlig = ".CHILD$";
                        if(fromC!=null)
                        {
                          if(!existsInFrom(fromC, midtable))
                            fromC += ", "+midtable;
                        }
                        else
                            fromC = "FROM "+midtable;
                        //adicionar a condição de join com outer join
                        if(inn)
                        {
                          joinRules.add(midtable+"."+"PARENT$="+parent_tab+attlig);
                          if(((Boolean)att_t.getSecond()).booleanValue()) //quando não nos ineressa a tabela filho o campo boolean fica falso
                          {                                               //para que agoras não escolhamos fazer a condição de ligação á tabela filho
                              joinRules.add(midtable+"."+"CHILD$="+s+".BOUI");
                          }
                        }
                        else
                        {
                          joinRules.add(midtable+"."+"PARENT$(+)="+parent_tab+attlig);
                          if(((Boolean)att_t.getSecond()).booleanValue()) //quando não nos ineressa a tabela filho o campo boolean fica falso
                          {                                               //para que agoras não escolhamos fazer a condição de ligação á tabela filho
                              joinRules.add(midtable+"."+"CHILD$="+s+".BOUI(+)");
                          }
                        }
                        break;
                    }
                    
                    case boDefAttribute.RELATION_MULTI_VALUES:
                    {   //acho que não houve nenhum caso que viesse parar aqui de qq maneira já está meio pronto
                        
                          midtable = att.getTableName();
                          String attlig = ".BOUI";
                          if(nodePai.getParent()!=null && !((Boolean)((Triple)nodePai.getUserObject()).getSecond()).booleanValue())
                            attlig = ".CHILD$";
                        
                            if(fromC!=null)
                            {
                              if(!existsInFrom(fromC, midtable))
                                fromC += ", "+midtable;
                            }
                            else
                                fromC = "FROM "+midtable;
                                
                            if(inn)
                            {
                              joinRules.add(midtable+"."+ att.getDbTableFatherFieldName() +"="+parent_tab+attlig);
                              if(((Boolean)att_t.getSecond()).booleanValue()) //quando não nos ineressa a tabela filho o campo boolean fica falso
                              {                                               //para que agoras não escolhamos fazer a condição de ligação á tabela filho
                                  joinRules.add(midtable+"."+att.getDbTableChildFieldName()+"="+s+".BOUI");
                              }
                            }
                            else
                            {
                              joinRules.add(midtable+"."+ att.getDbTableFatherFieldName() +"(+)="+parent_tab+attlig);
                              if(((Boolean)att_t.getSecond()).booleanValue()) //quando não nos ineressa a tabela filho o campo boolean fica falso
                              {                                               //para que agoras não escolhamos fazer a condição de ligação á tabela filho
                                  joinRules.add(midtable+"."+att.getDbTableChildFieldName()+"="+s+".BOUI(+)");
                              }
                            }
                        
                        break;
                    }
                    
                }
            //se não queremos que a tabela conste o valor booleano estará a falso, são situações onde basta a tabela de ligação   
            if(((Boolean)att_t.getSecond()).booleanValue())
            {    
                    if(fromC!=null)
                    {
                      if(!existsInFrom(fromC, s))
                        fromC += ", "+s;
                    }
                    else
                        fromC = "FROM "+s;
                    
            }
     //       addSecurity(bo_obj_att, false);
          }
          
          node = node.getNextNode();  //próximo nó
          
        }
        
        for(int i = 1; i < fromTree.size(); i++)
        {
            node = (DefaultMutableTreeNode)fromTree.get(i);
            midtable = (String)node.getUserObject();   
            fromC += ", " +  midtable;
            
            if(midtable.equalsIgnoreCase("EBO_TEXTINDEX") || midtable.equalsIgnoreCase("OEBO_TEXTINDEX"))
            {
                String left_table;
                if( selObj != null && parser.textIndexOnReturnObject )
                {
                    left_table = getExt()?selObj.getBoExtendedTable():selObj.getBoMasterTable();
                }
                else
                {
                    left_table = base_tab;
                }
                joinRules.add(left_table+".BOUI="+midtable+".UI$");
            }
            else
              joinRules.add(base_tab+".BOUI="+midtable+".BOUI(+)");
        }
        fromC += " ";       //um espacinho para não ficar tudo ligado :)
    }
    
    /**Simplificação da função, visto que nao é necessária qualquer troca de Wildcards
     * @param s     String a substituir
     * @return      a mesma string*/
    protected String makeWildcardString(String s)
    {
      return s;
    }

    private static boolean existsInFrom(String from, String word)
    {
        int pos;
        String w = null;
        if((pos = from.toUpperCase().indexOf(word.toUpperCase())) != -1)
        {
            w = getWord(from, pos);
            if(w.equalsIgnoreCase(word))
                return true;
        }
        return false;
    }
    
    private static String getWord(String phrase, int from)
    {
        String toRet=""; 
        for(int i = from; i < phrase.length(); i++)
        {
            if(phrase.charAt(i) == ' ' || phrase.charAt(i) ==',')
            {
                break;
            }
            else
            {
                toRet = toRet + phrase.charAt(i);  
            }
        }
        return toRet;
    }
}   
