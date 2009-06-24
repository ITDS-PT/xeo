<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="netgest.io.*"%>
<%@ page import="java.awt.Image"%>
<%@ page import="com.sun.jimi.core.Jimi"%>
<%@ page import="java.awt.image.ImageObserver"%>
<%!
	public static boSession bosession;
%>
<%
EboContext ctx=null;

String xid = request.getParameter("id");

if ( xid != null && !xid.equals("") )
{
    try
    { 
        long b = Long.parseLong(xid);
        
        if( bosession == null )
        {
			boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");
            bosession = boapp.boLogin("SYSTEM", boLoginBean.getSystemKey());
        }
        
        ctx = bosession.createRequestContext(request,response,pageContext);
        
        boObject imagem = null;   
        imagem = boObject.getBoManager().loadObject(ctx,b);
        
        if (imagem!=null && imagem.exists())
        {
            long slf = imagem.getAttribute("SYS_DTSAVE").getValueDate().getTime();
            long clf = request.getDateHeader("If-Modified-Since");
            
            if( ( slf - clf ) > 1500 )
            {
                netgest.io.iFile file = imagem.getAttribute( "file" ).getValueiFile();
                ServletContext context = pageContext.getServletContext();
                
                if ( file.exists() && file.isFile() ) 
                {                  
                    String bfilename = file.getName(); 
                    String mimetype = context.getMimeType(bfilename.toLowerCase());
                    Long FileSize = new Long(file.length()); 
                    int xfsize = FileSize.intValue(); 
    
    			  	response.setDateHeader("Last-Modified", slf );
                	response.setHeader("Cache-Control","public");
                	
                    int rb=0; 
                    ServletOutputStream so = response.getOutputStream();
                    
                    try 
                    {
                        InputStream is=file.getInputStream();
                        String height = request.getParameter("height");
                        String width = request.getParameter("width");
                        
                        if( height != null && width != null )
                        {
                    	    response.setContentType("image/jpeg"); 
                            ConvertImage.resize( is, so, mimetype, Integer.parseInt( height ), Integer.parseInt( width ) );
                        }
                        else
                        {
                    	    response.setContentType(mimetype); 
                    	    response.setContentLength(xfsize); 
                            byte[] a=new byte[4*1024];
                            
                            while ((rb=is.read(a)) > 0) 
                            { 
                                so.write(a,0,rb); 
                            } 
                        }
                        
                        is.close();
                    } 
                    catch (Exception e) 
                    {
                    	throw e;
                    }
                    
                    so.close();                            
                }
            }
            else
            {
                response.sendError( HttpServletResponse.SC_NOT_MODIFIED, "Not Modified" );
            }
        }
    }
    catch (Exception e) 
    {
        e.printStackTrace();
        throw e;
    }
    finally
    {
    	if(ctx != null)
        	ctx.close();
	}
}
%>
<%!

public static class ConvertImage 
{
   public static final void resize(InputStream imgInp, OutputStream imgOut ,String mimetype,int height,int width ) throws Exception
   {
        try
        {
            InputStream is = imgInp;
            Image ximg = Jimi.getImage(is,mimetype,Jimi.SYNCHRONOUS);
            iFileImageObserver imgo = new iFileImageObserver();
            
            int difh = ximg.getHeight(imgo);
            int timetoload = 100;
            int time=0;
            boolean error = false;
            
            while(timetoload > time && !imgo.error && imgo.p_height == -1)
            {
                time += 5;
                Thread.sleep(5);
            }

            if(timetoload < time || imgo.error)
            {
                error = true;
            }
            
            if(!error) 
			{
                difh = imgo.p_height-height;
                ximg.getWidth(imgo);
            
                time=0;
                
                while(timetoload > time && !imgo.error && imgo.p_width == -1)
                {
                    time += 5;
                    Thread.sleep(5);
                }
                
                int difw = imgo.p_width-width;
                int resizeh = -1;
                int resizew = -1;
                
                if(difh > difw)
                    resizeh = height;
                else
                    resizew = width;
                
                if(difw>0 || difh > 0)
                {
                    Image ximg1 = ximg.getScaledInstance(resizew,resizeh,Image.SCALE_FAST);
                    ximg.flush();
                    ximg = ximg1;
                    ximg1=null;
                }
                
                Jimi.putImage("image/jpeg",ximg,imgOut);
                ximg.flush();
                imgOut.flush();
                is.close();
                ximg = null;
                is = null;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
//        RENDERMODE = RENDER_DOWNLOAD;
   }
   
   private static class iFileImageObserver implements java.awt.image.ImageObserver
   {
       public int p_height=-1;
       public int p_width=-1;
       public boolean error = false;
       
       public void resetObject()
       {
            p_height=-1;
            p_width=-1;
            error = false;
       }
       
       public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
       {
            if ((infoflags & ERROR) == ImageObserver.ERROR)
                error = true;
            
            if((infoflags & HEIGHT) == HEIGHT)
                p_height = height;
            
            if((infoflags & WIDTH) == WIDTH)
                p_width = width;

            return p_height==-1 && p_width==-1;
       }
   }
}
%>