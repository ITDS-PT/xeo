/*Enconding=UTF-8*/
package netgest.bo.report;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import netgest.bo.runtime.EboContext;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class BDHTMLBuilder 
{
    //colors
    private static final String FONT_COLOR = "#000000";
    private static final String FONT_TYPE = "Arial Narrow";
    private static final String TABLE_HEADER_COLOR = "#DFDFDF";
    
    private static final String BLANK_LINE = "&nbsp;";
    private static final String PARAGRAPH_START = "<p>";
    private static final String PARAGRAPH_END = "</p>";
    private static final String BOLD_START = "<b>";
    private static final String BOLD_END = "</b>";    
    private static final String FONT_6_START = "<font face=\""+FONT_TYPE +"\" color=\"" +FONT_COLOR + "\" size=\"6\">";
    private static final String FONT_5_START = "<font face=\""+FONT_TYPE +"\" color=\"" +FONT_COLOR + "\" size=\"5\">";
    private static final String FONT_4_START = "<font face=\""+FONT_TYPE +"\" color=\"" +FONT_COLOR + "\" size=\"4\">";
    private static final String FONT_3_START = "<font face=\""+FONT_TYPE +"\" color=\"" +FONT_COLOR + "\" size=\"3\">";
    private static final String FONT_2_START = "<font face=\""+FONT_TYPE +"\" color=\"" +FONT_COLOR + "\" size=\"2\">";
    private static final String FONT_END = "</font>";
    private static final String TABLE_DESCRIPTION_HEADER = "<table border=\"1\" width=\"100%\"><tr width=\"100%\"><td width=\"100%\" bgcolor=\""+ TABLE_HEADER_COLOR+ "\"><p align=\"center\"><b><font face=\""+FONT_TYPE+"\" color=\"" +FONT_COLOR + "\" size=\"3\">Descrição da Tabela</font></b></td></tr></table>";
    private static final String TABLE_OBJECT_HEADER = "<table border=\"1\" width=\"100%\"><tr width=\"100%\"><td width=\"100%\" bgcolor=\""+ TABLE_HEADER_COLOR+ "\"><p align=\"center\"><b><font face=\""+FONT_TYPE+"\" color=\"" +FONT_COLOR + "\" size=\"3\">Objectos Alojados</font></b></td></tr></table>";
    private static final String TABLE_END = "</table>";
    private static final String TABLE_FIELDS_HEADER = "<table border=\"1\" width=\"100%\"><tr width=\"100%\"><td width=\"100%\" bgcolor=\""+ TABLE_HEADER_COLOR+ "\"><p align=\"center\"><b><font face=\""+FONT_TYPE+"\" color=\"" +FONT_COLOR + "\" size=\"3\">Campos</font></b></td></tr>\n";
    private static final String TABLE_RELA_HEADER = "<table border=\"1\" width=\"100%\"><tr width=\"100%\"><td width=\"100%\" bgcolor=\""+ TABLE_HEADER_COLOR+ "\"><p align=\"center\"><b><font face=\""+FONT_TYPE+"\" color=\"" +FONT_COLOR + "\" size=\"3\">Tabelas de Relações</font></b></td></tr>\n";
    private static final String TABLE_VIEWS_HEADER = "<table border=\"1\" width=\"100%\"><tr width=\"100%\"><td width=\"100%\" bgcolor=\""+ TABLE_HEADER_COLOR+ "\"><p align=\"center\"><b><font face=\""+FONT_TYPE+"\" color=\"" +FONT_COLOR + "\" size=\"3\">Vistas</font></b></td></tr>\n";
    private static final String INTERIOR_TABLE_DESCRIPTION_HEADER = "<table border=\"1\" width=\"100%\"><tr width=\"100%\"><td width=\"10%\" bgcolor=\""+TABLE_HEADER_COLOR+ "\"><p align=\"center\"><font size=\"2\"><b>Type</b></font></p></td><td width=\"25%\" bgcolor=\""+ TABLE_HEADER_COLOR+ "\" align=\"center\"><font size=\"2\"><b>Nome do Campo</b></font></td><td bgcolor=\"" +TABLE_HEADER_COLOR + "\" align=\"center\"><font size=\"2\"><b>Descrição do Campo</b></font></td></tr>";
    private static final String INTERIOR2_TABLE_DESCRIPTION_HEADER ="<table border=\"1\" width=\"100%\"><tr width=\"100%\"><td width=\"10%\" bgcolor=\"" +TABLE_HEADER_COLOR + "\"><p align=\"center\"><font size=\"2\"><b>&nbsp</b></font></p></td><td width=\"35%\" bgcolor=\"" +TABLE_HEADER_COLOR + "\" align=\"center\"><font size=\"2\"><b>Campo/Restrição</b></font></td><td width=\"25%\" bgcolor=\"" +TABLE_HEADER_COLOR + "\" align=\"center\"><font size=\"2\"><b>Tabela Referênciada</b>";
    private static final String INTERIOR2_TABLE_DESCRIPTION_HEADER_AUX = "</font></td><td width=\"25%\" bgcolor=\"" +TABLE_HEADER_COLOR + "\" align=\"center\"><font size=\"2\"><b>Campo Referênciado</b></font></td></tr>";
    private static final String RELATION1_1 = "<table border=\"1\" width=\"100%\"><tr><td width=\"100%\" bgcolor=\"" +TABLE_HEADER_COLOR + "\"><p align=\"center\"><a name=\"a1\"><b><font size=\"2\">Relações de 1 para 1</font></b></a></td></tr>";
    private static final String RELATIONN_N = "<table border=\"1\" width=\"100%\"><tr><td width=\"100%\" bgcolor=\"" +TABLE_HEADER_COLOR + "\"><p align=\"center\"><a name=\"a1\"><b><font size=\"2\">Relações de N para N</font></b></a></td></tr>";
    public int numeracao = 0;
    public ArrayList objList;
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public BDHTMLBuilder(ArrayList objList)
    {
        this.objList = objList;
    }
    
    public String generate()
    {
        String lastTable = null;
        ArrayList rSameTable = new ArrayList();
        StringBuffer index = new StringBuffer();
        StringBuffer html = new StringBuffer();
        StringBuffer toReturn = new StringBuffer();
        toReturn.append("<html>")
        .append("<head>\n")
        .append("<meta http-equiv=\"Content-Language\" content=\"pt\">\n")
        .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n")
        .append("<title>Relatório Base de Dados</title>\n")
        .append("</head>\n")
        .append("<body>\n");
       
        if (objList.size() == 1) {
        	rSameTable.clear();
        	rSameTable.add(objList.get(0));
        	generateTableHtml(rSameTable, objList, html, index);
        } else {
        	for(int i = 0; i < objList.size(); i++)
        	{
        		if(lastTable == null)
        		{
        			lastTable = ((XMLObject)objList.get(i)).getTableName();
        			rSameTable.clear();
        		}
        		if(((XMLObject)objList.get(i)).getTableName().equals(lastTable))
        		{
        			rSameTable.add(objList.get(i));
        		}
        		else
        		{
        			generateTableHtml(rSameTable, objList, html, index);
        			lastTable = ((XMLObject)objList.get(i)).getTableName();
        			rSameTable.clear();
        			rSameTable.add(objList.get(i));
        		}

        	}
        }
       
        if (objList.size() == 0)
        	toReturn.append("Não foram encontrados objectos");
        toReturn.append(index)
        .append(html)
        .append("</body>\n")
        .append("</html>\n");
        return toReturn.toString();
    }
    
    public void generateTableHtml(ArrayList tableList, ArrayList totalList, StringBuffer html, StringBuffer index)
    {
        //índice
        generateIndice(tableList, totalList, index);
        
        String tableName = ((XMLObject)tableList.get(0)).getTableName();
        String cap = ((XMLObject)tableList.get(0)).getCap();
        String anchor = ((XMLObject)tableList.get(0)).getAnchor();
        html.append(BLANK_LINE).append(BLANK_LINE)
            .append(PARAGRAPH_START)
            .append("<a name=\"" + anchor + "\">")
            .append(FONT_6_START)
            .append(BOLD_START)
            .append(cap).append(" ").append(tableName)
            .append(BOLD_END)
            .append(FONT_END)
            .append("\n")
            .append("</a>")
            .append(PARAGRAPH_END);
html.append("\n");html.append("\n");            
        //descrição
        html.append(PARAGRAPH_START).append(BOLD_START)
            .append(FONT_5_START).append(cap).append(".1")
            .append(" Descrição:&nbsp;&nbsp;").append(FONT_END)
            .append(BOLD_END)
            .append(FONT_4_START).append(((XMLObject)tableList.get(0)).getDescription())
            .append(FONT_END)
            .append("\n")
            .append(PARAGRAPH_END);
html.append("\n");html.append("\n");            
        //Descrição da Tabela
        html.append(PARAGRAPH_START).append(BOLD_START)
            .append(FONT_5_START).append(cap).append(".2")
            .append(" Descrição da Tabela&nbsp;&nbsp;").append(FONT_END)
            .append(BOLD_END)
            .append(PARAGRAPH_END);
        html.append(TABLE_DESCRIPTION_HEADER);        
        html.append(INTERIOR_TABLE_DESCRIPTION_HEADER);
        html.append("\n");
        html.append(getPrimaryKeys(tableList));
        html.append("\n");
        html.append(getUniqueKeys(tableList));
        html.append("\n");
        html.append(getIndexed(tableList));
        html.append(TABLE_END);
        html.append("\n");
        html.append(INTERIOR2_TABLE_DESCRIPTION_HEADER);
        html.append(INTERIOR2_TABLE_DESCRIPTION_HEADER_AUX);
        html.append(getForeignKeys(tableList));
        html.append(TABLE_END);
        html.append("\n");html.append("\n");        
        //objectos alojados
        html.append(PARAGRAPH_START).append(BOLD_START)
            .append(FONT_5_START).append(cap).append(".3")
            .append(" Objectos Alojados&nbsp;&nbsp;").append(FONT_END)
            .append(BOLD_END)
            .append(PARAGRAPH_END);
        html.append(TABLE_OBJECT_HEADER);
        html.append("\n");
        html.append(getObject(tableList));
        html.append("\n");
        html.append(TABLE_END);
html.append("\n");html.append("\n");
        //Campos
        html.append(PARAGRAPH_START).append(BOLD_START)
            .append(FONT_5_START).append(cap).append(".4")
            .append(" Campos&nbsp;&nbsp;").append(FONT_END)
            .append(BOLD_END)
            .append(PARAGRAPH_END);
        html.append(TABLE_FIELDS_HEADER);
        html.append(getSimpleFields(tableList));
         html.append("\n");
        html.append(TABLE_END);
        html.append(RELATION1_1);
        html.append(getOneToOneFields(tableList));
        html.append("\n");
        html.append(TABLE_END);
        html.append(RELATIONN_N);
        html.append(getNToNFields(tableList));
        html.append("\n");
        html.append(TABLE_END);
        
html.append("\n");html.append("\n");            
        //Views
        html.append(PARAGRAPH_START).append(BOLD_START)
            .append(FONT_5_START).append(cap).append(".5")
            .append(" Vistas&nbsp;&nbsp;").append(FONT_END)
            .append(BOLD_END)
            .append(PARAGRAPH_END);
        html.append(TABLE_VIEWS_HEADER);
         html.append(getViews(tableList));
         html.append("\n");
        html.append(TABLE_END);
        
        //Tabelas Relações
        html.append(PARAGRAPH_START).append(BOLD_START)
            .append(FONT_5_START).append(cap).append(".6")
            .append(" Tabelas de Relações(Bridges)&nbsp;&nbsp;").append(FONT_END)
            .append(BOLD_END)
            .append(PARAGRAPH_END);
        html.append(TABLE_RELA_HEADER);
        html.append(getTableRela(tableList));
         html.append("\n");
        html.append(TABLE_END);

        html.append("<p align=\"center\">&nbsp;</p>");
        html.append("<p align=\"center\">&nbsp;</p>");
        html.append("<p align=\"center\">&nbsp;</p>");
    }

    private String getUniqueKeys(ArrayList r)
    {
        ArrayList aux = new ArrayList();
        ArrayList duplicated = new ArrayList();
        StringBuffer colFieldName = new StringBuffer("");
        StringBuffer colFieldDesc = new StringBuffer("");
        XMLObject obj;
        XMLAttribute att;
        for(int i = 0; i < r.size(); i++)
        {
            obj = (XMLObject)r.get(i);
            aux = obj.getAttributes();
            for(int j = 0; j < aux.size(); j++)
            {
                att = (XMLAttribute)aux.get(j);
                if(att.getUnique() && !duplicated.contains(att.getAttributeName()))
                {
                    //fieldName
                    duplicated.add(att.getAttributeName());
                    colFieldName.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append(FONT_2_START)
                    .append("<td bgcolor=\"#FFFFFF\">").append(att.getAttributeBDName())
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
                    
                    //fieldName
                    colFieldDesc.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append(FONT_2_START)
                    .append("<td bgcolor=\"#FFFFFF\">").append(att.getDescription())
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
                }
            }
        }
        if(colFieldName.length() == 0)
        {
        //fieldName
            colFieldName.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
            .append(FONT_2_START)
            .append("<td bgcolor=\"#FFFFFF\">").append("&nbsp;")
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
                    
                    //fieldName
                    colFieldDesc.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append(FONT_2_START)
                    .append("<td bgcolor=\"#FFFFFF\">").append("&nbsp;")
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
        }
        StringBuffer sb = new StringBuffer("<tr>\n");
        sb.append("<td width=\"10%\" bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\">Chaves únicas</font></td>\n")
        .append("<td width=\"25%\" bgcolor=\"#FFFFFF\" align=\"center\">\n")
        .append("<table border=\"0\" width=\"100%\">\n")
        .append(colFieldName.toString())
        .append("</table>\n")
        .append("</td>\n")
        .append("<td width=\"65%\" bgcolor=\"#FFFFFF\" align=\"center\">\n")
        .append("<table border=\"0\" width=\"100%\">\n")
        .append(colFieldDesc.toString())
        .append("</table>\n")
        .append("</td>\n")
        .append("</tr>\n");
        
        return sb.toString();
    }

    private String getIndexed(ArrayList r)
    {
        ArrayList aux = new ArrayList();
        ArrayList duplicated = new ArrayList();
        StringBuffer colFieldName = new StringBuffer("");
        StringBuffer colFieldDesc = new StringBuffer("");
        XMLObject obj;
        XMLAttribute att;
        
        for(int i = 0; i < r.size(); i++)
        {
            obj = (XMLObject)r.get(i);
            aux = obj.getAttributes();
            for(int j = 0; j < aux.size(); j++)
            {
                att = (XMLAttribute)aux.get(j);
                if(att.getIndexed() && !duplicated.contains(att.getAttributeName()))
                {
                    duplicated.add(att.getAttributeName());
                    //fieldName
                    colFieldName.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append(FONT_2_START)
                    .append("<td bgcolor=\"#FFFFFF\">").append(att.getAttributeBDName())
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
                    
                    //fieldName
                    colFieldDesc.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append(FONT_2_START)
                    .append("<td bgcolor=\"#FFFFFF\">").append(att.getDescription())
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
                }
            }
        }
        if(colFieldName.length() == 0)
        {
        //fieldName
            colFieldName.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
            .append(FONT_2_START)
            .append("<td bgcolor=\"#FFFFFF\">").append("&nbsp;")
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
                    
                    //fieldName
                    colFieldDesc.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append(FONT_2_START)
                    .append("<td bgcolor=\"#FFFFFF\">").append("&nbsp;")
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
        }
        StringBuffer sb = new StringBuffer("<tr>\n");
        sb.append("<td width=\"10%\" bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\">Índices</font></td>\n")
        .append("<td width=\"25%\" bgcolor=\"#FFFFFF\" align=\"center\">\n")
        .append("<table border=\"0\" width=\"100%\">\n")
        .append(colFieldName.toString())
        .append("</table>\n")
        .append("</td>\n")
        .append("<td width=\"65%\" bgcolor=\"#FFFFFF\" align=\"center\">\n")
        .append("<table border=\"0\" width=\"100%\">\n")
        .append(colFieldDesc.toString())
        .append("</table>\n")
        .append("</td>\n")
        .append("</tr>\n");
        
        return sb.toString();
    }

    private String getPrimaryKeys(ArrayList r)
    {
        ArrayList aux = new ArrayList();
        ArrayList duplicated = new ArrayList();
        StringBuffer colFieldName = new StringBuffer("");
        StringBuffer colFieldDesc = new StringBuffer("");
        XMLObject obj;
        XMLAttribute att;
        for(int i = 0; i < r.size(); i++)
        {
            obj = (XMLObject)r.get(i);
            aux = obj.getAttributes();
            for(int j = 0; j < aux.size(); j++)
            {
                att = (XMLAttribute)aux.get(j);
                if(att.getPrimaryKey() && !duplicated.contains(att.getAttributeName()))
                {
                    duplicated.add(att.getAttributeName());
                    //fieldName
                    colFieldName.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append(FONT_2_START)
                    .append("<td bgcolor=\"#FFFFFF\">").append(att.getAttributeBDName())
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
                    
                    //fieldName
                    colFieldDesc.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append(FONT_2_START)
                    .append("<td bgcolor=\"#FFFFFF\">").append(att.getDescription())
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
                }
            }
        }
        if(colFieldName.length() == 0)
        {
        //fieldName
            colFieldName.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
            .append(FONT_2_START)
            .append("<td bgcolor=\"#FFFFFF\">").append("&nbsp;")
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
                    
                    //fieldName
                    colFieldDesc.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append(FONT_2_START)
                    .append("<td bgcolor=\"#FFFFFF\">").append("&nbsp;")
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
        }
        StringBuffer sb = new StringBuffer("<tr>\n");
        sb.append("<td width=\"10%\" bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\">Chaves Primárias</font></td>\n")
        .append("<td width=\"25%\" bgcolor=\"#FFFFFF\" align=\"center\">\n")
        .append("<table border=\"0\" width=\"100%\">\n")
        .append(colFieldName.toString())
        .append("</table>\n")
        .append("</td>\n")
        .append("<td width=\"65%\" bgcolor=\"#FFFFFF\" align=\"center\">\n")
        .append("<table border=\"0\" width=\"100%\">\n")
        .append(colFieldDesc.toString())
        .append("</table>\n")
        .append("</td>\n")
        .append("</tr>\n");
        
        return sb.toString();
        
    }

    private String getForeignKeys(ArrayList r)
    {
        ArrayList aux = new ArrayList();
        ArrayList duplicated = new ArrayList();
        StringBuffer colFieldName = new StringBuffer("");
        StringBuffer colFieldDesc = new StringBuffer("");
        StringBuffer colRef = new StringBuffer("");
        XMLObject obj;
        String att;
        int t = 1;
        for(int i = 0; i < r.size(); i++)
        {
            obj = (XMLObject)r.get(i);
            aux = obj.getFks();
            for(int j = 0; j < aux.size(); j++)
            {
                att = (String)aux.get(j);
                if(!duplicated.contains(att))
                {
                    duplicated.add(att);
                    StringTokenizer tok = new StringTokenizer(att, "|");
                    while(tok.hasMoreElements())
                    {
                        //fieldName
                        colFieldName.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                        .append(FONT_2_START)
                        .append("<td bgcolor=\"#FFFFFF\">").append(tok.nextToken())
                        .append("</td>\n")
                        .append(FONT_END)
                        .append("</tr>\n");
                        
                            //fieldName
                            colFieldDesc.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                            .append(FONT_2_START)
                            .append("<td bgcolor=\"#FFFFFF\">").append(tok.nextToken())
                            .append("</td>\n")
                            .append(FONT_END)
                            .append("</tr>\n");
                        
                            //fieldName
                            colRef.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                            .append(FONT_2_START)
                            .append("<td bgcolor=\"#FFFFFF\">").append(tok.nextToken())
                            .append("</td>\n")
                            .append(FONT_END)
                            .append("</tr>\n");                        
                    }                    
                }
            }
        }
        if(colFieldName.length() == 0)
        {
        //fieldName
            colFieldName.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
            .append(FONT_2_START)
            .append("<td bgcolor=\"#FFFFFF\">").append("&nbsp;")
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
                    
                    //fieldName
                    colFieldDesc.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append(FONT_2_START)
                    .append("<td bgcolor=\"#FFFFFF\">").append("&nbsp;")
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");
//fieldName
                    colRef.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append(FONT_2_START)
                    .append("<td bgcolor=\"#FFFFFF\">").append("&nbsp;")
                    .append("</td>\n")
                    .append(FONT_END)
                    .append("</tr>\n");                    
        }
        StringBuffer sb = new StringBuffer("<tr>\n");
        sb.append("<td width=\"10%\" bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\">Chaves Estrangeiras</font></td>\n")
        .append("<td width=\"35%\" bgcolor=\"#FFFFFF\" align=\"center\">\n")
        .append("<table border=\"0\" width=\"100%\">\n")
        .append(colFieldName.toString())
        .append("</table>\n")
        .append("</td>\n")
        .append("<td width=\"25%\" bgcolor=\"#FFFFFF\" align=\"center\">\n")
        .append("<table border=\"0\" width=\"100%\">\n")
        .append(colFieldDesc.toString())
        .append("</table>\n")
        .append("</td>\n")
        .append("<td width=\"25%\" bgcolor=\"#FFFFFF\" align=\"center\">\n")
        .append("<table border=\"0\" width=\"100%\">\n")
        .append(colRef.toString())
        .append("</table>\n")
        .append("</td>\n")
        .append("</tr>\n");
        
        return sb.toString();
    }

    private String getObject(ArrayList r)
    {
        StringBuffer sb = new StringBuffer();
        XMLObject obj;
        sb.append("<table border=\"1\" width=\"100%\">\n")
        .append("<tr width=\"100%\">\n")
        .append("<td width=\"35%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\">")
        .append("<p align=\"center\"><font size=\"2\"><b>Nome do Objecto</b></font></p>")
        .append("</td>\n")
        .append("<td width=\"65%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\" align=\"center\"><font size=\"2\"><b>Descrição</b></font></td>\n")
        .append("</tr>\n");
        
        for(int i = 0; i < r.size(); i++)
        {
            obj = (XMLObject)r.get(i);
            sb.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
            .append("<td bgcolor=\"#FFFFFF\">")
            .append(FONT_2_START)
            .append(obj.getObjName())
            .append(FONT_END)
            .append("</td>\n");
            sb.append("<td bgcolor=\"#FFFFFF\">")
            .append(FONT_2_START)
            .append(obj.getDescription())
            .append(FONT_END)
            .append("</td>\n")
            .append("</tr>\n");
        }
        return sb.toString();
    }


    private String getViews(ArrayList r)
    {
        StringBuffer sb = new StringBuffer();
        ArrayList duplicado = new ArrayList();
        ArrayList aux; 
        XMLObject obj;
        for(int i = 0; i < r.size(); i++)
        {
            obj = (XMLObject)r.get(i);
            aux = obj.getViews();
            for(int j = 0; j < aux.size(); j++)
            {
                if(!duplicado.contains((String)aux.get(j)))
                {
                    duplicado.add((String)aux.get(j));
                    sb.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append("<td bgcolor=\"#FFFFFF\">")
                    .append(FONT_2_START)
                    .append(aux.get(j))
                    .append((String)FONT_END)
                    .append("</td>\n")
                    .append("</tr>\n");
                }
            }
        }
        return sb.toString();
    }

    private String getTableRela(ArrayList r)
    {
        ArrayList duplicated = new ArrayList();
        StringBuffer sb = new StringBuffer();
        XMLObject obj;
        sb.append("<table border=\"1\" width=\"100%\">\n")
        .append("<tr width=\"100%\">\n")
        .append("<td width=\"35%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\">")
        .append("<p align=\"center\"><font size=\"2\"><b>Nome da Tabela</b></font></p>")
        .append("</td>\n")
        .append("<td width=\"65%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\" align=\"center\"><font size=\"2\"><b>Descrição</b></font></td>\n")
        .append("</tr>\n");
        ArrayList atts;
        String aux;
        for(int i = 0; i < r.size(); i++)
        {
            obj = (XMLObject)r.get(i);
            atts = obj.getTableBridges();
            for(int j = 0; j <atts.size(); j++)
            {
                aux = ((String)atts.get(j));
                if(!duplicated.contains(aux))
                {
                    StringTokenizer st = new StringTokenizer(aux, "|");
                    while(st.hasMoreElements())
                    {
                        duplicated.add(aux);
                        sb.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                        .append("<td bgcolor=\"#FFFFFF\">")
                        .append(FONT_2_START)
                        .append(st.nextToken())
                        .append(FONT_END)
                        .append("</td>\n");
                        sb.append("<td bgcolor=\"#FFFFFF\">")
                        .append(FONT_2_START)
                        .append(st.nextToken())
                        .append(FONT_END)
                        .append("</td>\n")
                        .append("</tr>\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    private String getSimpleFields(ArrayList r)
    {
        ArrayList duplicated = new ArrayList();
        StringBuffer sb = new StringBuffer();
        XMLObject obj;
        sb.append("<table border=\"1\" width=\"100%\">\n")
        .append("<tr width=\"100%\">\n")
        .append("<td width=\"30%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\">")
        .append("<p align=\"center\"><font size=\"2\"><b>Nome do Campo</b></font></p>")
        .append("</td>\n")
        .append("<td width=\"45%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\">")
        .append("<p align=\"center\"><font size=\"2\"><b>Descrição</b></font></p>")
        .append("</td>\n")
        .append("<td width=\"25%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\" align=\"center\"><font size=\"2\"><b>Tipo</b></font></td>\n")
        .append("</tr>\n");
        ArrayList atts;
        String aux;
        for(int i = 0; i < r.size(); i++)
        {
            obj = (XMLObject)r.get(i);
            atts = obj.getSimpleRel();
            for(int j = 0; j <atts.size(); j++)
            {
                aux = ((XMLAttribute)atts.get(j)).getAttributeBDName();
                if(!duplicated.contains(aux))
                {
                    duplicated.add(aux);
                    sb.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append("<td bgcolor=\"#FFFFFF\">")
                    .append(FONT_2_START)
                    .append(aux)
                    .append(FONT_END)
                    .append("</td>\n");
                    
                    sb.append("<td bgcolor=\"#FFFFFF\">")
                    .append(FONT_2_START)
                    .append(((XMLAttribute)atts.get(j)).getDescription())
                    .append(FONT_END)
                    .append("</td>\n");
                    
                    
                    sb.append("<td bgcolor=\"#FFFFFF\">")
                    .append(FONT_2_START)
                    .append(((XMLAttribute)atts.get(j)).getAttributeType())
                    .append(FONT_END)
                    .append("</td>\n")
                    .append("</tr>\n");   
                }
            }
        }
        return sb.toString();
    }

    private String getOneToOneFields(ArrayList r)
    {
        ArrayList duplicated = new ArrayList();
        StringBuffer sb = new StringBuffer();
        XMLObject obj;
        sb.append("<table border=\"1\" width=\"100%\">\n")
        .append("<tr width=\"100%\">\n")
        .append("<td width=\"30%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\">")
        .append("<p align=\"center\"><font size=\"2\"><b>Nome do Campo</b></font></p>")
        .append("</td>\n")
        .append("<td width=\"45%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\">")
        .append("<p align=\"center\"><font size=\"2\"><b>Descrição</b></font></p>")
        .append("</td>\n")
        .append("<td width=\"35%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\" align=\"center\"><font size=\"2\"><b>Tabela Relacionada</b></font></td>\n")
        .append("</tr>\n");
        ArrayList atts;
        String aux;
        for(int i = 0; i < r.size(); i++)
        {
            obj = (XMLObject)r.get(i);
            atts = obj.getRelationOneToOne();
            for(int j = 0; j <atts.size(); j++)
            {
                aux = ((XMLAttribute)atts.get(j)).getAttributeBDName();
                if(!duplicated.contains(aux))
                {
                    duplicated.add(aux);
                    sb.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append("<td bgcolor=\"#FFFFFF\">")
                    .append(FONT_2_START)
                    .append(aux)
                    .append(FONT_END)
                    .append("</td>\n");
                    
                    sb.append("<td bgcolor=\"#FFFFFF\">")
                    .append(FONT_2_START)
                    .append(((XMLAttribute)atts.get(j)).getDescription())
                    .append(FONT_END)
                    .append("</td>\n");
                    
                    
                    sb.append("<td bgcolor=\"#FFFFFF\">")
                    .append(FONT_2_START)
                    .append(((XMLAttribute)atts.get(j)).getTableReferenced())
                    .append(FONT_END)
                    .append("</td>\n")
                    .append("</tr>\n");   
                }
            }
        }
        return sb.toString();
    }


    private String getNToNFields(ArrayList r)
    {
        ArrayList duplicated = new ArrayList();
        StringBuffer sb = new StringBuffer();
        XMLObject obj;
        sb.append("<table border=\"1\" width=\"100%\">\n")
        .append("<tr width=\"100%\">\n")
        .append("<td width=\"20%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\">")
        .append("<p align=\"center\"><font size=\"2\">Attributo</font></p>")
        .append("</td>\n")
        
        .append("<td width=\"30%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\">")
        .append("<p align=\"center\"><font size=\"2\"><b>Descrição</b></font></p>")
        .append("</td>\n")
        
        
        .append("<td width=\"25%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\" align=\"center\"><font size=\"2\"><b>Tabela Relacionada</b></font></td>\n")
        .append("<td width=\"25%\" bgcolor=\""+ TABLE_HEADER_COLOR+"\" align=\"center\"><font size=\"2\"><b>Tabelas de Relações</b></font></td>\n")
        .append("</tr>\n");
        ArrayList atts;
        String aux;
        for(int i = 0; i < r.size(); i++)
        {
            obj = (XMLObject)r.get(i);
            atts = obj.getRelationZeroToMany();
            for(int j = 0; j <atts.size(); j++)
            {
                aux = ((XMLAttribute)atts.get(j)).getAttributeName();
                if(!duplicated.contains(aux))
                {
                    duplicated.add(aux);
                    sb.append("<tr bgcolor=\"#FFFFFF\" align=\"center\">\n")
                    .append("<td bgcolor=\"#FFFFFF\">")
                    .append(FONT_2_START)
                    .append(aux)
                    .append(FONT_END)
                    .append("</td>\n");
                    
                    sb.append("<td bgcolor=\"#FFFFFF\">")
                    .append(FONT_2_START)
                    .append(((XMLAttribute)atts.get(j)).getDescription())
                    .append(FONT_END)
                    .append("</td>\n");
                    
                    
                    sb.append("<td bgcolor=\"#FFFFFF\">")
                    .append(FONT_2_START)
                    .append(((XMLAttribute)atts.get(j)).getTableReferenced())
                    .append(FONT_END)
                    .append("</td>\n");
                    sb.append("<td bgcolor=\"#FFFFFF\">")
                    .append(FONT_2_START)
                    .append(((XMLAttribute)atts.get(j)).getDef().getBridge().getBoMasterTable())
                    .append(FONT_END)
                    .append("</td>\n")
                    .append("</tr>\n");   
                }
            }
        }
        return sb.toString();
    }

    public void generateIndice(ArrayList tableList, ArrayList totalList, StringBuffer index)
    {
        if(index.length() == 0)
        {
            index.append("<p><font size=\"6\"><b>INDEX</b></font></p>\n");
        }
        numeracao++;
        String tableName = ((XMLObject)tableList.get(0)).getTableName();
        String anchor = getAnchor(tableName, totalList);
        if(anchor == null)
        {
            index.append("<p>").append(String.valueOf(numeracao)).append(". ")
                .append(tableName).append("&nbsp;</p>\n");
        }
        else
        {
            index.append("<p> <a href=#").append(anchor).append(">").append(String.valueOf(numeracao)).append(". ")
                .append(tableName).append("&nbsp;</a></p>\n");
        }
    }
    
    private static String getAnchor(String tableName, ArrayList totalList)
    {
        for(int i = 0; i < totalList.size(); i++)
        {
            if(((XMLObject)totalList.get(i)).getTableName().equals(tableName))
            {
                return ((XMLObject)totalList.get(i)).getAnchor(); 
            }
        }
        return null;
    }
    
    public static void writeTableName(String cap, String tableName, StringBuffer htmlContentor)
    {
        htmlContentor.append("<FONT FACE=\"Arial Narrow\" COLOR=\"#000000\" SIZE=\"6\"><B>")
            .append(cap).append(" ").append(tableName).append("</B>");
    }
    
}