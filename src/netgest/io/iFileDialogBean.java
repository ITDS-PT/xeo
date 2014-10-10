/*Enconding=UTF-8*/
package netgest.io;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.system.Logger;
import netgest.utils.ObjectSorter;

import com.oreilly.servlet.MultipartRequest;


public class iFileDialogBean {
    //logger
    private static Logger logger = Logger.getLogger("netgest.io.iFileDialogBean");

    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    public static final int RENDER_DOWNLOAD=2;
    public static final int RENDER_UPLOAD=3;
    public static final int RENDER_CLOSEWINDOW=4;
    public static final int RENDER_RETURNRESULT=5;
    public static final int RENDER_DIALOG=1;
    public static final int RENDER_UPLOADRESULT=6;

    public static final int LIST_SMALLICONS=1;
    public static final int LIST_THUMBNAILS=2;
    public static final int LIST_DETAILS=3;

    public static final int MODE_FILEDIALOG=1;
    public static final int MODE_EXPLORERDIALOG=2;
    public static final int MODE_SIMPLEOPEN=4;

    public static final int SORT_ASCENDING=0;
    public static final int SORT_DESCENDING=1;

    public static final int SORT_NAME=0;
    public static final int SORT_DATE=1;
    public static final int SORT_SIZE=2;


    public static final int ACTION_CHECKIN=1;
    public static final int ACTION_CHECKOUT=2;
    public static final int ACTION_DELETE=3;
    public static final int ACTION_MKDIR=4;
    public static final int ACTION_BACK=5;
    public static final int ACTION_VIEW=6;
    public static final int ACTION_DOWNLOAD=7;
    public static final int ACTION_UPLOAD=8;
    public static final int ACTION_UPLOADDIALOG=9;
    public static final int ACTION_SELECT=10;
    public static final int ACTION_UPFOLDER=11;
    public static final int ACTION_DOWNLOADICON=12;
    public static final int ACTION_RETURNRESULT=13;
    public static final int ACTION_CANCEL=14;
    public static final int ACTION_FOLDERICON=15;

    public static final String FILEUPLOAD_OK="OK";
    public static final String FILEUPLOAD_OVERWRITED="OVERWRITED";
    public static final String FILEUPLOAD_NOTOVERWRITED="NOTOVERWRITED";
    public static final String FILEUPLOAD_NOTUPLOADED="NOTOVERWRITED";

    public static final int LARGE_ICON = 1;
    public static final int SMALL_ICON = 0;


    public int action;
    public String dialogtitle="Netgest File Dialog";
    public String filter="*.*|Todos os Ficheiros";
    public String defaultfilter="*.*";

    public String path;
    public String pressedbutton;
    public String oktext="OK";

    public int dialogHeight;
    public int dialogWidth;

    public int filesPerPage;

    public iFile[] upLoadedFiles;
    public String[] upLoadedResult;

    public static final String[] ERROR_MESSAGES = {
                                    MessageLocalizer.getMessage("COULD_NOT_RESERVE_FILE"),
                                    MessageLocalizer.getMessage("COULD_NOT_SET_FILE_AVAILABLE"),
                                    MessageLocalizer.getMessage("THE_SPECIFIED_FILE_DOESNT_EXIST")
                                 };
    public static final byte ERROR_FILENOTFOUND=2;
    public static final byte ERROR_CHECKIN=1;
    public static final byte ERROR_CHECKOUT=0;

    public Vector errors;
    public iFileService currentService = null;

    private iFileServer ifs = null;
    public iFile currentFile = null;

    public int btn_definition=127;

    public int CURRPAGE=1;
    public int RENDERMODE;
    public int FILEDSPMODE=LIST_SMALLICONS;
    public int SORTFIELD=SORT_NAME;
    public int SORTDIRECTION=SORT_ASCENDING;

    public int dialogmode=MODE_EXPLORERDIALOG;
    public boolean multiselect=false;
    public boolean allowupload=true;
    public boolean allowdownload=true;
    public boolean allowdirselection=false;

    private String p_lastpath = "";
    private Vector p_history = new Vector();



    public iFileDialogBean() {
    }
    public void setActiveButtons(int arg) {
        btn_definition = arg;
    }
    public void setDialogMode(int arg) {
        this.dialogmode = arg;
    }
    public void setPage(int arg) {
        CURRPAGE = arg;
    }
    public void setView(int arg) {
        FILEDSPMODE = arg;
    }
    public void setAction(int arg) {
        action=arg;
    }
    public void setoktext(String arg) {
        oktext=arg;
    }
    public void setDialogTitle(String arg) {
        dialogtitle=arg;
    }
    public void setFilter(String arg) {
        filter=arg;
    }
    public void setDefaultFilter(String arg) {
        defaultfilter=arg;
    }
    public void setPath(String arg) {
        path=arg;
    }
    public void setPressedbutton(String arg) {
        pressedbutton = arg;
    }
    public void setMultiSelect(boolean arg) {
        multiselect = arg;
    }
    public void setAllowDownload(boolean arg) {
        allowdownload = arg;
    }
    public void setAllowDirSelection(boolean arg) {
        allowdirselection = arg;
    }
    public void setDialogHeight(int arg) {
        dialogHeight=arg;
    }
    public void setDialogWidth(int arg) {
        dialogWidth=arg;
    }
    public void setSortDirection(int arg) {
        SORTDIRECTION=arg;
    }
    public void setSortField(int arg) {
        SORTFIELD=arg;
    }
    /*
    public void ProcessRequest(HttpServletRequest request,HttpServletResponse response,ServletContext sctx) throws IOException,Exception {
        ifs = this.checkSession(request,response,sctx);
        CheckArguments();
        this.currentFile = ifs.getFile(path);
        switch(action) {
            case ACTION_CHECKIN:
                CheckIn();
                break;
            case ACTION_CHECKOUT:
                CheckOut();
                break;
            case ACTION_DELETE:
                Delete();
                break;
            case ACTION_DOWNLOAD:
                Download();
                break;
            case ACTION_DOWNLOADICON:
                getImageIcon(this.path,63,79,response,request,sctx);
                break;
            case ACTION_MKDIR:
                MkDir();
                break;
            case ACTION_BACK:
                Back();
                break;
            case ACTION_UPFOLDER:
                this.path=this.GoToUpFolder(this.path);
                break;
            case ACTION_UPLOAD:
                RENDERMODE=RENDER_UPLOAD;
                break;
            case ACTION_VIEW:
                View();
                break;
            case ACTION_SELECT:
                if( (!currentFile.isDirectory() || (currentFile.isDirectory() && this.allowdirselection)) && (this.MODE_FILEDIALOG == this.dialogmode ||this.MODE_SIMPLEOPEN == this.dialogmode))
                    this.RENDERMODE = this.RENDER_CLOSEWINDOW;
                else {
                    if(!this.currentFile.isDirectory()) this.path = this.GoToUpFolder(this.path);
                    this.RENDERMODE = iFileDialogBean.RENDER_DIALOG;
                }
                break;
            case ACTION_RETURNRESULT:
                this.RENDERMODE = this.RENDER_RETURNRESULT;
                break;
            case ACTION_CANCEL:
                this.RENDERMODE = this.RENDER_CLOSEWINDOW;;
                break;
            default:
                RENDERMODE = RENDER_DIALOG;
        }
        if(!p_lastpath.equals(this.path)) {
            CURRPAGE = 1;

            if(ACTION_BACK!=this.action)
                p_history.add(p_lastpath);

            p_lastpath = this.path;
        }
        this.currentFile = ifs.getFile(path);
        this.currentService = ifs.getFileService(path);
    }
    */
    private void CheckArguments() {
        if(path==null) {
            path = (ifs.listServices()[0]).PREFIX+(ifs.listServices()[0]).NAME;
        }
        if(this.RENDERMODE == 0 )
            this.RENDERMODE = this.RENDER_DIALOG;
    }
    /*
    public static iFileServer checkSession(HttpServletRequest request,HttpServletResponse response,ServletContext sctx) throws Exception {
        HttpSession session = request.getSession(true);
        iFileServer ifs = null;
        ifs = (iFileServer)session.getAttribute("IFS_SERVER");
        ngtUser sysuser = (ngtUser)session.getAttribute("IFS_SYSUSER");
        if(ifs == null || sysuser == null) {
            ifs = startNewSession(request,response,sctx);
        } else if (sysuser != null) {
            ngtUser xsysuser = Utils.VerifyLogin(request,response);
            if(!xsysuser.getUserCRC().equals(sysuser.getUserCRC())) {
                ifs.unmount();
                startNewSession(request,response,sctx);
            }
        }
        return ifs;
    }
    */
    /*
    private static synchronized iFileServer startNewSession(HttpServletRequest request,HttpServletResponse response,ServletContext sctx)
            throws Exception {
        HttpSession session = request.getSession(true);

        // Check if another request was already created a ifs session


        // TODO: EboContext
        iFileServer ifs = null;
        ifs = (iFileServer)session.getAttribute("IFS_SERVER");
        if(ifs==null) {
            ngtUser sysuser = null;
            EboContext ngtctx = null;
            sysuser = Utils.VerifyLogin(request,response);
            if(sysuser!=null && sysuser.getIsLogedIn()) {
//                ngtctx = new ngtContext(sysuser);
                ifs = new iFileServer();
//                ifs.mount(ngtctx);
                session.setAttribute("IFS_SERVER",ifs);
            } else {
                ifs = (iFileServer)sctx.getAttribute("IFS_SERVER");
                if(ifs==null) {
                    ifs = new iFileServer();
                    ifs.mount();
                    sctx.setAttribute("IFS_SERVER",ifs);
                }
            }
            session.setAttribute("IFS_SYSUSER",sysuser);
        }
        return ifs;
    }
    */
    private String getFileTypes() {
        StringBuffer sb=new StringBuffer(1000);
        String fileTypes=filter;
        String sep="|";
        if (null!=fileTypes && fileTypes.length()>0) {
            int start=0;
            int end=0;
            String text="";
            String value="";
            String defaultext=this.defaultfilter;
            if (null!=defaultext && defaultext.length()>0) {
            } else {
                defaultext="*.*";
            }
            while (end>-1) {
                end=fileTypes.indexOf(sep,start);
                text=fileTypes.substring(start,end);
                start=end+1;
                end=fileTypes.indexOf(sep,start);
                if (end==-1)
                    value=fileTypes.substring(start);
                else
                    value=fileTypes.substring(start,end);
                if (defaultext.equals(value))
                    sb.append("<option value='"+value+"' selected='selected'>"+text+"</option>");
                else
                    sb.append("<option value='"+value+"'>"+text+"</option>");
                start=end+1;
            }
        } else {
            sb.append("<option value='*.*' selected='selected'>Todos os ficheiros (*.*)</option>");
        }
        return sb.toString();
   }
   public iFileService[] getServices() {
       return ifs.listServices();
   }
   public String getScript() {
        return "";
  }
    private void addToHistory(Vector hist, String path) {
      if (hist.size()>0) {
        if (!hist.get(hist.size()-1).equals(path)) {
          hist.add(hist.size(),path);
        }
      } else {
        hist.add(hist.size(),path);
      }
    }
    private String removeFromHistory(Vector hist) {
      String retval=null;
      if (hist.size()>1) {
        hist.remove(hist.size()-1);
      }
      if (hist.size()>0) {
        retval=(String)hist.get(hist.size()-1);
      }
      return retval;
    }
   /*
   public void getImageIcon(String ifilepath ,int height,int width,HttpServletResponse response,HttpServletRequest request,ServletContext sctx) {
        try {
            ifs = this.checkSession(request,response,sctx);
            iFile ifile = ifs.getFile(ifilepath);
            long dtlastd = request.getDateHeader("If-Modified-Since");
            if ( Math.abs(ifile.lastModified() - dtlastd) < 500 ) {
                response.sendError(response.SC_NOT_MODIFIED,"Not modififed");
                return;
            }
            String[] enctypes = Jimi.getDecoderTypes();


            InputStream is = ifile.getInputStream();
            Image ximg = Jimi.getImage(is,sctx.getMimeType(ifile.getName().toLowerCase()),Jimi.SYNCHRONOUS);
            iFileImageObserver imgo = new iFileImageObserver();
            int difh = ximg.getHeight(imgo);
            int timetoload = 100;
            int time=0;
            boolean error = false;
            while(timetoload > time && !imgo.error && imgo.p_height == -1) {
                time += 5;
                Thread.sleep(5);
            }

            if(timetoload < time || imgo.error) {
                error = true;
            }
            if(!error) {
                difh = imgo.p_height-height;
                ximg.getWidth(imgo);

                time=0;
                while(timetoload > time && !imgo.error && imgo.p_width == -1) {
                    time += 5;
                    Thread.sleep(5);
                }
                int difw = imgo.p_width-width;
                int resizeh = -1;
                int resizew = -1;
                if(difh > difw) {
                    resizeh = height;
                } else {
                    resizew = width;
                }
                if(difw>0 || difh > 0) {
                    Image ximg1 = ximg.getScaledInstance(resizew,resizeh,Image.SCALE_FAST);
                    ximg.flush();
                    ximg = ximg1;
                    ximg1=null;
                }
                response.setDateHeader("Last-Modified",ifile.lastModified());
                response.setHeader("Cache-Control","public");
                try {
                    Jimi.putImage("image/png",ximg,response.getOutputStream());
                } catch (IOException e) {
                    logger.finer(path + " - User abort download" );
                }
                ximg.flush();
                is.close();
                ximg = null;
                is = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        RENDERMODE = RENDER_DOWNLOAD;
   }
   */
   /*
   public static void getFolderIcon(ServletContext context,HttpServletRequest request,HttpServletResponse response,String path) {
        try {
            iFileServer ifs = checkSession(request,response,context);
            iFile file = ifs.getFile(path);
            if(file != null && file.isDirectory()) {
                String fname;
                String[] files = file.list();
                int length;
                if(files==null)
                    length = 0;
                else
                    length = files.length;

                switch (length) {
                    case 0:
                        fname="folder_0.gif";
                        break;
                    case 1:
                        fname="folder_1.gif";
                        break;
                    case 2:
                        fname="folder_2.gif";
                        break;
                    case 3:
                        fname="folder_3.gif";
                        break;
                    case 4:
                        fname="folder_4.gif";
                        break;
                    case 5:
                        fname="folder_5.gif";
                        break;
                    case 6:
                        fname="folder_6.gif";
                        break;
                    case 7:
                        fname="folder_7.gif";
                        break;
                    case 8:
                        fname="folder_8.gif";
                        break;
                    case 9:
                        fname="folder_9.gif";
                        break;
                    default:
                        fname="folder_10.gif";
                        break;
                }
                String imgpath = context.getRealPath("/ngtifs/images/");
                if (!imgpath.endsWith(File.separator)) {
                    imgpath += File.separator;
                }
                imgpath += fname;
                File ximgfile = new File(imgpath);
                response.setContentType(context.getMimeType(fname.toLowerCase()));
//                response.setContentLength((int)ximgfile.length());
                ServletOutputStream os=response.getOutputStream();
                FileInputStream fis = new FileInputStream(ximgfile);
                byte[] buff = new byte[4096];
                int rbytes;
                while ((rbytes=fis.read(buff))>0)
                    os.write(buff);

                fis.close();
            } else {
                response.sendError(response.SC_NOT_FOUND,path + " Not Found ");
            }
        } catch (Exception e) {
            e.fillInStackTrace();
            throw new RuntimeException("iFileDialogBean:getFolderIcon:"+e.getClass().getName()+" Error:\n"+e.getMessage());
        }
   }

   */
   /*
   public static void downloadFile(ServletContext context,HttpServletRequest request,HttpServletResponse response,String path, boolean display)
        throws IOException,Exception {
        iFileServer ifs = checkSession(request,response,context);
        iFile file = ifs.getFile(path);
        try {
            if (file.exists() && file.isFile()) {
                long dtlastd = request.getDateHeader("If-Modified-Since");
                  if ( file.lastModified() - dtlastd < 500 ) {
                    response.sendError(response.SC_NOT_MODIFIED,"Not modififed");
                } else {
                    response.setDateHeader("Last-Modified",file.lastModified() );
                    response.setHeader("Cache-Control","public");

                    String bfilename = file.getName();
                    String mimetype = context.getMimeType(bfilename.toLowerCase());

                    Long FileSize = new Long(file.length());
                    int xfsize = FileSize.intValue();

                    response.setContentType(mimetype);
                    response.setContentLength(xfsize);

                    if (!display)
                        response.setHeader("Content-Disposition","attachment; filename=\""+bfilename+"\"");

                    int rb=0;
                    ServletOutputStream so = response.getOutputStream();

                    try {
                        InputStream is=file.getInputStream();
                        byte[] a=new byte[4*1024];
                        while ((rb=is.read(a)) > 0) {
                            so.write(a,0,rb);
                        }
                        is.close();
                    } catch (Exception e) {
                    }

                    so.close();
                }
            } else {
                response.sendError(response.SC_NOT_FOUND);
                response.flushBuffer();
            }
        }
        catch (Exception e) {
            PrintWriter out = response.getWriter();
            out.print(e.getMessage());
            e.printStackTrace(out);
        }

   }
   */
   private void CheckIn() {
       try {
           currentFile.checkIn();
           RENDERMODE = RENDER_DIALOG;
       } catch (Exception e) {
           addErrorMessage(ERROR_MESSAGES[0]);
       }
       this.path = GoToUpFolder(this.path);
   }
   private void CheckOut() {
       try {
           currentFile.checkOut();
           RENDERMODE = RENDER_DIALOG;
       } catch (Exception e) {
           addErrorMessage(ERROR_MESSAGES[1]);
       }
       this.path = GoToUpFolder(this.path);
   }
   private void Delete() {
       try {
            if(currentFile.exists()) {
               path = GoToUpFolder(path);
               currentFile.delete();
               RENDERMODE = RENDER_DIALOG;
            } else {
                addErrorMessage(ERROR_MESSAGES[ERROR_FILENOTFOUND]);
            }
       } catch (Exception e) {
           addErrorMessage(ERROR_MESSAGES[2]);
       }
   }
   private void Download() {
       RENDERMODE = RENDER_DOWNLOAD;
   }
   private void View() {
       RENDERMODE = RENDER_DOWNLOAD;
   }
   private void MkDir() {
       try {
           currentFile.mkdir();
           RENDERMODE = RENDER_DIALOG;
           this.path = GoToUpFolder(this.path);
       } catch (Exception e) {
           addErrorMessage(ERROR_MESSAGES[2]);
       }
   }
   private void Back() {
       RENDERMODE = RENDER_DIALOG;
       if(p_history.size()>0) {
           String npath=(String)p_history.remove(p_history.size()-1);
           if(npath != null && npath.length()>0)
                this.path=npath;
       }
   }
   public void UpLoad(HttpServletRequest request) {
       RENDERMODE = RENDER_UPLOADRESULT;
       try {
           Vector upfiles   = new Vector();
           Vector upresults = new Vector();
           MultipartRequest mr=new MultipartRequest(request,"./",128*1024*1024);
           String path=mr.getParameter("path");
           Enumeration oEnum=mr.getFileNames();
           while(oEnum.hasMoreElements()) {
                String filename = (String)oEnum.nextElement();
                if(filename.startsWith("FILE_")) {
                    String idx = filename.substring(5,filename.length());
                    boolean overwrite = mr.getParameter("OVER_"+idx)!=null && mr.getParameter("OVER_"+idx).equals("Y")?true:false;
                    boolean versioned = mr.getParameter("VERS_"+idx)!=null && mr.getParameter("VERS_"+idx).equals("Y")?true:false;
                    if(filename!=null && filename.length()>0) {
                        String fname=mr.getFilesystemName(filename);
                        if(fname != null) {
                            iFile ifile=ifs.getFile(path+(path.endsWith("/")?"":"/")+fname);
                            try {
                                if(!ifile.exists() || overwrite) {
                                    boolean exists = ifile.exists();
                                    if(exists) {
                                       ifile.delete();
                                    }
                                    ifile.createNewFile();
                                    
                                    InputStream  is = new FileInputStream("./"+fname);
                                    ifile.setBinaryStream( is );
                                    is.close();
                                    if(versioned) {
                                       ifile.makeVersioned();
                                    }
                                    if(exists) {
                                       upresults.add(FILEUPLOAD_OVERWRITED);
                                    } else {
                                       upresults.add(FILEUPLOAD_OK);
                                    }

                                } else {
                                   upresults.add(FILEUPLOAD_NOTOVERWRITED);
                                }
                                File xf = new File("./"+fname);
                                xf.delete();
                            } catch(Exception e) {
                                addErrorMessage(e.getMessage());
                                upresults.add(e.getMessage());
                            }
                            upfiles.add(ifs.getFile(path+"/"+fname));
                        }
                    }
                }
           }
           upLoadedFiles = (iFile[])upfiles.toArray(new iFile[0]);
           upLoadedResult = (String[])upresults.toArray(new String[0]);
       } catch (IOException e) {
           throw new RuntimeException(e.getMessage());
       }
       RENDERMODE = RENDER_UPLOADRESULT;
   }
   private void addErrorMessage(String message) {
       if(errors == null) errors = new Vector();
       errors.add(message);
   }
   public iFile[] listPage(iFile[] pagefiles) {
        if(pagefiles==null)
            pagefiles = listFiles();
        if(pagefiles != null) {
            int filestolist = Math.min(pagefiles.length,(CURRPAGE*filesPerPage))-((CURRPAGE-1)*(filesPerPage));
            int ini = (CURRPAGE-1)*(filesPerPage);
            iFile[] files = new iFile[filestolist];
            for(int i=0;i<(filestolist);i++) {
                files[i] = pagefiles[i+ini];
            }
            return files;
        }
        return pagefiles;
   }
   public iFile[] listFiles() {
       try {
            iFile[] files =  currentFile.listFiles();
            if(files!=null) {
                sortObject xsobj = new sortObject();
                xsobj.sortfield = this.SORTFIELD;
                xsobj.sortdir = this.SORTDIRECTION;
                ObjectSorter.sort(files,xsobj);
            }
            return files;
       } catch (Exception e) {
           addErrorMessage(ERROR_MESSAGES[ERROR_FILENOTFOUND]);
       }
       return null;
   }
   public String getFileImage(iFile file,int size) {
        if(size == LARGE_ICON) {
            if(file.isDirectory())
                return "folder_large.gif";
            else
                return "uknfile_large.gif";
        } else if (size== SMALL_ICON) {
            if(file.isDirectory())
                return "folder.gif";
            else
                return "uknfile.gif";
        }
        return "";
   }
   public static String GoToUpFolder(String path) {
       if(path.substring(2).endsWith("/")) {
           path = path.substring(0,path.length()-1);
       }
       if(path.lastIndexOf("/")>1) {
           path = path.substring(0,path.lastIndexOf("/"));
       }
       return path;
   }
   private class iFileImageObserver implements java.awt.image.ImageObserver {
       public int p_height=-1;
       public int p_width=-1;
       public boolean error = false;
       public void resetObject() {
            p_height=-1;
            p_width=-1;
            error = false;
       }
       public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            if ((infoflags & ERROR) == ImageObserver.ERROR) {
                error = true;
            } if((infoflags & HEIGHT) == HEIGHT) {
                p_height = height;
            }
            if((infoflags & WIDTH) == WIDTH) {
                p_width = width;
            }
            return p_height==-1 && p_width==-1;
       }
   }

   public String[] getIndexList(iFile[] files) {
        this.filesPerPage = this.RENDERMODE==this.LIST_THUMBNAILS?50:100;
        int stop = this.filesPerPage;

        if(files == null || files.length <= stop) return new String[0];

        int nrpages = (files.length%stop==0)?files.length/stop:files.length/stop+1;
        String[] idxlst = new String[nrpages];
        int i;
        String lastword = null;
        for(i=1;i<nrpages;i++) {
            String iniword;
            if(i==1) {
                iniword = compare(files[i*stop-stop].getName(),files[i*stop-stop+1].getName());
            } else {
                iniword = compare(files[i*stop-stop].getName(),files[i*stop-stop-1].getName());
            }
            String fimword = compare(files[i*stop-1].getName(),files[i*stop].getName());
            idxlst[i-1] = SORT_NAME==SORTFIELD?"de&nbsp;" + iniword.toUpperCase() + "&nbsp;a&nbsp;" + fimword.toUpperCase() :""+i;
        }
        if((i*stop-stop)<files.length) {
            String iniword,fimword;
            if(files.length-1 == (i*stop-stop)) {
                iniword = compare(files[i*stop-stop].getName(),files[i*stop-stop].getName());
                fimword = compare(files[files.length-1].getName(),files[files.length-1].getName());
            } else {
                iniword = compare(files[i*stop-stop].getName(),files[i*stop-stop+1].getName());
                fimword = compare(files[files.length-1].getName(),files[files.length-2].getName());
            }
            idxlst[i-1] = SORT_NAME==SORTFIELD?"de " + iniword.toUpperCase() + " a " + fimword.toUpperCase():""+i;
        }
        return idxlst;
   }


   public static String compare(String ini,String end) {
        ini = ini.toLowerCase();
        end = end.toLowerCase();
        String iniword = "";
        String fimword = "";
        int maxchars = Math.min(ini.length(),end.length());
        boolean brk = false;
        for(int i=0;i<maxchars;i++) {
            if(ini.charAt(i) != end.charAt(i)) {
              brk = true;
            }
            iniword += ini.charAt(i);
            fimword += end.charAt(i);
            if(brk) break;
        }
        //logger.finest(iniword + " - " + fimword.toLowerCase());
        return iniword;
   }
   public String getReturnResult() {
        String ret="";;
        ret+="<iFileReturn>";
        ret+="    <path>"+this.path+"</path>";
        ret+="    <button>"+this.pressedbutton+"</button>";
        ret+="    <isdir>"+this.currentFile.isDirectory()+"</isdir>";
        ret+="    <filename>"+this.currentFile.getName()+"</filename>";
        ret+="    <cancel>"+!(this.pressedbutton !=null && (this.pressedbutton.equals("ok") || this.pressedbutton.equals("dblclick")))+"</cancel>";
        ret+="</iFileReturn>";

        return ret;
   }
   private class sortObject implements ObjectSorter.Comparer {
        public int sortfield;
        public int sortdir;
        public long compare(Object obj,Object obj2) {
            long ret;
            if(((iFile)obj).isDirectory() && ((iFile)obj2).isDirectory())
                if(sortfield==iFileDialogBean.SORT_DATE) {
                    ret = ((iFile)obj).lastModified()-((iFile)obj2).lastModified();
                } else {
                    ret = ((iFile)obj).getName().toLowerCase().compareTo(((iFile)obj2).getName().toLowerCase());
                }
            else if (((iFile)obj).isFile() && ((iFile)obj2).isFile())
                if(sortfield==iFileDialogBean.SORT_DATE) {
                    ret = ((iFile)obj).lastModified()-((iFile)obj2).lastModified();
                } else if (sortfield==iFileDialogBean.SORT_SIZE) {
                    ret = ((iFile)obj).length() - ((iFile)obj2).length();
                } else {
                    ret = ((iFile)obj).getName().toLowerCase().compareTo(((iFile)obj2).getName().toLowerCase());
                }
            else if (((iFile)obj).isFile() && ((iFile)obj2).isDirectory())
                return 1;
            else if (((iFile)obj).isDirectory() && ((iFile)obj2).isFile())
                return -1;
            else
                ret = 1;
            return sortdir==iFileDialogBean.SORT_ASCENDING?ret:ret*-1;
        }
    }
}
