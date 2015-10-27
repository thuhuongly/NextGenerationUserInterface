/**
 * 
 */
package be.ac.vub.ngui.utils;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

/**
 * @author thuhuongly
 *
 */
public class MailTest {
	private String userName;
	private String password;
	private String receivingHost;

	public void setAccountDetails(String userName, String password) {

		this.userName = userName;// sender's email can also use as User Name
		this.password = password;

	}

	public void readGmail() {

		/*
		 * this will print subject of all messages in the inbox of
		 * sender@gmail.com
		 */

		this.receivingHost = "imap.gmail.com";// for imap protocol

		Properties props2 = System.getProperties();

		props2.setProperty("mail.store.protocol", "imaps");
		// I used imaps protocol here

		Session session2 = Session.getDefaultInstance(props2, null);

		try {

			Store store = session2.getStore("imaps");

			store.connect(this.receivingHost, this.userName, this.password);

			Folder folder = store.getFolder("INBOX");// get inbox

			folder.open(Folder.READ_ONLY);// open folder only to read

			Message message[] = folder.getMessages();

			for (int i = 0; i < message.length; i++) {

				// print subjects of all mails in the inbox

				System.out.println(InternetAddress.toString(message[i].getFrom()));

				// anything else you want

			}

			// close connections

			folder.close(true);

			store.close();

		} catch (Exception e) {

			System.out.println(e.toString());

		}

	}

	public static void main(String[] args) {
		String senderPassword = new String("qazxsw123456");

		String senderUserName = new String("group5ngui@gmail.com");

		// Create a GmailClient object

		MailTest newGmailClient = new MailTest();

		// Setting up account details

		newGmailClient.setAccountDetails(senderUserName, senderPassword);

		// Receive mails

		newGmailClient.readGmail();

	}

}
