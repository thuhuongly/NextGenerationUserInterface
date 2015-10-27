/**
 * 
 */
package be.ac.vub.ngui.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.transport.TTransportException;
import com.google.gson.Gson;

import be.ac.vub.ngui.model.Document;
import be.ac.vub.ngui.utils.Constant;

public class NoteService {

	/***************************************************************************
	 * You must change the following values before running this sample code *
	 ***************************************************************************/

	// Real applications authenticate with Evernote using OAuth, but for the
	// purpose of exploring the API, you can get a developer token that allows
	// you to access your own Evernote account. To get a developer token, visit
	// https://sandbox.evernote.com/api/DeveloperToken.action
	public static final String AUTH_TOKEN = "S=s1:U=91882:E=157f8fac154:C=150a1499308:P=1cd:A=en-devtoken:V=2:H=141657464b2409f3531e4eafa765ffb8";

	/***************************************************************************
	 * You shouldn't need to change anything below here to run sample code *
	 ***************************************************************************/

	private UserStoreClient userStore;
	private static NoteStoreClient noteStore;
	
	/**
	 * Intialize UserStore and NoteStore clients. During this step, we
	 * authenticate with the Evernote web service. All of this code is
	 * boilerplate - you can copy it straight into your application.
	 */
	public NoteService(String token) throws Exception {
		// Set up the UserStore client and check that we can speak to the server
		EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token);
		ClientFactory factory = new ClientFactory(evernoteAuth);
		userStore = factory.createUserStoreClient();

		boolean versionOk = userStore.checkVersion("Evernote EDAMDemo (Java)",
				com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
				com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
		if (!versionOk) {
			System.err.println("Incompatible Evernote client protocol version");
			System.exit(1);
		}

		// Set up the NoteStore client
		noteStore = factory.createNoteStoreClient();
	}

	/**
	 * Console entry point.
	 */
	public static void main(String args[]) throws Exception {
		String token = System.getenv("AUTH_TOKEN");
		if (token == null) {
			token = AUTH_TOKEN;
		}
		if ("your developer token".equals(token)) {
			System.err.println("Please fill in your developer token");
			System.err
					.println("To get a developer token, go to https://sandbox.evernote.com/api/DeveloperToken.action");
			return;
		}

		NoteService demo = new NoteService(token);
		try {
			demo.listNotes();
		} catch (EDAMUserException e) {
			// These are the most common error types that you'll need to
			// handle
			// EDAMUserException is thrown when an API call fails because a
			// paramter was invalid.
			if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
				System.err.println("Your authentication token is expired!");
			} else if (e.getErrorCode() == EDAMErrorCode.INVALID_AUTH) {
				System.err.println("Your authentication token is invalid!");
			} else if (e.getErrorCode() == EDAMErrorCode.QUOTA_REACHED) {
				System.err.println("Your authentication token is invalid!");
			} else {
				System.err.println("Error: " + e.getErrorCode().toString() + " parameter: " + e.getParameter());
			}
		} catch (EDAMSystemException e) {
			System.err.println("System error: " + e.getErrorCode().toString());
		} catch (TTransportException t) {
			System.err.println("Networking error: " + t.getMessage());
		}
	}

	/**
	 * Get all notes from EverNote
	 * 
	 * @throws Exception
	 */
	public static List<Document> listNotes() throws Exception {
		String token = System.getenv("AUTH_TOKEN");
		List<Document> resultString = new ArrayList<Document>();
		if (token == null) {
			token = NoteService.AUTH_TOKEN;
		}
		if ("your developer token".equals(token)) {
			System.err.println("Please fill in your developer token");
			System.err
					.println("To get a developer token, go to https://sandbox.evernote.com/api/DeveloperToken.action");
			return null;
		}

		NoteService demo = new NoteService(token);
		try {
			resultString = demo.getNotes();
		} catch (EDAMUserException e) {
			// These are the most common error types that you'll need to
			// handle
			// EDAMUserException is thrown when an API call fails because a
			// paramter was invalid.
			if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
				System.err.println("Your authentication token is expired!");
			} else if (e.getErrorCode() == EDAMErrorCode.INVALID_AUTH) {
				System.err.println("Your authentication token is invalid!");
			} else if (e.getErrorCode() == EDAMErrorCode.QUOTA_REACHED) {
				System.err.println("Your authentication token is invalid!");
			} else {
				System.err.println("Error: " + e.getErrorCode().toString() + " parameter: " + e.getParameter());
			}
		} catch (EDAMSystemException e) {
			System.err.println("System error: " + e.getErrorCode().toString());
		} catch (TTransportException t) {
			System.err.println("Networking error: " + t.getMessage());
		}

		return resultString;
	}

	/**
	 * Retrieve and display a list of the user's notes.
	 */
	public static List<Document> getNotes() throws Exception {
		// List the notes in the user's account
		//System.out.println("Listing notes:");
		List<Document> result = new ArrayList<Document>();
		// First, get a list of all notebooks
		List<Notebook> notebooks = noteStore.listNotebooks();

		for (Notebook notebook : notebooks) {
			//System.out.println("Notebook: " + notebook.getName());

			// Next, search for the first 100 notes in this notebook, ordering
			// by creation date
			NoteFilter filter = new NoteFilter();
			filter.setNotebookGuid(notebook.getGuid());
			filter.setOrder(NoteSortOrder.CREATED.getValue());
			filter.setAscending(true);

			NoteList noteList = noteStore.findNotes(filter, 0, 100);
			List<Note> notes = noteList.getNotes();
			for(int i = 0;  i < notes.size(); i++) {
				Note note = notes.get(i);
				Document data = new Document();
				data.setId(note.getGuid());
				data.setTitle(note.getTitle());

				data.setDatatype(Constant.DATA_TYPE_NOTE);

				List<String> authors = new ArrayList<String>();
				authors.add(note.getAttributes().getAuthor());
				data.setAuthors(authors);
				data.setKeywords(null);

				data.setCreatedDate(new Date());
				data.setTasks(null);
				data.setContent(note.getContentHash().toString());

				/*Gson gson = new Gson();
				String json = gson.toJson(data);*/
				result.add(data);
			}
		}
		return result;
	}

}