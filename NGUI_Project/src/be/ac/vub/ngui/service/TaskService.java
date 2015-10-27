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
import java.util.List;

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
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;

import be.ac.vub.ngui.model.Document;
import be.ac.vub.ngui.utils.Constant;

/**
 * @author thuhuongly
 *
 */
public class TaskService {
	/** Application name. */
	private static final String APPLICATION_NAME = "Google Tasks API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".credentials/tasks-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** Global instance of the scopes required by this quickstart. */
	private static final List<String> SCOPES = Arrays.asList(TasksScopes.TASKS_READONLY);

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
	 * Get all Tasks from Google calendar
	 * 
	 * @throws IOException
	 */
	public static List<Document> listTasks() throws IOException {
		// Build a new authorized API client service.
		Tasks service = TaskService.getTasksService();

		// Print the first 10 task lists.
		com.google.api.services.tasks.model.Tasks result = service.tasks().list("MDYyODMzMzU1MzY5NjQ5NDk3NDY6MDow")
				.setMaxResults(Long.valueOf(10)).execute();
		List<Task> tasklists = result.getItems();
		List<Document> resultString = new ArrayList<Document>();

		if (tasklists == null || tasklists.size() == 0) {
			System.out.println("No task lists found.");
		} else {
		//	System.out.println("Task lists:");
			for (Task task : tasklists) {
				Document data = new Document();
				data.setId(task.getId());
				data.setTitle(task.getTitle());

				data.setDatatype(Constant.DATA_TYPE_TASK);

				List<String> authors = new ArrayList<String>();
				authors.add(task.getParent());
				data.setAuthors(authors);

				if (task.getNotes() != null) {
					data.setKeywords(Arrays.asList(task.getNotes().split(" ")));
				} else {
					data.setKeywords(null);
				}

				data.setCreatedDate(new Date());
				data.setTasks(null);
				data.setContent(task.getNotes());

				/*Gson gson = new Gson();
				String json = gson.toJson(data);*/

				resultString.add(data);
			}
		}
		return resultString;
	}
	
	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
		// Load client secrets.
		InputStream in = TaskService.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		//System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Tasks client service.
	 * 
	 * @return an authorized Tasks client service
	 * @throws IOException
	 */
	public static Tasks getTasksService() throws IOException {
		Credential credential = authorize();
		return new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	public static void main(String[] args) throws IOException {
		// Build a new authorized API client service.
		Tasks service = getTasksService();

		// Print the first 10 task lists.
		com.google.api.services.tasks.model.Tasks result = service.tasks().list("MDYyODMzMzU1MzY5NjQ5NDk3NDY6MDow").setMaxResults(Long.valueOf(10)).execute();
		List<Task> tasklists = result.getItems();
		if (tasklists == null || tasklists.size() == 0) {
			System.out.println("No task lists found.");
		} else {
			System.out.println("Task lists:");
			System.out.println(tasklists);
		}
	}

}