package netgest.bo.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import netgest.bo.builder.boBuilder;
import netgest.bo.builder.boBuilderOptions;
import netgest.bo.builder.boBuilderProgress;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;
import netgest.bo.utils.IProfileUtils;

public class XEOBuilderFilter implements Filter {

	public static boolean developmentMode=false;
	
	public static boolean running=false;
	private boolean autologin=false;
	public String autologinUser="SYSUSER";
	public String autologinprofile="";
	
	
	boolean buildSucess=true;
	HttpServletRequest request;
	HttpServletResponse response;
	
	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		this.request = (HttpServletRequest)request;
		this.response = (HttpServletResponse)response;
		boSession session = this.getBoSession();
	
		if (autologin && session==null)
		{
			 EboContext ctx = null;
			 boApplication bapp = boApplication
						.getApplicationFromStaticContext("XEO");
					 bapp.suspendAgents();
					try {
						session = bapp.boLogin(autologinUser, boLoginBean
							.getSystemKey());
						this.getHttpSession(true).setAttribute("boSession", session);
						
						if (this.autologinprofile!=null && !this.autologinprofile.equals(""))
						{
							ctx=session.createRequestContext(null, null, null);
							
							boObject profile=IProfileUtils.getProfileByName(ctx, this.autologinprofile);
							if (profile.exists())
								session.setPerformerIProfileBoui(Long.toString(profile.getBoui()));
						}
					} catch (boLoginException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (boRuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					finally {
						if (ctx!=null) ctx.close();
					}
		}
		if (developmentMode)
		{
			long builderLastRun=boBuilder.getXEOStudioBuilderLastRun();
			if (builderLastRun>0 && !running)
			{
				if (builderLastRun==1000 && isValidRequestForFullBuild())
				{
					fullBuild();
					return;
				}
				else if (builderLastRun!=1000)
				{
					partialBuild();
				}
			}
		}		
		if (buildSucess)
			chain.doFilter(request, response);
	}

	public void init(FilterConfig config) throws ServletException {
		
		String xeodev=System.getProperty("xeo.development");
		String alogin=System.getProperty("xeo.autologin");
		this.autologinUser=System.getProperty("xeo.autologinuser")!=null?System.getProperty("xeo.autologinuser"):
				this.autologinUser;
		this.autologinprofile=System.getProperty("xeo.autologinprofile");
		
		if (xeodev!=null && (xeodev.equalsIgnoreCase("true") ||
				xeodev.equalsIgnoreCase("yes"))){
			developmentMode=true;
			boApplication.getDefaultApplication().setDevelopmentMode(true);								
		}
		if ((alogin!=null && (alogin.equalsIgnoreCase("true") ||
				alogin.equalsIgnoreCase("yes"))) || this.autologinUser!=null){
			this.autologin = true;							
		}

	}
	
	private void fullBuild()
	{
		try {
				running=true;
				response.sendRedirect("builderDevelopment.jsp");			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void partialBuild()
	{
		boBuilderOptions	buildOptions = new boBuilderOptions();
		 boBuilderProgress builderProgress = new boBuilderProgress();
		try 
		{
			running=true;
			boApplication bapp = boApplication
				.getApplicationFromStaticContext("XEO");
			bapp.suspendAgents();
			boSession session = null;
				session = bapp.boLogin("SYSTEM", boLoginBean
					.getSystemKey());
			EboContext ctx = session.createRequestContext(null, null, null);	
			buildOptions.setBuildDatabase(true);
			buildOptions.setIntegrateWithXEOStudioBuilder(true);
			buildOptions.setGenerateAndCompileJava(false);
			buildOptions.setBuildWorkplaces(true);
			buildOptions.setMarkDeployedObjects(true);
			System.out.println(MessageLocalizer.getMessage("XEO_STUDIO_PARTIAL_BUILD_STARTED"));
			boBuilder.buildAll( ctx, buildOptions, builderProgress );
			System.out.println(MessageLocalizer.getMessage("OK"));
		} catch (Exception e) {	
			e.printStackTrace();
			System.out.println(builderProgress.getLog());
			buildSucess=false;
		}
		finally
		{
			running = false;
		}
	}
	
	public boolean isValidRequestForFullBuild()
	{		 
		 boolean toRet=false;
		 if (request.getRequestURL().toString().indexOf("Login.xvw")>-1 ||
				 request.getRequestURL().toString().indexOf("login.jsp")>-1) 
			 toRet=true;
		 return toRet;
	}
	
	private HttpSession getHttpSession(boolean createNew) {
		HttpServletRequest req = (HttpServletRequest) this.request;
		HttpSession session = req.getSession(createNew);
		return session;
	}
	
	private boSession getBoSession() {
		HttpSession session = getHttpSession(false);
		if (session != null) {
			boSession xeoSession = (boSession) session
					.getAttribute("boSession");
			return xeoSession;
		}
		return null;
	}
}
