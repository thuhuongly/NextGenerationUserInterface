/**
 * 
 */
package be.ac.vub.ngui.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

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
			
			// add tasks
			documents.addAll(TaskService.listTasks());
			
			// add notes
			documents.addAll(NoteService.listNotes());
			
			// add link
			for (int i = 0; i < documents.size(); i++) {
				for (int j = i + 1; j < documents.size(); j++) {
					Document source = documents.get(i);
					Document target = documents.get(j);
					List<String> values = new ArrayList<String>();
					
					// check keyword relationship
					if (shareKeywords(source, target)) {
						Link link = new Link();
						link.setSource(i);
						link.setTarget(j);
						values.add(Constant.RELATIONSHIP_TYPE_KEYWORD);
						link.setValues(values);
						
						links.add(link);
					}
					
					// check authorship
					if (shareAuthors(source, target)) {
						Link link = new Link();
						link.setSource(i);
						link.setTarget(j);
						values.add(Constant.RELATIONSHIP_TYPE_AUTHORSHIP);
						link.setValues(values);
						
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
	private static boolean shareKeywords(Document source, Document target) {

		List<String> keywordSource = source.getKeywords();
		List<String> keywordTarget = target.getKeywords();
		
		if (keywordSource != null) {
			for (String keyword : keywordSource) {
				if(keywordTarget != null && keyword != null && keywordTarget.contains(keyword)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Check if two documents share some authors
	 * @param source
	 * @param target
	 * @return
	 */
	private static boolean shareAuthors(Document source, Document target) {

		List<String> authorSource = source.getAuthors();
		List<String> authorTarget = target.getAuthors();

		if (authorSource != null) {
			for (String author : authorSource) {
				if (authorTarget != null && author != null && authorTarget.contains(author)) {
					return true;
				}
			}
		}
		return false;
	}
}
