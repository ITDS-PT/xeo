package netgest.io.metadata;

public class MetadataException extends Exception {

	/**
	 * The serial UID
	 */
	private static final long serialVersionUID = -4814947287217613824L;

	public MetadataException() {
		}

	public MetadataException(String message) {
		super(message);
	}

	public MetadataException(Throwable cause) {
		super(cause);
	}

	public MetadataException(String message, Throwable cause) {
		super(message, cause);
	}

}
