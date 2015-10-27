/**
 * 
 */
package be.ac.vub.ngui.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import be.ac.vub.ngui.model.Document;
import be.ac.vub.ngui.utils.Constant;

/**
 * @author thuhuongly
 *
 */
public class GmailService {
	/** Application name. */
	private static final String APPLICATION_NAME = "Gmail API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".credentials/gmail-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** Global instance of the scopes required by this quickstart. */
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.MAIL_GOOGLE_COM);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Get all e-mails from Google mail
	 * 
	 * @throws IOException
	 */
	public static List<Document> listEmails() throws IOException {
		// Build a new authorized API client service.
		Gmail service = GmailService.getGmailService();
		List<Message> messages = GmailService.listMessages(service, "me");

		List<Document> result = new ArrayList<Document>();

		for (Message message : messages) {
			Document data = new Document();
			data.setId(message.getId());
			MessagePart payload = message.getPayload();
			if (payload != null) {
				List<MessagePartHeader> header = message.getPayload().getHeaders();
				Map<String, String> headerMap = new HashMap<String, String>();

				for (MessagePartHeader messagePartHeader : header) {
					headerMap.put(messagePartHeader.getName(), messagePartHeader.getValue());
				}
				data.setTitle(headerMap.get("Subject"));

				data.setDatatype(Constant.DATA_TYPE_EMAIL);

				List<String> authors = new ArrayList<String>();
				authors.add(headerMap.get("From"));
				data.setAuthors(authors);

				data.setKeywords(message.getLabelIds());

				data.setCreatedDate(new Date());
				data.setTasks(null);
				data.setContent(message.getSnippet());

				/*Gson gson = new Gson();
				String json = gson.toJson(data);
*/
				result.add(data);
			}
		}
		return result;
	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
		// Load client secrets.
		InputStream in = GmailService.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("me");
		//System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Gmail client service.
	 * 
	 * @return an authorized Gmail client service
	 * @throws IOException
	 */
	public static Gmail getGmailService() throws IOException {
		Credential credential = authorize();
		return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	public static void main(String[] args) throws IOException {
		// Build a new authorized API client service.
		Gmail service = getGmailService();
		List<Message> messages = listMessages(service,"me");
		System.out.println(messages);
	}

	/**
	 * List all Messages of the user's mailbox with labelIds applied.
	 *
	 * @param service
	 *            Authorized Gmail API instance.
	 * @param userId
	 *            User's email address. The special value "me" can be used to
	 *            indicate the authenticated user.
	 * @param labelIds
	 *            Only return Messages with these labelIds applied.
	 * @throws IOException
	 */
	public static List<Message> listMessages(Gmail service, String userId)
			throws IOException {
		ListMessagesResponse response = service.users().messages().list(userId).execute();

		List<Message> messages = new ArrayList<Message>();
		while (response.getMessages() != null) {
			messages.addAll(response.getMessages());
			if (response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = service.users().messages().list(userId).setPageToken(pageToken)
						.execute();
			} else {
				break;
			}
		}

		List<Message> messageInfos = new ArrayList<Message>();
		for (Message message : messages) {
			messageInfos.add(getMessage(service, userId, message.getId()));
		}

		return messageInfos;
	}
	
	 /**
	   * Get Message with given ID.
	   *
	   * @param service Authorized Gmail API instance.
	   * @param userId User's email address. The special value "me"
	   * can be used to indicate the authenticated user.
	   * @param messageId ID of Message to retrieve.
	   * @return Message Retrieved Message.
	   * @throws IOException
	   */
	  public static Message getMessage(Gmail service, String userId, String messageId)
	      throws IOException {
	    Message message = service.users().messages().get(userId, messageId).execute();
	   // System.out.println(message.toPrettyString());
	    return message;
	  }

}
