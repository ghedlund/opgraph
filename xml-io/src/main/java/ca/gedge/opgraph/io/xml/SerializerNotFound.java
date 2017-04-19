package ca.gedge.opgraph.io.xml;

public class SerializerNotFound extends Exception {

	private final Class<?> type;

	public SerializerNotFound(Class<?> type) {
		this(type, "");
	}

	public SerializerNotFound(Class<?> type, String message) {
		super(message);
		this.type = type;
	}

	public SerializerNotFound(Class<?> type, Throwable cause) {
		super(cause);
		this.type = type;
	}

	public Class<?> getType() {
		return this.type;
	}

	@Override
	public String getMessage() {
		return "Serializer not found for type " + getType().toString() + ". " + super.getMessage();
	}

}
