package be.ac.vub.ngui.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {
	public static void main(String[] args) {
		
		// Recipient's email ID needs to be mentioned.
		String to = "group5ngui@gmail.com";

		// Sender's email ID needs to be mentioned
		String from = "BeatSigner@gmail.com";

		String subject = "Meeting for thesis today";
		
		String content = "Hi guys, "
				+ "\n\n We need a meeting today to discuss about your reasearch training and thesis";
		
		send(to, from, subject, content);
	}

	public static void send(String from, String to, String subject, String content) {
		
		final String username = "group5ngui@gmail.com";
		final String password = "qazxsw123456";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(content);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}