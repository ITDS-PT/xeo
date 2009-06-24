package netgest.bo.runtime;

public interface boObjectFactoryData {
	public String getObjectName();
	public long	  getBoui();
	public Object getData();
	public String serialize();
	public void   deserialize( String data );
}
