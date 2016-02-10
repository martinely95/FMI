package bg.uni_sofia.fmi.corejava.exceptions;

import java.io.IOException;

public class ServerIsDownException extends IOException {
    /**
	 * wtf?
	 */
	private static final long serialVersionUID = 1L;
	private String messageToSend;
	
	public String getMessageToSend() {
		return messageToSend;
	}

	public void setMessageToSend(String messageToSend) {
		this.messageToSend = messageToSend;
	}

	public ServerIsDownException() {
        super();
    }
	
	public ServerIsDownException(String message) {
        super(message);
    }
	
	public ServerIsDownException(String message, String messageToSend) {
        super(message);
        this.messageToSend = messageToSend;
    }

    public ServerIsDownException(Throwable cause) {
        super(cause);
    }

    public ServerIsDownException(String message, Throwable cause) {
        super(message, cause);
    }
}