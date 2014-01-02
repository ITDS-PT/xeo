/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import netgest.bo.def.*;
import javax.servlet.jsp.*;
import netgest.bo.runtime.*;
import java.util.*;

public final class docHTMLgenerateToolBar  {
    public static final byte TEXT_MENU=0;
    public static final byte ONCLICK_MENU=1;
    public static final byte SUBMENU=2;
    public static final byte TABINDEX=3;
    public static final byte ACCESSKEY=4;
    public static final byte IMG=5;
    public static final byte TITLE=6;
     
    //public docHTMLgenerateToolBar() {
   // }


    public static int getHeightMenu(Hashtable[] menus,String text_right,PageContext page,boObjectList bolist) throws boRuntimeException,java.io.IOException {
        JspWriter out=page.getOut();
        int height=0;
        Hashtable menuf; //=menuText();
        Hashtable menub; //=menuTextB();
        boolean menuFlat=false;
        boolean menuBar=false;

        for (int i = 0; i < menus.length; i++)  {
          if(menus[i].get("MENUFLAT") !=null)  {
                menuFlat=true;
                menuf=menus[i];
          }
          else if( menus[i].get("MENUBAR") !=null) {
                menuBar=true;
                menub=menus[i];
          }
          
        }
        

        
        if(menuFlat) height+=24;
        if(menuBar) height+=24;
        return height;
    }
    
    public static void render(Hashtable[] menus,String text_right,PageContext page,boObjectList bolist) throws boRuntimeException,java.io.IOException {
        JspWriter out=page.getOut();
        int height=0;
        Hashtable menuf=null; //=menuText();
        Hashtable menufr=null; //=menuText();
        Hashtable menub=null; //=menuTextB();
        boolean menuFlat=false;
        boolean menuFlatRight=false;
        boolean menuBar=false;

        for (int i = 0; i < menus.length; i++)  {
         if(menus[i] !=null){
          if(menus[i].get("MENUFLAT") !=null)  {
                menuFlat=true;
                menuf=menus[i];
          }
          else if(menus[i].get("MENUFLATRIGHT") !=null)  {
                menuFlatRight=true;
                menufr=menus[i];
          }
          else if( menus[i].get("MENUBAR") !=null) {
                menuBar=true;
                menub=menus[i];
          }
         }
        }
        

        
        if(menuFlat) height+=24;
        if(menuBar) height+=24;
        
 
        
        out.write( Gtxt.text[ Gtxt.TB_CONT ]);
        out.write( Gtxt.text[ Gtxt.TB_CONT+1 ]);
        out.write( Gtxt.text[ Gtxt.TB_CONT+2 ]);
        out.print(height);
        out.write( Gtxt.text[ Gtxt.TB_CONT+3 ]);
        out.write( Gtxt.text[ Gtxt.TB_CONT+4 ]);
                    
        if(menuFlat){
            out.write( Gtxt.text[ Gtxt.MNU_FLAT ]);
            out.print('1'); //IDMENU;
            out.write( Gtxt.text[ Gtxt.MNU_FLAT+1 ]);
            out.write( Gtxt.text[ Gtxt.MNU_FLAT+2 ]);
            out.write( Gtxt.text[ Gtxt.MNU_FLAT+3 ]);

            String[][] xm=(String[][]) menuf.get("MENUFLAT");
            for (int i = 0; i < xm.length; i++)  {
                    
                        if ( xm[i]!=null )
                        {
                            out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM ] );
                            if( xm[i][TABINDEX] !=null){ 
                                out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_TAB_I ] );
                                out.print(xm[i][TABINDEX] );
                                out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_TAB_I +1] );
                            }
                            else{
                                out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_TAB_I ] );
                                out.print('0');
                                out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_TAB_I +1] );
                            }
                            
                            if( xm[i][ACCESSKEY] !=null){ 
                                out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_AK ] );
                                out.print(xm[i][ACCESSKEY] );
                                out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_AK +1] );
                            }
                            if( xm[i][SUBMENU] !=null){ 
                                out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_ID_SUB ] );
                                out.print(xm[i][SUBMENU] );
                                out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_ID_SUB +1] );
                            }
                            if( xm[i][ONCLICK_MENU ] !=null){ 
                                out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_ONCLICK ] );
                                out.print(xm[i][ONCLICK_MENU] );
                                out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_ONCLICK +1] );
                            }
                            out.write(Gtxt.text[ Gtxt.MNU_FLAT_ITEM+1] );
    
                            out.print(xm[i][TEXT_MENU] );
    
                         //text[MNU_FLAT_ITEM ]="<span class='menu' ".toCharArray();
                         //          text[MNU_FLAT_ITEM_AK ]="accessKey='".toCharArray();/*letter */ text[MNU_FLAT_ITEM_AK+1]="' ".toCharArray(); 
                         //          text[MNU_FLAT_ITEM_TAB_I]="tabIndex='".toCharArray();/*tabindex */text[MNU_FLAT_ITEM_TAB_I+1]="' ".toCharArray();
                         //          text[MNU_FLAT_ITEM_ID_SUB]=" menu='".toCharArray();/*idSubmneu mnulist */ text[MNU_FLAT_ITEM_ID_SUB+1]="' ".toCharArray();
                         //          text[MNU_FLAT_ITEM_ONCLICK]=" onclick='".toCharArray();/*action onclick */ text[MNU_FLAT_ITEM_ONCLICK+1]="' ".toCharArray();
                         //          text[MNU_FLAT_ITEM +1]=" >".toCharArray();/*Text-> <u>F</u>ile*/ 
    
                            if( xm[i][SUBMENU] !=null)
                            { 
    
                                Vector submenus=new Vector();
                                //submenus.size();
                                submenus.add(xm[i][SUBMENU]);
    
                                for( int y=0; y< submenus.size(); y++){
                                    
                                
                                String[][] xsub=(String[][]) menuf.get( (String)submenus.get(y) );
    
                                 //  text[MNU_LIST ] ="<table class='mnuList' ".toCharArray();
                                //   text[MNU_LIST_ID_MENU]="id='".toCharArray(); /*mnufile*/ text[MNU_LIST_ID_MENU+1]="' ".toCharArray();
                                //   text[MNU_LIST+1 ] ="cellSpacing='0' cellPadding='3'>".toCharArray();
                                //   text[MNU_LIST+2 ] ="<colgroup><col class='mnuLeft'><col><tbody>".toCharArray();
    
    
                                
                                out.write( Gtxt.text[ Gtxt.MNU_LIST  ] );
                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ID_MENU ] );
                                //out.print( xm[i][SUBMENU] );
                                out.print( (String)submenus.get(y)  );
                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ID_MENU+1 ] );
                                    
                                out.write( Gtxt.text[ Gtxt.MNU_LIST+1  ] );
                                out.write( Gtxt.text[ Gtxt.MNU_LIST+2  ] );
                                if(xsub!=null)
                                for (int z = 0; z < xsub.length; z++)  {
                                
                                    if( xsub[z][SUBMENU]==null){
                                            if( xsub[z][TEXT_MENU].equalsIgnoreCase("SPACER") ){
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_SPACER ] );
                                                //text[MNU_LIST_ITEM_SPACER]="<tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'></td></tr>".toCharArray();
                                            }
                                            else{
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM ] );
    
                                                if( xsub[z][TABINDEX] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_TAB_I ] );
                                                    out.print(xsub[z][TABINDEX] );
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_TAB_I +1] );
                                                }
                                                else{
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_TAB_I ] );
                                                    out.print('0');
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_TAB_I +1] );
                                                }
                            
                                                if( xsub[z][ACCESSKEY] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_AK ] );
                                                    out.print(xsub[z][ACCESSKEY] );
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_AK +1] );
                                                }
                                                if( xsub[z][ONCLICK_MENU ] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_ONCLICK ] );
                                                    out.print(xsub[z][ONCLICK_MENU] );
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_ONCLICK +1] );
                                                }
    
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM+1 ] );
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM+2 ] );
                                                out.print( xsub[z][TEXT_MENU]);
                                                
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_END ] );
                                                
                                              //text[MNU_LIST_ITEM]="<tr ".toCharArray(); 
                                              //text[MNU_LIST_ITEM_AK]=" accessKey='".toCharArray(); /*S*/text[MNU_LIST_ITEM_AK+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEM_TAB_I]="tabIndex='".toCharArray();/*tabindex */text[MNU_LIST_ITEM_TAB_I+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEM_ONCLICK]=" onclick='".toCharArray();/*action onclick */ text[MNU_LIST_ITEM_ONCLICK+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEM +1]=" >".toCharArray();
                                             // text[MNU_LIST_ITEM +2]="<td>&nbsp;</td><td class='mnuItm'>".toCharArray();
                                                 //<u>S</u>ave
                                              //text[MNU_LIST_ITEM_END]="</td></tr>".toCharArray();
                                              
                                            }
       
                                    }
                                    else{
                                    
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS ] );
    
                                                if( xsub[z][TABINDEX] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_TAB_I ] );
                                                    out.print(xsub[z][TABINDEX] );
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_TAB_I +1] );
                                                }
                                                else{
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_TAB_I ] );
                                                    out.print('0');
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_TAB_I +1] );
                                                }
                            
                                                if( xsub[z][ACCESSKEY] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_AK ] );
                                                    out.print(xsub[z][ACCESSKEY] );
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_AK +1] );
                                                }
                                                if( xsub[z][SUBMENU] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_ID_SUB ] );
                                                    out.print(xsub[z][SUBMENU] );
                                                    submenus.add(xsub[z][SUBMENU]);    
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_ID_SUB +1] );
                                                }
                                                if( xsub[z][ONCLICK_MENU ] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_ONCLICK ] );
                                                    out.print(xsub[z][ONCLICK_MENU] );
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_ONCLICK +1] );
                                                }
    
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS+1 ] );
                                                out.print( xsub[z][TEXT_MENU]);
                            
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_END ] );
                                     
    
                                             // text[MNU_LIST_ITEMWS]="<tr ".toCharArray(); 
                                             // text[MNU_LIST_ITEMWS_AK]=" accessKey='".toCharArray(); /*S*/text[MNU_LIST_ITEMWS_AK+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEMWS_TAB_I]="tabIndex='".toCharArray();/*tabindex */text[MNU_LIST_ITEMWS_TAB_I+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEMWS_ID_SUB]=" menu='".toCharArray();/*idSubmneu mnulist */ text[MNU_LIST_ITEMWS_ID_SUB+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEMWS_ONCLICK]=" onclick='".toCharArray();/*action onclick */ text[MNU_LIST_ITEMWS_ONCLICK+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEMWS +1]=" ><td></td><td class='mnuItm' noWrap><span class='mnuSubItem'>".toCharArray();
                                             //    //New
                                             // text[MNU_LIST_ITEMWS_END]="</span><img src='templates/menu/std/mnu_rArrow.gif' align='top' WIDTH='16' HEIGHT='13'></td></tr>".toCharArray();
                                    }
                    
                                }
                                
    
    
                               out.write( Gtxt.text[ Gtxt.MNU_LIST_END  ] );
                            }
    
                        
                            }
    
                            out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_END ] );
                        }
            }
            

            //menu á direita
            
            //----
            out.write( Gtxt.text[ Gtxt.MNU_FLAT_END ]);


            /*
           * menu á direita
			<TD class="icMenu mnuRight" noWrap>
				<SPAN class=menu 
					title="Create a new Activity" 
					onclick="openStdDlg('/Activities/dlg_create.aspx',window, 350, 300);" 
					tabIndex=0><IMG class=mnuBtn 
					src="resources/activity/ico16.gif"> New 
					Activity</SPAN>
            </TD>

            */
            
            if( menuFlatRight){
            xm=(String[][]) menufr.get("MENUFLATRIGHT");
                for (int i = 0; i < xm.length; i++)  {
                        if ( xm[i] != null )
                        {
                            out.print("<TD class='icMenu mnuRight' noWrap><SPAN class=menu " );
                            if( xm[i][ TITLE ] != null ){
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_TITLE ] );
                                out.print( xm[i][ TITLE ] );
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_TITLE+1 ] );
                            }
                            if( xm[i][ ONCLICK_MENU ] != null ){
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_ONCLICK ] );
                                out.print( xm[i][ ONCLICK_MENU ] );
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_ONCLICK+1 ] );
                            }
                            
                            if( xm[i][SUBMENU] !=null){ 
                                out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_ID_SUB ] );
                                out.print(xm[i][SUBMENU] );
                                out.write( Gtxt.text[ Gtxt.MNU_FLAT_ITEM_ID_SUB +1] );
                            }
                            
                            if( xm[i][TABINDEX] !=null){ 
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_TAB_I ] );
                                out.print(xm[i][TABINDEX] );
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_TAB_I +1] );
                            }
                            
                            else{
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_TAB_I ] );
                                out.print('0');
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_TAB_I +1] );
                            }
                            out.write('>');
                            if( xm[i][ IMG ] != null){
                                out.print("<IMG class=mnuBtn src='");
                                out.print( xm[i][ IMG ] );
                                out.print("'>");
                            }
                            out.print( xm[i][ TEXT_MENU ] );
                            
                            if( xm[i][SUBMENU] !=null)
                            { 
    
                                Vector submenus=new Vector();
                                //submenus.size();
                                submenus.add(xm[i][SUBMENU]);
    
                                for( int y=0; y< submenus.size(); y++){
                                    
                                
                                String[][] xsub=(String[][]) menufr.get( (String)submenus.get(y) );
    
                                 //  text[MNU_LIST ] ="<table class='mnuList' ".toCharArray();
                                //   text[MNU_LIST_ID_MENU]="id='".toCharArray(); /*mnufile*/ text[MNU_LIST_ID_MENU+1]="' ".toCharArray();
                                //   text[MNU_LIST+1 ] ="cellSpacing='0' cellPadding='3'>".toCharArray();
                                //   text[MNU_LIST+2 ] ="<colgroup><col class='mnuLeft'><col><tbody>".toCharArray();
    
    
                                
                                out.write( Gtxt.text[ Gtxt.MNU_LIST  ] );
                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ID_MENU ] );
                                //out.print( xm[i][SUBMENU] );
                                out.print( (String)submenus.get(y)  );
                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ID_MENU+1 ] );
                                    
                                out.write( Gtxt.text[ Gtxt.MNU_LIST+1  ] );
                                out.write( Gtxt.text[ Gtxt.MNU_LIST+2  ] );
                                if(xsub!=null)
                                for (int z = 0; z < xsub.length; z++)  {
                                
                                    if( xsub[z][SUBMENU]==null){
                                            if( xsub[z][TEXT_MENU].equalsIgnoreCase("SPACER") ){
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_SPACER ] );
                                                //text[MNU_LIST_ITEM_SPACER]="<tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'></td></tr>".toCharArray();
                                            }
                                            else{
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM ] );
    
                                                if( xsub[z][TABINDEX] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_TAB_I ] );
                                                    out.print(xsub[z][TABINDEX] );
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_TAB_I +1] );
                                                }
                                                else{
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_TAB_I ] );
                                                    out.print('0');
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_TAB_I +1] );
                                                }
                            
                                                if( xsub[z][ACCESSKEY] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_AK ] );
                                                    out.print(xsub[z][ACCESSKEY] );
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_AK +1] );
                                                }
                                                if( xsub[z][ONCLICK_MENU ] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_ONCLICK ] );
                                                    out.print(xsub[z][ONCLICK_MENU] );
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_ONCLICK +1] );
                                                }
    
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM+1 ] );
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM+2 ] );
                                                out.print( xsub[z][TEXT_MENU]);
                                                
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEM_END ] );
                                                
                                              //text[MNU_LIST_ITEM]="<tr ".toCharArray(); 
                                              //text[MNU_LIST_ITEM_AK]=" accessKey='".toCharArray(); /*S*/text[MNU_LIST_ITEM_AK+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEM_TAB_I]="tabIndex='".toCharArray();/*tabindex */text[MNU_LIST_ITEM_TAB_I+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEM_ONCLICK]=" onclick='".toCharArray();/*action onclick */ text[MNU_LIST_ITEM_ONCLICK+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEM +1]=" >".toCharArray();
                                             // text[MNU_LIST_ITEM +2]="<td>&nbsp;</td><td class='mnuItm'>".toCharArray();
                                                 //<u>S</u>ave
                                              //text[MNU_LIST_ITEM_END]="</td></tr>".toCharArray();
                                              
                                            }
       
                                    }
                                    else{
                                    
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS ] );
    
                                                if( xsub[z][TABINDEX] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_TAB_I ] );
                                                    out.print(xsub[z][TABINDEX] );
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_TAB_I +1] );
                                                }
                                                else{
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_TAB_I ] );
                                                    out.print('0');
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_TAB_I +1] );
                                                }
                            
                                                if( xsub[z][ACCESSKEY] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_AK ] );
                                                    out.print(xsub[z][ACCESSKEY] );
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_AK +1] );
                                                }
                                                if( xsub[z][SUBMENU] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_ID_SUB ] );
                                                    out.print(xsub[z][SUBMENU] );
                                                    submenus.add(xsub[z][SUBMENU]);    
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_ID_SUB +1] );
                                                }
                                                if( xsub[z][ONCLICK_MENU ] !=null){ 
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_ONCLICK ] );
                                                    out.print(xsub[z][ONCLICK_MENU] );
                                                    out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_ONCLICK +1] );
                                                }
    
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS+1 ] );
                                                out.print( xsub[z][TEXT_MENU]);
                            
                                                out.write( Gtxt.text[ Gtxt.MNU_LIST_ITEMWS_END ] );
                                     
    
                                             // text[MNU_LIST_ITEMWS]="<tr ".toCharArray(); 
                                             // text[MNU_LIST_ITEMWS_AK]=" accessKey='".toCharArray(); /*S*/text[MNU_LIST_ITEMWS_AK+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEMWS_TAB_I]="tabIndex='".toCharArray();/*tabindex */text[MNU_LIST_ITEMWS_TAB_I+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEMWS_ID_SUB]=" menu='".toCharArray();/*idSubmneu mnulist */ text[MNU_LIST_ITEMWS_ID_SUB+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEMWS_ONCLICK]=" onclick='".toCharArray();/*action onclick */ text[MNU_LIST_ITEMWS_ONCLICK+1]="' ".toCharArray();
                                             // text[MNU_LIST_ITEMWS +1]=" ><td></td><td class='mnuItm' noWrap><span class='mnuSubItem'>".toCharArray();
                                             //    //New
                                             // text[MNU_LIST_ITEMWS_END]="</span><img src='templates/menu/std/mnu_rArrow.gif' align='top' WIDTH='16' HEIGHT='13'></td></tr>".toCharArray();
                                    }
                    
                                }
                                
    
    
                               out.write( Gtxt.text[ Gtxt.MNU_LIST_END  ] );
                            }
    
                        
                            }
    
                            
                            
                            
                            out.print("</SPAN></TD>");
                            
                        }

                        
                }

                
            }
            //out.print("dizeres á direita");
            
            out.write( Gtxt.text[ Gtxt.MNU_FLAT_TD_TITLE ]);
            if(text_right!=null) out.print(text_right);
            out.write( Gtxt.text[ Gtxt.MNU_FLAT_TD_TITLE_END ]);
            
            out.write( Gtxt.text[ Gtxt.MNU_FLAT_END+1 ]);
            
        }

        if(menuBar){
            out.write( Gtxt.text[ Gtxt.MNU_BAR ]);
            out.write( Gtxt.text[ Gtxt.MNU_BAR+1 ]);
            
            String[][] xm=(String[][]) menub.get("MENUBAR");
            for (int i = 0; i < xm.length; i++)  {

                    if ( xm[i]!=null )
                    {
                        if( xm[i][ IMG] !=null) {   
                        
                      //  text[ MNU_BAR_ITEMWI ]="<span class='menuFlat' ".toCharArray();
                      //  text[ MNU_BAR_ITEMWI_TITLE   ]="title='".toCharArray();/* title*/text[ MNU_BAR_ITEMWI_TITLE+1 ]="' ".toCharArray();
                      //  text[ MNU_BAR_ITEMWI_ONCLICK ]="onclick='".toCharArray();/* onclick*/text[ MNU_BAR_ITEMWI_ONCLICK+1 ]="' ".toCharArray();
                      //  text[ MNU_BAR_ITEMWI_TAB_I   ]="tabindex='".toCharArray();/* onclick*/text[ MNU_BAR_ITEMWI_TAB_I+1 ]="' ".toCharArray();
                      //  text[ MNU_BAR_ITEMWI_IMG     ]="><img class='mnuBtn' src='templates/menu/std/".toCharArray();/*img name*/text[ MNU_BAR_ITEMWI_IMG+1 ]="' width='16' height='16'> ".toCharArray();
                        /*text*/
                      //  text[ MNU_BAR_ITEMWI_END]="</span>".toCharArray();
                            out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI ] );
                            if( xm[i][ TITLE ] != null ){
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_TITLE ] );
                                out.print( xm[i][ TITLE ] );
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_TITLE+1 ] );
                            }
                            if( xm[i][ ONCLICK_MENU ] != null ){
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_ONCLICK ] );
                                out.print( xm[i][ ONCLICK_MENU ] );
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_ONCLICK+1 ] );
                            }
                            if( xm[i][TABINDEX] !=null){ 
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_TAB_I ] );
                                out.print(xm[i][TABINDEX] );
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_TAB_I +1] );
                            }
                            else{
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_TAB_I ] );
                                out.print('0');
                                out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_TAB_I +1] );
                            }
                            out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_IMG ] );
                            out.write( xm[i][ IMG ] );
                            out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_IMG +1] );
                            out.write( xm[i][ TEXT_MENU] );
                            out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEMWI_END ] );
                        }
                        else {
                            
                            if( xm[i][ TEXT_MENU ].equalsIgnoreCase("SPACER") ){
                               //  text[ MNU_BAR_ITEM_SPACER]="<img src='templates/menu/std/mnu_hSpacer.gif' WIDTH='2' HEIGHT='17'>".toCharArray();
                               out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEM_SPACER  ]);
                            }
                            else{
    
                      //      text[ MNU_BAR_ITEM ]="<span class='menuFlat' ".toCharArray();
                      //      text[ MNU_BAR_ITEM_TITLE   ]="title='".toCharArray();/* title*/text[ MNU_BAR_ITEM_TITLE+1 ]="' ".toCharArray();
                       //     text[ MNU_BAR_ITEM_ONCLICK ]="onclick='".toCharArray();/* onclick*/text[ MNU_BAR_ITEM_ONCLICK+1 ]="' ".toCharArray();
                       //     text[ MNU_BAR_ITEM_TAB_I   ]="tabindex='".toCharArray();/* onclick*/text[ MNU_BAR_ITEM_TAB_I+1 ]="' ".toCharArray();
                       //     /*text*/
                        //    text[ MNU_BAR_ITEM_END]="</span>".toCharArray();
                                   out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEM ] );
                                    if( xm[i][ TITLE ] != null ){
                                        out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEM_TITLE ] );
                                        out.print( xm[i][ TITLE ] );
                                        out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEM_TITLE+1 ] );
                                    }
                                    if( xm[i][ ONCLICK_MENU ] != null ){
                                        out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEM_ONCLICK ] );
                                        out.print( xm[i][ ONCLICK_MENU ] );
                                        out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEM_ONCLICK+1 ] );
                                    }
                                    if( xm[i][TABINDEX] !=null){ 
                                        out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEM_TAB_I ] );
                                        out.print(xm[i][TABINDEX] );
                                        out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEM_TAB_I +1] );
                                    }
                                    else{
                                        out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEM_TAB_I ] );
                                        out.print('0');
                                        out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEM_TAB_I +1] );
                                    }
                                    out.write( xm[i][ TEXT_MENU] );
                                    out.write( Gtxt.text[ Gtxt.MNU_BAR_ITEM_END ] );
    
    
                            
                            }
    
                        }
                    }
            }

            //out.print("dizeres á direita");
            if(!menuFlat)
            {
                out.write( "</td>".toCharArray());
                out.write( Gtxt.text[ Gtxt.MNU_FLAT_TD_TITLE ]);
                if(text_right!=null) out.print(text_right);
                out.write( Gtxt.text[ Gtxt.MNU_FLAT_TD_TITLE_END ]);
            }
            out.write( Gtxt.text[ Gtxt.MNU_BAR_END ]);
            //text[ MNU_BAR_END ]="</td></tr></tbody></table>".toCharArray();
    
            
        }
        
        out.write( Gtxt.text[ Gtxt.TB_CONT_END]);

    }

    
    
    
    private static class Gtxt {
        private static final char text[][]=new char[120][];
        private final static byte TB_CONT = 0; //ate 4
        private final static byte TB_CONT_END = 5; //
        
        private final static byte MNU_FLAT = 6; //ate 9
        private final static byte MNU_FLAT_END = 10; //ate 11
        
        private final static byte MNU_FLAT_ITEM = 12; // ate 13
        private final static byte MNU_FLAT_ITEM_END = 14; 
        private final static byte MNU_FLAT_ITEM_AK = 15; // +16
        private final static byte MNU_FLAT_ITEM_TAB_I = 17; // +18
        private final static byte MNU_FLAT_ITEM_ID_SUB = 19; // +20
        private final static byte MNU_FLAT_ITEM_ONCLICK = 21; // +22

        private final static byte MNU_LIST = 23; // +24+25
        private final static byte MNU_LIST_END = 26;
        private final static byte MNU_LIST_ID_MENU = 27;// +28
        
        private final static byte MNU_LIST_ITEM_SPACER = 30;
        
        private final static byte MNU_LIST_ITEM = 32; // +33+34
        private final static byte MNU_LIST_ITEM_END = 35;
        private final static byte MNU_LIST_ITEM_AK = 36;//+37
        private final static byte MNU_LIST_ITEM_TAB_I = 38;//+39
        private final static byte MNU_LIST_ITEM_ONCLICK = 40;//+41

        private final static byte MNU_LIST_ITEMWS = 50; // +51
        private final static byte MNU_LIST_ITEMWS_END = 52;
        private final static byte MNU_LIST_ITEMWS_AK = 53;//+54
        private final static byte MNU_LIST_ITEMWS_TAB_I = 55;//+56
        private final static byte MNU_LIST_ITEMWS_ONCLICK = 57;//+58
        private final static byte MNU_LIST_ITEMWS_ID_SUB = 59;//+60

        private final static byte MNU_BAR = 70;//+71
        private final static byte MNU_BAR_END = 72;//

        private final static byte MNU_BAR_ITEMWI = 73;
        private final static byte MNU_BAR_ITEMWI_END = 74;
        private final static byte MNU_BAR_ITEMWI_TITLE = 75; //+76
        private final static byte MNU_BAR_ITEMWI_ONCLICK = 77; //+78
        private final static byte MNU_BAR_ITEMWI_TAB_I = 79; //+80
        private final static byte MNU_BAR_ITEMWI_IMG = 81; //+82

        private final static byte MNU_BAR_ITEM = 83;
        private final static byte MNU_BAR_ITEM_END = 84;
        private final static byte MNU_BAR_ITEM_TITLE = 85; //+86
        private final static byte MNU_BAR_ITEM_ONCLICK = 87; //+88
        private final static byte MNU_BAR_ITEM_TAB_I = 89; //+90
        private final static byte MNU_BAR_ITEM_SPACER = 91; 

        private final static byte MNU_FLAT_TD_TITLE = 92;
        private final static byte MNU_FLAT_TD_TITLE_END = 93;
        
        static {
        text[TB_CONT+ 0]="<table style='z-Index:1000' class='layout' cellSpacing='0' cellPadding='0'>".toCharArray();
            text[TB_CONT+ 1]="<tbody>".toCharArray();
                text[TB_CONT+ 2]="<tr height='".toCharArray();/*--height--*/;text[TB_CONT+ 3]="'>".toCharArray();
                    text[TB_CONT+ 4]="<td>".toCharArray();


        
        text[MNU_FLAT  ]="<table class='mnubarFlat' id='mnuBar".toCharArray();/* IDMENU*/  text[MNU_FLAT+1]="' cellSpacing='0' cellPadding='0'>".toCharArray();
            text[MNU_FLAT+2]="<tbody><tr><td width='9'><img hspace='3' src='templates/menu/std/mnu_vSpacer.gif' WIDTH='5' HEIGHT='18'/></td>".toCharArray();
             text[MNU_FLAT+3]="<td class='icMenu' noWrap='1'>".toCharArray();

             
                                text[MNU_FLAT_ITEM ]="<span class='menu' ".toCharArray();
                                text[MNU_FLAT_ITEM_AK ]="accessKey='".toCharArray();/*letter */ text[MNU_FLAT_ITEM_AK+1]="' ".toCharArray(); 
                                text[MNU_FLAT_ITEM_TAB_I]="tabIndex='".toCharArray();/*tabindex */text[MNU_FLAT_ITEM_TAB_I+1]="' ".toCharArray();
                                text[MNU_FLAT_ITEM_ID_SUB]=" menu='".toCharArray();/*idSubmneu mnulist */ text[MNU_FLAT_ITEM_ID_SUB+1]="' ".toCharArray();
                                text[MNU_FLAT_ITEM_ONCLICK]=" onclick=\"".toCharArray();/*action onclick */ text[MNU_FLAT_ITEM_ONCLICK+1]="\" ".toCharArray();
                                text[MNU_FLAT_ITEM +1]=" >".toCharArray();/*Text-> <u>F</u>ile*/ 
                                   
                                     text[MNU_LIST ] ="<table class='mnuList' ".toCharArray();
                                     text[MNU_LIST_ID_MENU]="id='".toCharArray(); /*mnufile*/ text[MNU_LIST_ID_MENU+1]="' ".toCharArray();
                                     text[MNU_LIST+1 ] ="cellSpacing='0' cellPadding='3'>".toCharArray();
                                     text[MNU_LIST+2 ] ="<colgroup/><col class='mnuLeft'/><col/><tbody>".toCharArray();
              
                  
                                         text[MNU_LIST_ITEM_SPACER]="<tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>".toCharArray();

                /*<tr accessKey="S" onclick="boForm.Save();" tabIndex="0">
                <td>&nbsp;</td>
                <td class="mnuItm"><u>S</u>ave</td></tr>*/

                                          text[MNU_LIST_ITEM]="<tr ".toCharArray(); 
                                          text[MNU_LIST_ITEM_AK]=" accessKey='".toCharArray(); /*S*/text[MNU_LIST_ITEM_AK+1]="' ".toCharArray();
                                          text[MNU_LIST_ITEM_TAB_I]="tabIndex='".toCharArray();/*tabindex */text[MNU_LIST_ITEM_TAB_I+1]="' ".toCharArray();
                                          text[MNU_LIST_ITEM_ONCLICK]=" onclick=\"".toCharArray();/*action onclick */ text[MNU_LIST_ITEM_ONCLICK+1]="\" ".toCharArray();
                                          text[MNU_LIST_ITEM +1]=" >".toCharArray();
                                          text[MNU_LIST_ITEM +2]="<td>&nbsp;</td><td class='mnuItm'>".toCharArray();
                                             //<u>S</u>ave
                                          text[MNU_LIST_ITEM_END]="</td></tr>".toCharArray();

/*
                <tr menu="subnewSub">
                <td>&nbsp;</td>
                <td class="mnuItm" noWrap><span class="mnuSubItem">New</span><img src="templates/menu/std/mnu_rArrow.gif" align="top" WIDTH="16" HEIGHT="13"></td></tr>
                */
                                          text[MNU_LIST_ITEMWS]="<tr ".toCharArray(); 
                                          text[MNU_LIST_ITEMWS_AK]=" accessKey='".toCharArray(); /*S*/text[MNU_LIST_ITEMWS_AK+1]="' ".toCharArray();
                                          text[MNU_LIST_ITEMWS_TAB_I]="tabIndex='".toCharArray();/*tabindex */text[MNU_LIST_ITEMWS_TAB_I+1]="' ".toCharArray();
                                          text[MNU_LIST_ITEMWS_ID_SUB]=" menu='".toCharArray();/*idSubmneu mnulist */ text[MNU_LIST_ITEMWS_ID_SUB+1]="' ".toCharArray();
                                          text[MNU_LIST_ITEMWS_ONCLICK]=" onclick=\"".toCharArray();/*action onclick */ text[MNU_LIST_ITEMWS_ONCLICK+1]="\" ".toCharArray();
                                          text[MNU_LIST_ITEMWS +1]=" ><td>&nbsp;</td><td class='mnuItm' noWrap='1'><span class='mnuSubItem'>".toCharArray();
                                             //New
                                          text[MNU_LIST_ITEMWS_END]="</span><img src='templates/menu/std/mnu_rArrow.gif' align='top' WIDTH='16' HEIGHT='13'/></td></tr>".toCharArray();
                
                                    text[MNU_LIST_END ]="</tbody></table>".toCharArray();
                                  text[MNU_FLAT_ITEM_END]="</span>".toCharArray();


          text[MNU_FLAT_END]="</td>".toCharArray();

          text[MNU_FLAT_TD_TITLE]="<td class='mnuTitle mnuRight' id='tdTitle' noWrap='1'>".toCharArray();
         // aqui é o titulo á direita do menu
          text[MNU_FLAT_TD_TITLE_END]="</td>".toCharArray();

          
          text[MNU_FLAT_END+1]="</tr></tbody></table>".toCharArray();

        
          /*
           * menu á direita
			<TD class="icMenu mnuRight" noWrap>
				<SPAN class=menu 
					title="Create a new Activity" 
					onclick="openStdDlg('/Activities/dlg_create.aspx',window, 350, 300);" 
					tabIndex=0><IMG class=mnuBtn 
					src="resources/activity/ico16.gif"> New 
					Activity</SPAN>
            </TD>

            */
            

/*
   <table class="mnubar" id="mnuBar2" cellSpacing="0" cellPadding="0">
        <tbody>
        <tr>
          <td width="9"><img hspace="3" src="templates/menu/std/mnu_vSpacer.gif" WIDTH="5" HEIGHT="18"></td>
          <td class="icMenu" noWrap>

			<span class="menuFlat" title="Save" onclick="crmForm.Save();" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_save.gif" WIDTH="16" HEIGHT="16"> Save</span>
			<span class="menuFlat" title="Save and Close" onclick="crmForm.SaveAndClose();" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_saveClose.gif" WIDTH="16" HEIGHT="16"> </span>
			<span class="menuFlat" title="Print..." onclick="crmForm.Print();" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_print.gif" WIDTH="16" HEIGHT="16"></span>
			<img src="templates/menu/std/mnu_hSpacer.gif" WIDTH="2" HEIGHT="17">
			<span class="menuFlat" title="Forward" onclick="forward();" tabIndex="0">Forward</span>
			<span class="menuFlat" title="Reply" onclick="reply();" tabIndex="0">Reply</span>
			<span class="menuFlat" title="Reply All" onclick="replyall();" tabIndex="0">Reply All</span>

		  </td>
		</tr></tbody></table>

        */




      text[ MNU_BAR  ]="<table class='mnubar' id='mnuBar2' cellSpacing='0' cellPadding='0'>".toCharArray();
      text[ MNU_BAR+1]="<tbody><tr><td width='9'><img hspace='3' src='templates/menu/std/mnu_vSpacer.gif' WIDTH='5' HEIGHT='18'/></td><td class='icMenu' noWrap='1'>".toCharArray();
//<span class="menuFlat" title="Save" onclick="crmForm.Save();" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_save.gif" WIDTH="16" HEIGHT="16"> Save</span>
            text[ MNU_BAR_ITEMWI ]="<span class='menuFlat' ".toCharArray();
            text[ MNU_BAR_ITEMWI_TITLE   ]="title='".toCharArray();/* title*/text[ MNU_BAR_ITEMWI_TITLE+1 ]="' ".toCharArray();
            text[ MNU_BAR_ITEMWI_ONCLICK ]="onclick=\"".toCharArray();/* onclick*/text[ MNU_BAR_ITEMWI_ONCLICK+1 ]="\" ".toCharArray();
            text[ MNU_BAR_ITEMWI_TAB_I   ]="tabindex='".toCharArray();/* onclick*/text[ MNU_BAR_ITEMWI_TAB_I+1 ]="' ".toCharArray();
            text[ MNU_BAR_ITEMWI_IMG     ]="><img class='mnuBtn' src='templates/menu/std/".toCharArray();/*img name*/text[ MNU_BAR_ITEMWI_IMG+1 ]="' width='16' height='16'/> ".toCharArray();
            /*text*/
            text[ MNU_BAR_ITEMWI_END]="</span>".toCharArray();
//<img src="templates/menu/std/mnu_hSpacer.gif" WIDTH="2" HEIGHT="17">
            text[ MNU_BAR_ITEM_SPACER]="<img src='templates/menu/std/mnu_hSpacer.gif' WIDTH='2' HEIGHT='17'/>".toCharArray();
//<span class="menuFlat" title="Reply All" onclick="replyall();" tabIndex="0">Reply All</span>
            text[ MNU_BAR_ITEM ]="<span class='menuFlat' ".toCharArray();
            text[ MNU_BAR_ITEM_TITLE   ]="title='".toCharArray();/* title*/text[ MNU_BAR_ITEM_TITLE+1 ]="' ".toCharArray();
            text[ MNU_BAR_ITEM_ONCLICK ]="onclick=\"".toCharArray();/* onclick*/text[ MNU_BAR_ITEM_ONCLICK+1 ]="\" ".toCharArray();
            text[ MNU_BAR_ITEM_TAB_I   ]="tabindex='".toCharArray();/* onclick*/text[ MNU_BAR_ITEM_TAB_I+1 ]="'> ".toCharArray();
            /*text*/
            text[ MNU_BAR_ITEM_END]="</span>".toCharArray();
            

      
      text[ MNU_BAR_END ]="</td></tr></tbody></table>".toCharArray();
    
      
      text[TB_CONT_END]="<div class='barInterval'></div></td></tr></tbody></table>".toCharArray();

        
    
        }
    }
}