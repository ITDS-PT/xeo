/*Enconding=UTF-8*/

package netgest.bo.ql;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import netgest.bo.boConfig;
import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
/**
 *  Classe derivada do QLProducer especifica para a versão 9i da Oracle
 * 
*/
public class QLProducer9i extends QLProducer {

	
	private ArrayList tablesused = new ArrayList();
	private int tabN = 0;
	
    /**Construtor por defeito que realiza todas as inicializaçõe necessárias*/
    private QLProducer9i ()
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
    /**Construtor que define qual o Parser, o contexto e se o sistema de seguranças está activo
    *  @param qp   parser que chamou o o objecto QLProducer
    *  @param xeboctx    contexto de execução da quer
    *  @param sec    activa ou desactiva o sistema de seguranças
    *  */
    public QLProducer9i(QLParser qp, EboContext xeboctx, boolean sec)
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
        String s, midtable, parent, base_tab, parent_tab;
        fromC = null;
        String root = getRoot(0);
        if(root!=null && root.equalsIgnoreCase(objTable) || !existsInFromTree(objTable)) //se o obejcto seleccionado não existir na árvore incluir na clausula from
        {
            fromC = "FROM "+objTable+" ";
            base_tab = objTable;
            midtable = base_tab;
        }
        else
        {
            fromC = "FROM "+(String)node.getUserObject();   //senão por a tabela que está à cabeça
            base_tab = (String)node.getUserObject();
            midtable = base_tab;
        }
        if(node!=null)
            node = node.getNextNode();  //próximo nó
        
        tablesused.add(base_tab);
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
                            //joinRules.add(parent_tab+"."+att.getName()+"$="+s+".BOUI");
                        //    fromC += " INNER JOIN " + s + " ON("+parent_tab+"."+att.getName()+"$="+s+".BOUI)";
                        //else
                            //joinRules.add(parent_tab+"."+att.getName()+"$="+s+".BOUI(+)");
                    	
                    	//Avoids inclusion of already relationed tables and database error.
                    	if (fromC.indexOf(" LEFT OUTER JOIN " + s)==-1)
                    	{                    		
                    		fromC += " LEFT OUTER JOIN " + getTableAlias(s) + " ON("+parent_tab+"."+att.getName()+"$="+s+".BOUI)";
                    	}
                        break;
                    }
                    case boDefAttribute.RELATION_1_TO_N:
                    {   //midtable terá o valor da tabela que faz a relação entre os dois objectos
                    	
                        midtable = boDefHandler.getBoDefinition(parent).getAttributeRef(att.getName()).getBridge().getBoMasterTable();
                        
                        String attlig = ".BOUI)";
                        if(nodePai.getParent()!=null && !((Boolean)((Triple)nodePai.getUserObject()).getSecond()).booleanValue())
                          attlig = ".CHILD$)";   
                        if(fromC!=null)           
                          if(inn)
                            fromC += " INNER JOIN "+ getTableAlias(midtable) +" ON("+midtable+"."+"PARENT$="+parent_tab+attlig;
                          else
                            fromC += " LEFT OUTER JOIN "+ getTableAlias(midtable) +" ON("+midtable+"."+"PARENT$="+parent_tab+attlig;
                        else
                            fromC = "FROM "+getTableAlias(midtable);
                        //adicionar a condição de join com outer join
                        if(((Boolean)att_t.getSecond()).booleanValue())
                        {                           	
                                if(fromC!=null)
                                    fromC += " INNER JOIN " + getTableAlias(s) + " ON("+midtable+"."+"CHILD$="+s+".BOUI)";
                                else
                                    fromC = "FROM "+getTableAlias(s);                                
                        }
                        break;
                    }
                    
                    case boDefAttribute.RELATION_1_TO_N_WBRIDGE:
                    {   //midtable terá o valor da tabela que faz a relação entre os dois objectos
                        midtable = boDefHandler.getBoDefinition(parent).getAttributeRef(att.getName()).getBridge().getBoMasterTable();            
                        
                        String attlig = ".BOUI)";
                        if(nodePai.getParent()!=null && !((Boolean)((Triple)nodePai.getUserObject()).getSecond()).booleanValue())
                          attlig = ".CHILD$)";   
                        if(fromC!=null)           
                          if(inn)
                            fromC += " INNER JOIN "+getTableAlias(midtable)+" ON("+midtable+"."+"PARENT$="+parent_tab+attlig;
                          else
                            fromC += " LEFT OUTER JOIN "+getTableAlias(midtable)+" ON("+midtable+"."+"PARENT$="+parent_tab+attlig;
                        else
                            fromC = "FROM "+getTableAlias(midtable);
                            
                        if(((Boolean)att_t.getSecond()).booleanValue())
                        {    	                    		
                                if(fromC!=null)
                                    fromC += " INNER JOIN " + getTableAlias(s) + " ON("+midtable+"."+"CHILD$="+s+".BOUI)";
                                else
                                    fromC = "FROM "+getTableAlias(s);
                                
                        }
                        break;
                    }
                    
                    case boDefAttribute.RELATION_MULTI_VALUES:
                    {   
                        midtable = boDefHandler.getBoDefinition(parent).getAttributeRef(att.getName()).getBridge().getBoMasterTable();
                        
                        String attlig = ".BOUI)";
                        if(nodePai.getParent()!=null && !((Boolean)((Triple)nodePai.getUserObject()).getSecond()).booleanValue())
                          attlig = ".CHILD$)";
                        if(fromC!=null)           
                          if(inn)
                            fromC += " INNER JOIN "+getTableAlias(midtable)+" ON("+midtable+"."+"PARENT$="+parent_tab+attlig;
                          else
                            fromC += " LEFT OUTER JOIN "+getTableAlias(midtable)+" ON("+midtable+"."+"PARENT$="+parent_tab+attlig;
                        else
                            fromC = "FROM "+getTableAlias(midtable);
                          
                        if(((Boolean)att_t.getSecond()).booleanValue())
                        {    
                        	
                                if(fromC!=null)
                                    fromC += " INNER JOIN " + getTableAlias(s) + " ON("+parent_tab+"."+att.getName()+"$="+s+".BOUI)";
                                else
                                    fromC = "FROM "+getTableAlias(s);                               
                        }
                        
                        break;
                    }
                    
                }
            //se não queremos que a tabela conste o valor booleano estará a falso, são situações onde basta a tabela de ligação   
            
        //        addSecurity(bo_obj_att, false);
            }
            node = node.getNextNode();  //próximo nó
        }
        
        for (int i = 1; i < fromTree.size(); i++) {
            node = (DefaultMutableTreeNode) fromTree.get(i);
            midtable = (String) node.getUserObject();
            // fromC += ", " + midtable;
            if (midtable.equalsIgnoreCase("EBO_TEXTINDEX")
                       || midtable.equalsIgnoreCase("OEBO_TEXTINDEX")) {

                  String database = boConfig.getApplicationConfig().getDataDataSourceClassName();
                  if (database.equalsIgnoreCase(OracleDBM.SQLSERVER_IMPL)) {
                       fromC += " INNER JOIN " + midtable + " ON(" + midtable + ".UI$=" + base_tab + ".BOUI)";
                  } else {
                       fromC += " INNER JOIN o" + midtable + " ON(o" + midtable + ".UI$=" + base_tab + ".BOUI)";
                  }

            } else {
                  fromC += " LEFT OUTER JOIN " + midtable + " ON(" + midtable
                             + "." + "BOUI=" + base_tab + ".BOUI)";
            }
      }
      fromC += " "; // um espacinho para não ficar tudo ligado :)

        
    }
    
    
    /**Simplificação da função, visto que nao é necessária qq troca de Wildcards
     * @param s     String a substituir
     * @return      a mesma string*/
    protected String makeWildcardString(String s)
    {
      return s;
    }
    
    
    
	//Problem with objects that have a relation to itself
	//when you need to do an operation on that relation (ex: ORDER)                    		

    
    /**Função auxiliar que permite verificar se uma tabela já está a ser utilizada
     * na query, existem if's que usam esta função e no caso de ser true
     * então devem criar um alias para a tabela (função seguinte)
     * @param table     nome da tabela a verificar
     * @return      verdadeiro se encontrar
     * */
    private boolean isTableInUse(String table)
    {
    	boolean toRet=false;  	
    	for (int i=0;i<tablesused.size();i++)
    	{
    		String usedTable=(String)tablesused.get(i);
    		if (table.equalsIgnoreCase(usedTable))
    		{
    			toRet=true;
    			break;
    		}
    	} 	
    	return toRet;
    }
    
    private String getTableAlias(String table)
    {
    	String tabname=table;   	
  
		if (isTableInUse(table))
			tabname=table + " " + table + (tabN++);
		tablesused.add(table);
    	
    	return tabname;
    }
    
}   
