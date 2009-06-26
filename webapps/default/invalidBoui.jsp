<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="netgest.bo.builder.*,netgest.bo.def.*,netgest.bo.*,netgest.bo.runtime.*,netgest.utils.*,java.util.*" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>untitled</title>
</head>
<body>

<pre>
<%

     boDefHandler[] defs =  boDefHandler.listBoDefinitions();
//     Hashtable bridges ;
//     Hashtable fields;
     
     ArrayList tabledatt = new ArrayList();
     
     
     for (int k = 0; k < defs.length; k++)
     {
        System.out.println( defs[k].getName() );
//        buildInherit( defs[k] );        
        if( defs[k].getName().equalsIgnoreCase("Ebo_Registry") || defs[k].getClassType() != boDefHandler.TYPE_CLASS )
        {
            continue;
        }
        boDefAttribute[] atts = defs[k].getBoAttributes();
        try 
        {
            int i = 0;
            tabledatt.add( "UPDATE "+defs[k].getBoPhisicalMasterTable()+" t SET TEMPLATE$=NULL WHERE NOT exists ( SELECT BOUI FROM EBO_TEMPLATE x where t.template$=x.boui ) and t.template$ is not null;" );
            tabledatt.add( "UPDATE "+defs[k].getBoPhisicalMasterTable()+" t SET CREATOR$=NULL WHERE NOT exists ( SELECT BOUI FROM EBO_PERF x where t.creator$=x.boui )  and t.creator$ is not null;" );
            tabledatt.add( "DELETE EBO_REGISTRY t WHERE NAME='"+defs[k].getName()+"' AND NOT exists ( SELECT BOUI FROM "+defs[k].getBoPhisicalMasterTable()+" x where t.ui$=x.boui );" );
            for (; i < atts.length-1 ; i++) 
            {
                if ( atts[i].getDbIsBinding() )
                {
                    if( atts[i].getDbIsTabled() )
                    {
                        tabledatt.add( "DELETE FROM "+atts[i].getDbTableName()+" t WHERE NOT exists ( SELECT BOUI FROM "+defs[k].getBoPhisicalMasterTable()+ " x where x.boui = t.t$parent$ );");
                        if ( atts[i].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE )
                        {
                            tabledatt.add( "DELETE FROM "+atts[i].getDbTableName()+" t WHERE NOT exists ( SELECT BOUI FROM EBO_REGISTRY x where t."+atts[i].getDbName()+" = x.ui$);" );
                        }
                    }
                    else if ( atts[i].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE )
                    {
                        //if( boBuilder.getUndeployedDefinitions(atts[i].getReferencedObjectName()) != null )
                        //{
                            if( ( atts[i].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE && atts[i].getMaxOccurs() > 1 ) )
                            {
                                tabledatt.add( "DELETE "+atts[i].getBridge().getBoMasterTable()+" t where NOT exists (SELECT BOUI FROM "+defs[k].getBoMasterTable()+" x where t.parent$=x.boui );");
                                tabledatt.add( "DELETE "+atts[i].getBridge().getBoMasterTable()+" t where NOT exists (SELECT BOUI FROM EBO_REGISTRY x where t.child$=x.ui$ );" );
                            }
                            else
                            {
                                tabledatt.add( "UPDATE "+defs[k].getBoPhisicalMasterTable()+" t SET "+atts[i].getDbName()+"=NULL WHERE NOT exists (SELECT UI$ FROM EBO_REGISTRY x where t."+atts[i].getDbName()+"=x.ui$) and t."+atts[i].getDbName()+" is not null;" );
                            }
                        //}
                    }
                }
            }
        } 
        catch (Exception e)
        {   
            e.printStackTrace();
        }
     }
     StringBuffer sb = new StringBuffer(); 
     for (int i = 0; i < tabledatt.size(); i++ )
     {  
        sb.append( tabledatt.get( i ) ).append("\r\n");
     }
     out.print( sb );            
%>
</pre>


</body>
</html>
