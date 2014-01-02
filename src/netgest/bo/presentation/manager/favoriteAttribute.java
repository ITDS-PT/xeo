/*Enconding=UTF-8*/
package netgest.bo.presentation.manager;

import java.io.Serializable;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.BridgeAttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.cacheBouis;

import netgest.utils.ClassUtils;
import netgest.utils.ObjectSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Stack;


public final class favoriteAttribute implements Serializable
{
    private long[] p_lastBouis = new long[5];
    private long[] p_topFive   = new long[5];
    private long[] p_bouis     = new long[15];
    private int[] p_HintBouis  = new int[15];
    private String p_myKey     = null;

    public favoriteAttribute(String key)
    {
        p_myKey = key;

        Arrays.fill(p_bouis, 0);
        Arrays.fill(p_topFive, 0);
        Arrays.fill(p_lastBouis, 0);
        Arrays.fill(p_HintBouis, 0);
    }

    public void add(long boui)
    {
        boolean isTop = false;

        boolean found = false;
        int min       = 99999;
        int posMin    = 0;

        for (int i = 0; i < p_bouis.length; i++)
        {
            if (!found)
            {
                if (p_bouis[i] == 0)
                {
                    p_bouis[i] = boui;
                    p_HintBouis[i]++;
                    found = true;
                }
                else if (p_bouis[i] == boui)
                {
                    p_HintBouis[i]++;
                    found = true;
                }
            }

            if (p_HintBouis[i] < min)
            {
                posMin     = i;
                min        = p_HintBouis[i];
            }
        }

        if (!found) // desaparece aquele que tem o valor minimo
        {
            p_bouis[posMin]         = boui;
            p_HintBouis[posMin]     = 1;
        }

        //construir o topFive
        Arrays.fill(p_topFive, 0);

        int p1 = -1;
        int p2 = -1;
        int p3 = -1;
        int p4 = -1;

        for (int i = 0; i < 5; i++)
        {
            int pos = 0;
            int m   = 0;

            for (int j = 0; j < p_bouis.length; j++)
            {
                if ((p_HintBouis[j] >= m) && (j != p1) && (j != p2) && (j != p3) && (j != p4))
                {
                    m       = p_HintBouis[j];
                    pos     = j;
                }
            }

            p_topFive[i] = p_bouis[pos];

            if (i == 0)
            {
                p1 = pos;
            }
            else if (i == 1)
            {
                p2 = pos;
            }
            else if (i == 2)
            {
                p3 = pos;
            }
            else if (i == 3)
            {
                p4 = pos;
            }

            if (p_bouis[pos] == boui)
            {
                isTop = true;
            }
        }

        if (!isTop)
        {
            for (int i = p_lastBouis.length - 1; i > 0; i--)
            {
                p_lastBouis[i] = p_lastBouis[i - 1];
            }

            p_lastBouis[0] = boui;
        }
    }

    public void add(boObject[] favoritesObjects)
    {
        for (int i = 0; i < favoritesObjects.length; i++)
        {
            add(favoritesObjects[i].getBoui());
        }
    }

    public void add(long[] bouis)
    {
        for (int i = 0; i < bouis.length; i++)
        {
            add(bouis[i]);
        }
    }

    public StringBuffer getHTMLFavorites(AttributeHandler attr, StringBuffer name)
        throws boRuntimeException
    {
//        StringBuffer toRet = new StringBuffer("<div  id=fav").append(name).append(
//                " onmouseover=\"this.style.display=''\" onmouseout=\"this.style.display='none'\" style='position:absolute;display:none'><table class=\"layout\"><colgroup><col width=30px/><col width=100%/>"
//            );
        StringBuffer toRet = new StringBuffer();
        
        
        toRet = new StringBuffer("<div  id=fav").append(name).append(
                " style='display:none'><table class=\"layout\"><colgroup><col width=30px/><col width=100%/>"
            );


        toRet.append("<tr><td colspan=2 style='padding:3px;border-bottom:2px inset #FFFFFF' >")
             .append("<img hspace=4 onclick='closeFav()' src='ieThemes/0/taskBar/buttclose.gif' />")
             .append(attr.getDefAttribute().getLabel()).append("</td></tr>");

        boolean found = false;

        String value = attr.getValueString() + ";";

        String[] values  = value.split(";");
        StringBuffer inp = new StringBuffer();
        boolean vazio    = false;

        if (
            (attr.getDefAttribute().getRelationType() != boDefAttribute.RELATION_1_TO_1) &&
            (attr.getDefAttribute().getRuntimeMaxOccurs() > 1)
        )
        {
            inp.append(" type='CHECKBOX' ");
        }
        else
        {
            inp.append(" name=_ignore_").append("f1").append(name).append(" type='radio' ");
            vazio = true;
        }

        StringBuffer bvalue = new StringBuffer();

        if (!attr.getDefAttribute().hasTransformer())
        {
            for (int i = 0; i < values.length; i++)
            {
                String c = null;
                cacheCardID.getCardId(attr.getParent().getEboContext(), ClassUtils.convertToLong(values[i]));

                c = cacheCardID.getCardId(
                        attr.getParent().getEboContext(), ClassUtils.convertToLong(values[i])
                    );

                if (c != null)
                {
                    found = true;
                    bvalue.append("<tr boui='").append(values[i])
                          .append("'><td><input style='border:0' checked ").append(inp).append(
                        " /></td><td>"
                    ).append(c).append("</td></tr>");
                }
            }
        }
        else
        {
            if( vazio ) // relcao 1-1 
            {
                String c = null;
                if(attr.getObject()!=null && attr.getObject().getAttribute("refObj")!=null)
                {
                c = cacheCardID.getCardId(
                    attr.getParent().getEboContext(), attr.getObject().getAttribute("refObj").getValueLong() );
                if (c != null)
                {
                    found = true;
                    bvalue.append("<tr boui='").append( attr.getObject().getAttribute("refObj").getValueLong() )
                          .append("'><td><input style='border:0' checked ").append(inp).append(
                        " /></td><td>"
                    ).append(c).append("</td></tr>");
                    value=""+attr.getObject().getAttribute("refObj").getValueLong()+";";
                }
                }
            }
            else
            {
                boObject o = attr.getParent();
                bridgeHandler  b = o.getBridge( attr.getName() );
                boBridgeIterator it= b.iterator();
                it.beforeFirst();
                value="";
                AttributeHandler refAttr = null;
                long boui= -1;
                while ( it.next() )
                {
                    boObject aux = it.currentRow().getObject();
                    if(aux != null)
                    {
                        refAttr = aux.getAttribute("refObj");
                        if(refAttr != null)
                        {
                            boui = ClassUtils.convertToLong(refAttr.getValueString(),-1);
                            if(boui != -1)
                            {
                                String c=null;
                                c = cacheCardID.getCardId(
                                    attr.getParent().getEboContext(), boui );
                                if(c!=null)
                                {
                                     bvalue.append("<tr boui='").append( boui )
                                          .append("'><td><input style='border:0' checked ").append(inp).append(
                                        " /></td><td>"
                                    ).append(c).append("</td></tr>");   
                                    found=true;
                                }
                                value+=boui+";";
                            }
                        }
                    }
                }
            }
            
            
        }
        long new_lastBouis[]=this.getMatchesBouis(p_lastBouis,attr);
        for (int i = 0; i <new_lastBouis.length; i++)
        {
            if ((new_lastBouis[i] != 0) && (value.indexOf("" + new_lastBouis[i] + ";") == -1))
            {
                found = true;

                String c = cacheCardID.getCardId(attr.getParent().getEboContext(), new_lastBouis[i]);

                if (c != null)
                {
                    toRet.append("<tr boui='").append(new_lastBouis[i]).append(
                        "'><td><input style='border:0' "
                    ).append(inp).append(" /></td><td>").append(c).append("</td></tr>");
                }
            }
        }

        long new_topFive[]=this.getMatchesBouis(p_topFive,attr);
        for (int i = 0; i < new_topFive.length; i++)
        {
            if ((new_topFive[i] != 0) && (value.indexOf("" + new_topFive[i] + ";") == -1))
            {
                found = true;

                String c = cacheCardID.getCardId(attr.getParent().getEboContext(), new_topFive[i]);

                if (c != null)
                {
                    toRet.append("<tr boui='").append(new_topFive[i]).append("'><td><input style='border:0' ")
                         .append(inp).append(" /></td><td>").append(c).append("</td></tr>");
                }
            }
        }

        if (!found)
        {
            toRet = new StringBuffer();
        }
        else
        {
            toRet.append(bvalue);

            if (vazio)
            {
                toRet.append("<tr boui=''><td><input style='border:0' ").append(inp).append(" /></td><td>")
                     .append("Limpar campo").append("</td></tr>");
            }

            toRet.append("<tr><td colspan=2 style='border-bottom:2px inset #FFFFFF' >").append("&nbsp;")
                 .append("</td></tr>");
            toRet.append("<tr><td colspan=2 style='padding:3px;'>")
                 .append("<button style='filter:none' onclick='applyFav()'>Aplicar</button>&nbsp;")
                 .append("<button style='filter:none' onclick='closeFav();window.fav.oSrc.Lookup();'>Mais</button>").append("</td></tr>");
            toRet.append("</table></div>");
        }

        return toRet;
    }
    
    private long[] getMatchesBouis(long [] array_bouis,AttributeHandler attr)
    {
      //Verifica se existe filter no Atributo se existir devolver apenas os objectos que respeitem o filtro
      long [] arrayToReturn=array_bouis;
      try
      {
        arrayToReturn=array_bouis;
        
         String filter= attr.getFilterBOQL_query();
         if (filter!=null && !filter.equals(""))
         {
           boolean hasFavorites=false;
           String filterFavorites=" boui in (";
           for (int i=0;i<array_bouis.length;i++)
           {
              long currBoui=array_bouis[i];
              if (currBoui!=0) 
              {
                filterFavorites+=(currBoui+(i==array_bouis.length-1?"":","));
                hasFavorites=true;
              }
           }
           filterFavorites+=")";
           if (!hasFavorites) return arrayToReturn;
           
           int indexOfOrderBy=filter.toUpperCase().indexOf("ORDER BY");
           int indexOfWhere=filter.toUpperCase().indexOf("WHERE");
           
           //Irá falhar para situações do tipo select xxxx where xxx in (select xxx where yyyy order by xx) order by xxx
           //select xxxx where xxx in (select xxx where yyyy order by xx) 
           //Resumindo se existir order numa clausula que não a clausula mãe irá falhar
           
           if (indexOfOrderBy!=-1 && indexOfWhere!=-1)
           {
             filter=filter.substring(0,indexOfOrderBy-1)+" and "+filterFavorites+" "+filter.substring(indexOfOrderBy,filter.length());
           }
           else if (indexOfOrderBy!=-1)
           {
             filter=filter.substring(0,indexOfOrderBy-1)+" "+filterFavorites+" "+filter.substring(indexOfOrderBy,filter.length());
           }
           else if (indexOfWhere!=-1)
           {
             filter+=" and "+filterFavorites;
           }
           else
           {
             filter+=filterFavorites;
           }
           boObjectList list=boObjectList.list(attr.getEboContext(),filter);
           arrayToReturn =new long[list.getRowCount()];
           list.beforeFirst();
           int i=0;
           while (list.next())
           {
            arrayToReturn[i]=list.getCurrentBoui();
            i++;
           }
           return arrayToReturn;
         }
         else return arrayToReturn;      
      }
      catch (Exception e)
      {
        return arrayToReturn;
      }
    }
}
