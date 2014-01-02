package netgest.bo.utils;

/**
 * 
 * Represents a file to include
 *
 */
public class XeoUserThemeFile {

	/**
	 * Path to the file to include
	 */
	private String path;
	
	/**
	 * Description of the file to include
	 */
	private String description;
	
	/**
	 * Identifier of the file
	 */
	private String id;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public XeoUserThemeFile(String path, String description, String id) {
		super();
		this.path = path;
		this.description = description;
		this.id = id;
	}
	
	
	
	
}
