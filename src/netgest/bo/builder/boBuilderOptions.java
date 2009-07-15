package netgest.bo.builder;

public class boBuilderOptions {
	
	private boolean fullBuild = false;
	private boolean buildWorkplaces = false;
	private boolean removeUserWorkplaces = false;
	private boolean buildDatabase = true;
	private boolean markDeployedObjects = true;
	private boolean generateAndCompileJava = true;
	
	public boolean getFullBuild() {
		return fullBuild;
	}
	
	public void setFullBuild(boolean fullBuild) {
		this.fullBuild = fullBuild;
	}
	
	public boolean getBuildWorkplaces() {
		return buildWorkplaces;
	}
	
	public void setBuildWorkplaces(boolean buildWorkplaces) {
		this.buildWorkplaces = buildWorkplaces;
	}
	
	public boolean getRemoveUserWorkplaces() {
		return removeUserWorkplaces;
	}

	public void setRemoveUserWorkplaces(boolean removeWorkplaces) {
		this.removeUserWorkplaces = removeWorkplaces;
	}
	
	public boolean getBuildDatabase() {
		return buildDatabase;
	}
	
	public void setBuildDatabase(boolean buildDatabase) {
		this.buildDatabase = buildDatabase;
		if( !this.buildDatabase ) {
			setBuildWorkplaces( false );
			setMarkDeployedObjects( false );
			setRemoveUserWorkplaces( false );
		}
	}

	public boolean getMarkDeployedObjects() {
		return markDeployedObjects;
	}
	
	public void setMarkDeployedObjects(boolean markDeployedObjects) {
		this.markDeployedObjects = markDeployedObjects;
	}

	public boolean getGenerateAndCompileJava() {
		return generateAndCompileJava;
	}

	public void setGenerateAndCompileJava(boolean generateAndCompileJava) {
		this.generateAndCompileJava = generateAndCompileJava;
	}
	
	
	
}
