/*Enconding=UTF-8*/
package netgest.bo.ql;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import netgest.bo.boConfig;
import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefClsState;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefInterface;
import netgest.bo.def.boDefViewer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.security.securityRights;
import netgest.bo.system.boSession;
import netgest.bo.xep.Xep;
import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;

/**
 *
 * 
 *  Classe responsável pela identificação dos vários componentes de uma querie XEO.
 *  <p>
 *  Ao longo da identificação envia a informação necessária ao Produtor e detecta possíveis erros sintácticos.
 *  <p>
 *  O reconhecimento da linguagem de interrogação é feito com base numa gramática.
 *  <p>
 * @author Ricardo Andrade
*/
public class QLParser  {

    
    private static final String SEL_SELECT="select";
    /**
     * Variável estática com a string que identifica a restrição da selcção de objectos criados pelo utilizador. Na query toma a forma MY
     */
    protected static final String RELOBJ_MY="my";
    /**
     * Variável estática com a string que identifica a restrição da selcção de objectos criados pelo grupo do utilizador. Na query toma a forma MYGROUP
     */
    protected static final String RELOBJ_MYGROUP="mygroup";
    private static final String RELOBJ_MYGROUPS="mygroups";
    private static final String USING_USING="using";
    private static final String STATE_STATE="state";
    private static final String OPST_AND="and";
    private static final String OPST_OR="or";
    private static final String WHERE_WHERE="where";
    private static final String EXPRE_EQUAL="=";
    private static final String EXPRE_LESS="<";
    private static final String EXPRE_GREAT=">";
    private static final String EXPRE_LE="<=";
    private static final String EXPRE_GE=">=";
    private static final String EXPRE_DIFF="!=";
    private static final String EXPRE_DIFF2="<>";
    private static final String EXPRE_LIKE="like";
    private static final String ATRIB_BRIDGE="bridge";
    private static final String CALC_PLUS="+";
    private static final String CALC_MINUS="-";
    private static final String CALC_PROD="*";
    private static final String CALC_DIV="/";
    private static final String ORDER_OREDER="order";
    private static final String ORDER_BY="by";
    private static final String GROUP_GROUP="group";
    private static final String GROUP_BY="by";
    private static final String VIEW_VIEWER="viewer";
    private static final String AGG_SUM="sum";
    private static final String AGG_AVG="avg";
    private static final String AGG_COUNT="count";
    private static final String AGG_MIN="min";
    private static final String AGG_MAX="max";
    private static final String DIREC_ASC="asc";
    private static final String DIREC_DESC="desc";
    private static final String NOT_NOT="not";
    private static final String OPWx_IN="in";
    private static final String OPWx_EXIST="exists";
    private static final String OPWx_IS="is";
    private static final String OPWx_NULL="null";
    private static final String EXT_EXT="ext";
    private static final String EXT_EXTEN="extended";
    private static final String OPWx_START="startswith";
    private static final String OPWH_CONTAIN="contains";
    private static final String FROM_FROM="from";
    private static final String GROUP_ERR1="Impossible to make group by after an aggregate function";

    private static final String SEL_ERR1="Target Object not defiened";
    private static final String SEL_ERR2="Object name is incorrect or doesn't exist";
    private static final String SEL_ERR3="Query should begin with SELECT word";
    private static final String USING_ERR1="UserID not specified";
    private static final String USING_ERR2=" is not a recognized user";
    private static final String STATE_ERR1="State not defined";
    private static final String STATE_ERR2="Not a valid state for this object";
    private static final String STATE_ERR3="Incorrect state sintax (pristate.secstate)";
    private static final String WHERE_ERR1="No where expression defined";
    private static final String VAL_ERR1="Empty WHERE expression";
    private static final String VAL_ERR2="Incomplete expression";
    private static final String RVAL_ERR2="Missing ' to terminate the string";
    private static final String LVAL_ERR1="Attribute required after Object (obj_name.att_name)";
    private static final String ATRIB_ERR1="Not a valid attribute";
    private static final String ATRIB_ERR2="You must define a bridge atribute (bridge_name.bridge.bridge_attribute)";
    private static final String ATRIB_ERR3="Expecting attribute after '.' ";
    private static final String ATT_ERR4="It’s not possible to compare bridge attributes";
    private static final String AGG_ERR1="Aggregate function must have an atribute.";
    private static final String AGG_ERR2="Aggregate function needs an atribute between parentheses.";
    private static final String CALC_ERR1="Incomplete Expression"; 
    private static final String TYPE_ERR1="Type missmatch; String not expected";
    private static final String TYPE_ERR2="Type missmatch; Number not expected";
    private static final String EXPRE_ERR1="Expression must have a relational operator (=,<,>,<=,>=,!=, <>, like, in, is null)";
    private static final String EXPRE_ERR2="Incomplete Expression";
    private static final String EXPRE_ERR3="Missing right parentheses";
    private static final String OPST_ERR1="Missing left parentheses";
    private static final String OPWx_ERR1="Missing parentheses";
    private static final String OPWX_ERR2="Operator IS without NULL";
    private static final String ORDER_ERR1="Unexpected end of query";
    private static final String ORDER_ERR2="Object doesn't make part of any query expression";
    private static final String VIEW_ERR1="Viewer must be defined for the object";
    private static final String VIEW_ERR2="Viewer not defined for the selected object";
    private static final String EXT_ERR1="Class doesn't have any extended sub-calsses";
    private static final String ATT_ERR1="Final attribute can not be extended";
    private static final String FATT_ERR1="Incomplete Function";
    private static final String FATT_ERR2="Unrecognized Function";
    private static final String FATTx_ERR1="Missing argument in function";
    private static final String STARTS_ERR1="Incorrect STARTSWITH statement (ex: arg STARTSWITH 'a')";
    private static final String CONT_ERR1="Incorrect CONTAINS statement (ex: SELECT task where CONTAINS 'a')";
    private static final String ATVAL_ERR1="Incorrect use of [ ] (ex: [ sqlstatement ])";
    private static final String ALLI_ERR1="Allias already in use";
    private static final String PERF_ID_TAG="CTX_PERFORMER_BOUI";
    private static final String PERF_USERNAME_TAG="CTX_PERFORMER_USERNAME";
    private static final String PERF_GROUPS_BOUIS_TAG="CTX_BOUIS_ALLPERFORMERGROUPS";
    private static final String PERF_GROUPS_TAG="CTX_ALLPERFORMERGROUPS"; // Grupos e hierarquia de grupos
    private static final String PERF_FLAT_GROUSPS_TAG="CTX_PERFORMERGROUPS"; // Só grupos que o utilizador pertence directamente
    private static final String PERF_MEMBER_OF_FLAT="CTX_MEMBEROF_FLAT"; // Todas as chaves excepto grupos hierarquicos.
    private static final String PERF_SAME_GROUP_USERS="CTX_USERS_OF_SAME_GROUP"; // Todas as chaves dos utilizadores do mesmo grupo.
    private static final String PERF_GROUPS_SET="CTX_SET_OF_GRUPS_USER_SEE"; // Conjunto de grupos de onde um user pode ver as actividades 
    private static final String XWFMYWORK="CTX_XWFMYORWK";

    

    private Vector words;           //vector de tokens a serem reconhecidos
    private Vector words_pos;       //posição na string da query dos vários tokens
    private String textQuery;       //string da query original
    private int consumer;           //indicador de posição a ser consumida
    
    private String textObj;         //objecto a ser selecionado (select X)
    private String lastAtt;         //último atributo a ser invocado
    private String lastObj;         //último objecto a ser invocado
    private String actualObj;       //objecto que estamos a referenciar em cada momento
    private String realObj;         //nome real na BD no actualObj
    private String relOp;           //último operado realcional usado
    private int numPar=0;           //contador de parênteses de expressões númericas 
    private int subQueries=0;       //contador de parênteses de sub-queris
    private Vector lastParV;        //vector que gere a ordem pela qual os parentêses apareceram


    private Vector prodV;           //vector que armazena produtores para não misturar contexto de subqueries
    private Hashtable ht_allias;
    
    private boDefHandler boReferenced;  //o handler do objecto seleccionado
    protected EboContext ctx;                     //contexto passada pela a query
 //   private boolean ext_obj=false;
    private int pos=0;
    private boolean sec=true;
    private boolean contRule=false;
    protected boolean textIndexOnReturnObject=false; //Flag para saber se o textindex é feito pelo o object que está no from. ou pelo que está a ser retornado
    private boolean sucess=false;
    private String viewer="list";
    
    private boolean typeStr = true;
    private boolean typeNum = true;

    private boolean prod9i = true;    
    private boolean firstSel = true;
    private boolean inSel = true;
    private boolean withPar = false;
    private Vector par;
    private static final boolean otherChar = false;
    
    private boolean copyGroupToOrder = true;
    
    private boDefHandler objDef = null;
    
//    private int fromPos = -1;
//    private int wherePos = -1;
//    private int standbyPos = -1;
    
    private Vector positions;
    
    private Xep xep_eval = null;
    
    
/**
 *
 * 
 *  Construtor por defeito da calsse QLParser
*/
    public QLParser()
    {
        relOp = OPST_AND;
        boReferenced=null;
        prodV = new Vector();
        lastParV = new Vector();
        par = new Vector();
        ht_allias = new Hashtable();
        positions= new Vector();
    }
/**
 *  Construtor da calsse QLParser que permite indicar a utilização de parametros
 *  
 *  @param withParameters   quando passado o valor true irá preparar o parser para trabalhar com parametros
*/
    public QLParser(boolean withParameters)
    {
        relOp = OPST_AND;
        boReferenced=null;
        prodV = new Vector();
        lastParV = new Vector();
        withPar = withParameters;
        par = new Vector();
        ht_allias = new Hashtable();
        positions= new Vector();
    }
/**
 *  Processa <tt>strQuery</tt> sobre o contexto <tt>xeboctx</tt> devolvendo a clausula <code>FROM</code>, a clausula <code>WHERE</code> e subsquentes.
 *  
 *  @param strQuery   string com a query XEO
 *  @param xeboctx    contexto de execução da query
 *  @return uma {@link java.lang.String} com a clausula <code>FROM</code>, a clausula <code>WHERE</code> e subsquentes
 *  @see        java.lang.String
*/    
    public String getFromAndWhereClause( String strQuery, EboContext xeboctx )
    {
      if(hasNoSecurityHint(strQuery))
      {
        strQuery = removeNoSecurityHint(strQuery);
        sec = false;
      }
      return getFromAndWhereClause(strQuery, xeboctx, sec);
    }
    
    public String getFromAndWhereClause( String strQuery, EboContext xeboctx, boolean security )
    {
      if(hasNoSecurityHint(strQuery))
      {
        strQuery = removeNoSecurityHint(strQuery);
        security = false;
      }
      String toRet=toSql( strQuery,xeboctx, security);
      return prod().getFromAndWhereClause();
    }

/**
 *  Processa <tt>strQuery</tt> sobre o contexto <tt>xeboctx</tt> devolvendo a clausula FROM.
 *  
 *  @param strQuery   string com a query XEO
 *  @param xeboctx    contexto de execução da query
 *  @return uma {@link java.lang.String} com a clausula FROM
*/
  public String getFromClause( String strQuery, EboContext xeboctx )
  {
      if(hasNoSecurityHint(strQuery))
      {
        strQuery = removeNoSecurityHint(strQuery);
        sec = false;
      }
      return getFromClause(strQuery, xeboctx, sec);
  }
  
  public String getFromClause( String strQuery, EboContext xeboctx, boolean security )
  {
      if(hasNoSecurityHint(strQuery))
      {
        strQuery = removeNoSecurityHint(strQuery);
        security = false;
      }
      String toRet=toSql( strQuery,xeboctx, security);
      return prod().getFromClause();
  }

/**
 *  Processa <tt>strQuery</tt> sobre o contexto <tt>xeboctx</tt> devolvendo a clausula WHERE e subsquentes.
 *  
 *  @param strQuery   string com a query XEO
 *  @param xeboctx    contexto de execução da query
 *  @return uma {@link java.lang.String} com a clausula WHERE e subsquentes
*/  
  public String getWhereClause( String strQuery, EboContext xeboctx )
  {
      if(hasNoSecurityHint(strQuery))
      {
        strQuery = removeNoSecurityHint(strQuery);
        sec = false;
      }
      return getWhereClause(strQuery,xeboctx, sec);
  }
  
  public String getWhereClause( String strQuery, EboContext xeboctx, boolean security )
  {
      if(hasNoSecurityHint(strQuery))
      {
        strQuery = removeNoSecurityHint(strQuery);
        security = false;
      }
      String toRet=toSql( strQuery,xeboctx, security);
      return prod().getWhereClause();
  }

/**
 *  Processa <tt>strQuery</tt> sobre o contexto <tt>xeboctx</tt> devolvendo a clausula FROM, a clausula WHERE e subsquentes.
 *  
 *  @return um {@link Vector} com o valor dos vários parametros encontrados na query
*/  
  public Vector getQueryParameters()
  {
    return par;
  }

/**
 *  Mostra se na Query processada foi pedido que o objecto seleccionado fosse estendido.
 *  
 *  @return se houve referência à opção <code>EXT</code> ou <code>EXTENDED</code> de forma a estender o objecto seleccionado devolve true, caso contrário devolve false
*/    
  public boolean isObjectExtended()
  {
	  if( this.objDef != null ) {
		  return false;
	  }
	  return prod().getExt();
  }

/**
 *  Testa se a query foi processada com sucesso, i.e., se não foram detectados erros sintácticos.
 *  
 *  @return se não forem detectados erros sintáticos retornará true, caso contrário retornará false
*/    
  public boolean Sucess()
  {
      return sucess;
  }

/**
 *  Devolve o nome do viewer especificado na query. Método para a consola HTML.
 *  
 *  @return nome do Viewer especificado na query
*/    
  public String getViewer()
  {
      return viewer;
  }

/**
 *  Devolve o nome do objecto seleccionado na query.
 *  
 *  @return nome do objecto seleccionado na query.
*/   
  public String getObjectName()
  {
	  if( this.objDef != null ) {
		  return this.objDef.getName();
	  }
      return prod().getObjectDef().getName();
  }

  public String getSelectedObjectName()
  {
      if( prod().selObj != null )
      {
        return prod().selObj.getName();
      }
      return getObjectName();
  }


  public boDefHandler getSelectedObjectDef()
  {
      if( prod() != null && prod().selObj != null )
      {
        return prod().selObj;
      }
      return getObjectDef();
  }
  
  public String getTextQuery()
  {
      return textQuery;
  }
  /*
   * Verifica se o textindex é feito pelo o object que está no from. ou pelo que está a ser retornado
   */
  public boolean isTextIndexOnReturnObject()
  {
      return textIndexOnReturnObject;
  }

/**
 *  Trasnforma a string da query XEO numa string para ser passada por URL substituindo os caracteres especiais. Método para a consola HTML.
 *  
 *  @return string da query XEO com os caracteres especiais substituídos
*/   
  public String getURLboql()
  {
    return getURLboql(this.textQuery);
  }
  
  public static String getURLboql(String query)
  {
      String res = query;
      
      res = res.replaceAll("%", "%25");
      
      
      res = res.replaceAll("&", "%26");
      res = res.replaceAll("#", "%23");
      res = res.replaceAll("=","%3D");
      res = res.replaceAll("'","%27");
      res = res.replaceAll("<","%3C");
      res = res.replaceAll(">","%3E");
      res = res.replace('+','=');
      res = res.replaceAll("=","%2B");
      res = res.replaceAll("/","%2F");
      res = res.replaceAll("!","%21");
      res = res.replaceAll(" ", "+");
      res = res.replaceAll("\\[", "%5B");
      res = res.replaceAll("]", "%5D");
      res = res.replaceAll("\\{", "%7B");
      res = res.replaceAll("}", "%7D");
      
       
      
      return res;
  }
  public static String stupidEncode(String query)
  {
      String res = query;
      
      res = res.replaceAll("'","&");
      
       
      
      return res;
  }
/**
 *  Devolve a definição do objecto seleccionado.
 *  
 *  @return devolve a definição do objecto seleccionado.
*/   
  public boDefHandler getObjectDef() {
	  if( this.objDef != null ) {
		  return this.objDef;
	  }
	  else
		  return prod().getObjectDef();
  }

    private QLProducer prod()
    {
    	if( prodV.size() > 0 ) {
    		return (QLProducer)prodV.lastElement();
    	}
    	return null;
    	
    }
    
    private int fromPos()
    {
      if(positions.size() > 0)
      {
        Triple tp = (Triple)positions.get(0);
        Integer inte = (Integer)tp.getFirst();
        if(inte == null)
          return -1;
        else
          return inte.intValue();
      }
      else
       return -1; 
    }
    
    private int wherePos()
    {
      if(positions.size() > 0)
      {
        Triple tp = (Triple)positions.get(0);
        Integer inte = (Integer)tp.getSecond();
        if(inte == null)
          return -1;
        else
          return inte.intValue();
      }
      else
       return -1; 
    }
    
    
    private String getCurrentWord()
    {
        String s = (String)words.get(consumer);
        incConsumer();
        return s;
    }
    
    private boolean incConsumer()
    {
        consumer++;
        return consumer >= words.size();
    }
    
    private boolean endOfQuery()
    {
        return consumer >= words.size();
    }
    
    private String lookahead(int i)
    {
        if(consumer+i>=words.size())
            return null;
        return (String)words.get(consumer+i);
    }
    
    private String lookCurrentWord()
    {
        if(endOfQuery())
            return null;
        return (String)words.get(consumer);
    }
    
/**
 *  Faz o processamento da query dada em <tt>strQuery</tt> sobre o contexto <tt>xeboctx</tt>.
 *  Processa a query com o sistema de segunças activo.
 *  
 *  @param strQuery   string com a query XEO
 *  @param xeboctx    contexto de execução da query
 *  @return String com consulta SQL correspondente
 *  @exception boqlParserException se forem detectados erros sintaticos será lanaçada uma excepção
*/      
    public String toSql(String strQuery, EboContext xeboctx) throws boqlParserException
    {
        return toSql(strQuery, xeboctx, true);
    }
/**
 *  Faz o processamento da query dada em <tt>strQuery</tt> sobre o contexto <tt>xeboctx</tt> trabalhando com parametros.
 *  
 *  Ao chamar esta função o utilizador passa os valores dos parametros marcados com o sinal ? na query, se o Parser encontrar outros argumentos acrescenta ao vector de parametros.
 *  Processa a query com o sistema de segunças activo.
 *  
 *  @param strQuery   string com a query XEO
 *  @param xeboctx    contexto de execução da query 
 *  @param paremeters vector de parametros do utilizador
 *  @return String com consulta SQL correspondente
 *  @exception boqlParserException se forem detectados erros sintaticos será lanaçada uma excepção
*/ 
    public String toSql(String strQuery, EboContext xeboctx, Vector paremeters) throws boqlParserException
    {
      par = paremeters;
      return toSql(strQuery, xeboctx, true);
    }
/**
 *  Faz o processamento das query dada em <tt>strQuery1</tt> e <tt>strQuery2</tt> sobre o contexto <tt>xeboctx</tt> e fazendo o SQL correspondente à sua intersecção
 *  
 *  Depios de processar <tt>strQuery1</tt> e <tt>strQuery2</tt> cria o SQL que faz a intersecção de ambas
 *  Processa a query com o sistema de segunças activo.
 * 
 *  @param strQuery1   string com a primeira query XEO
 *  @param strQuery2   string com a segunda query XEO
 *  @param xeboctx    contexto de execução da query 
 *  @return String com consulta SQL correspondente
 *  @exception boqlParserException se forem detectados erros sintaticos será lanaçada uma excepção
*/ 
    public String toSql(String strQuery1, String strQuery2, EboContext xeboctx) throws boqlParserException
    {
        String res1= toSql(strQuery1, xeboctx, true);
        String res2= toSql(strQuery2, xeboctx, true);
        return "("+res1+") INTERSECT ("+res2+")";
    }
/**
 *  Faz o processamento das query dada, com ou sem seguranças, em <tt>strQuery1</tt> e <tt>strQuery2</tt> sobre o contexto <tt>xeboctx</tt> e fazendo o SQL correspondente à sua intersecção
 *  
 * 
 *  @param strQuery   string com a primeira query XEO
 *  @param xeboctx    contexto de execução da query 
 *  @param sec        parametro que define se o sistema de seegunranças deve estar activo (true) ou não(false)
 *  @return String com consulta SQL correspondente
 *  @exception boqlParserException se forem detectados erros sintaticos será lanaçada uma excepção
*/     
    public String toSql(String strQuery, EboContext xeboctx, boolean sec) throws boqlParserException
    {
    	
    	if( strQuery.startsWith( "{" ) ) {
    		
    		
    		String objectName = strQuery.substring(1,strQuery.indexOf('}',2));
    		String sqlResult  = strQuery.substring(strQuery.indexOf('}',2) + 1 );
    		
    		this.objDef = boDefHandler.getBoDefinition( objectName );
    		if( this.objDef == null ) {
    			throw new RuntimeException( MessageLocalizer.getMessage("OBJECT")+" [" + objectName + "] "+MessageLocalizer.getMessage("DOESNT_EXIST_IN_THE_SYSTEM") );  
    		}
    		return sqlResult;
    	}
    	
    	if( prodV != null && prodV.size() > 0 ) {
    		prodV.clear();
    	}
    	
        //se tiver seguranças mas a query tem a hint /*NO_SECURIT*/ então a hint tem prioridade
        if(hasNoSecurityHint(strQuery))
        {
            strQuery = removeNoSecurityHint(strQuery);
            sec = false;
        }
        
        if(strQuery.indexOf( "/*TEXINDEXONRETURNOBJECT*/" ) > -1 ) 
        {
            textIndexOnReturnObject = true;
            strQuery = strQuery.replaceAll( "\\/\\*TEXINDEXONRETURNOBJECT\\*\\/","" );
        }
        if ( strQuery.indexOf( XWFMYWORK  ) >-1 )
        {
            try
            {
                String xgroups="(0=1)";
                long keysG[]=securityRights.getPerformerXWF( xeboctx , xeboctx.getBoSession().getPerformerBoui() );
                if ( (keysG != null && keysG.length >0) )
                {
                    
                      xgroups="( ";
                        
                        for (int j = 0; keysG != null && j < keysG.length ; j++) 
                        {
                           
                            xgroups+=" assignedQueue ="+keysG[j]+" ";
                            if ( j+1 < keysG.length  )
                            {
                                xgroups+=" or ";
                            }
                        }
                    xgroups+=")";
                }
                strQuery= strQuery.replaceAll(XWFMYWORK,xgroups );
            }
            catch (SQLException e)
            {
                
            }
            catch (boRuntimeException e)
            {
                
            }
        }

        if ( strQuery.indexOf( PERF_USERNAME_TAG  )>-1 )
        {
            strQuery = strQuery.replaceAll( PERF_USERNAME_TAG, xeboctx.getSysUser().getUserName() );
        }
        
        if ( strQuery.indexOf( PERF_ID_TAG  )>-1 )
        {
            strQuery = strQuery.replaceAll( PERF_ID_TAG, String.valueOf(xeboctx.getBoSession().getPerformerBoui()) );
        }
        
        if ( strQuery.indexOf( PERF_FLAT_GROUSPS_TAG  )  >-1 )
        {
            try
            { 
                long keysG[]=securityRights.getPerformerFlatGroupsKeys( xeboctx , xeboctx.getBoSession().getPerformerBoui() );
                String xgroups =" assignedQueue in (";
                int j;
                for (j = 0; keysG != null && j < keysG.length ; j++) 
                {
                    if( j > 0 )
                    {
                        xgroups += ", ";
                    }
                    xgroups += keysG[j];
                }
                if( j==0 ) xgroups = "0=1";
                else xgroups +=")";
                strQuery= strQuery.replaceAll(PERF_FLAT_GROUSPS_TAG,xgroups );
            }
            catch (Exception e)
            {
                e=e;    
            }
        }
        if ( strQuery.indexOf( PERF_MEMBER_OF_FLAT  )  >-1 )
        {
            try
            { 
                long keysG[]=securityRights.getPerformerFlatKeys( xeboctx , xeboctx.getBoSession().getPerformerBoui() );
                String xgroups =" assignedQueue in (";
                int j;
                for (j = 0; keysG != null && j < keysG.length ; j++) 
                {
                    if( j > 0 )
                    {
                        xgroups += ", ";
                    }
                    xgroups += keysG[j];
                }
                if( j==0 ) xgroups = "0=1";
                else xgroups +=")";
                strQuery= strQuery.replaceAll(PERF_MEMBER_OF_FLAT,xgroups );
            }
            catch (Exception e)
            {
                e=e;   
            }
        }
        if ( strQuery.indexOf( PERF_SAME_GROUP_USERS ) > -1 )
        {
            try
            {
                
                long[] sameusers = securityRights.getSameGroupPerformers( xeboctx, xeboctx.getBoSession().getPerformerBoui() );                   
                StringBuffer sb = new StringBuffer();
                if( sameusers.length < 1000 )
                {
                    for (int i = 0; i < sameusers.length; i++) 
                    {
                        if( i > 0 ) sb.append(',');
                        sb.append( sameusers[i] );
                    }
                    if( sameusers.length == 0 ) sb.append('0'); 
                    strQuery = strQuery.replaceAll( PERF_SAME_GROUP_USERS, "("+sb.toString()+")" );
                }
                else 
                {
                    long[] groups = securityRights.getPerformerGroupsKeys( ctx, xeboctx.getBoSession().getPerformerBoui() );
                    sb.append("select distinct parent$ from oixeouser$groups ug where ug.CHILD$ in ("); 
                    if( groups.length > 0 )
                    {
                        for (int i = 0; i < groups.length; i++) 
                        {
                            if (i > 0)  sb.append(',');
                            sb.append( groups[i] );
                        }
                        sb.append(')');
                    }
                    String q = sb.toString().replaceAll("\\$","\\§");
                    strQuery = strQuery.replaceAll( PERF_SAME_GROUP_USERS, "(["+q+"])" );
                    strQuery = strQuery.replaceAll("\\§","\\$");
                } 
                
            }
            catch (Exception e)
            {
                e=e;
            }
        }
        //Alteração que vai fazer o sql que verifica se o grupo de uma determinada actividade se encontra num conjunto de bouis de 
        //grupos possiveis. 
        if ( strQuery.indexOf( PERF_GROUPS_SET ) > -1 )
        {
            try
            {   
                long[] groups = securityRights.getPerformerGroupsKeys( ctx, xeboctx.getBoSession().getPerformerBoui() );                  
                StringBuffer sb = new StringBuffer();
                
                    for (int i = 0; i < groups.length; i++) 
                    {
                        if( i > 0 ) sb.append(',');
                        sb.append( groups[i] );
                    }
                    if( groups.length == 0 ) sb.append('0'); 
                    strQuery = strQuery.replaceAll( PERF_GROUPS_SET, "("+sb.toString()+")" );
             
            }
            catch (Exception e)
            {
                e=e;
            }
        }
        
        
        try
        {
            for (int kwi = 0; kwi < 2; kwi++) 
            {
                String tolook = kwi==0?PERF_GROUPS_TAG:PERF_GROUPS_BOUIS_TAG;
                boolean bouis = kwi==1;
                String xgroups=bouis?"0":"(0=1)";
                if ( strQuery.indexOf( tolook ) > -1 )
                {
                    long keysG[]=securityRights.getPerformerAllKeys( xeboctx , xeboctx.getBoSession().getPerformerBoui() );
                    //boObject perf = boObject.getBoManager().loadObject(xeboctx, xeboctx.getBoSession().getPerformerBoui() );
                    boObjectList xlist=boObjectList.list( xeboctx, "select workQueue where administrator=CTX_PERFORMER_BOUI",1,100); 
                    if ( (keysG != null && keysG.length >0) || !xlist.isEmpty() )
                    {
                        if( !bouis  )
                            xgroups=" assignedQueue in (";
                        else
                            xgroups ="";            
                        xgroups +="";            
                        
                        boolean havegroups = false;
                        for (int j = 0; keysG != null && j < keysG.length ; j++) 
                        {
                            havegroups = true;
                            xgroups+= keysG[j];
                            if ( j+1 < keysG.length  )
                            {
                                xgroups+=", ";
                            }
                        }
                        String xq="";
                        if ( !xlist.isEmpty() )
                        {
                            xlist.beforeFirst();
                            while ( xlist.next() )
                            {
                                if( xq.length()>0) xq+=", ";
                                xq+=xlist.getCurrentBoui();
                                
                            }
                        }
                        if ( xq.length() > 0 )
                        {
                          if (havegroups)
                          {
                            xgroups+=",";
                          }
                          xgroups+=xq;
                        }
                        if( !bouis  )
                            xgroups+=") ";
                    }
                    strQuery= strQuery.replaceAll(tolook,xgroups );
                }
            }
        }
        catch (SQLException e)
        {
        }
        catch (boRuntimeException e)
        {
        }
        
        if ( strQuery.indexOf( "MYRECEIVEDMESSAGES"  )>-1 )
        { 
            String xgroups="(0=1)";
            try
            {
                
                xgroups="( toRef ="+xeboctx.getBoSession().getPerformerBoui()+" or ccRef = "+xeboctx.getBoSession().getPerformerBoui()+" or bccRef = "+xeboctx.getBoSession().getPerformerBoui();
                long keysG[]=securityRights.getPerformerAllKeys( xeboctx , xeboctx.getBoSession().getPerformerBoui() );
                //boObject perf = boObject.getBoManager().loadObject(xeboctx, xeboctx.getBoSession().getPerformerBoui() );
                boObjectList xlist=boObjectList.list( xeboctx, "select workQueue where administrator=CTX_PERFORMER_BOUI",1,100); 
                if ( (keysG != null && keysG.length >0) || !xlist.isEmpty() )
                {
                    
                  
                    for (int j = 0; keysG != null && j < keysG.length ; j++) 
                    {
                           xgroups+=" or toRef ="+keysG[j]+" or ccRef = "+keysG[j]+" or bccRef = "+keysG[j];
                        
                    }
                  
                    if ( !xlist.isEmpty() )
                    {
                        xlist.beforeFirst();
                        while ( xlist.next() )
                        {
                            
                            xgroups+=" or toRef ="+ xlist.getCurrentBoui() +" or ccRef ="+ xlist.getCurrentBoui() +" or bccRef ="+ xlist.getCurrentBoui() +" ";
                            
                        }
                    }

                }
                 xgroups+=" ) ";
            }
            catch (SQLException e)
            {
                
            }
            catch (boRuntimeException e)
            {
                
            }
            
            strQuery= strQuery.replaceAll("MYRECEIVEDMESSAGES",xgroups );
        }
        
        
        if ( strQuery.indexOf( "MYSENDMESSAGES"  )>-1 )
        { 
            String xgroups="(0=1)";
            try
            {
                long keysG[]=securityRights.getPerformerAllKeys( xeboctx , xeboctx.getBoSession().getPerformerBoui() );
                //boObject perf = boObject.getBoManager().loadObject(xeboctx, xeboctx.getBoSession().getPerformerBoui() );
                boObjectList xlist=boObjectList.list( xeboctx, "select workQueue where administrator=CTX_PERFORMER_BOUI",1,100);
                xgroups="( fromRef ="+xeboctx.getBoSession().getPerformerBoui();
                if ( (keysG != null && keysG.length >0) || !xlist.isEmpty() )
                {
                    
                    
                    for (int j = 0; keysG != null && j < keysG.length ; j++) 
                    {
                        
                        xgroups+=" or fromRef ="+keysG[j];
                       
                    }
                 
                    if ( !xlist.isEmpty() )
                    {
                        xlist.beforeFirst();
                        while ( xlist.next() )
                        {
                         
                            xgroups+=" or fromRef ="+ xlist.getCurrentBoui();
                            
                        }
                    }
                    
                  
                }
                xgroups+=" ) ";
            }
            catch (SQLException e)
            {
                
            }
            catch (boRuntimeException e)
            {
                
            }
            
            strQuery= strQuery.replaceAll("MYSENDMESSAGES",xgroups );
        }
        
        strQuery = QLOrderAndGroupByCardID.parseOrderByCardID(strQuery);
        strQuery = QLOrderAndGroupByCardID.parseGroupByCardID(strQuery);
       
        
        this.textQuery = strQuery;
        this.ctx = xeboctx;
        this.tokenizeStr();
        this.sec = sec;
        String toRet=null;
        if(textQuery !=null && this.textQuery.length() > 1)
        {
          toRet=run();
        }

        return toRet;    
    }
    
    //divide a query em elementos lógicos e reconhecivies armazenando-os num vector decorando ainda as posições 
    //em que elas aparecem para posterior identificação dos erros
    private void tokenizeStr(){
        String tok, tok2;
  
/*        try{
            if(ctx != null)
            {
                String id = ""+ctx.getBoSession().getPerformerBoui();
                repQuery = textQuery.replaceAll(PERF_ID_TAG, id);
            }
        }catch(Exception e) { repQuery = this.textQuery; }
*/
        //StringTokenizer stok = new StringTokenizer(textQuery, " .,()[]=<>!+-*/'\n", true); //caracteres a reconhecer como divisores
        
        String patterns = "\\{\\{|\\}\\}|[\\.\\,\\(\\)\\[\\]=<>!\\+\\-\\*/'\\n\\t\\?]|[a-zA-Z_$0-9]++|\\w++|\\W";
        Pattern p = Pattern.compile(patterns);
        Matcher m = p.matcher(textQuery);
        
        words = new Vector();   //vector de tokens
        words_pos = new Vector();   //vector da posição dos tokens na string da query
        int curr_pos = 0;
        consumer = 0;           
        Triple tpos=null;
        while (m.find())
        {
//            tok = textQuery.substring(m.start(), m.end());
            tok = m.group();
            curr_pos+=tok.length(); //incrementando á medida que vamos passando pela string por forma a podermos guardar as posições
            if(tok.length() > 1 && !tok.equals("{{"))    //se os tokens tiverem um tamanho maior que 1 então não existem mais verificações a fazer
            {                       
               words.add(tok.trim());   
               words_pos.add(new Integer(curr_pos));
               if(tok.equalsIgnoreCase(SEL_SELECT))
                tpos = new Triple();
               else
                 if(tok.equalsIgnoreCase(FROM_FROM) && !words.get(words.size()-2).equals("."))
                 {
                    if(tpos != null)
                      tpos.setFirst(new Integer(words.size()-1));
                 }
                 else
                  if(tok.equalsIgnoreCase(WHERE_WHERE))
                   {
                      if(tpos==null)
                        tpos = new Triple();
                      tpos.setSecond(new Integer(words.size()-1));
                      positions.add(tpos);
                      tpos=null;
                   }
            }
           else                     //se tiverem tamanho igual a 1...
               if(!tok.equals(" ") && tok.charAt(0)!=10 && tok.charAt(0)!=13 && tok.charAt(0)!=9 )   //...e se forem diferentes de espaços e quebras de linha
               {
                   if(!tok.equals("<") && !tok.equals(">") && !tok.equals("!")) //é necessário apanhar os casos <=, >=, !=, <>
                        if(tok.equals("'")) //nas strings queremos apanhar todos até á próxima plica
                        {
                            words.add(tok);
                            words_pos.add(new Integer(curr_pos));
                            if(m.find())
                            {
                                tok2 = m.group();
                                String str_val="";
                                String tok3=null;
                                while(tok2!=null && !tok2.equals("'") )
                                {
                                    
                                    str_val+=tok2;          //vamos juntando as strings em str_val
                                    if(m.find())
                                    {
//                                        tok2= textQuery.substring(m.start(), m.end());
                                        tok2 = m.group();
                                        if("'".equals(tok2) && textQuery.length() > m.end()+1)
                                        {
                                          tok3= textQuery.substring(m.end(), m.end()+1);
                                          if("'".equals(tok3))
                                          {
                                            str_val+=tok2+tok3;
                                            if(m.find() && m.find())
//                                              tok2= textQuery.substring(m.start(), m.end());
                                                tok2 = m.group();
                                          }
                                        }
                                        else
                                          tok3 = null;
                                    }
                                    else
                                        tok2=null;
                                }
                                if(!str_val.equalsIgnoreCase(""))
                                {
                                  words.add(str_val);
                                  curr_pos+=str_val.length();
                                  words_pos.add(new Integer(curr_pos));
                                }
                                if(tok2!=null)
                                {
                                    words.add(tok2);
                                    curr_pos+=tok2.length();
                                    words_pos.add(new Integer(curr_pos));
                                }
                                else
                                  break;
                            }
                            else
                              break;
                        }
                        else    //este é o caso em que tem tamanho = 1 mas é algo diferente de todos os casos de divisores com tamanho = 1
                        {
                            if(tok.equals("[")) //nas strings queremos apanhar todos até á próxima plica
                            {
                                words.add(tok);
                                words_pos.add(new Integer(curr_pos));
                                if(m.find())
                                {
                                    tok2 = textQuery.substring(m.start(), m.end());
                                    String str_val="";
                                    while(tok2!=null && !tok2.equals("]"))
                                    {
                                        str_val+=tok2;          //vamos juntando as strings em str_val
                                        if(m.find())
                                            tok2 = textQuery.substring(m.start(), m.end());
                                        else
                                            tok2=null;
                                    }
                                    if(!str_val.equalsIgnoreCase(""))
                                    {
                                      words.add(str_val);
                                      curr_pos+=str_val.length();
                                      words_pos.add(new Integer(curr_pos));
                                    }
                                    if(tok2!=null)
                                    {
                                        words.add(tok2);
                                        curr_pos+=tok2.length();
                                        words_pos.add(new Integer(curr_pos));
                                    }
                                }
                            }
                            else
                              if(tok.equals("{{")) //nas strings queremos apanhar todos até á próxima plica
                              {
                                  words.add(tok);
                                  words_pos.add(new Integer(curr_pos));
                                  if(m.find())
                                  {
                                      tok2 = textQuery.substring(m.start(), m.end());
                                      String str_val="";
                                      while(tok2!=null && !tok2.equals("}}"))
                                      {
                                          str_val+=tok2;          //vamos juntando as strings em str_val
                                          if(m.find())
                                              tok2 = textQuery.substring(m.start(), m.end());
                                          else
                                              tok2=null;
                                      }
                                      if(!str_val.equalsIgnoreCase(""))
                                      {
                                        words.add(str_val);
                                        curr_pos+=str_val.length();
                                        words_pos.add(new Integer(curr_pos));
                                      }
                                      if(tok2!=null)
                                      {
                                          words.add(tok2);
                                          curr_pos+=tok2.length();
                                          words_pos.add(new Integer(curr_pos));
                                      }
                                  }
                              }
                              else
                              {
                                words.add(tok);
                                words_pos.add(new Integer(curr_pos));
                              }
                        }
                   else //se o divisor é < , > , ! há que conferir se não estamos perante um destes casos: <=, >=, !=, <>
                        if(m.find()) 
                       {
                          tok2= textQuery.substring(m.start(), m.end());
                          if(tok2.charAt(0)!=10 && tok2.charAt(0)!=13 && !tok2.equalsIgnoreCase(" "))//não queremos espaços e afins
                          {
                            if(tok2.equals("=")||tok2.equals(">")) //é um dos nossos casos e queremos que sejam incluídos como um único token
                            {
                                words.add(tok.concat(tok2));
                                curr_pos++;
                                words_pos.add(new Integer(curr_pos));
                            }
                            else
                            {
                                //falso alarme. não é nenhum caso especial, guardamos como dois tokens separados
                                words.add(tok);
                                words_pos.add(new Integer(curr_pos));
                                words.add(tok2);
                                curr_pos+=tok2.length();
                                words_pos.add(new Integer(curr_pos));
                            }
                          }
                          else
                          {
                            words.add(tok);
                            curr_pos+=tok.length();
                            words_pos.add(new Integer(curr_pos));
                          }
                       }
                        else//se logo a seguir vinha um espaço ou afim então só nos interessa guardar o primeiro
                        {
                            words.add(tok);
                            curr_pos+=tok.length();
                            words_pos.add(new Integer(curr_pos));
                        }
               }
        }
        if(tpos!=null)
          positions.add(tpos);
  }
  
    private String run() throws boqlParserException
    {
        ResultQL res = S(false); //chamar S onde se comaça a analisar a query
        if(res.failed())    //caso falhe enviamos msg de erro
           throw new boqlParserException("QLParser", "XEO.QL", new Exception(res.getMessage()+errorSpot()), res.getMessage(), errorSpot());
        else
            if(numPar > 0 || subQueries > 0)    //se o utilizador esquecer de fechar parentêses    
                throw new boqlParserException("QLParser", "XEO.QL", new Exception(EXPRE_ERR3+errorSpot()), MessageLocalizer.getMessage("EXPRE_ERR3"), errorSpot());
            else
               if(endOfQuery()) //se está tudo correcto e todos os tokens foram consumidos podemos devolver o resultado
               {    sucess=true;
                    return res.getMessage();
               }
               else{
            	   String message = MessageLocalizer.getMessage("UNEXPECTED_CHAR_QUERY_SHOULD_BE_OVER_CORRECT_SO_FAR");
            	   String errorSpot = errorSpot();
                    throw new boqlParserException("QLParser", "XEO.QL", new Exception(message + errorSpot), message ,errorSpot);
               }
  }
  
  //função que sistematiza a remoção de produtores
  //cada subquerie cria um produtor para não misturara mtas variáveis que indicam estados. Depois é removido no fim da subquerie 
  private void removeProd()
  {
      int i=prodV.size();
      if(i>1)
      {
          prodV.removeElementAt(i-1);
      }
  }
  
  
  //S -> SEL STATE WHERE ORDER VIEW $
  private boolean substUserId()
  {
    String w;
    int count_par=0;
    if(ctx != null){
        
      
      for(int i=0; i < words.size(); i++)
      {
          w = (String)words.get(i);

          String id = "";
        id += ctx.getBoSession().getPerformerBoui();

        if(withPar)
        {
          if( w.equalsIgnoreCase(PERF_ID_TAG) )
          {
            words.set(i, "?");

            if(count_par > par.size())
              return false;

            par.add(count_par, new BigDecimal(id));
            
            count_par++;
          }
          else
            if(w.equalsIgnoreCase("?"))
            {
              count_par++;
              if(count_par > par.size())
                return false;
              
            }
        }
        else
        {
          if(w.equalsIgnoreCase(PERF_ID_TAG)  )
            words.set(i, id);
          else if ( w.equals( PERF_GROUPS_TAG ) || w.equals( XWFMYWORK ) )
          {
              
          }
        }
      }
      return true;
    }
    return true;
  }
  private ResultQL S(boolean ext)
  {
    lastObj = lastAtt = null;
    firstSel = true;
    inSel = true;
      if(prod9i)
       prodV.add(new QLProducer9i(this, ctx, this.sec)); //criar um produtor para esta query
      else
        prodV.add(new QLProducer8i(this, ctx, this.sec)); //criar um produtor para esta query
      prod().setExt(ext);
       ResultQL res = SEL();
        if(res.failed())
            return res;
        else
        {
            
            res = STATE();
             if(res.failed())
             {
                removeProd();
                return res;
             }
            else
            {
                 inSel = false;
                 firstSel = false;
                 lastObj = lastAtt = null;
                 typeStr = typeNum = true;
                 res=WHERE();
                 if(res.failed())
                 {
                    removeProd();
                    return res;
                 }
                else
                {
                  res=GROUP();
                  if(res.failed())
                  {
                      removeProd();
                      return res;
                  }
                  else
                  {
                    res=ORDER();
                    if(res.failed())
                    {
                        removeProd();
                        return res;
                    }
                    else
                    {
                        res=VIEW();
                        if(res.failed())
                        {
                            removeProd();
                            return res;
                        }
                        else
                        {
                                String sres = prod().toString(); //produzir o texto sql
                                removeProd();                   //remover o prod criado, importante por causa das subqueries
                                return new ResultQL(1, sres);   //devolver o resultado sql no tipo de retorno
                        }
                    }
                  }
                }
            }
               
        }
  }
  
  //SEL -> “select ” RELOBJ USING SOBJ
  private ResultQL SEL()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = getCurrentWord();
    if(s.equalsIgnoreCase(SEL_SELECT))
    {
      ResultQL res_h = HINT();
      if(res_h.failed())
          return res_h;
      else
      {
        ResultQL res_int = RELOBJ();
        if(res_int.failed())
            return res_int;
        else
        {
            res_int = USING();
            if(res_int.failed())
                return res_int;
            else
                if(endOfQuery())
                    return new ResultQL(2, SEL_ERR1); //"Target Object not defiened"
                else
                { 
                    ResultQL res;
                    if(fromPos() >= 0)
                    {
                      int standbyPos = consumer;
                      
                      consumer = fromPos() + 1;
                      res = SOBJ();
                      int interPos = consumer;
                      if(res.failed())
                        return res;
                      prod().addFirstSelectRule(actualObj, false);
                      consumer = standbyPos;
                      firstSel = false;
                      inSel = false;
                      lastObj = lastAtt = null;
                      ResultQL res_f = FROM();
                      if(res_f.failed())
                        return res_f;
                      else
                      {
                        if(wherePos() >= 0)
                          consumer = wherePos();
                        else
                          consumer = interPos;
                      }
                      if(positions.size() > 0)
                        positions.remove(0);
                    }
                    else
                    {
                      if(positions.size() > 0)
                        positions.remove(0);
                      res = SOBJ();
                      //Possibilitar DISTINCT deverá ser feito de outra forma o XEOQL deverá ignorar em qualquer passo 
                      //da query qualquer palavra que se encontre entre [] e continuar para a frente
                      if (res.getMessage()!=null && res.getMessage().indexOf("DISTINCT")>-1)prod().addSelectRule("DISTINCT ");
                      //FIM
                      prod().addFirstSelectRule(actualObj, true);                      
                    }
                    if(res.failed())
                        return res;
                    else
                    {
                        realObj = prod().prodSEL(prod().getExt()); //ao acabar a parte do SELECT enviar a info recolhida para o produtor
                        return new ResultQL(1);
                    }
                }
        }
      }
    }
    else    
        return new ResultQL(2, SEL_ERR3);//"Query should begin with SELECT word"
  }
  
  private ResultQL HINT()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    String hinttype=lookahead(2);
    if(s.equalsIgnoreCase("/") && lookahead(1) != null && lookahead(1).equalsIgnoreCase("*") && lookahead(2) != null && (lookahead(2).equalsIgnoreCase("+")
    		|| lookahead(2).equalsIgnoreCase("TOP"))) //SQLServer
    {
      incConsumer();incConsumer();
      if(!incConsumer())
      {
        String sh = lookCurrentWord();
        String hint = "/*";
        hint+=hinttype;
        while(!sh.equalsIgnoreCase("*") && !endOfQuery())
        {
          hint += " " +sh + " ";
          incConsumer();
          sh = lookCurrentWord();
        }
         if(sh.equalsIgnoreCase("*"))
         {
            incConsumer();
            sh = lookCurrentWord();
            if(sh.equalsIgnoreCase("/"))
            {
              incConsumer();
              prod().setHint( hint+" */");
              return new ResultQL(1);
            }
            else
              return new ResultQL(2, "HINT ERROR");
         }
         else
          return new ResultQL(2, "HINT ERROR");
      }
      else
        return new ResultQL(2, "HINT ERROR");
    }
    else
    {
      prod().setHint("");
      return new ResultQL(0);
    }
  }
  
  //RELOBJ -> “my ” | “mygroup ” | “mygroups ” | _
  private ResultQL RELOBJ()
  {
    ResultQL res = new ResultQL();
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(RELOBJ_MY) || s.equalsIgnoreCase(RELOBJ_MYGROUP) || s.equalsIgnoreCase(RELOBJ_MYGROUPS))
    {   
        prod().prodRELOBJ(s);   //se houver condições deste tipo informar o produtor
        incConsumer();
        return new ResultQL(1);
    }
    else
        return new ResultQL(0);   
  }
  
  //USING -> “using ” userid | _
  private ResultQL USING()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(USING_USING))
    {   
        if(incConsumer())
            return new ResultQL(2, USING_ERR1);//"UserID not specified"
        if(lookahead(1).equalsIgnoreCase("/") && isUser(lookCurrentWord(), lookahead(2)))
        {   
            incConsumer();incConsumer();incConsumer();
            if(substUserId())
              return new ResultQL(1);
            else
              return new ResultQL(2, "Parameters Error");
        }
        else
        {
          if(substUserId())
            return new ResultQL(2, lookCurrentWord()+USING_ERR2);//" is not a recognized user"
          else
            return new ResultQL(2, "Parameters Error");
          
        }
            
    }
    else
    {
      if(substUserId())
        return new ResultQL(0);
      else
        return new ResultQL(2, "Parameters Error");
    }
         
  }
  
  //SOBJ -> objname | objname “.” SATT
  private ResultQL SOBJ()
  {
    if(endOfQuery())
        return new ResultQL(2, SEL_ERR1);
    String s = lookCurrentWord();
    if(isObj(s)) //s corresponde a objname
    {   
        textObj = s;                            //este será sempre o objecto a que os atributos se referem
        boReferenced = boDefHandler.getBoDefinition(textObj);   //guardar o boDefHandler associado
        changeActualObj(s);                     //guardar como objecto actual
        pos = prod().setRootFrom(actualObj);          //defenir como raiz na árvore de joins aplicada no FROM
        if(incConsumer())
            return new ResultQL(1, s);
        String s1 = lookCurrentWord();
        if(s1.equalsIgnoreCase("."))            //caso em q queremos escolher argumentos dentro do objecto (objname “.” SATT)
        {
            incConsumer();
            ResultQL r = SATT(); //SATT fará esse trabalho recursivamente
            if(r.failed())
                return r;
            else
            {
                ResultQL re = EXT();
                if (re.failed())
                    return re;
                else
                    return r;
            }
            
        }
        else         //caso em que queremos mesmo seleccionar o objecto "principal"
        {   
            ResultQL re = EXT();
            if(re.failed())
                return re;
            else
                return new ResultQL(1, s);
        }
    }
    //Possibilitar DISTINCT deverá ser feito de outra forma o XEOQL deverá ignorar em qualquer passo 
    //da query qualquer palavra que se encontre entre [] e continuar para a frente
    else if (s.equals("DISTINCT"))
    {
      this.incConsumer();
      ResultQL re=SOBJ();
      re.setMessage(s+" "+re.getMessage());
      return re;
    }
    //FIM
    else
        return new ResultQL(2, SEL_ERR2);//"Object name is incorrect or doesn't exist"
      
  }
  
  private ResultQL EXT()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(EXT_EXT) || s.equalsIgnoreCase(EXT_EXTEN))
    {
//        prod().setExt(true);
//        Vector subC = boReferenced.getBoAllSubClasses();
//        if(subC.size()<1)
//            return new ResultQL(2, EXT_ERR1);//"Class doesn't have any extended sub-calsses"
        incConsumer();
        prod().setExt(true);
        return new ResultQL(1);
    }
    else
        return new ResultQL(0);
      
  }
  
  //SATT -> attname | attname “.” SATT | attname “.bridge.” SATT 
  private ResultQL SATT()
  {
    if(endOfQuery())
        return new ResultQL(2,ATRIB_ERR1);//"Not a valid attribute."
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase("["))
    {
      if(incConsumer())
        return new ResultQL(2, ATVAL_ERR1);
      String msg = lookCurrentWord();
      
      if(!incConsumer() && lookCurrentWord().equalsIgnoreCase("]"))
      {
        incConsumer();
        return new ResultQL(1, msg);
      }
      else
        return new ResultQL(2, ATVAL_ERR1);
    }
    else
    if(s.equalsIgnoreCase("{{"))
    {
      if(incConsumer())
        return new ResultQL(2, ATVAL_ERR1);
      startXep();
      String code = lookCurrentWord();
      if(xep_eval == null) xep_eval = new Xep();
      xep_eval.addCode(code+";");
      String msg = null;
      try{
        msg = xep_eval.evalToString(ctx);
      }catch(boRuntimeException e){ }
      if(msg != null && !incConsumer() && lookCurrentWord().equalsIgnoreCase("}}"))
      {
        incConsumer();
        return new ResultQL(1, msg);
      }
      else
        return new ResultQL(2, ATVAL_ERR1);
    }
    else
    {
      String sa = isAttribute(s, inSel);
      if(sa!=null)//se sa é um atributo
      {
          if(incConsumer())//se acabou a query
          {
  //            String objt = objTypeOfAtt(actualObj, s, new Boolean(true));
  //            if(objt==null && this.ext_obj)
  //                return new ResultQL(2, ATT_ERR1);//"Final attribute can not be extended"
  //            if(objt!=null)
  //              changeActualObj(objt);   
                                                      //introduz na arvore tabela do atributo caso necessário e 
                                                                                //muda o objecto actual para o objecto do atributo
              return new ResultQL(1, realObj+"."+sa);         //devolve a tabela do objecto com o nome do objecto
          }
          else
          {
              String s2 = lookCurrentWord();
              if(s2.equalsIgnoreCase("."))//se houverem mais subatributos
              {
                  incConsumer();
                  ResultQL r = SATT();
                  return r;
              }
              else
              {
         //         String objt = objTypeOfAtt(actualObj, s, new Boolean(true));
         //         if(objt==null)
         //             return new ResultQL(2, ATT_ERR1);//"Final attribute can not be extended"
         //         if(objt!=null)
         //           changeActualObj(objt); 
                                                                                  //introduz na arvore tabela do atributo caso necessário e 
                                                                                //muda o objecto actual para o objecto do atributo
                  return new ResultQL(1, realObj+"."+sa);                     //devolve a tabela do objecto com o nome do objecto
              }
          }
      }
      else
          return new ResultQL(2,ATRIB_ERR1);//"Not a valid attribute."
    }
  }
  
  private ResultQL OBJAT()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(",")) 
    {
      firstSel = false;
      lastObj = lastAtt = null;
      changeActualObj(textObj);
      incConsumer();
      ResultQL r = SATT();
      if(r.failed())
        return r;
      else
      {
        ResultQL res = ALLI(r.getMessage());
        if(res.failed())
          return res;
        else
          if(res.success())
            prod().addSelectRule(", "+r.getMessage()+" "+res.getMessage());
          else
            prod().addSelectRule(", "+r.getMessage());
        return OBJAT();
      }
    }
    else
      return new ResultQL(0);
  }

   private ResultQL FROM()
   {
      if(endOfQuery())
        return new ResultQL(0);
      String s = lookCurrentWord();
      ResultQL res = SATT();
      if(res.failed())
        return res;
      else
      {
        ResultQL res_alli = ALLI(res.getMessage());
        if(res_alli.failed())
          return res_alli;
        else
        {
           if(res_alli.success())
            prod().addSelectRule(res.getMessage()+" "+res_alli.getMessage());
          else
            prod().addSelectRule(res.getMessage());
            
          return OBJAT();
        }
      }
   }
  
   private ResultQL ALLI(String realExp)
   {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(FROM_FROM) || s.equalsIgnoreCase(","))
      return new ResultQL(0);
    if(ht_allias.containsKey(s))
      return new ResultQL(2, ALLI_ERR1);
    else
    {
      Triple to;
      if(lastObj == null &&  lastAtt == null)
        to = null;
      else
        to = new Triple(lastObj, lastAtt, null);
      ht_allias.put(s, new Triple(realExp, actualObj, to));
      incConsumer();
      return new ResultQL(1, prod().makeAlias(s));
    }
    //return new ResultQL(0);
   }
  //STATE -> “state ” SUBST OPST
  private ResultQL STATE()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(STATE_STATE))
    {   changeActualObj(textObj);
        if(incConsumer())
            return new ResultQL(2, STATE_ERR1);//"State not defined"
        else
        {
            String s_pri = lookCurrentWord();
            ResultQL r = SUBST();
            if (r.failed())
                return r;
            else    //em caso de sucesso devolve o condição que traduz o estado
            {
                ResultQL r2 = OPST();   //possibilidade de juntar condições de estado com and e or 
                if (r2.failed())
                    return r2;
                else
                    if(r2.success())    //caso tenham existido mais condições de estados informamaos o produtor dos dois resultados
                    {  
                        prod().prodSTATE( r.getMessage() + r2.getMessage());
                        return new ResultQL(1);
                    }
                    else    //caso n existam mais condições de estados informamos o produtor apenas do primeiro resultado
                    {
                        prod().prodSTATE( r.getMessage());
                        return new ResultQL(1, r.getMessage());
                    }
            }
        }
    }
    else
        return new ResultQL(0);
  }
  
  //SUBST -> finalstate | parentstate“.”SUBST
  private ResultQL SUBST()
  {
    if(endOfQuery())
        return new ResultQL(2, STATE_ERR2);
    String s_pri = lookCurrentWord();
    String priRes = isPrimaryState(s_pri);  //é necessário começarmos por um estado primário
    if(priRes!=null)
    {
        
        if(!incConsumer() && lookCurrentWord().equals("."))     //se forem especificados sub-estados
            if(!incConsumer())
            {
                String s_sec = lookCurrentWord();
                String secRes = isState(s_pri, s_sec);      //veirificar se é sub-estado
                if(secRes!=null)
                {   
                    incConsumer();
                    return new ResultQL(1, priRes + " AND " + secRes);  //se for juntar as dusa condições com um AND
                }
                else
                    return new ResultQL(2, STATE_ERR2);//"Not a valid state for this object"
            }
            else
                return new ResultQL(2, STATE_ERR3);//"Incorrect state sintax (pristate.secstate)"
        else
            return new ResultQL(1, priRes);     //caso em que não foi pedido subestado.
    }
    else
        return new ResultQL(2, STATE_ERR2);//"Not a valid state for this object"
  }
  
  //OPST -> “ and ” SUBST OPST | “ or ” SUBST OPST | _
  private ResultQL OPST()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(OPST_AND) || s.equalsIgnoreCase(OPST_OR)) //estamos a analisar um AND ou OR
    {
        changeActualObj(textObj);   //antes de analisar uma nova expressão voltamos a mudar o nosso actualObj para o Obj principal, aquele que seleccionamos

        if(incConsumer())
            return new ResultQL(2, STATE_ERR1);//"State not defined"
        else
        {

            ResultQL rs = SUBST();  //verificar estados
            if (rs.failed())
                return rs;
            else
            {
                ResultQL r = OPST();    //podem voltar a existir novas condições de estados
                if (r.failed())
                    return r;
                else
                    if(r.success())
                    {  //juntar operador, estado e novas condições
                        return new ResultQL(1, " "+s+" "+rs.getMessage() + r.getMessage()); 
                    }
                    else
                    {   //se não houverem novas condições juntar só o operador e a condição de estado
                        return new ResultQL(1, " " +s+" "+rs.getMessage());
                    }
            }
             
        }
    }
    else
        return new ResultQL(0);
  }
  
  //WHERE -> “where ” EXPRE | _
  private ResultQL WHERE()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(WHERE_WHERE))
    {   changeActualObj(textObj);
        if(incConsumer())
            return new ResultQL(2,WHERE_ERR1);//"No where expression defined"
        else
        {
            ResultQL r = EXPRE();
            if (r.failed())
                return r;
            else
            {
                
//                prod().prodWHERE(); //informar produtor do final da clausula where
                return new ResultQL(1);
        
            }
        }
    }
    else
        return new ResultQL(0);
  }
  
  //EXPRE -> OPWH EXPx
  //A partir deste ponto a gramática foi facturizada à esquerda para que o reconhecimento seja mais fácil; 
  //O comportamento de entar em profundidade nas funções serve para assegurar um certo contexto do erro
  private ResultQL EXPRE()
  {
    if(endOfQuery())
        return new ResultQL(0);
    
    ResultQL r = OPWH();    //uma expressão WHERE (EXPRE) é definida por uma expressão lógicas (OPWH)...
    if (r.failed())
        return r;
    else
    {
        if(r.getMessage()!=null)
            prod().addWhereRule(r.getMessage());
        if(endOfQuery())
            return new ResultQL(1);   
        ResultQL r2 = EXPx();   //...que podem estar ligadas com operadores lógicos
        if (r2.failed())
            return r2;
        else
        {
            return new ResultQL(1); //não é necessário retornar nenhuma mensagem para além de assinalar o suceeso pois é tudo realizado a níveis inferiores
        }
    }
  }
    
  //EXPx -> “and” OPWH EXPx | “or” OPWH EXPx | _
  //qunado existem várias expressoes lógicas
  private ResultQL EXPx()
  {
    typeStr = true;
    typeNum = true;
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    while(s.equalsIgnoreCase(")"))  //A questão dos parênteses é uma questão complexa!
    {    //se virmos um ) antes de um operador lógico temos duas hipótes: ou é de uma expressão lógica ou é de uma sub-query
        if(lastPar())   //caso seja de uma expressão lógica...
        {
            if(numPar <= 0) //...e não foi detectado á priori o respectivo "("
                return new ResultQL(2, OPST_ERR1);//"Missing left parentheses"
            else        //...e foi detectado á priori o respectivo "("
            {
                numPar--;   //descontar um dos ) que já vimos
                if (!contRule)prod().addWhereRule(")");   //como foi adicionada uma regra do género: "( regra"; agora adicionamos: ")"
                else contRule=false;
                if(incConsumer())
                    return new ResultQL(0);
                s = lookCurrentWord();            //deixar s como a próxima palavra a analisar
            }
        }
        else
            if(subQueries > 0)  //caso seja o fim de uma sub-query
            {
//                subQueries--;   //reduzir o contador de sub-queries
                return new ResultQL(0);
            }
            else
                return new ResultQL(2, OPST_ERR1);//"Missing left parentheses"
    
    }
    if(s.equalsIgnoreCase(OPST_AND) || s.equalsIgnoreCase(OPST_OR))//se detectarmos um operador lógico...
    {
        relOp = s;
        lastObj = lastAtt = null;
        if(s.equalsIgnoreCase(OPST_OR))
          prod().foundOr();
        if(incConsumer())
            return new ResultQL(2, EXPRE_ERR2);
        ResultQL r = OPWH();    //...esperamos que se siga uma expressão lógica
        if (r.failed())
            return r;
        else
        {                       //após uma expressao lógica podem de novo aparecer ")"
            
            String s2 = lookCurrentWord();
            if(s2!=null && s2.equalsIgnoreCase(")"))    //procedimento semelhante ao acima descrito 
            {
                if(lastPar())
                {
                    if(numPar <= 0)
                        return new ResultQL(2, OPST_ERR1);//"Missing left parentheses"
                    else
                    {
                        numPar--;
                        if (!contRule) prod().addWhereRule(s+" "+r.getMessage()+")");  //a regra leva agora o operador o resultado da msg e ")"
                        else 
                        {
                          prod().addWhereRule(s+" "+r.getMessage());
                          contRule=false;
                        }
                        if(incConsumer())
                            return new ResultQL(0);
                        s = lookCurrentWord();            
                    }
                }
                else
                    if(subQueries > 0)
                    {
 //                       subQueries--;
                        prod().addWhereRule(s+" "+r.getMessage());
                        return new ResultQL(0);
                    }
            }
            else    //se não houverem ")" e só adicionar a condição
                prod().addWhereRule(s+" "+r.getMessage());
            
            if(endOfQuery())
                return new ResultQL(1, r.getMessage());
            ResultQL r2 = EXPx();       //podem existir mais expressões lógicas ligadas com operadores lógicos ou mais ")"
            if (r2.failed())
                return r2;
            else
                return new ResultQL(1, r2.getMessage());
        }
    }
    else
        return new ResultQL(0);
  }
  
  private static ArrayList getAllClassNames( boDefHandler bodef )
  {
        ArrayList classes = new ArrayList();
        
        if( bodef.getClassType() == boDefHandler.TYPE_INTERFACE )
        {
            boDefInterface bodefInterface = (boDefInterface)bodef;
            String classNames[] = bodefInterface.getImplObjects();
            for (int i = 0; i < classNames.length; i++) 
            {
                boDefHandler   cDef         = boDefHandler.getBoDefinition( classNames[i] );
                if( !classes.contains( cDef.getName() ) )
                {
                    classes.add( cDef.getName() );
                }
                boDefHandler[] childClasses = cDef.getTreeSubClasses();
                for (int j=0;j < childClasses.length; j++ ) 
                {
                    if( !classes.contains( childClasses[j].getName() ) )
                    {
                        classes.add( childClasses[j].getName() );
                    }
                }
            }
        }
        else
        {
            boDefHandler childClasses[] =  bodef.getTreeSubClasses();
            classes.add( bodef.getName() );
            for (int j=0;j < childClasses.length; j++ ) 
            {
                if( !classes.contains( childClasses[j].getName() ) )
                {
                    classes.add( childClasses[j].getName() );
                }
            }
        }
        return classes;
  }
  
  public static String textIndexUiClass(String bodefName)
  {
    StringBuffer sb = new StringBuffer();
    boDefHandler boDef = boDefHandler.getBoDefinition(bodefName);
    ArrayList cNames = getAllClassNames(boDef);
    for (int i = 0; i < cNames.size(); i++)
    {
        if(sb.length() == 0) sb.append("(");
        else sb.append(" OR ");

        String database = boConfig.getApplicationConfig().getDataDataSourceClassName();
        if (database.equalsIgnoreCase(OracleDBM.SQLSERVER_IMPL))
        	sb.append("Ebo_TextIndex.uiclass = '").append(cNames.get(i)).append("'"); //SQLServer
        else
        	sb.append("OEbo_TextIndex.uiclass = '").append(cNames.get(i)).append("'");
    }
    if(sb.length() > 0) sb.append(")");
    return sb.toString(); 
  }
  
  //OPWH -> CALC OPWx
  //representa uma expressão lógica
  private ResultQL OPWH()
  {
     if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(OPWH_CONTAIN) || (s.equalsIgnoreCase("NOT") && lookahead(1)!=null && lookahead(1).equalsIgnoreCase(OPWH_CONTAIN)))
    {
      ResultQL rnot = NOT();
      String snot="";
      if(rnot.success())
        snot = rnot.getMessage()+" ";
      if(incConsumer())
        return new ResultQL(2, CONT_ERR1);
      ResultQL r = CALC(true, false);
      if (r.failed())
          return r;
      else
      {
          String res;
          String database = boConfig.getApplicationConfig().getDataDataSourceClassName();
          //if (database.equalsIgnoreCase(OracleDBM.SQLSERVER_IMPL))
        	  prod().setRootFrom("Ebo_TextIndex");
          //else
         //	  prod().setRootFrom("OEbo_TextIndex");
          res = textIndexUiClass(prod().selObj.getName()) + " AND ";
          if(actualObj.equalsIgnoreCase(textObj))
          {
            if(prod().getExt())
            {
     //         res = snot+boDefHandler.getBoDefinition(textObj).getBoExtendedTable() + ".BOUI"+
     //         " IN (SELECT UI$ FROM EBO_TEXTINDEX WHERE CONTAINS(TEXT,'"+lookCurrentWord()+"')>0 and rownum < 200)";
    //          res = "EXISTS (SELECT UI$ FROM EBO_TEXTINDEX WHERE UI$="+ snot+boDefHandler.getBoDefinition(textObj).getBoExtendedTable() + ".BOUI "+
    //          "AND CONTAINS(TEXT,'"+lookCurrentWord()+"')>0 and rownum < 200)";
              res += "("+snot+prod().makeContains(r.getMessage())+" )";
            }
            else
            {
     //         res = snot+boDefHandler.getBoDefinition(textObj).getBoMasterTable()+ ".BOUI"+
     //         " IN (SELECT UI$ FROM EBO_TEXTINDEX WHERE CONTAINS(TEXT,'"+lookCurrentWord()+"')>0 and rownum < 200)";
    //          res = "EXISTS (SELECT UI$ FROM EBO_TEXTINDEX WHERE UI$="+ snot+boDefHandler.getBoDefinition(textObj).getBoMasterTable() + ".BOUI "+
    //          "AND CONTAINS(TEXT,'"+lookCurrentWord()+"')>0 and rownum < 200)";
              res += "("+snot+prod().makeContains(r.getMessage())+" )";

            }
          }
          else
          {
     //       res = snot+boDefHandler.getBoDefinition(actualObj).getBoMasterTable()+ ".BOUI"+
     //       " IN (SELECT UI$ FROM EBO_TEXTINDEX WHERE CONTAINS(TEXT,'"+lookCurrentWord()+"')>0 and rownum < 200)";
     //       res = "EXISTS (SELECT UI$ FROM EBO_TEXTINDEX WHERE UI$="+ snot+boDefHandler.getBoDefinition(actualObj).getBoMasterTable() + ".BOUI "+
     //         "AND CONTAINS(TEXT,'"+lookCurrentWord()+"')>0 and rownum < 200)";
            res += "("+snot+prod().makeContains(r.getMessage())+" )";

          }
          return new ResultQL(1, res);
        
      }
    }
    else
    {
      ResultQL r = CALC(true, true);  //uma expressão lógica começa por uma expressão aritmética, os argumentos servirão para futuro TypeChecking 
      if (r.failed())
          return r;
      else
      {
      //TNY COMENTADO Nao existiu implicações
          //if(endOfQuery())
         //     return new ResultQL(2, EXPRE_ERR1); //não pode terminar sem um operador relacional
          ResultQL r2 = OPWHx();              //confere a existência do operador relacional e continuação de expressões aritméticas
          if (r2.failed())
              return r2;
          else
          {
              if(r2.getCode() >= 3)
              {
                String att = r.getMessage();
                int pos = att.lastIndexOf('(');
                String paratt = "";
                if(pos >= 0 && pos < att.length())
                {
                  paratt = att.substring(0,pos+1);
                  att = att.substring(pos+1);
                }
                
                return new ResultQL(1, paratt + prod().makeStartsWith(att, r2.getMessage(), r2.getCode()==4));
              }
              else
                if(r.getMessage() != null && r2.getMessage() != null)
                    return new ResultQL(1, r.getMessage()+r2.getMessage()); //se existirem operador aritmético devovler o conteúdo das duas mensagens
                else
                    return new ResultQL(1, r.getMessage());//caso não existam operadores artiméticos, devolver apenas o valor
          }
      }
    }
      
  }
  
  //OPWx -> “=” CALC | “<” CALC | “>” CALC | “!=” CALC | “<=” CALC | “>=” CALC | “<>” CALC | NOT “like ‘string’ ” | NOT “in” “(” S “)” | NOT “is null”
  //representa os operadores relacionais que comparam duas expressões
  private ResultQL OPWHx()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    //operadores relacionais
    if(s.equalsIgnoreCase(EXPRE_EQUAL) || s.equalsIgnoreCase(EXPRE_LESS) || s.equalsIgnoreCase(EXPRE_GREAT) || 
       s.equalsIgnoreCase(EXPRE_LE) || s.equalsIgnoreCase(EXPRE_GE) || s.equalsIgnoreCase(EXPRE_DIFF)|| s.equalsIgnoreCase(EXPRE_DIFF2))
    {   
        changeActualObj(textObj);
        if(incConsumer())//se a query terminar depois de um operador relacional falha
            return new ResultQL(2, EXPRE_ERR2);//"Incomplete Expression"
        ResultQL r = CALC(true, true);  //detectar uma expressão aritmética
        if (r.failed())
            return r;
        else
        {
            return new ResultQL(1, s+r.getMessage());
        }
    
    }
    else //outros operadores como like, in, is null
    {
        if(s.equalsIgnoreCase(OPWx_IS))//como o operador IS pode ter o NOT entre si e a palavra NULL tem de ser tratado diferentemente
        {
            if(incConsumer())
                return new ResultQL(2, MessageLocalizer.getMessage("EXPRE_ERR1"));    
            ResultQL r0 = NOT();//detectar a existência do modificador NOT
            String snot="";
            if(r0.success())
                snot+=" "+r0.getMessage();
            String sh = lookCurrentWord();
            if(sh.equalsIgnoreCase(OPWx_NULL))
            {
                incConsumer();
                prod().foundOr();
                return new ResultQL(1, " "+s+" "+snot+" "+sh);
            }
            else
                return new ResultQL(2, OPWX_ERR2);//"Operator IS without null" 
            

        }
        else
        {      
            ResultQL r0 = NOT();    //detectar a existência do modificador NOT
            String snot="";
            if(r0.success())
                snot+=" "+r0.getMessage();
            if(endOfQuery())
                return new ResultQL(2, EXPRE_ERR2);//"Incomplete Expression"
            s = lookCurrentWord();    
            if(s.equalsIgnoreCase(EXPRE_LIKE))  //operador like receberá do lado direito uma string, por enquanto não está validado 
            {                                   //mas o problema será resolvido a quando da implementação do TypeCheking
                if(incConsumer())
                    return new ResultQL(2, EXPRE_ERR2);//"Incomplete Expression"
                ResultQL r = CALC(true, false);
                if (r.failed())
                    return r;
                else
                    return new ResultQL(1, snot+" LIKE "+prod().makeWildcardString(r.getMessage()));
            }
            else
                if(s.equalsIgnoreCase(OPWx_IN))//operador IN recebe uma sub-query no parametro direito
                {                               //tratamento de subqueries é delicado
                    if(incConsumer())
                        return new ResultQL(2, EXPRE_ERR2);//"Incomplete Expression"
                    String s1 = lookCurrentWord();
                    if("(".equalsIgnoreCase(s1))
                    {
                        if(incConsumer())
                            return new ResultQL(2, EXPRE_ERR2);//"Incomplete Expression"
                        boolean sub_querie = false;
                        ResultQL r = FATT();
                        if(r.failed())
                        {
                          addSubQ();  //incrementar o contador de parênteses contabilizando para as sub-queries
                          String a = actualObj;   //guardar o estado dos objectos antes da chamada da função S
                          String t = textObj;
                          r = S(false);       //começar a analisar uma nova query
                          actualObj = a;          //repor o estado antes da chamada da sub-query
                          textObj = t;
                          sub_querie = true;
                          if(r.failed())
                            return r;
                        }
                        s1 = lookCurrentWord();
                        if(")".equalsIgnoreCase(s1))    //esperamos que no fim da querie sejam fechados os parênteses
                        {
                         
                            incConsumer();  //avançar para a proxima palavra
                            if(sub_querie)
                                subQueries--;
                            return new ResultQL(1, snot+" "+s+" ("+r.getMessage()+")"); //devovolver NOT IN (SQL)
                        }
                        else
                            return new ResultQL(2, OPWx_ERR1);
                    }
                    else
                        return new ResultQL(2, EXPRE_ERR2);//"Incomplete Expression"
                    
                }
                else 
                  if(s.equalsIgnoreCase(OPWx_START))
                  {
                    if(incConsumer())
                      return new ResultQL(2, STARTS_ERR1);//Incorrect STARTSWITH statement (ex: arg STARTSWITH 'a')
                    String s2 = lookCurrentWord();
                    if("'".equalsIgnoreCase(s2))
                    {
                      if(incConsumer())
                        return new ResultQL(2, STARTS_ERR1);
                      else
                      { 
                        if(s2.trim().length() == 1 && lookahead(1)!=null && lookahead(1).equalsIgnoreCase("'"))
                        {
                          s2 = lookCurrentWord();
                          incConsumer();incConsumer();
                          if(snot!="")
                            return new ResultQL(4, s2);
                          else
                            return new ResultQL(3, s2);
                        }
                        else
                          return new ResultQL(2, STARTS_ERR1);
                      }
                    }
                    else
                       return new ResultQL(2, STARTS_ERR1);
                  }
                  //Tony
                  //Caso de Excepção para contains entre ()
                  else if (")".equals(s)  && lookahead(-4).equalsIgnoreCase(OPWH_CONTAIN))
                  {
                    incConsumer();
                    return new ResultQL(0);
                  }
                  else if ((s.equalsIgnoreCase(OPST_AND) ||s.equalsIgnoreCase(OPST_OR)) && lookahead(-4).equalsIgnoreCase(OPWH_CONTAIN))
                  {                    
                    addPar();
                    contRule=true;
                    //addSubQ();
                    //incConsumer();
                    //incConsumer();
                    return new ResultQL(0);
                  }
                  else
                      return new ResultQL(2, EXPRE_ERR1);
        }
    
                
    }
  }
  
  //NOT -> “not” | _
  //serve para reconhecer a palavra not
  private ResultQL NOT()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(NOT_NOT))
    {
        incConsumer();
        return new ResultQL(1, s);
    }
    else
        return new ResultQL(0);
  }
  
  //CALC -> VAL CALCx | “ ‘ ” string “ ‘ ” | “ ? ”
  //detecta expressões aritméticas, strings e o caracter ? usado para posterior parametrização
  private ResultQL CALC(boolean string, boolean num)
  {
    if(endOfQuery())
        return new ResultQL(0);
    
    String s = lookCurrentWord();
    String out = "";
    
    if( s.equalsIgnoreCase("?") ) {
    	String fnSysdate = s;
    	if( ctx != null && s.equalsIgnoreCase("sysdate") )
    		fnSysdate = ctx.getDataBaseDriver().getDriverUtils().fnSysDateTime();
    	
        incConsumer();
        return new ResultQL(1, fnSysdate);
    }
    
    if ( s.equalsIgnoreCase( "SYSDATE" ) || s.equalsIgnoreCase( "NOW" ) ||  s.equalsIgnoreCase( "datetime" )
    		|| s.equalsIgnoreCase("'microseconds'") || s.equalsIgnoreCase("DATE"))//reconhecer logo o ? ou SYSDATE
    {   
    	String fnSysdate = s;
    	if( ctx != null && s.equalsIgnoreCase("sysdate") )
    		fnSysdate = ctx.getDataBaseDriver().getDriverUtils().fnSysDateTime();
    	
        incConsumer();
        return new ResultQL(1, fnSysdate);
    }
    
    if(s.equalsIgnoreCase("'"))//reconhecer uma string atá á próxima '
    {
        do
        {                   //juntar os tokens aqui, provavelmente não será necessário pois o parser já fez isso,  
            out += lookCurrentWord();    //mas mais vale ter a certeza
            if(incConsumer())
                return new ResultQL(2,RVAL_ERR2);//"Missing ' to terminate the string"
        }
        while(!lookCurrentWord().equalsIgnoreCase("'"));
        incConsumer();
        if(typeStr)
        {
          typeNum=false;
          return new ResultQL(1, out+"'");
        }
        else
        {
            consumer = consumer-2;
            return new ResultQL(2, TYPE_ERR1);//Type missmatch; String not expected
        }
    }
    //TONY
    else if (s.equals("(") && lookahead(1).equalsIgnoreCase(OPWH_CONTAIN))
    {
      incConsumer();
      ResultQL r3=OPWH();
      return r3;
    }
    else
    {
        ResultQL r = VAL(); //descobrir o valor númerico ou atributo
        if (r.failed())
            return r;
        else
        {
            if(endOfQuery())
                return new ResultQL(1, r.getMessage());
            ResultQL r2 = CALCx();  //detectar operadores aritméticos caso existam
            if (r2.failed())
                return r2;
            else
            {   
                if(r.getMessage() != null && r2.getMessage() != null)   //se foram detectados operadores aritméticos
                    return new ResultQL(1, r.getMessage()+r2.getMessage()); 
                else
                    return new ResultQL(1, r.getMessage());
            }
        }
    }
      
  }
  
  //CALCx -> “+” VAL CALCx | “*” VAL CALCx | “-“ VAL CALCx | “/” VAL CALCx | _
  //depois de identicado um VAL há que reconhecer possíveis operadores aritméticos seguidos de novo VAL
  private ResultQL CALCx()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(CALC_PLUS) || s.equalsIgnoreCase(CALC_MINUS) || s.equalsIgnoreCase(CALC_PROD) || s.equalsIgnoreCase(CALC_DIV))
    {   
        changeActualObj(textObj);
        lastObj = lastAtt = null;
        if(incConsumer())
            return new ResultQL(2, EXPRE_ERR2);//"Incomplete Expression"
        ResultQL r = VAL();     //detectar valores (númericos ou atributos)
        if (r.failed())
            return r;
        else
        {
            if(endOfQuery())
                return new ResultQL(1, s+r.getMessage());
            ResultQL r2 = CALCx();          //detectar outros operadores aritméticos
            if (r2.failed())
                return r2;
            else
                if(r.getMessage() != null && r2.getMessage() != null)   //caso tenham sido achados mais operadores aritméticos devovler a 
                    return new ResultQL(1, s+r.getMessage()+r2.getMessage());//a soma dos dois resultados
                else
                    return new ResultQL(1, s+r.getMessage());

        }
    
    
    }
    return new ResultQL(0); //devovler zero significa que não foi detectado nenhum, á o lambda da gramática
  }
  
  //VAL -> number | “-“number | “(“ CALC “)” | ATTVAL
  //função para detectar valores nu,éricos, expresseos artiméticas entre () e valores dos atributos
  private ResultQL VAL()
  {
    if(endOfQuery())
        return new ResultQL(2,VAL_ERR1);
    
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase("(")) //de novo a questão dos ()! Consideramos quando virmos um ( que está a envolver uma expressão aritmética
    {                           //caso não sejam fechados incrementamos para posterior análise
      /*  if (lookahead(1).equalsIgnoreCase(OPWH_CONTAIN)) 
        {
          incConsumer();
          ResultQL rcont=OPWH();
          return rcont;
        }*/
        if(incConsumer())
            return new ResultQL(2, VAL_ERR2);
        ResultQL r = CALC(true, true);  //a seguir do ( vem uma expressão aritmética
        if (r.failed())
            return r;
        else
        {
            if(endOfQuery())
                return new ResultQL(2, EXPRE_ERR3);//"Incomplete Expression"
            s = lookCurrentWord();
            if(s.equalsIgnoreCase(")")) //se confirmar a nossa suspeita, no fim de uma expressão aritmética são fechados os parênteses
            {
                incConsumer();
                return new ResultQL(1, "("+r.getMessage()+")");
            }
            else
            {
                addPar();           //se não se confirmar, então é porque os parênteses são de uma expressão lógica, adicionamos para assinalar o seu início
                return new ResultQL(1, "("+r.getMessage());//juntamos ás regras "( expressão" para depois ao detectarmos o ) adicionamos apenas  ")"
            }
        }
    }
    else
    {   String n = isNumber();  //verificar se estamos a analisar um número
        if(n!=null)
        {
          if(typeNum)
            return new ResultQL(1, n);
          else
          {
            consumer--;
            return new ResultQL(2, TYPE_ERR2);
          }
        }
        else
        {
            changeActualObj(textObj);
            ResultQL r = ATVAL();   //a última hipóteses é estarmos a analizar a referência a um atributo
            if (r.failed())
            {
              if (!s.equalsIgnoreCase(OPWx_IN) && lookahead(1)!=null && lookahead(1).equalsIgnoreCase("("))
              {
                if(incConsumer())
                    return new ResultQL(2, EXPRE_ERR2);//"Incomplete Expression"
                String s1 = lookCurrentWord();
                if(s1.equalsIgnoreCase("("))
                {
                    if(incConsumer())
                        return new ResultQL(2, EXPRE_ERR2);//"Incomplete Expression"
                    ResultQL r2 = FATT();   //a última hipóteses é estarmos a analizar uma função
                    if (r2.failed())
                      return r2;
                    else
                    {
                      String s2 = lookCurrentWord();
                    //POSTGRES Problem with date_trunc
                      while (s2.equals(":") || s2.equalsIgnoreCase("TIMESTAMP"))
                      {
                    	  r2.setMessage(r2.getMessage()+s2);
                    	  incConsumer();
                    	  s2=lookCurrentWord();                    	  
                      }
                    //END POSTGRES Problem with date_trunc
                      if(s2.equalsIgnoreCase(")"))
                      {
                          incConsumer();
                          return new ResultQL(1, s+"("+r2.getMessage()+")");
                      }
                      else
                        return new ResultQL(2, EXPRE_ERR3);
                    }
                }
                else
                  return new ResultQL(2, FATT_ERR2);//"Unrecognized Function"
              }
              else
                return r;
            }
            else
            { 
                return new ResultQL(1, r.getMessage());
            }
        }
    }
  }
  
  //ATTVAL -> ATRIB | objname “.” ATRIB
  //começar a reconhecer um atributo, principalmente porque esse atributo pode esta explicitamente relacionado com o objecto seleccionado
  private ResultQL ATVAL()
  {
      if(endOfQuery())
        return new ResultQL(2,VAL_ERR1);
    
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase("["))
    {
      if(incConsumer())
        return new ResultQL(2, ATVAL_ERR1);
      String msg = lookCurrentWord();
      
      if(!incConsumer() && lookCurrentWord().equalsIgnoreCase("]"))
      {
        incConsumer();
        return new ResultQL(1, msg);
      }
      else
        return new ResultQL(2, ATVAL_ERR1);
    }
    if(s.equalsIgnoreCase("{{"))
    {
      if(incConsumer())
        return new ResultQL(2, ATVAL_ERR1);
      startXep();
      String code = lookCurrentWord();
      if(xep_eval == null) xep_eval = new Xep();
      xep_eval.addCode(code+";");
      String msg = null;
      try{
        msg = xep_eval.evalToString(ctx);
      }catch(boRuntimeException e){ }
      if(msg != null && !incConsumer() && lookCurrentWord().equalsIgnoreCase("}}"))
      {
        incConsumer();
        return new ResultQL(1, msg);
      }
      else
        return new ResultQL(2, ATVAL_ERR1);
    }
    else
      if(ht_allias.containsKey(s))
      {
        
        Triple t = (Triple)ht_allias.get(s);
        
        if(!incConsumer() && lookCurrentWord().equalsIgnoreCase("."))
        {
          changeActualObj((String)t.getSecond());
          Triple t2 = (Triple)t.getThird();
          if(t2!=null)
          {
            lastObj = (String)t2.getFirst();
            lastAtt = (String)t2.getSecond();
            consumer--;
            isAttribute(lastAtt);
            incConsumer();
          }
          else
            lastObj = lastAtt = null;
          incConsumer();
          //return ATRIB();
        }
        else
          return new ResultQL(1, (String)t.getFirst());
      }
      else
        if(s.equals(textObj)) //comparar se estamos a ter uma referência explicita (select X where X.y = Z)
            if(incConsumer())
                return new ResultQL(2, LVAL_ERR1);//"Attribute required after Object (obj_name.att_name)"
            else
            {
                String s2 = lookCurrentWord();
                if(!s2.equalsIgnoreCase(".") || incConsumer())
                    return new ResultQL(2, LVAL_ERR1);//"Attribute required after Object"
                else
                    changeActualObj(s);
            }
    //caso exista ou não referencia explicita, tem de se seguir um atributo        
    ResultQL r = ATRIB();
    if (r.failed())
        return r;
    else
    {
        return new ResultQL(1, r.getMessage());
    }    
  }
  
  //ATRIB -> attname | attname “.” ATRIB | attname “.bridge.” ATRIB | AGG
  //reconhecer atributos
  private ResultQL ATRIB()
  {
    if(endOfQuery())
        return new ResultQL(2,ATRIB_ERR1);//"Not a valid attribute."
    String s = lookCurrentWord();
    String sa = isAttribute(s);
    if(sa!=null)    //se for um atributo reconhecido do actualObj
    {
        if(incConsumer())
            return new ResultQL(1, realObj+"."+sa);
        else
        {
            String s2 = lookCurrentWord();
            if(s2.equalsIgnoreCase("."))    //ver se segue outro sub-atributo
            {
                if(incConsumer())
                    return new ResultQL(2, ATRIB_ERR2);//"Expecting attribute after '.' "
                else
                {   //muda o actualObj para o objecto do actributo
                  //  String objt = objTypeOfAtt(actualObj, s, new Boolean(true));
               //     if(objt==null)
               //         return new ResultQL(2, ATT_ERR1);//"Final attribute can not be extended"
              //      changeActualObj(objt);    
                    ResultQL r = ATRIB();   //novo atributo
                    if (r.failed())
                        return r;
                    else
                        return new ResultQL(1,r.getMessage());
                }
            }
            else
                return new ResultQL(1, realObj+"."+sa);

        }
    }
    else
        if(s.equalsIgnoreCase(ATRIB_BRIDGE) && lookahead(-1).equalsIgnoreCase(".")) 
        {   //caso de expressar claramente que queremos o atributo da bridge
            if(incConsumer())
                return new ResultQL(2,ATRIB_ERR2);
            String s2 = lookCurrentWord();
            if(s2.equalsIgnoreCase("."))
            {
                if(incConsumer())
                    return new ResultQL(2,ATRIB_ERR3);
                ResultQL r = ATRIB();   //novo atributo
                if (r.failed())
                    return r;
                else
                    return new ResultQL(1, r.getMessage());
            }
            else
            {

                return new ResultQL(2, ATRIB_ERR2);//"You must define a bridge atribute (bridge_name.bridge.bridge_attribute)"
            }
        }
        else
        {
            ResultQL r2 = AGG();    //funções de agregação (count, sum, etc.)
            if(r2.success())
            {   //se for reconhecida devemos adicionar esta regra ao grupo das regas de group
                //prod().addGroupRule(boDefHandler.getBoDefinition(lastObj).getBoMasterTable()+".BOUI", true);
                prod().addGroupRule(realObj+".BOUI", true);
                return r2;
            }            
            else
              return new ResultQL(2, ATRIB_ERR1);
        }
  }
  
  //AGG -> “Sum(“ ATRIB “)” | “Avg(“ ATRIB “)” | “Count(“ ATRIB “)” | “Min(“ ATRIB “)” | “Max(“ ATRIB “)”
  //detecta funções de agragação
  private ResultQL AGG()
  {
    if(endOfQuery())
        return new ResultQL(2,ATRIB_ERR1);//"Not a valid attribute."
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(AGG_SUM) || s.equalsIgnoreCase(AGG_AVG) || s.equalsIgnoreCase(AGG_COUNT) 
            || s.equalsIgnoreCase(AGG_MIN) || s.equalsIgnoreCase(AGG_MAX))
    {
        if(incConsumer())
            return new ResultQL(2, AGG_ERR1);//"Aggregate function must have an atribute."
        String s1=lookCurrentWord();
        if(s1.equalsIgnoreCase("("))    //todas começam e acabam com parênteses, este não é complicado de tratar :)
        {
            if(incConsumer())
                return new ResultQL(2, AGG_ERR1);//"Aggregate function must have an atribute."
            s1=lookCurrentWord();
            String sa =isAttribute(s1);
            if(sa!=null)    //dentro da expressão temos de ter um atributo do objecto actual
            {
                if(incConsumer())
                    return new ResultQL(2, AGG_ERR2);
                String s2=lookCurrentWord();
                if(s2.equalsIgnoreCase(")"))    //temos de ter os ) a fechar
                {
                    incConsumer();
                    return new ResultQL(1, s+"("+realObj+"."+sa+")");    //devolvemos "Fx(att)"
                }
                else
                    return new ResultQL(2, ATRIB_ERR1);//"
            }
            else
                return new ResultQL(2, ATRIB_ERR1);//"
        }
        else
            return new ResultQL(2, AGG_ERR2);//"Aggregate function needs an atribute between parentheses."
    }
    else
        return new ResultQL(2,  ATRIB_ERR1);
      
  }
  
  private ResultQL FATT()
  {
      typeStr = true;
      typeNum = true;
//      if(incConsumer())
//        return new ResultQL(2, FATT_ERR1);//"Incomplete Function"
      ResultQL r = CALC(true, true); //descobrir o valor númerico ou atributo
      if (r.failed())
          return r;
      else
      {
        ResultQL r2 = FATTx(); //descobrir o valor númerico ou atributo
        if (r2.failed())
            return r2;
        else
          if(r2.success())
            return new ResultQL(1, r.getMessage()+r2.getMessage());
          else
            return new ResultQL(1, r.getMessage());
      }
  }
  
  private ResultQL FATTx()
  {
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(","))
    {
      if(incConsumer())
        return new ResultQL(2, FATTx_ERR1);//"Missing argument in function"
      ResultQL r = CALC(true, true); //descobrir o valor númerico ou atributo
      if (r.failed())
          return r;
      else
      {
        ResultQL r2 = FATTx(); //descobrir o valor númerico ou atributo
        if (r2.failed())
            return r2;
        else     
          if(r2.success())
            return new ResultQL(1, ","+r.getMessage()+r2.getMessage());
          else
            return new ResultQL(1, ","+r.getMessage());
      }
    }
    else
      return new ResultQL(0);
  }
  
  private ResultQL GROUP()
  {
    changeActualObj(textObj);
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(GROUP_GROUP))    //detectar a palavra ORDER
        if(consumer+1 >= words.size())
            return new ResultQL(2, ORDER_ERR1); //não pode acabar a query
        else
            if(lookahead(1).equalsIgnoreCase(GROUP_BY)) //detectar a plavra BY sem incrementar o counsumer para poder ser detectado
            {
                changeActualObj(textObj);   //voltamos a repor o actualObj como o objecto seleccionado
                lastObj = lastAtt = null;
                incConsumer();  //como há pouco não incrementámos, agora temos de o fazer duas vezes
                if(incConsumer())
                    return new ResultQL(2, ORDER_ERR1); //não pode acabar
                //na verdade apartir daqui é muito semlhante ao ATTVAL, mas sem a restrição de a referência explicita ao objecto
                //não ter de ser igual ao objecto seleccionado
                if(prod().existsGroupBy())
                  return new ResultQL(2, GROUP_ERR1);
                String s2 = lookCurrentWord();
                boDefHandler bodef = boDefHandler.getBoDefinition(textObj);
                if(bodef!=null && !bodef.hasAttribute(s2) && isObj(s2))   //cá está! basta que seja um objecto...
                {
                    
                    if(!prod().existsInFromTree(realObj))//...mas que já tenha sido referenciado
                        return new ResultQL(2, ORDER_ERR2);
                    if(incConsumer())
                        return new ResultQL(2, LVAL_ERR1);//"Attribute required after Object (obj_name.att_name)"
                    else
                    {
                        String s3 = lookCurrentWord();
                        if(!s3.equalsIgnoreCase(".") || incConsumer())
                            return new ResultQL(2, LVAL_ERR1);//"Attribute required after Object"
                        else
                            changeActualObj(s2);
                    }
                }
                //quer haja referencia explicita a algum objecto quer não, segue-se um atributo        
                ResultQL r = ATVAL();
                if (r.failed())
                    return r;
                else
                {
                    prod().addGroupRule(r.getMessage(), false);
                    ResultQL r2 = GRPx();  //para o caso de haver mais do que um critério de ordenação
                    if (r2.failed())
                        return r2;
                    else
                    {
                        return new ResultQL(1);
                    }
                        
                }
                
            }
    return new ResultQL(0); 
  }
  
  //ORDEx -> “,” ATTVAL DIR ORDEx | _
  //irá detectar os vários critérios de ordenação recursivamente
  private ResultQL GRPx()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(",")) //detectar a , que separa os vários critérios
    {
        changeActualObj(textObj);   //voltar a definir o objecto actual como o objecto selccionado 
        lastObj = lastAtt = null;
        if(incConsumer())
            return new ResultQL(2, ORDER_ERR1);
        String s2 = lookCurrentWord();
        //procedimento semelhante ao ORDER mas agora pode ser recursivo. 
        boDefHandler bodef = boDefHandler.getBoDefinition(textObj);
        if(bodef!=null && !bodef.hasAttribute(s2) && isObj(s2))   //cá está! basta que seja um objecto...
        {
            if(!prod().existsInFromTree(realObj))
                        return new ResultQL(2, ORDER_ERR2);
            if(incConsumer())
                return new ResultQL(2, LVAL_ERR1);//"Attribute required after Object (obj_name.att_name)"
            else
            {
                String s3 = lookCurrentWord();
                if(!s3.equalsIgnoreCase(".") || incConsumer())
                    return new ResultQL(2, LVAL_ERR1);//"Attribute required after Object"
                else
                    changeActualObj(s2);
            }
        }
        ResultQL r = ATVAL();
        if (r.failed())
            return r;
        else
        {
            
            prod().addGroupRule(r.getMessage(), false);     
            ResultQL r2 = GRPx();
            if (r2.failed())
                return r2;
            else
            {
                return new ResultQL(1);
            }
        }
        
        
    }
    else
        return new ResultQL(0);
  }
  
  //ORDER -> “order by ” ATTVAL DIR ORDEx | _
  //detecta a clausula order by para fazer uma ordenação aos dados
  private ResultQL ORDER()
  {
    changeActualObj(textObj);
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(ORDER_OREDER))    //detectar a palavra ORDER
        if(consumer+1 >= words.size())
            return new ResultQL(2, ORDER_ERR1); //não pode acabar a query
        else
            if(lookahead(1).equalsIgnoreCase(ORDER_BY)) //detectar a plavra BY sem incrementar o counsumer para poder ser detectado
            {
                changeActualObj(textObj);   //voltamos a repor o actualObj como o objecto seleccionado
                lastObj = lastAtt = null;
                incConsumer();  //como há pouco não incrementámos, agora temos de o fazer duas vezes
                if(incConsumer())
                    return new ResultQL(2, ORDER_ERR1); //não pode acabar
                //na verdade apartir daqui é muito semlhante ao ATTVAL, mas sem a restrição de a referência explicita ao objecto
                //não ter de ser igual ao objecto seleccionado
                String s2 = lookCurrentWord();
                boDefHandler bodef = boDefHandler.getBoDefinition(textObj);
                if(bodef!=null && !bodef.hasAttribute(s2) && isObj(s2))   //cá está! basta que seja um objecto...
                {
                    
                    if(!prod().existsInFromTree(realObj))//...mas que já tenha sido referenciado
                        return new ResultQL(2, ORDER_ERR2);
                    if(incConsumer())
                        return new ResultQL(2, LVAL_ERR1);//"Attribute required after Object (obj_name.att_name)"
                    else
                    {
                        String s3 = lookCurrentWord();
                        if(!s3.equalsIgnoreCase(".") || incConsumer())
                            return new ResultQL(2, LVAL_ERR1);//"Attribute required after Object"
                        else
                            changeActualObj(s2);
                    }
                }
                //quer haja referencia explicita a algum objecto quer não, segue-se um atributo        
                ResultQL r = ATVAL();
                if (r.failed())
                    return r;
                else
                {
                    ResultQL r3 = DIREC();  //pode estar especificada a direcção da ordenação
                    if (r3.success())
                        prod().addOrderRule(r.getMessage()+" "+r3.getMessage(), getCopyGroupToOrder());
                    else
                        prod().addOrderRule(r.getMessage(), getCopyGroupToOrder());
                    ResultQL r2 = ORDEx();  //para o caso de haver mais do que um critério de ordenação
                    if (r2.failed())
                        return r2;
                    else
                    {
                        return new ResultQL(1);
                    }
                        
                }
                
            }
    return new ResultQL(0); 
  }
  
  //ORDEx -> “,” ATTVAL DIR ORDEx | _
  //irá detectar os vários critérios de ordenação recursivamente
  private ResultQL ORDEx()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(",")) //detectar a , que separa os vários critérios
    {
        changeActualObj(textObj);   //voltar a definir o objecto actual como o objecto selccionado
        lastObj = lastAtt = null;
        if(incConsumer())
            return new ResultQL(2, ORDER_ERR1);
        String s2 = lookCurrentWord();
        //procedimento semelhante ao ORDER mas agora pode ser recursivo. 
        boDefHandler bodef = boDefHandler.getBoDefinition(textObj);
        if(bodef!=null && !bodef.hasAttribute(s2) && isObj(s2))     
        {
            if(!prod().existsInFromTree(realObj))
                        return new ResultQL(2, ORDER_ERR2);
            if(incConsumer())
                return new ResultQL(2, LVAL_ERR1);//"Attribute required after Object (obj_name.att_name)"
            else
            {
                String s3 = lookCurrentWord();
                if(!s3.equalsIgnoreCase(".") || incConsumer())
                    return new ResultQL(2, LVAL_ERR1);//"Attribute required after Object"
                else
                    changeActualObj(s2);
            }
        }
        ResultQL r = ATVAL();
        if (r.failed())
            return r;
        else
        {
            ResultQL r3 = DIREC();
            if (r3.success())
                prod().addOrderRule(r.getMessage()+" "+r3.getMessage(), getCopyGroupToOrder());
            else
                prod().addOrderRule(r.getMessage(), getCopyGroupToOrder());     
            ResultQL r2 = ORDEx();
            if (r2.failed())
                return r2;
            else
            {
                return new ResultQL(1);
            }
        }
        
        
    }
    else
        return new ResultQL(0);
  }
  
  private boolean getCopyGroupToOrder() {
    return copyGroupToOrder;
  }
  
   //DIREC -> “ASC” | “DESC” | _
   //detecta apenas a direcção da Ordenação que pode ser ASC ou DESC
   private ResultQL DIREC()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(DIREC_ASC) || s.equalsIgnoreCase(DIREC_DESC))
    {
        incConsumer();
        return new ResultQL(1, s);
    }
    else
        return new ResultQL(0);
    
  }
      
      
  //VIEW -> “viewer ” vwname | _
  //detecta o pdeido de visualizar a informação na forma de um viewer existente no objecto seleccionado
  //esta é apenas a estrutura
  private ResultQL VIEW()
  {
    if(endOfQuery())
        return new ResultQL(0);
    String s = lookCurrentWord();
    if(s.equalsIgnoreCase(VIEW_VIEWER))
    {   changeActualObj(textObj);
        if(incConsumer())
            return new ResultQL(2, VIEW_ERR1);//"Viewer must be defined for the object"
        else
        {
            String s2 = lookCurrentWord();
            if(isViewer())  //ainda não está a funcionar completamente
            {
                incConsumer();
                return new ResultQL(1);
            }
            else
                return new ResultQL(2, VIEW_ERR2);//"Viewer not defined for the selected object"
        }
    }
    
    return new ResultQL(0);
      
  }
  
 
  
  //verifica se existe um objecto com o nome W. Versão retirada da antiga boql 
  private boolean isObj(String W) {
    if ( W.indexOf('.') > 0)
    {   
        String[] xw=W.split("\\.");
        W=xw[0];
        
    }
    boDefHandler boObj = boDefHandler.getBoDefinition(W);
    if (boObj==null) return false;
    else
    {
        realObj = boObj.getBoMasterTable();
        return true;
    }
  }
  
  private String isAttribute(String W) { //por defeito compara se o attributo pretence ao actualObj
    return isAttribute(actualObj, W, false);
  }
  
  private String isAttribute(String W, boolean sel) { //por defeito compara se o attributo pretence ao actualObj
    return isAttribute(actualObj, W, sel);
  }
  
  private String isAttribute(String Obj, String Att, boolean insel) {  //função que permite verificar se um dado attributo pertence a um dado objecto
                                                //deve devolver o nome real do atributo e defiir o realObj que é a tabela na base de dados que o atributo pertence 
    boDefHandler bo_obj = boDefHandler.getBoDefinition(Obj);
    boDefAttribute bo_att=null;
    String res;
    if(bo_obj!=null)    //se o obejcto existir
    {
        bo_att = bo_obj.getAttributeRef(Att);
        if(bo_att!=null)    //se o atributo pertencer ao objecto
            res = bo_att.getDbName();
        else
            res = null;
    }
    else
        res = null;
    
    if(res!=null )   //se o resultado já estiver definido
    {
      byte b = bo_att.getAtributeType();
        if(b == 1 && bo_att.getBridge()!=null /*bo_att.getObjects()!=null*/) //caso o atributo não seja terminal 
        {
            String s = this.objTypeOfAtt(Obj, Att, new Boolean(false));
            changeActualObj(s);
            
            if(bo_att.getDbIsTabled())
            {
              res=bo_att.getDbTableChildFieldName();
              realObj = bo_att.getTableName();                //achar a tabela de relação
            }
            else
            {
              res=bo_att.getBridge().getChildFieldName();      //o atributo child da tabela é o que contém o BOUI do objecto
              realObj = bo_att.getBridge().getBoMasterTable();                //achar a tabela de relação
            }
        }
        else
        {
          String s="", s2;
          if(bo_att.getObjects()==null && lastObj!=null && lastAtt!=null)
          {
            s = this.objTypeOfAtt(lastObj, lastAtt, new Boolean(true)); //ele vai incluir o anterior
          }
          
         // if(b == boDefAttribute.TYPE_OBJECTATTRIBUTE && s!=null && !s.equalsIgnoreCase("boObject"))
          if(lookahead(1)!=null && lookahead(1).equalsIgnoreCase(".") || firstSel)
           {
              s2 = this.objTypeOfAtt(Obj, Att, new Boolean(true));
              if (!s2.equalsIgnoreCase("boObject") && (s==null || s2!=null) /*&& bo_att.getRelationType() != boDefAttribute.RELATION_1_TO_1*/ )
              {
                s = s2;
              }
              if(!s.equalsIgnoreCase("boObject") && !s.equalsIgnoreCase("") /**/)
              {
                changeActualObj(s);
                if(prod().getExt() && Obj.equalsIgnoreCase(textObj))
                  realObj = boDefHandler.getBoDefinition(Obj).getBoExtendedTable();
                else
                  realObj = boDefHandler.getBoDefinition(Obj).getBoMasterTable();
              }
              else
              {
                  changeActualObj(Obj);
                  if(prod().getExt() && Obj.equalsIgnoreCase(textObj))
                  realObj = boDefHandler.getBoDefinition(Obj).getBoExtendedTable();
                else
                  realObj = boDefHandler.getBoDefinition(Obj).getBoMasterTable();
              }
           }
            
          else
          {
            if(s!=null && !s.equalsIgnoreCase("boObject") && !s.equalsIgnoreCase(""))
            {
              changeActualObj(s);
            }
            else
              if(s!=null && s.equalsIgnoreCase("") )
              {
                if(bo_att!=null && bo_att.getAtributeType()==bo_att.TYPE_OBJECTATTRIBUTE)
                {
                  if(bo_att.getReferencedObjectName().equalsIgnoreCase("boObject") && bo_att.getRelationType() == boDefAttribute.RELATION_MULTI_VALUES)
                  {
                      s = this.objTypeOfAtt(Obj, Att, new Boolean(false)); 
                      changeActualObj(s);
                      realObj = bo_att.getTableName();  
                      
                  }
                }
              }
            if(s!=null && !s.equalsIgnoreCase("boObject"))
              if(actualObj.equalsIgnoreCase(textObj) && prod().getExt())
                realObj = boDefHandler.getBoDefinition(actualObj).getBoExtendedTable();
              else
                realObj = boDefHandler.getBoDefinition(actualObj).getBoMasterTable();
       //     realObj = boDefHandler.getBoDefinition(actualObj).getBoMasterTable(); //definir realObj como a tabela do Objecto
          }
        }
        lastAtt = Att;
    }
    else        //se o resultado ainda não estiver definido vamos ver se o atributo pertence á bridge
    {
      if(lastObj != null)
      {
         boDefHandler boH = boDefHandler.getBoDefinition(lastObj);  //buscar o objecto anterior  
         if(boH!=null)
         {
             boDefAttribute boA = boH.getAttributeRef(lastAtt);     //ver se ele pertence ao anterior e obter a sua referencia
             if(boA!=null)
             {
                boDefBridge boB = boA.getBridge();              //ver se existe uma bridge entre a ligar o objecto ao atributo
                if(boB!=null)                                   //caso exista
                {
                    boDefAttribute boAB = boB.getAttributeRef(Att);
                    if(boAB==null)
                        return null;
                    bo_att = boAB;
                    res = boAB.getDbName();   //verificar se existe o atributo na bridge
                    if(res!=null)
                    {
                    
                      byte b = boAB.getAtributeType();
                      if(b == 1 /*&& boAB.getBridge()!=null /*&& bo_att.getObjects()!=null*/) //caso o atributo não seja terminal 
                      {
                        if(lookahead(1)!=null && lookahead(1).equalsIgnoreCase(".") || insel)
                        {
                          changeActualObj(lastObj);
                          String s = this.objTypeOfAtt(Obj, boAB, new Boolean(true), new Boolean(firstSel));
                          changeActualObj(s);
                        }
                        realObj = boB.getBoMasterTable();                //achar a tabela de relação
                 //         res="CHILD$";                                                   //o atributo child da tabela é o que contém o BOUI do objecto
                      }
                      else
                      {
                          String s = this.objTypeOfAtt(Obj, boAB, new Boolean(true), new Boolean(firstSel));
                          if(s!=null)
                            changeActualObj(s);
 //                         realObj = boDefHandler.getBoDefinition(Obj).getBoMasterTable(); //definir realObj como a tabela do Objecto
                      }
                   //     String s = this.objTypeOfAtt(Obj, boAB, new Boolean(true));
                   //     if(s!=null)
                   //       changeActualObj(s);
//                        realObj = boDefHandler.getBoDefinition(lastObj).getAttributeRef(lastAtt).getBridge().getBoMasterTable();
                    }
                        
                        //definir realObj como a tabela que representa a bridge
                }
             }    
          }
      }
         if(res==null && prod().getExt())
         {
            boDefHandler[] subC;
//             if(bo_obj==null)
              boDefHandler p_obj = prod().getObjectDef();
              if(p_obj != null)
                subC = p_obj.getTreeSubClasses();
              else
                subC = bo_obj.getTreeSubClasses();
              
//            else
//              subC = bo_obj.getTreeSubClasses();
              
             boDefHandler bo;
             boDefAttribute boA;
             for(int i=0; i < subC.length; i++)
             {
                bo = (boDefHandler)subC[i];
                boA = bo.getAttributeRef(Att);
                if(boA!=null)
                {
                    res = boA.getDbName();
                    byte b = boA.getAtributeType();
                    if(b == boDefAttribute.TYPE_ATTRIBUTE)
                      if(p_obj != null)
                            realObj = prod().getObjectDef().getBoExtendedTable();
                          else
                            realObj = bo_obj.getBoExtendedTable();
                    else
                    {
                      if(lookahead(1)!=null && lookahead(1).equalsIgnoreCase("."))
                      {
                        String s3;
                        if(p_obj != null)
                          s3 = this.objTypeOfAtt(prod().getObjectDef().getName(), boA, new Boolean(true), new Boolean(false));
                        else
                          s3 = this.objTypeOfAtt(bo_obj.getName(), boA, new Boolean(true), new Boolean(false));
                        if(s3!=null)
                        {
                          changeActualObj(s3);
                          realObj = boDefHandler.getBoDefinition(s3).getBoMasterTable();
                        }
                     
                        else
                        {
                          if(p_obj != null)
                            realObj = prod().getObjectDef().getBoExtendedTable();
                          else
                            realObj = bo_obj.getBoExtendedTable();
                          changeActualObj(bo.getName());
                        }
                      }
                      else
                      {
                        boDefBridge boB = boA.getBridge();
                        if(boB == null)
                        {
                          changeActualObj(bo.getName());
                          if(p_obj != null)
                            realObj = prod().getObjectDef().getBoExtendedTable();
                          else
                            realObj = bo_obj.getBoExtendedTable();
                        }
                        else
                        {
                          prod().addChild(Obj, boA, new Boolean(false), new Boolean(false), 0);
                          realObj = boB.getBoMasterTable();
                          res = "CHILD$";
                        }
                        
                        
                      }
                    }
                    
   //                 realObj = bo.getBoMasterTable();
  //                  pos = prod().setRootFrom(bo.getName()); 
                    break;
                }
             }
         }
    }
    if(bo_att != null)
    {
      switch(bo_att.getValueType())
      {
        case boDefAttribute.VALUE_NUMBER:
          typeStr = false;
          typeNum = true;
          break;
        case boDefAttribute.VALUE_CHAR:
          typeStr = true;
          typeNum = false;
          break;
        default:
          typeStr = true;
          typeNum = true;
      }
    }
    if(res!=null)
      res = "\""+res+"\"";
    return res;
  }
  
  //devolve o tipo de objecto do atributo e envia esta informação ao produtor para que este adicione a tabela á clausula FROM
  //pre: Obj e Att !=null e Att pertence a Obj
  private String objTypeOfAtt(String Obj, String Att, Boolean need)
  {    
    boDefAttribute boAtt = boDefHandler.getBoDefinition(Obj).getAttributeRef(Att);
    return objTypeOfAtt(Obj, boAtt, need, new Boolean(firstSel));
  } 
  private String objTypeOfAtt(String Obj, boDefAttribute boAtt, Boolean need, Boolean inner)
  {
    String s= Obj;
    try
    {
        s = boAtt.getReferencedObjectName(); //retirar o nome do tipo do objecto
    }catch(Exception e)
    {
        return null;
    }
    if(s!=null)
    {
        if(prod().addChild(Obj, boAtt, need, inner, pos))   //enviar a informação para o produtor para que conste na clausula FROM
          realObj = boDefHandler.getBoDefinition(s).getBoMasterTable();   //actualizar o realObj
    }
    return s;
  }
  
  private boolean isUser(String user, String pwd) { 
    boSession bosession=null;
    try{
          bosession = ctx.getApplication().boLogin( user, pwd );
          if(bosession != null)
          {
            //ctx.setPerformer(boObject.getBoManager().loadObject(ctx,bosession.getPerformerBoui()));
            HttpServletRequest request = ctx.getRequest(); 
            HttpServletResponse  response = ctx.getResponse(); 
            PageContext pageContext =  ctx.getPageContext();
            ctx.close();
            ctx = bosession.createRequestContext(request, response, pageContext);
          } 
             
            prod().setCtx(ctx);
    }catch(Exception e){ return false;}
    if(bosession == null)
      return false;
    else
    {
      
      return true;
    }
  }
  
  //confere se existe um estado W no actualObj e retorna a condição para o confirmar
  private String isPrimaryState(String W) { 
    boDefClsState boCls = boDefHandler.getBoDefinition(actualObj).getBoClsState();
    if(boCls==null)
        return null;
    boDefClsState boS = boCls.getChildState(W);
    if(boS!=null)//se o estado existir revolver a condição que o define
    {
        String res = boS.getParent().getName();
        if(prod().getExt())
          res=boDefHandler.getBoDefinition(actualObj).getBoExtendedTable()+"."+res.toUpperCase()+"="+boS.getNumericForm();
        else
          res=boDefHandler.getBoDefinition(actualObj).getBoMasterTable()+"."+res.toUpperCase()+"="+boS.getNumericForm();
        return res;
    }
    else
        return null;
  }
  
  //confere se existe um estado secundário pretencente a um estado primário e devolve a condição que o define
  private String isState(String pri, String sec) { 
   boDefClsState s = boDefHandler.getBoDefinition(actualObj).getBoClsState().getChildState(pri);
   return isState(s, sec, true);
  }
  
  //dado o estado anterior / pai, que pode ser primário ou não,  verificar se state é um subestado deste
  private String isState(boDefClsState antState, String state, boolean isPrimary)
  {
    boDefClsState boS = antState.getChildState(state);
    if(boS!=null)                   //se state for subestado de antState
        return realObj+"."+antState.getName()+"="+boS.getNumericForm(); //devolver condição
    else                            //se não for subestado é possível ser necessário descer mais na árvore de estados
    {
        String method = antState.getMethod();
        String res;
        if(isPrimary || method.equalsIgnoreCase("parallel"))    //se for paralelo inda há possibilidade de ter como filho state
        {
            boDefClsState[] st_array = antState.getChildStates();
            for(int i=0; i < st_array.length; i++)              //vamos ter de percorrer todos os subestados de antState
            {     
                res = isState(st_array[i], state, false);       //chamr recursivamente a função
                if(res!=null)
                    return res;
            }
            return null;
        }
        else
            return null;
    }
  }
  
  private boolean isViewer() { //TODO:precisa de ser melhorada
    boDefHandler obj = boDefHandler.getBoDefinition(textObj);
    String s = lookCurrentWord();
    boDefViewer st = obj.getViewer(s);
    String s2 = lookahead(1);
    if(s2!=null && s2.equalsIgnoreCase("."))
    {
        incConsumer();
        if(incConsumer())
            return false;
        s2 = lookCurrentWord();         
        ngtXMLHandler form = st.getForm(s2);
        if(form!=null)
        {
            viewer = form.getNodeName();
            return true;
        }
        else
            return false;
    }
    else
        return false;
  }
  
  //confere se a string corrente é  um número. Valida inteiros ou reais. Devolve uma String com o número validado
  private String isNumber()
  {
    long i = 0;
    String min = "";
    if(lookCurrentWord().equalsIgnoreCase("-"))
        if(incConsumer())//se a query terminar a seguir ao sinal menos retornar null para ser detectado o erro na classe de chamada
            return null;
        else
            min="-";            //memorizar o sinal -
    String s = lookCurrentWord();
    //try{
       // i = new Long(s);     //se não for possível transformar s num Integer então não se trata de um número
        i = ClassUtils.convertToLong( s , Long.MIN_VALUE ); //no limite pode dar problemas ...
        if ( i == Long.MIN_VALUE )
        {
            return null;
        }
   // }catch(Exception e)
   // {
   //     return null;
   // }



    if(s.equalsIgnoreCase( Long.toString( i ) ))    //para além de ter sido convertido em Integer é necessário confirmar se após a conversão estamos a falar do mesmo número
        if(!incConsumer() && lookCurrentWord().equalsIgnoreCase("."))   //se a query não terminar e tivermos um ponto a seguir
        {
            if(incConsumer())//avançar mais um token, se terminar devolver null
                return null;
            String s1 = lookCurrentWord();
            i = ClassUtils.convertToLong( s1 , Long.MIN_VALUE ); //no limite pode dar problemas ...
            if ( i == Long.MIN_VALUE )
            {
                return null;
            }

//            try{
//                i = new Long(s1);//tentar trasnformar a parte decimal num inteiro para confirmar ser um número
//            }catch(Exception e)
//            {
//                return null;
//            }

            if(s1.equalsIgnoreCase( Long.toString( i ) ) )//voltar a confirmar se depois da trasnformação estamos a falar do mesmo número
            {
                incConsumer();
                return min+s+"."+s1;            //devovler a parte inteira e a parte decimal
            }
            else
                return null;
        }
        else    //se não for um ponto ou a query terminar podemos devolver o resultado
            return min+s;
    else
        return null;
  }



  
  //devovle uma string identica á query original com o erro realçado
  private String errorSpot()
  {
    String s = textQuery;
    String sp = s.substring(0, ((Integer)words_pos.get(consumer-1)).intValue()); //retirar a parte antes do erro
    String ss;
    if(endOfQuery())
        ss = "<b>[...] </b>";   //se o erro for derivado da query não ter terminado correctamente
    else
        ss = "<b>[" + lookCurrentWord() + "]</b> " + s.substring(((Integer)words_pos.get(consumer)).intValue());
        //realçar o erro e juntar o resto da string
    
    String res = sp + ss;
    return "<br>"+res+"</br>";  //resutlado com a quebra de linha 
      
  }
  
  
  //fuunção que sistematiza a definição do objecto actual
  private void changeActualObj(String newObj)
  {
      lastObj = actualObj;
      actualObj = newObj;
      if(newObj.equalsIgnoreCase(textObj))
      {
        pos=0;
        //lastObj = actualObj = null;
        if(prod().getExt())
          realObj = boDefHandler.getBoDefinition(textObj).getBoExtendedTable();
        else
          realObj = boDefHandler.getBoDefinition(textObj).getBoMasterTable();
      }
  }
  
  //fuunção que sistematiza a incrementação do número de parênteses das experessões lógicas
  private void addPar()
  {
      lastParV.add(new Boolean(true));
      numPar++;
  }
 
   //fuunção que sistematiza a incrementação do número de parênteses derivados de sub-queries 
  private void addSubQ()
  {
      lastParV.add(new Boolean(false));
      subQueries++;
  }
  
  //função que devolve se os últimos parênteses abertos estavam relacionados com uma expressão lógica
  //ATENÇÃO: depois de chamada é modificado o vector que guarda a seq de parênteses, removendo o último
  //pre: lastParV.size() > 0
  private boolean lastPar()
  {
      boolean b=false;
      if(lastParV != null && lastParV.size()>0 && lastParV.lastElement()!=null)
      {
          b = ((Boolean)lastParV.lastElement()).booleanValue();
          lastParV.removeElementAt(lastParV.size()-1);  //remove a última referencia dos parenteses.
      }
      return b;
  }
  
  private void startXep()
  {
    if(xep_eval == null) xep_eval = new Xep();
    if(xep_eval.getCode().length() < 1)
    {
      try{    
        xep_eval.addBoObjectVariable("USER", boObject.getBoManager().loadObject(this.ctx, ctx.getBoSession().getPerformerBoui()));
      }catch(boRuntimeException e){}
    }
    else
    {
      xep_eval.clearCode();
    }
  }
  
  public Xep getXepEvaluator()
  {
    if(xep_eval == null) xep_eval = new Xep();
    return xep_eval;
  }
  
  private boolean hasNoSecurityHint(String boql)
  {
    if(boql != null && boql.length() > 0)
    {
        if(boql.indexOf("NO_SECURITY") > -1)
        {
            return true;
        }
    }
    return false;
  }
  private String removeNoSecurityHint(String boql)
  {
    return boql.replaceAll("/\\*NO_SECURITY\\*/", "");
  }
  
  public void copyGroupToOrder(boolean value)
  {
    copyGroupToOrder = value;
  }
  
}