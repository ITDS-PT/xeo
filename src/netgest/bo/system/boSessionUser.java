/*Enconding=UTF-8*/
package netgest.bo.system;

import netgest.bo.system.locale.LocaleSettings;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boSessionUser 
{

	String language;
	
    String userName;
    String name;
    String srName;
    String email;
    String notify;
    private String themeName = "";
    private Map<String,String> themeFiles = new HashMap<String, String>();
    int securityLevel;
    long[] groups;
    long[] queues;
    long[] roles;
    long[] applications;
    long boui;
    long[] mailboxes;
    Locale userLocale;
    TimeZone userTimezone;
    
    boolean isAdministrator = false;

	private LocaleSettings localeSettings;

    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public boSessionUser()
    {
    }

    public String getLanguage(){
    	if (this.userLocale != null){
    		return this.userLocale.toString();
    	} else {
    		return language;
    	}
    }
    
    public String getUserName()
    {
        return userName;
    }


    public String getName()
    {
        return name;
    }


    public String getSrName()
    {
        return srName;
    }


    public String getEmail()
    {
        return email;
    }


    public String getNotify()
    {
        return notify;
    }


    public int getSecurityLevel()
    {
        return securityLevel;
    }


    public long[] getGroups()
    {
        return groups;
    }


    public long[] getQueues()
    {
        return queues;
    }


    public long[] getRoles()
    {
        return roles;
    }


    public long[] getApplications()
    {
        return applications;
    }


    public long getBoui()
    {
        return boui;
    }


    public long[] getMailboxes()
    {
        return mailboxes;
    }

    public boolean isAdministrator()
    {
        return isAdministrator;
    }
    public void setLanguage(String lang){
    	if (lang != null) {
    		userLocale = new Locale(lang);
    		language=lang;
    	}
    }

    /**
     * 
     * Retrieves the Theme name for the user
     * 
     * @return The name of the theme
     */
    public String getThemeName(){
    	return themeName;
    }

    /**
     * 
     * Sets the session theme name
     * 
     * @param theme
     */
    public void setTheme(String theme){
    	this.themeName = theme;
    }
    
    /**
     * 
     * Retrieves the list of files to include in this theme
     * 
     * @return A map of id -> filePath
     */
    public Map<String,String> getThemeFiles(){
    	return themeFiles;
    }

    /**
     * 
     * Sets the list of files to include in this theme
     * 
     * @param files
     */
    public void setThemeFiles(Map<String,String> files){
    	themeFiles = files;
    }
    
    void setLocale(Locale newLocale) {
    	this.userLocale = newLocale;
    }
    
    Locale getLocale() {
    	return userLocale;
    }
    
    TimeZone getTimeZone() {
    	return userTimezone;
    }
    
    void setTimeZone(TimeZone newTimeZone) {
    	this.userTimezone = newTimeZone;
    }
    
    public LocaleSettings getLocaleSettings() {
    	if (this.localeSettings == null)
    		return LocaleSettings.DEFAULT;
    	return this.localeSettings;
    }
    
    void setLocaleSettings(LocaleSettings settings){ 
    	this.localeSettings = settings;
    }

}