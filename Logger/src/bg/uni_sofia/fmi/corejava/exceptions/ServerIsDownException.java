package bg.uni_sofia.fmi.corejava.exceptions;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class ServerIsDownException extends IOException {
    /**
	 * wtf?
	 */
	private static final long serialVersionUID = 1L;
	private String messageToSend;  // this is the accumulated message so far
	private static final AtomicLong sequenceNumber
    = new AtomicLong(0);
	
	public static long getSequencenumber() {
		return sequenceNumber.getAndIncrement();
	}

	public String getMessageToSend() {
		return messageToSend;
	}

	public void setMessageToSend(String messageToSend) {
		this.messageToSend = messageToSend;
	}

	public ServerIsDownException() {
        super();
        sequenceNumber.getAndIncrement();
    }
	
	public ServerIsDownException(String message) {
        super(message);
        sequenceNumber.getAndIncrement();
    }
	
	public ServerIsDownException(String message, String messageToSend) {
        super(message);
        this.messageToSend = messageToSend;
        sequenceNumber.getAndIncrement();
    }

    public ServerIsDownException(Throwable cause) {
        super(cause);
        sequenceNumber.getAndIncrement();
    }

    public ServerIsDownException(String message, Throwable cause) {
        super(message, cause);
        sequenceNumber.getAndIncrement();
    }
}