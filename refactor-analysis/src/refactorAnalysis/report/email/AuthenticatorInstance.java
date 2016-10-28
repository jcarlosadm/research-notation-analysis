package refactorAnalysis.report.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class AuthenticatorInstance extends Authenticator{
	
	private String user;
	private String password;

	public AuthenticatorInstance(String user, String password) {
		this.user = user;
		this.password = password;
	}
	
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, password);
	}
}
