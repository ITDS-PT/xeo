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

import netgest.bo.builder.boBuilder;
import netgest.bo.builder.boBuilderOptions;
import netgest.bo.builder.boBuilderProgress;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;

public class XEOBuilderFilter implements Filter {

	public static boolean developmentMode=false;
	
	public static boolean running=false;
	boolean buildSucess=true;
	HttpServletRequest request;
	HttpServletResponse response;
	
	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {		
		if (developmentMode)
		{
			this.request = (HttpServletRequest)request;
			this.response = (HttpServletResponse)response;
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
		if (xeodev!=null && (xeodev.equalsIgnoreCase("true") ||
				xeodev.equalsIgnoreCase("yes"))){
			developmentMode=true;
			boApplication.getDefaultApplication().setDevelopmentMode(true);
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
		 running=true;
		 boBuilderOptions	buildOptions = new boBuilderOptions();
		 boBuilderProgress builderProgress = new boBuilderProgress();
		 boApplication bapp = boApplication
			.getApplicationFromStaticContext("XEO");
		 bapp.suspendAgents();
		 boSession session = null;
		try {
			session = bapp.boLogin("SYSTEM", boLoginBean
				.getSystemKey());
		} catch (boLoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 EboContext ctx = session.createRequestContext(null, null, null);	
		 buildOptions.setBuildDatabase(true);
		 buildOptions.setIntegrateWithXEOStudioBuilder(true);
		 buildOptions.setGenerateAndCompileJava(false);
		 buildOptions.setBuildWorkplaces(true);
		 buildOptions.setMarkDeployedObjects(true);
		 try {
			System.out.println(MessageLocalizer.getMessage("XEO_STUDIO_PARTIAL_BUILD_STARTED"));
			boBuilder.buildAll( ctx, buildOptions, builderProgress );
			System.out.println(MessageLocalizer.getMessage("OK"));
			running=false;
		} catch (Exception e) {	
			e.printStackTrace();
			System.out.println(builderProgress.getLog());
			buildSucess=false;
			running=false;
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
}
