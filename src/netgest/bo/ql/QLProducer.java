/*Enconding=UTF-8*/

package netgest.bo.ql;




import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefDatabaseObject;
import netgest.bo.def.boDefHandler;
import netgest.bo.ql.V2.IQLObjectAttribute;
import netgest.bo.runtime.EboContext;
import netgest.bo.security.securityRights;

/**
 * 
 * 
 * 
 *  Classe abstracta que contém as funções necessárias para qualquer QLProducer responsável pela produção de queries SQL. 
 *  <p>
 *  Os métodos estao implementados de forma a suportar uma sintaxe compatível com o Oracle 8i. 
 *  <p>
 *  Ao criar um novo produtor que extende esta classe, ou uma classe que herde desta, será apenas necessário redefinir as funções que apresentem 
 *  mudanças importantes.
 *  <p>
 *  Na sua essência, a classe do tipo QLProducer vai recebendo informações do Parser sobre as várias condições, acumulando estados, caso necessário, 
 *  e através do método <tt>toString()</tt> produz cada uma das clausulas que compõem o SQL fazendo da sua união o SQL desejado.
 *  <p>
 *  @author Ricardo Andrade
 */
public abstract class QLProducer  {

/**o parser que chamou este producer*/
    protected QLParser parser; 
    /**o contexto passado para a query*/
    protected EboContext ctx;                 
    /**A clasusla SQL correspondente ao <code>SELECT</CODE>*/
    protected String selectC;    
    /**A clasusla SQL correspondente ao <code>FROM</CODE>*/
    protected String fromC;     
    /**A clasusla SQL correspondente ao <code>WHERE</CODE>*/
    protected String whereC;     
    /**A clasusla SQL correspondente ao <code>ORDER BY</CODE>*/
    protected String orderC;                
    /**A clasusla SQL correspondente ao <code>GROUP BY</CODE>*/
    protected String groupC;   
    /**A clasusla SQL correspondente ao <code>HAVING</CODE>*/
    protected String havingC;                 
    /**indica se será necessário produzir a clausula <code>WHERE</CODE>*/
    protected boolean needWhere = false;  
    /**indica se será necessário produzir a clausula <code>ORDER BY</CODE>*/
    protected boolean needOrder = false;
    /**indica se será necessário produzir a clausula <code>GROUP BY</CODE>*/
    protected boolean needGroup = false;   
    /**indica se será necessário produzir a clausula <code>HAVING</CODE>*/
    protected boolean needHaving = false;     
    /**indica se a próxima condição pertence à clausula <code>HAVING</CODE>(true) ou à clausula <code>WHERE</CODE>*/
    protected boolean groupHaving = false;     
    /**guarda objecto relacional (MY, MYGROUP, etc.)*/
    protected String hint;     
     /**guarda a hint se ouver*/
    protected String relobj;     
     /**guarda o user (USING)*/
    protected String user;                   
    /**nome da tabela da BD do objecto Seleccionado */
    protected String objTable;  
    /**handler do obejcto seleccionado*/
    protected boDefHandler selObj;            
    
    /** vector que guarda os vários atributos que vão fazer parte da clausula <code>SELECT</CODE>*/
    protected Vector selectRules;
    /**vector que guarda árvores que descrevem os joins.<p> Uma árvore é mts vezes suficiente.
     * Este tipo de árvores tem na cabeça uma String com o nome do objecto seleccionado e os seus filhos são do tipo boDefAttribute
     * */
    protected Vector fromTree;                
    /**regras da clausla <code>WHERE</CODE> que vão sendo guardadas à medida que o parser informa*/
    protected Vector whereRules;   
   /**regras de join que vão sendo guardadas à medida que o parser informa */
    protected Vector joinRules;    
    /**regras sobre o estado dos objectos que vão sendo guardadas à medida que o parser informa*/
    protected Vector stateRules;        
    /**regras da clausla <code>ORDER BY</CODE> que vão sendo guardadas à medida que o parser informa*/
    protected Vector orderRules;       
    /**regras da clausla <code>GROUP BY</CODE> que vão sendo guardadas à medida que o parser informa*/
    protected Vector groupRules;              
    /**regras da clausla <code>HAVING</CODE> que vão sendo guardadas à medida que o parser informa*/
    protected Vector havingRules;       
    /**regras referentes ao sistema de chaves de segurança que vão sendo guardadas à medida que o parser informa*/
    protected Vector keyRules;
    /** valor booleano sobre se o objecto foi estendido*/
    protected boolean ext_obj=false;
    /** valor booleano sobre se o sistema de segurança está activo*/
    protected boolean security=true;
    /** valor booleano sobre se ocorreram <code>OR</code> na query*/
    protected boolean exists_or = true;
    /** posição em que o objecto a ser selecioando se encontra no vector <tt>selectRules</tt>*/
    protected int selobjPos = 0;
    
//    protected int pFromPos=-1;
    /** expressão Wildcard que representa um ou mais caracteres*/
    protected String anyWildcard = "%";
    /** expressão Wildcard que representa um caracter*/
    protected String oneWildcard = "_";
    /** expressão que representa o caracter de escape*/
    protected String escapeChar = "\\";
    
    /**Construtor por defeito que realiza todas as inicializaçõe necessárias*/
    protected QLProducer()
    {
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
    protected QLProducer(QLParser qp, EboContext xeboctx, boolean sec)
    {
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
    /**
 *  Retorna apenas a clausula <code>FROM</CODE>.
 *  
 *  @return uma {@link java.lang.String} com a clausula <code>FROM</CODE>
*/
    protected String getFromClause()
    {
      if(fromC != null)
        return fromC.substring(5);
      else
        return "";
    }
 /**  Retorna a query SQL tirando a clausula <code>SELECT</CODE>
 *  
 *  @return uma {@link java.lang.String} com a clausula <code>FROM</code>, a clausula <code>WHERE</code> e subsquentes
 *  */
    protected String getFromAndWhereClause()
    {
        return getFromClause()+getWhereClause()+getGroupClause()+getHavingClause()+getOrderClause();
    }
  /**
   * Retorna apenas a clausula <code>WHERE</CODE>
   * @return String com a clausula SQL <code>WHERE</CODE>
   * */
  
    protected String getWhereClause()
    {
        if(whereC != null)
            return whereC;
        else
            return "";
    }
    /**
   * Retorna apenas a clausula <code>ORDER BY</CODE>
   * @return String com a clausula SQL <code>ORDER BY</CODE>
   * */
    protected String getOrderClause()
    {
        if(orderC != null)
            return orderC;
        else
            return "";
    }
    /**
   * Retorna apenas a clausula <code>GROUP BY</CODE>
   * @return String com a clausula SQL <code>GROUP BY</CODE>
   * */
    protected String getGroupClause()
    {
        if(groupC != null)
            return groupC;
        else
            return "";
    }
   /**
   * Retorna apenas a clausula <code>HAVING</CODE>
   * @return String com a clausula SQL <code>HAVING</CODE>
   * */ 
    protected String getHavingClause()
    {
        if(havingC != null)
            return havingC;
        else
            return "";
    }
    /**devovle o handler do objecto seleccionado
     * @return Handler do Objecto sleccinado*/
    protected boDefHandler getObjectDef() {
        return selObj;  
    }
    
    /**Produz a clausla <code>SELECT</CODE>. Invocada pelo parser quando termina de analisar o <code>SELECT</CODE> passando o nome do objecto seleccionado.
     * <p>
     * Faz uso do Vector <tt>selectRules</tt> para identificar o objecto a ser selecionado e incluir os campos a serem mostrados.
     * Verifica ainda se será necessário estender o objecto e implentar as seguranças
     * @param ext    indica a necessidade de extender o objecto seleccionado
     * @return String o nome da view do objecto selccionado*/
    protected String prodSEL(boolean ext)
    {   
        int i = 0;
        if(selobjPos<0)
        {
          selObj = boDefHandler.getBoDefinition((String)selectRules.get(0)); //guarda referencia para o objecto
          i = 1;
        }
        else
          selObj = boDefHandler.getBoDefinition((String)selectRules.get(selobjPos)); //guarda referencia para o objecto
        
        if(ext)
            objTable = selObj.getBoExtendedTable();           //guarda o nome da tabela do objecto seleccionado
        else
            objTable = selObj.getBoMasterTable();           //guarda o nome da tabela do objecto seleccionado

        selectC = "SELECT "+hint+" ";          //constroi a respectiva clausla SELECT do SQL
        for(; i < selectRules.size(); i++)
        {
          if(i == selobjPos) {
        	  if( selObj.getDataBaseManagerXeoCompatible() ) {
        		  selectC += objTable+".BOUI ";
        	  }
        	  else {
        		IQLObjectAttribute[] ret = null;
        		boDefDatabaseObject[] dbobjs = selObj.getBoDatabaseObjects();
        		for( int k=0; k < dbobjs.length; k++ ) {
        			if( dbobjs[k].getType() == boDefDatabaseObject.DBOBJECT_PRIMARY ) {
        				String[] selatts = dbobjs[k].getExpression().split(",");
        				for( int z=0; z < selatts.length; z++ ) {
        					if( z > 0 ) {
        						selectC += ",";
        					}
        					selectC += objTable+"." + selObj.getAttributeRef( selatts[z].trim() ).getDbName();
        				}
        				break;
        			}
        		}        		  
        	  }
          } 
          else
            selectC += (String)selectRules.get(i);
        }
        selectC += " ";
        checkSecClassName(selObj);
        addSecurity(selObj, true);
        
        return objTable;
    }
    /**Verifica se o utilziador cujo contexto é realizada a query pode visualizar o objecto, ou filhos do objecto, selccionado
     * @param obj    referencia para o objecto a ser analizado*/
    protected void checkSecClassName(boDefHandler obj)
    {
      if(security && obj.getClassType()!=boDefHandler.TYPE_ABSTRACT_CLASS)
      {
        boDefHandler[] boSC = obj.getTreeSubClasses();
        for(int i = 0; i < boSC.length; i++)
        {
          boDefHandler bo = (boDefHandler)boSC[i];
          try{
            if(!securityRights.hasRights(ctx, bo.getName()))
            {
              needWhere = true;
              whereRules.add(objTable+".CLASSNAME<>'"+bo.getName()+"' AND ");
            }
          }catch(Exception e) { }
        }
      }
    }
    /**Adiciona os mecanismos de segurança para o referido objecto. Na prática adiciona as tabelas de chaves e impõe as condições no vector <tt>keysRules</tt>
     * @param obj    referencia para o objecto a ser analizado
     * @deprecated método fora de utilização por ser ineficiente*/
    protected void addSecurityE(boDefHandler obj)
    {
        if(security && obj.implementsSecurityRowObjects() && obj.getClassType()!=boDefHandler.TYPE_ABSTRACT_CLASS)
        {
            long[] ks;
            try{
                if ( securityRights.isSupervisor( ctx ))
                {
                    return;
                }
                ks = securityRights.getPerformerKeys(ctx);
                if(ks == null)
                    return;
            }catch(Exception e) {  return; }
            needWhere=true;
            boDefAttribute boAk = obj.getAttributeRef("KEYS");
            boDefAttribute boAkp = obj.getAttributeRef("KEYS_PERMISSIONS");
            addChild(obj.getName(), boAk, new Boolean(false), new Boolean(false), 0);
            addChild(obj.getName(), boAkp, new Boolean(false), new Boolean(false), 0);
            for(int i=0; i < ks.length; i++)
            {
                keyRules.add(boAk.getBridge().getBoMasterTable()+".CHILD$="+ks[i]);
                keyRules.add(boAkp.getBridge().getBoMasterTable()+".CHILD$="+ks[i]);
            }
            keyRules.add(boAk.getBridge().getBoMasterTable()+".CHILD$ is null");
//            keyRules.add(boAkp.getBridge().getBoMasterTable()+".CHILD$ is null");
        }
    }
    /**Adiciona os mecanismos de segurança para o referido objecto. Na prática adiciona as tabelas de chaves e impõe as condições no vector <tt>keysRules</tt>
     * @param obj    referencia para o objecto a ser analizado*/
    protected void addSecurity(boDefHandler obj, boolean sel_obj)
    {
        boolean is = obj.implementsSecurityRowObjects();
        boolean abs = obj.getClassType()!=boDefHandler.TYPE_ABSTRACT_CLASS;
        if(security && obj.implementsSecurityRowObjects() && obj.getClassType()!=boDefHandler.TYPE_ABSTRACT_CLASS)
        {
            long[] ks;
            try{
                if ( securityRights.isSupervisor( ctx ))
                {
                    return;
                }
                ks = securityRights.getPerformerKeys(ctx);
                if(ks == null || ks.length <=0)
                    return;
            }catch(Exception e) {  return; }
            needWhere=true;
            
            boDefAttribute boAk = obj.getAttributeRef("KEYS");
            boDefAttribute boAkp = obj.getAttributeRef("KEYS_PERMISSIONS");
            
            String kTab = boAk.getBridge().getBoMasterTable();
            String rule = "EXISTS (SELECT " + kTab+".CHILD$ FROM ";
            
            rule+= kTab + " WHERE " + kTab+".PARENT$ = ";
                if(sel_obj && ext_obj)
                {
                  rule+= obj.getBoExtendedTable();
                  existsInFromTree(obj.getBoExtendedTable()); 
                }
                else
                {
                  rule+= obj.getBoMasterTable();
                  existsInFromTree(obj.getBoMasterTable()); 
                }
            rule+=".boui AND " + kTab+".CHILD$ in (";
            String keyStore = "";
            for(int i=0; i < ks.length; i++)
            {
                if(i != 0)
                {
                    keyStore+=",";
                }
                keyStore+=ks[i];
            }
            rule+=keyStore+") )";// OR "+ kTab+".CHILD$ is null)";
            keyRules.add(rule);
            
            /*
            kTab = boAkp.getBridge().getBoMasterTable();
            rule = "EXISTS (SELECT " + kTab+".CHILD$ FROM ";
            rule+= kTab + " WHERE " + kTab+".PARENT$ = ";
            if(sel_obj && ext_obj)
              rule+= obj.getBoExtendedTable();
            else
              rule+= obj.getBoMasterTable();
            rule+=".boui AND " + kTab+".CHILD$ in ("+ keyStore+"))";
            keyRules.add(rule);
            
            kTab = boAk.getBridge().getBoMasterTable();
            rule = "NOT EXISTS (SELECT " + kTab+".CHILD$ FROM ";
            rule+= kTab + " WHERE " + kTab+".PARENT$ = ";
            if(sel_obj && ext_obj)
              rule+= obj.getBoExtendedTable();
            else
              rule+= obj.getBoMasterTable();
            rule+=".boui)";
            keyRules.add(rule);
            */
//            keyRules.add(boAkp.getBridge().getBoMasterTable()+".CHILD$ is null");
        }
    }
    
    /**Adiciona os mecanismos de segurança para o referido objecto. Na prática adiciona as tabelas de chaves e impõe as condições no vector <tt>keysRules</tt>
     */
    protected void Optimizing()
    {
      if(whereRules.size() == 1 && joinRules.size() == 2 && ((DefaultMutableTreeNode)fromTree.get(0)).getLeafCount()==1)
      {
        StringTokenizer rtok = new StringTokenizer((String)whereRules.get(0), ".=");
        if(rtok.countTokens() == 3)
        {
          String tab = rtok.nextToken();
          String boui = rtok.nextToken();
          String number = rtok.nextToken();
          Integer i = new Integer(0);
          if(boui.equalsIgnoreCase("boui"));
          {
            try{
                i = new Integer(number);
            }catch(Exception e)
            {
                return;
            }  
            i=i;
            if(fromC.startsWith("FROM "+tab))
            {
              String subst = tab+"."+boui;
              String jr0 = (String)joinRules.get(0);
              int pos=jr0.indexOf(subst);
              if(pos==0)
                jr0 =  number + jr0.substring(subst.length()) ;
              else
                jr0 = jr0.substring(0, pos)+number;
              
              joinRules.setElementAt(jr0,0);
              StringTokenizer seltok = new StringTokenizer((String)joinRules.get(1)," =");
              //seltok.nextToken();
              String tabsel = seltok.nextToken();
              selectC = "SELECT distinct " + tabsel + " AS "+boui;
              StringTokenizer fromtok = new StringTokenizer(tabsel, ".");
              fromC = " FROM "+ fromtok.nextToken()+" ";
              joinRules.remove(1);
              whereRules.remove(0);
            }
          }
        }
      }
      
    }
    
    /**Produz a clausula <code>FROM</CODE>.
     * Esta talvez seja a função que mais precisa de ser redefinida para evoluir o QLProducer para outras versões de SQL. <p>
     * 
    *A produção da clausula <code>FROM</CODE> envolve a sequência de tabelas a constar no <code>FROM</CODE> mas também a criação das condições de join queas ligam.
    * <p>
    *Temos de percorrer a árvore criada para o efeito que contém os atributos usados e que nos darão as tabelas necessárias.
    * A raiz da árvore é uma string com o objecto seleccionado e em que os seus sucessores são obvjectos do tipo {@link Triple} contendo três objectos:
    * um boDefAttribute que relaciona o filho com o seu pai e nos dá as tabelas a serem introduzidas, um Boolean que assinala a necessidade de a tabela filho ser introduzida e outro Boolean que indica a necesside de ser usado um Inner Join para fazer a ligação.
    * */
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
                          if(fromC.indexOf(midtable)<0)
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
                          if(fromC.indexOf(midtable)<0)
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
                              if(fromC.indexOf(midtable)<0)
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
                      if(fromC.indexOf(s)<0)
                        fromC += ", "+s;
                    }
                    else
                        fromC = "FROM "+s;
                    
            }
          //  addSecurity(bo_obj_att, false);
          }
          
          node = node.getNextNode();  //próximo nó
          
        }
        
        for(int i = 1; i < fromTree.size(); i++)
        {
            node = (DefaultMutableTreeNode)fromTree.get(i);
            midtable = (String)node.getUserObject();   
            fromC += ", " +  midtable;
            if(midtable.equalsIgnoreCase("EBO_TEXTINDEX") || midtable.equalsIgnoreCase("OEBO_TEXTINDEX")) //SQLServer
              joinRules.add(base_tab+".BOUI="+midtable+".UI$(+)");
            else
              joinRules.add(base_tab+".BOUI="+midtable+".BOUI(+)");
        }
        fromC += " ";       //um espacinho para não ficar tudo ligado :)
    }
    
    /**Esta função irá produzir a clausula <code>WHERE</CODE>. Nesta versão a Clausula <code>WHERE</CODE> é fromada por 3 partes
    *WHERE ((joins) AND (states) AND (Keys) AND (condicions)) 
    */
    protected void prodWHERE()
    {
        needWhere = false;      //assim que começamos deixa de ser necessário lembrar de fazer o WHERE
        String res="";          //string com as condições do utilziador (condicions)
        String resJoin="";      //string com as condições de join (joins)  
        String resState="";     //string com as condições dos estados (state)
        String r="";            //junção de todas as anteriores
        String resKey="";
         try{
            if(relobj!=null)    //antes de começar a produzir tempo ainda para meter as condições do MY
            {
                if(relobj.equalsIgnoreCase(parser.RELOBJ_MY))
                {
                    joinRules.add(objTable+".SYS_USER="+ctx.getBoSession().getPerformerBoui());
                    needWhere = true;
                }
                if(relobj.equalsIgnoreCase(parser.RELOBJ_MYGROUP))//TODO:ainda por fazer, não ficou definido
                {
                    
                    joinRules.add(objTable+".SYS_USER="+ctx.getBoSession().getPerformerBoui());
                    needWhere = true;
                }
            }
            
        }catch(Exception e){}   //para apanhar se der uma excepção, principalmente por falta de contexto
        
        if(joinRules.size() > 0)
            resJoin=(String)joinRules.get(0);   //pôr a primeira
        for(int i=1; i < joinRules.size(); i++) //e iterativamente as restantes
            resJoin += " AND " + (String)joinRules.get(i);
        if(stateRules.size() > 0)               
            resState=(String)stateRules.get(0);
        for(int i=1; i < stateRules.size(); i++)
            resState += " AND " + (String)stateRules.get(i);
        if(keyRules.size() > 0)               
            resKey=(String)keyRules.get(0);
        for(int i=1; i < keyRules.size(); i++)
            resKey += " OR " + (String)keyRules.get(i);
        for(int i=0; i < whereRules.size(); i++)
            res += " "+(String)whereRules.get(i);
        if (res.equalsIgnoreCase("") && resJoin.equalsIgnoreCase("") && resKey.equalsIgnoreCase("") && resState.equalsIgnoreCase(""))  
            return;                         //se nada foi feito então não deviamos estar aqui, só para confirmar.
        //WHERE ((joins) AND (states) AND (condicions)) 
        if(!resJoin.equalsIgnoreCase(""))
            r += "(("+resJoin+")";          //((joins)
        if(!resState.equalsIgnoreCase(""))
            if(!r.equalsIgnoreCase(""))     
                r += " AND ("+resState+")"; //AND (states)
            else
                r += "("+resState+")";      
        if(!resKey.equalsIgnoreCase(""))
            if(!r.equalsIgnoreCase(""))     
                r += " AND ("+resKey+")"; //AND (Keys)
            else
                r += "("+resKey+")";      
        if(!res.equalsIgnoreCase(""))
            if(!r.equalsIgnoreCase(""))
                r += " AND ("+res+")";      //AND (condicions)
            else
                r += "("+res+")";
                
        if(!resJoin.equalsIgnoreCase(""))
            r += ")";                       //fechar o primeiro (
            
        if(!r.equalsIgnoreCase(""))
            whereC = "WHERE "+r;            //por fim, o necessário WHERE com tudo o resto
    }
    
    /**função para produzir a clausula <code>ORDER BY</CODE>
    */
    protected void prodORDER()
    {
        needOrder = false;
        if(orderRules.size() > 0)
            orderC=" ORDER BY "+(String)orderRules.get(0);
        for(int i=1; i < orderRules.size(); i++)
            orderC += ", " + (String)orderRules.get(i);
  }
    
    /**função que na verdade não produz condições mas que guarda o MY e assinala a necessidade de ser processado
     * @param ref    referência á opção escolhida (MY|MYGROUP)
     * */
    protected void prodRELOBJ(String ref)
    {
        needWhere = true;
        relobj = ref;
    }
    
    /**como a anterior não produz, mas guarda a condição dos estados fornceida pelo Parser
     * @param ref    condição sobre estados enviada pelo Parser 
     * */
    protected void prodSTATE(String ref)
    {
        needWhere = true;
        stateRules.add(ref);
    }
    
    /**cria a clausula SQL do <code>GROUP BY</CODE>*/
    protected void prodGroup()
    {
        if(groupRules.size() > 0)                                   
            groupC = " GROUP BY " + (String)groupRules.get(0);      //junta o primeiro
        for(int i=1; i < groupRules.size(); i++)                    //e os restantes iterativamente
        {
            groupC += ", " + (String)groupRules.get(i);
        }
    }
    
    /**cria a clausula <code>HAVING</CODE> do SQL com base no vector de regras associado*/
    protected void prodHaving()
    {
        if(havingRules.size() > 0)
            havingC = " HAVING " + (String)havingRules.get(0);
        for(int i=1; i < havingRules.size(); i++)
        {
            havingC += " " + (String)havingRules.get(i);
        }
    }
    
    /**o Parser usa esta função para adicionar uma regra de Order.
     * <p>
     * Para além de introduzir no respectivo vector, fará também a verificação se existe uma clausula <code>GROUP BY</CODE> e adiciona o atributo caso este ainda não tenha sido especificado
     * @param ref    condição de ordenação enviada pelo Parser 
     * */
    protected void addOrderRule(String ref, boolean copyGroupToOrder)
    {
        needOrder = true;
        orderRules.add(ref);
        if(copyGroupToOrder && groupRules.size() > 0)
        {
          String ref_s=ref;
          int pos = ref.toUpperCase().indexOf(" ASC");
          if(pos >= 0)
            ref_s = ref.substring(0,pos);
          else
          {
            pos = ref.toUpperCase().indexOf(" DESC");
            if(pos >= 0)
              ref_s = ref.substring(0,pos);
          }
          
          if(!groupRules.contains(ref_s)) {
        	  // Só copia se a coluna a ordenar não for um indice de coluna
        	  try {
        		  Integer.parseInt( ref_s );
        	  }
        	  catch( NumberFormatException e ) {
        		  groupRules.add(ref_s);
        	  }
          }
        }

    }
    
    /**a função que produz o resultado final, desencadeiando as funções necessarias
     * @return  String com o SQL correspondente à query XEO introduzida
     * */
    public String toString()
    {   String res="";
        prodFrom();
        if(needWhere)
           prodWHERE();
        if(needOrder)
           prodORDER();
        if(needGroup)
        {
           prodGroup();
        }
        if(needHaving)
        {
           prodHaving();
        }
        if(selectC!=null)
            res += selectC;
        if(fromC!=null)
            res += fromC;
        if(whereC!=null)
            res += whereC;
        if(groupC!=null)
            res += groupC;
        if(havingC!=null)  
            res += havingC;
        if(orderC!=null)
            res += orderC;
        return res;
    }
    
    /**devolve o nome real na base de dados de uma dado objecto
     * @param W    nome do objecto a processar 
     * @return     nome da tabela da base de dados que representa o obecjto
     * */
    protected String DBObjName(String W) {
        if ( W.indexOf('.') > 0)
        {   
            String[] xw=W.split("\\.");
            W=xw[0];
        }
        return boDefHandler.getBoDefinition(W).getDbName();
    }
    
    /**Define a raiz da árvore que guarda as relações entre as tabelas utilizadas.<p>
    *A raiz é diferente de todos os seus filhos pois é uma String
    * @param table    nome do objecto seleccionado
    * @return         posição no vector <tt>fromTree</tt> em que o objecto que representa a raiz foi introduzido
    * */
    protected int setRootFrom(String table)
    {
        String masTab = boDefHandler.getBoDefinition(table).getBoMasterTable();
        String rootTab;
        int i;
        for(i = 0; i < fromTree.size(); i++)
        {
            rootTab = getRoot(i);
            if(rootTab.equalsIgnoreCase(masTab))
                return i;
        }
        fromTree.add(new DefaultMutableTreeNode(masTab));
        return i;
    }
    
    /**adiciona um filho à árvore <code>FROM</CODE>.
     * recebe o nome do pai, o atributo a introduzir, se vamos querer que ele conste no SQL, 
    *e a posição no vector de árvores <code>FROM</CODE> (0 como default)
    * @param parent    nome do pai do nó a adicionar
    * @param newChild  boDef do atributo que queremos adicionar á estrutura
    * @param need      true se quisermos que a tabela filho conste no SQL a produzir, false se contrário
    * @param inner     true se queremos explicitamente usar um Inner Join para ligar este attributo à tabela pai
    * @param pos       posição do vector <tt>fromTree</tt>, ou seja, qual a árvore a que estamos a adicionar o atributo
    * @return          <tt>true</tt> se a operação ocorreu com sucesso, <tt>false</tt> caso contrário
    * */
    protected boolean addChild(String parent, boDefAttribute newChild, Boolean need, Boolean inner, int pos)
    {
        if(parent==null || newChild==null)  //ter s certeza que os dados de entrada estão correctos
            return false;
            
        //nomde das tabelas do filho e do pai            
        String refChild = newChild.getReferencedObjectName();
        boDefHandler boChild = boDefHandler.getBoDefinition(refChild);
        String schild = boChild.getBoMasterTable();

        boDefHandler boParent = boDefHandler.getBoDefinition(parent);
        String realParent = boParent.getBoMasterTable();
        
        if(pos >= fromTree.size())  //caso ainda não tenha sido criada a árvore
        {
            fromTree.add(new DefaultMutableTreeNode(realParent));
        }
        
        
        //buscar a raiz da árvore, ficando node=child
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)fromTree.get(pos);
        DefaultMutableTreeNode child = (DefaultMutableTreeNode)fromTree.get(pos);
        String s = ((String)node.getUserObject());  //a raiz da árvore é uma string
        boolean stop = false;
        while(!s.equalsIgnoreCase(realParent) && !stop)  //achar na árvore o pai
        {
            child = child.getNextNode();
            if(child==null)
            {
                if(ext_obj)
                {
                     boDefHandler[] subC = selObj.getTreeSubClasses();
                     if(subC.length <= 0)
                        return false;
                     boDefHandler bo;
                     boDefAttribute boA;
                     for(int i=0; i < subC.length; i++)
                     {
                        bo = (boDefHandler)subC[i];
                        if(bo.getBoMasterTable().equalsIgnoreCase(realParent))
                        {
                            child = node;
                            stop = true;
                            break;
                        }
                     }
                 }
                
            }
            else
            {
                s = ((boDefAttribute)(((Triple)child.getUserObject()).getFirst())).getReferencedObjectName();
                s = boDefHandler.getBoDefinition(s).getBoMasterTable();
            }
        }
        node = child;
        int i;
/*        try{
          Triple p = (Triple)node.getUserObject();
          if(p != null)
            p.setSecond(new Boolean(true));
        }catch(Exception e) { }
*/        for(i=0; i < node.getChildCount(); i++) //ver se o pai já não possui um filho igual ao que queremos introduzir
        {
            child = (DefaultMutableTreeNode)node.getChildAt(i);
            boDefAttribute node_att = (boDefAttribute)((Triple)child.getUserObject()).getFirst();
            s = node_att.getReferencedObjectName();
            s = boDefHandler.getBoDefinition(s).getBoMasterTable();
            boolean show = ((Boolean)((Triple)child.getUserObject()).getSecond()).booleanValue();
            if(s.equalsIgnoreCase(schild) )
              if(node_att.getName().equalsIgnoreCase(newChild.getName()))
                if(newChild.getBridge()!=null)
                {
                    if(node_att.getBridge().getBoMasterTable().equalsIgnoreCase(newChild.getBridge().getBoMasterTable()))
                    {
                        Boolean need_val = (Boolean)((Triple)child.getUserObject()).getSecond();
                        if(!need_val.booleanValue())
                          ((Triple)child.getUserObject()).setSecond(need);
                        return false;
                    }
                }
                else
                    return false;
        }
        //se não, estamos prontos a introduzir
        node.add(new DefaultMutableTreeNode(new Triple(newChild, need, inner)));

        return true;
    }
    
    /**verificar a existencia do objecto na árvore, se existir o valor booleano que deifne se a tabela filho deve aparacer passa a <code>true</code>   
     * @param n   nome do objecto a pesquisar
     * @return    <tt>true</tt> se o objecto existe na árvore, <tt>false</tt> caso contrário*/
    protected boolean existsInFromTree(String n)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)fromTree.get(0);
        String s = (String)node.getUserObject();
        if(s==null)    
            return false;
        else
            if(s.equalsIgnoreCase(n))
                return true;
        boDefAttribute att;
        node = node.getNextNode();
        while(node!=null)       //procurar até percorrermos toda a árvore
        {
            Triple att_p = (Triple)node.getUserObject();    //cada nó é formado pelo par (attributo, booleano) 
            att = (boDefAttribute)((Triple)node.getUserObject()).getFirst();
            String attName = att.getReferencedObjectName();
            String attTab = boDefHandler.getBoDefinition(attName).getBoMasterTable();
            if(attTab.equalsIgnoreCase(n))              //se for igual 
            {
                att_p.setSecond(new Boolean(true));     //valor booleano = true
                return true;
            }
            node = node.getNextNode();
        }
        return false;
    }
    /**função para adicionar um atributo a ser seleccionado
     * @param rule  atributo a ser seleccionado 
     */
    protected void addSelectRule(String rule)
    {
        selectRules.add(rule);
    }
    /**função para definir o objecto a ser seleccionado
     * @param rule      objecto a ser seleccionado 
     * @param withboui  <tt>true</tt> se for para adicionar o seu boui nos atributos a seleccionar
     */
    protected void addFirstSelectRule(String rule, boolean withboui)
    {
      if(withboui)
        selobjPos = selectRules.size();
      else
        selobjPos = -1;
      selectRules.add(rule);
    }
    
    /**função para adicionar as condições definidas pelo utilizador 
     * @param rule  regra a ser adicionada */
    protected void addWhereRule(String rule)
    {
        needWhere = true;
        if(groupHaving)
        {
            addHavingRule(rule);
            groupHaving = false;
        }
        else
            whereRules.add(rule);
    }
    
    /**função para adicionar regras para a clausula <code>HAVING</CODE>, chamada nas funções de agregação (SUM, COUNT, etc.)
     * @param rule  regra a ser adicionada
     * */
    protected void addHavingRule(String rule)
    {
        needHaving = true;
        havingRules.add(rule);
    }
    
    /**função para adicionar regras para a clausula <code>GROUP BY</CODE>
     * @param rule        regra a ser adicionada
     * @param withHaving  <tt>true</tt> se for chamada no contexto de uma função de agregação que necessita especificar uma regra <code>HAVING</CODE>*/
    protected void addGroupRule(String rule, boolean withHaving)
    {
        needGroup = true;
        groupHaving = withHaving;
        if(!groupRules.contains(rule))
          groupRules.add(rule);
    }
    /**Devolve o nome do obejcto seleccionado da árovre <tt>pos</tt>
     * @param pos        posição do vector <tt>fromTree</tt>, ou seja, qual a árvore a que nos estamos a referir
     * @return           nome do obejcto seleccionado
     */ 
    protected String getRoot(int pos)
    {
        
        if(fromTree.size() > pos)
        {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)fromTree.get(pos);
            if(root != null)
                return (String)root.getUserObject();
            else
                return null;
        }
        else
            return null;
    }
    /**Define a existencia de hint na query
     * @param h     hint a ser introduzida
     */
    protected void setHint(String h)
    {
        hint = h;
    }
    /**Define se existe na query XEO referencia à opção EXT que define o obejcto seleccionado como objecto estendido
     * @param val     <tt>true</tt> se houver referência, <tt>false</tt> caso contrário
     */
    protected void setExt(boolean val)
    {
        ext_obj = val;
    }
    /**Verifica se existe na query XEO referencia à opção EXT que define o obejcto seleccionado como objecto estendido
     * @return      <tt>true</tt> se houver referência, <tt>false</tt> caso contrário
     */
    protected boolean getExt()
    {
        return ext_obj;
    }
    /**Define o contexto em que a query é executada
     * @param boctx     contexto em que a query é executada
     */
    protected void setCtx(EboContext boctx)
    {
      ctx = boctx;
    }
    /**Informa o QLProducer da existência de <tt>OR</tt>s na query. Útil para efeitos de optimização
     */
    protected void foundOr()
    {
      exists_or=true;
    }
    /**Verifica se foi detectado até ao momento a necessidade da presença da clausula <code>GROUP BY</code>
     * @return    tt>true</tt> se houber necessidade da clausula <code>GROUP BY</code>, <tt>false</tt> caso contrário
     */
    protected boolean existsGroupBy()
    {
      return needGroup;
    }
    
/*    protected int fromPos()
    {
      return pFromPos;
    }
    
    protected void setFromPos(int i)
    {
      pFromPos = i;
    }
*/    
    /**Produz o SQL necessário para expressar a condição StartsWith. É provavel que tenha que ser mudada quando for utilizada noutras versões pois usa funções tipicas do Oracle
     * @param arg     atributo que vai ser pesquisado
     * @param letter  letra pela qual a procura deve ser feita
     * @param not     <tt>true</tt> se a construção not antecede StratsWith na query XEO, <tt>false</tt> caso contrário
     * @return      Condição SQL que expressa StartsWith
     */
    protected String makeStartsWith(String arg, String letter, boolean not)
    {
      char c = letter.toLowerCase().charAt(0);
      String res="";
      String snot="";
      if(not)
        snot=" not";
      switch(c)
      {
        case '#':
        {
          if(not)
            res = arg + snot+" LIKE '0%' AND " + arg + snot+" LIKE '1%' AND " + arg + snot+" LIKE '2%' AND "
                + arg + snot+" LIKE '3%' AND " + arg + snot+" LIKE '4%' AND " + arg + snot+" LIKE '5%' AND "
                + arg + snot+" LIKE '6%' AND " + arg + snot+" LIKE '7%' AND " + arg + snot+" LIKE '8%' AND "
                + arg + snot+" LIKE '9%'" ;
          else
            res = arg + snot+" LIKE '0%' OR " + arg + snot+" LIKE '1%' OR " + arg + snot+" LIKE '2%' OR "
                + arg + snot+" LIKE '3%' OR " + arg + snot+" LIKE '4%' OR " + arg + snot+" LIKE '5%' OR "
                + arg + snot+" LIKE '6%' OR " + arg + snot+" LIKE '7%' OR " + arg + snot+" LIKE '8%' OR "
                + arg + snot+" LIKE '9%'" ;
          break;
        }
        default:
        {
			res = " UPPER( translate(" + arg + ",'ÁÀÃÂáãàâÉÈÊéèêÍÌíìÓÒÕÔóòõôÚÙúù','AAAAaaaEEEeeeIIiiOOOOooooUUuu') )"+snot+" LIKE translate('" + letter.toUpperCase() + "%','ÁÀÃÂáãàâÉÈÊéèêÍÌíìÓÒÕÔóòõôÚÙúù','AAAAaaaEEEeeeIIiiOOOOooooUUuu')";   
        }
      }
      return res;
    }
    /**Produz o SQL necessário para expressar a condição CONTAINS. É provavel que tenha que ser mudada quando for utilizada noutras versões pois usa funções tipicas do Oracle
     * @param arg   texto a pesquisar
     * @return      condição SQL que expressa CONTAINS*/
    protected String makeContains(String arg)
    {
    	return parser.ctx.getDataBaseDriver().getDriverUtils()
    	.getFullTextSearchWhere( "TEXT" ,  arg );
      //return " CONTAINS(TEXT,"+arg+")>0 ";
    }
    /**Produz a string correspondente á passada mas com os Wildcards da respectiva versão. 
     * Noutras versões que não da Oracle basta redefinir as variáveis que definem os caracteres especiais e manter esta função.
     * @param s     String a substituir
     * @return      String alterada com os Wildcards da presente versão*/
    protected String makeWildcardString(String s)
    {
      char[] charray = s.toCharArray();
      int p = s.indexOf("%");
      while(p>=0)
      {
        if(p!= 0 && escapeChar.charAt(0) != charray[p-1])
        {
          charray[p]=anyWildcard.charAt(0);
        }
        p = s.indexOf("%", p+1);
      }
      p = s.indexOf("_");
      while(p>=0)
      {
        if(p!= 0 && escapeChar.charAt(0) != charray[p-1])
        {
          charray[p]=oneWildcard.charAt(0);
        }
        p = s.indexOf("_", p+1);
      }
      return new String(charray);
    }
    
    /**Produz a string correspondente ao allias da nome passado em <tt>s</tt>.
     * No caso do Oracle a função apenas retorna a String <tt>s</tt>, noutras versões, por exemplo no SQL SERVER é necessário acrescentar a palavra AS.
     * @param s     String de alias
     * @return      String alterada da forma usada pelo alias*/
    protected String makeAlias(String s)
    {
      return "\""+s+"\"";
    }
}   


