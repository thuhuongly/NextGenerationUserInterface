/**
 * 
 */
package be.ac.vub.ngui.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

import be.ac.vub.ngui.model.Data;
import be.ac.vub.ngui.model.Document;
import be.ac.vub.ngui.model.Link;
import be.ac.vub.ngui.utils.Constant;

/**
 * @author thuhuongly
 *
 */
public class DataProcessing {

	public static void main(String[] args) throws IOException {
		List<Document> documents = new ArrayList<Document>();
		List<Link> links = new ArrayList<Link>();
		try {
			// add publications
			documents.addAll(MendeleyService.listPublications());
			
			// add emails
			documents.addAll(GmailService.listEmails());
			
			// add notes
			documents.addAll(NoteService.listNotes());
			
			// add tasks
			for (Document doc : documents) {
				doc.setKind(Constant.DOCUMENT_KIND_DIGITAL);
				List<String> taskList = new ArrayList<String>();
				
				Map<String, String> tasks = TaskService.listTasks();
				Iterator<Entry<String, String>> it = tasks.entrySet().iterator();
				
				while (it.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) it.next();
					String taskTitle = entry.getKey();
					String notes = entry.getValue();
					if (notes != null) {
						List<String> desc = Arrays.asList(notes.split(";"));
						if (desc.contains(doc.getTitle())) {
							taskList.add(taskTitle);
						}
					}
				}
				if (doc.getTasks() == null) {
					doc.setTasks(taskList);
				}
			}
			
			// add link
			for (int i = 0; i < documents.size(); i++) {
				for (int j = i + 1; j < documents.size(); j++) {
					Document source = documents.get(i);
					Document target = documents.get(j);
					
					// check keyword relationship
					List<String> shareKeyWord = shareKeywords(source, target);
					if (shareKeyWord.size() > 0) {
						Link link = new Link();
						link.setSource(i);
						link.setTarget(j);
						link.setType(Constant.RELATIONSHIP_TYPE_KEYWORD);
						link.setValue(shareKeyWord);
						
						links.add(link);
					}
					
					// check authorship
					List<String> shareAuthor = shareAuthors(source, target); 
					if (shareAuthor.size() > 0) {
						Link link = new Link();
						link.setSource(i);
						link.setTarget(j);
						link.setType(Constant.RELATIONSHIP_TYPE_AUTHORSHIP);
						link.setValue(shareAuthor);
						
						links.add(link);
					}
					
					// check task relationship
					List<String> shareTask = shareTasks(source, target);
					if (shareTask.size() > 0) {
						Link link = new Link();
						link.setSource(i);
						link.setTarget(j);
						link.setType(Constant.RELATIONSHIP_TYPE_TASK);
						link.setValue(shareTask);
						
						links.add(link);
					}
				}
			}
			
			Data data = new Data(documents, links);
			Gson dataGson = new Gson();
			String dataJson = dataGson.toJson(data);
			
			//write converted json data to a file named "data.json"
			FileWriter writer = new FileWriter("data.json");
			writer.write(dataJson);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Check if two documents share some keywords
	 * @param source
	 * @param target
	 * @return
	 */
	private static List<String> shareKeywords(Document source, Document target) {

		List<String> linkdata = new ArrayList<String>();
		List<String> keywordSource = source.getKeywords();
		List<String> keywordTarget = target.getKeywords();
		
		if (keywordSource != null) {
			for (String keyword : keywordSource) {
				if(keywordTarget != null && keyword != null && keywordTarget.contains(keyword)) {
					if (!keyword.contains("INBOX")) {
						linkdata.add(keyword);
					}
				}
			}
		}
		return linkdata;
	}
	
	/**
	 * Check if two documents share some authors
	 * @param source
	 * @param target
	 * @return
	 */
	private static List<String> shareAuthors(Document source, Document target) {

		List<String> linkdata = new ArrayList<String>();
		List<String> authorSource = source.getAuthors();
		List<String> authorTarget = target.getAuthors();

		if (authorSource != null) {
			for (String author : authorSource) {
				if (authorTarget != null && author != null && authorTarget.contains(author)) {
					linkdata.add(author);
				}
			}
		}
		return linkdata;
	}
	
	/**
	 * Check if two documents share some authors
	 * @param source
	 * @param target
	 * @return
	 */
	private static List<String> shareTasks(Document source, Document target) {

		List<String> linkdata = new ArrayList<String>();
		List<String> taskSource = source.getTasks();
		List<String> taskTarget = target.getTasks();

		if (taskSource.size() > 0) {
			for (String task : taskSource) {
				if (taskTarget.size() > 0 && taskTarget.contains(task)) {
					linkdata.add(task);
				}
			}
		}
		return linkdata;
	}
}
