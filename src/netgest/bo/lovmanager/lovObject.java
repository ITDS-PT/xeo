/*Enconding=UTF-8*/
package netgest.bo.lovmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import netgest.bo.def.v2.boDefHandlerImpl;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.boApplication;
import netgest.bo.system.boContext;
import netgest.bo.system.boSessionUser;
import netgest.utils.StringUtils;

import com.ibm.regex.Match;
import com.ibm.regex.RegularExpression;

public class lovObject {
	private ArrayList p_lov_cod = new ArrayList();
	private ArrayList p_lov_description = new ArrayList();
	private int p_pointer = -1;
	private int p_count = 0;
	private long p_lovboui = -1;
	private String p_name = "";
	private String p_language = null;
	//////////
	
	public lovObject(EboContext ctx, String name, String[] onlyThisValues)
			throws boRuntimeException {
		
		p_name = name;
		boObject lov;
		lov = boObject.getBoManager().loadObject(ctx, "Ebo_LOV",
				"name='" + name + "'");

		p_language = lov.getAttribute("lang").getValueString();
		
		if (lov.exists()) {
			p_lovboui = lov.getBoui();
			bridgeHandler lovdetails = lov.getBridge("details");

			
			lovdetails.beforeFirst();

			// Ebo_LOVDetails det;
			boObject details;

			if (lovdetails.getRowCount() > 0) {
				

				while (lovdetails.next()) {
					details = lovdetails.getObject();
					String xcod = details.getAttribute("value").getValueString();
					String xlabel = details.getAttribute("description")
							.getValueString();
					boolean toAdd = true;

					if (onlyThisValues != null) {
						toAdd = false;

						for (int i = 0; (i < onlyThisValues.length) && !toAdd; i++) {
							if (xcod.equals(onlyThisValues[i])) {
								toAdd = true;
							}
						}
					}

					if (toAdd) {
						add(xcod, xlabel);
					}
					/////////////////
					if(this.p_language==null || (this.p_language != null && p_language.length() == 0))
						this.p_language=boApplication.getDefaultApplication().getApplicationLanguage();
					
					/////////
				}
			}
		}
	}

	// psantos ini
	public lovObject(EboContext ctx, long lovBoui, String name, String sql,
			String field_description, String field_cod,
			Hashtable queryParameters, String[] onlyThisValues) {
		PreparedStatement pstm = null;
		ResultSet rslt = null;
		Connection cn = null;
		///////////////
		if (this.p_language==null)
			this.p_language=boApplication.getDefaultApplication().getApplicationLanguage();
		
		////////////
		try {
			p_lovboui = lovBoui;
			cn = ctx.getConnectionData();
			RegularExpression regex = new RegularExpression(
					"(:([a-zA-Z0-9_$]+))");
			ArrayList values = new ArrayList();
			Match match = new Match();
			while (regex.matches(sql, match)) {
				String parName = match.getCapturedText(2).toUpperCase();
				values.add(queryParameters.get(parName));
				sql = sql.substring(0, match.getBeginning(1)) + "?"
						+ sql.substring(match.getEnd(1));
			}
			pstm = cn.prepareStatement(sql);
			for (int i = 0; i < values.size(); i++) {
				if (values.get(i) == null) {
					pstm.setString(i + 1, null);
				} else {
					pstm.setObject(i + 1, values.get(i));
				}
			}
			rslt = pstm.executeQuery();
			while (rslt.next()) {
				Object o = rslt.getObject(field_cod);
				String xcod = "";
				if (o != null) {
					xcod = o.toString();
				}
				String[] fieldsDescription = field_description.split(",");
				StringBuffer str = new StringBuffer();

				for (int i = 0; i < fieldsDescription.length; i++) {
					str.append(rslt.getObject(field_description)).append(" ");
				}

				boolean toAdd = true;

				if (onlyThisValues != null) {
					toAdd = false;

					for (int i = 0; (i < onlyThisValues.length) && !toAdd; i++) {
						if (xcod.equals(onlyThisValues[i])) {
							toAdd = true;
						}
					}
				}

				if (toAdd) {
					add(xcod, str.toString(), true);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				rslt.close();
				pstm.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
			}
		}

	}

	public lovObject(EboContext ctx, long lovBoui, String name, String sql,
			String field_description, String field_cod,

			String[] onlyThisValues) {
		PreparedStatement pstm = null;
		ResultSet rslt = null;
		Connection cn = null;
		try {
			p_lovboui = lovBoui;
			cn = ctx.getConnectionData();
			RegularExpression regex = new RegularExpression(
					"(:([a-zA-Z0-9_$]+))");
			Match match = new Match();

			pstm = cn.prepareStatement(sql);
			rslt = pstm.executeQuery();
			while (rslt.next()) {
				Object o = rslt.getObject(field_cod);
				String xcod = "";
				if (o != null) {
					xcod = o.toString();
				}
				String[] fieldsDescription = field_description.split(",");
				StringBuffer str = new StringBuffer();

				for (int i = 0; i < fieldsDescription.length; i++) {
					str.append(rslt.getObject(field_description)).append(" ");
				}

				boolean toAdd = true;

				if (onlyThisValues != null) {
					toAdd = false;

					for (int i = 0; (i < onlyThisValues.length) && !toAdd; i++) {
						if (xcod.equals(onlyThisValues[i])) {
							toAdd = true;
						}
					}
				}

				if (toAdd) {
					add(xcod, str.toString(), true);
				}
			}/////////
			if (this.p_language==null)
				this.p_language=boApplication.getDefaultApplication().getApplicationLanguage();
			
			////////////
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rslt != null)
					rslt.close();
				if (pstm != null)
					pstm.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
			}
		}

	}

	// psantos fim

	public lovObject(EboContext ctx, long lovBoui, String name,
			String tableName, String whereClause, String field_description,
			String field_cod, String[] onlyThisValues)
			throws boRuntimeException {
		PreparedStatement pst = null;
		ResultSet rslt = null;
		try {
			p_lovboui = lovBoui;
			Connection cn;
			cn = ctx.getConnectionData();

			String sql = "";
			String[] fields1 = field_description.split(",");
			String[] fields2 = field_cod.split(",");

			if ("".equals(whereClause) || (whereClause == null))

			{
				String f = "";
				for (int i = 0; i < fields1.length; i++) {
					f += fields1[i];
					if (i + 1 < fields1.length) {
						f += ",";
					}
				}
				for (int i = 0; i < fields2.length; i++) {
					if (f.toUpperCase().indexOf(fields2[i].toUpperCase()) == -1) {

						f += "," + fields2[i];
					}
				}

				sql = "select " + f + " from " + tableName + " order by "
						+ field_description;
			} else {
				sql = "select " + field_description + "," + field_cod
						+ " from " + tableName + " where " + whereClause
						+ " order by " + field_description;
			}

			pst = cn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			rslt = pst.executeQuery();			
				/////////
			if (this.p_language==null)
				this.p_language=boApplication.getDefaultApplication().getApplicationLanguage();
			
			////////////
			// boolean haveResults = rslt.next();
			while (rslt.next()) {
				Object o = rslt.getObject(field_cod);
				String xcod = "";
				if (o != null) {
					xcod = o.toString();
				}
				String[] fieldsDescription = field_description.split(",");
				StringBuffer str = new StringBuffer();

				for (int i = 0; i < fieldsDescription.length; i++) {
					str.append(rslt.getObject(fieldsDescription[i]))
							.append(" ");
				}

				boolean toAdd = true;

				if (onlyThisValues != null) {
					toAdd = false;

					for (int i = 0; (i < onlyThisValues.length) && !toAdd; i++) {
						if (xcod.equals(onlyThisValues[i])) {
							toAdd = true;
						}
					}
				}

				if (toAdd) {
					add(xcod, str.toString());
				}
			}
		} catch (SQLException e) {
			throw new boRuntimeException(MessageLocalizer.getMessage("ERROR_CREATING_LOVOBJECT"), "", e);
		} finally {
			try {
				if (rslt != null)
					rslt.close();
			} catch (Exception e) {
			}
			try {
				if (pst != null)
					pst.close();
			} catch (Exception e) {
			}
		}
	}

	// psantos ini
	private void add(String cod, String description, boolean allvalues) {
		if (allvalues || !p_lov_cod.contains(cod)) {
			p_lov_cod.add(cod);
			p_lov_description.add(description);
			p_count++;
		}
	}

	// psantos fim

	private void add(String cod, String description) {
		if (!p_lov_cod.contains(cod)) {
			p_lov_cod.add(cod);
			p_lov_description.add(description);
			p_count++;
		}
	}

	public int getSize() {
		return p_count;
	}

	public boolean beforeFirst() {
		p_pointer = -1;

		return true;
	}

	public boolean first() {
		if (p_count > 0) {
			p_pointer = 1;

			return true;
		}

		return false;
	}

	public boolean next() {
		p_pointer++;

		if (p_pointer >= p_count) {
			return false;
		}

		return true;
	}

	public boolean previous() {
		p_pointer--;

		if (p_pointer < 0) {
			p_pointer = -1;

			return false;
		}

		return true;
	}

	public String getCode() {
		if (invalidIndex()) {
			return null;
		}

		return (String) p_lov_cod.get(p_pointer);
	}
	
	 

	/**
	 * 
	 * @return LOV description(String) returns the translation in the current
	 *         language
	 * @throws boRuntimeException 
	 */
	public String getDescription() throws boRuntimeException {
		
		if (isUserInSession()){
			boSessionUser boUser = getSessionUser();
			String language = getLanguage();
			if (userHasApplicationLanguage( boUser, language )) 
			{
				if (invalidIndex()) {
					return null;
				}
				return (String) p_lov_description.get(p_pointer);
			}
			else {
				String description = getTranslation(p_name, (String) p_lov_cod
						.get(p_pointer), (String) p_lov_description.get(p_pointer));

				return description;
			}
		}
		
		if (invalidIndex()) {
			return null;
		}
		return (String) p_lov_description.get(p_pointer);
		
	}
	
	/**
	 * 
	 * Returns the description for the code, without any attempt to translate
	 * 
	 * @return The description associated to the code
	 */
	public String getRawDescription(){
		return (String) p_lov_description.get(p_pointer);
	}

	protected boolean invalidIndex() {
		return (p_pointer == -1) || (p_pointer >= p_count);
	}

	private boSessionUser getSessionUser() {
		boContext bctx = boApplication.currentContext();
		if (bctx != null){
			EboContext ctx = bctx.getEboContext();
			if (ctx != null){
				boSessionUser boUser = ctx.getSysUser();
				if (boUser != null)
					return boUser;
			}
		}
		return null;
	}

	protected boolean userHasApplicationLanguage( boSessionUser boUser, String language ) {
		return boUser.getLanguage() != null && boUser.getLanguage().equals(language);
	}
	
	protected boolean isUserInSession(){
		boContext bctx = boApplication.currentContext();
		if (bctx != null){
			EboContext ctx = bctx.getEboContext();
			if (ctx != null){
				boSessionUser boUser = ctx.getSysUser();
				if (boUser != null)
					return true;
			}
		}
		return false;
	}

	public long getLovBoui() {
		return p_lovboui;
	}

	public boolean findLovItemByCode(String code) {
		boolean toRet = false;
		beforeFirst();
		while (next()) {
			if (getCode().equals(code)) {
				toRet = true;
				break;
			}
		}
		return toRet;
	}

	/**
	 * Pedro Rio
	 * 
	 * Retrieves an item description, given its code
	 * 
	 * @param code
	 *            The code of the
	 * 
	 * @return A string with the description of the given code
	 * 
	 * @throws boRuntimeException If the description could not be loaded from database 
	 */
	public String getDescriptionByCode(String code) throws boRuntimeException {
		beforeFirst();
		while (next()) {
			if (getCode().equals(code))
					return getDescription();
		}
		return null;
	}

	public boolean findLovItemByDescription(String description) throws boRuntimeException {
		boolean toRet = false;
		beforeFirst();
		while (next()) {
			if (p_lov_description.get(p_pointer).equals(description)) {
				toRet = true;
				break;
			}
		}
		if (!toRet){
			beforeFirst();
			while (next()){
				String translatedDescription = getTranslation( p_name , getCode() , "" );
				if (translatedDescription.equals( description )){
					toRet = true;
					break;
				}
			}
		}
		
		return toRet;
	}

	public String getLanguage() {
		return p_language;
	}
	public void setLanguage(String lang){
		p_language=lang;
	}

	/**
	 * 
	 * @param lovName
	 * @param value
	 * @param defaultDescription
	 * @return LOVtranslation(String)
	 * @throws boRuntimeException 
	 * 
	 */

	public static String getTranslation(String lovName, String value,
			String defaultDescription) throws boRuntimeException {		
		boObject lov;	
		String usedLanguage=null;
		String label;
		EboContext ctx = boApplication.currentContext().getEboContext();
		if (ctx != null){
		lov = boObject.getBoManager().loadObject(ctx, "Ebo_LOV",
				"name='" + lovName + "'");	
		AttributeHandler fileName=lov.getAttribute("xeolovfile");
		if (fileName!=null){
				
				boApplication app = boApplication
						.getApplicationFromStaticContext("XEO");
				usedLanguage = app.getApplicationLanguage();
				if(boApplication.currentContext()!=null)
					if(boApplication.currentContext().getEboContext()!=null)
						if(boApplication.currentContext().getEboContext().getBoSession()!=null)
				{
					boSessionUser user = boApplication.currentContext().getEboContext()
							.getBoSession().getUser();
					if (user.getLanguage() != null && user.getLanguage() != "")
						usedLanguage = user.getLanguage();
					}
					
					HashMap<String, Properties> map = boDefHandlerImpl
							.getLanguagesMap();
					
				
				if (usedLanguage != null
						&& map.containsKey(fileName + "_" + usedLanguage.toUpperCase()
								+ ".properties")) {
					
					Properties prop = map.get(fileName + "_"
							+ usedLanguage.toUpperCase() + ".properties");
					
					label = prop.getProperty(lovName+"."+value);
					if (StringUtils.isEmpty( label ))
						label = defaultDescription;
				} else {
					label = defaultDescription;
				}
		}
		else
			label = defaultDescription;
		}
		else
			label = defaultDescription;

		return label;
	}

}
