package netgest.bo.utils;

/**
 * 
 * Representation of a user theme
 * 
 * @author 
 *
 */
public class XeoUserTheme {

	/**
	 * The name of theme
	 */
	private String name;
	
	/**
	 * The description of the theme
	 */
	private String description;
	
	/**
	 * Whether the theme is the default theme or not
	 */
	private boolean active;
	
	/**
	 * The list of files to include
	 */
	private XeoUserThemeFile[] files;
	
	public XeoUserTheme(String name, String description, boolean active, XeoUserThemeFile[] files ){
		this.name = name;
		this.description = description;
		this.active = active;
		this.files = files;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public XeoUserThemeFile[] getFiles() {
		return files;
	}

	public void setFiles(XeoUserThemeFile[] files) {
		this.files = files;
	}
	
	
	
	
	
}
