package refactorAnalysis.report.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailManager {

	private StringBuffer message = new StringBuffer();

	private String to = "";
	private String from = "";
	private AuthenticatorInstance authenticator = null;
	private String subject = "";

	private Properties props;

	private static EmailManager instance = null;

	private EmailManager() {
		this.props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
	}

	public static EmailManager getInstance() {
		if (instance == null) {
			instance = new EmailManager();
		}
		
		return instance;
	}

	public void setFrom(String from, String password) {
		this.from = from;
		this.authenticator = new AuthenticatorInstance(from, password);
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setMessage(String message) {
		this.message.setLength(0);
		this.message.append(message);
	}

	public void appendMessage(String message) {
		this.message.append(message);
	}

	public boolean sendMessage() {

		if (this.to == null || this.to.isEmpty() || this.from == null || this.from.isEmpty()
				|| this.authenticator == null) {
			return false;
		}

		Session session = Session.getDefaultInstance(this.props, this.authenticator);
		
		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(this.from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.to));
			message.setSubject(this.subject);
			message.setContent(this.message.toString(), "text/html");

			Transport.send(message);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
