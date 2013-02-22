package netgest.bo.system.config;

/**
 * 
 * Represents the RenderKit element in bo
 * 
 * @author PedroRio
 *
 */
public class RenderKit{
	
	private String id;
	private String themeClass;
	
	public RenderKit(String id, String theme){
		this.id = id;
		themeClass = theme;
	}
	
	public String getId() {
		return id;
	}
	public String getThemeClass() {
		return themeClass;
	}
	
	
}