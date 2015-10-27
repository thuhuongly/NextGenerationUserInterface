/**
 * 
 */
package be.ac.vub.ngui.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.json.JSONArray;

import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.gson.Gson;

import be.ac.vub.ngui.model.Document;
import be.ac.vub.ngui.model.Publication;

/**
 * @author thuhuongly
 *
 */
public class DataUtils {
	public void convertToJson(Document data) {

		Gson gson = new Gson();

		/*obj.setTitle("PerCon : A Personal Digital Library for Heterogeneous Data");
		obj.setDatatype("Publication");
		List<String> authors = new ArrayList<String>();
		authors.add("Su Inn Park");
		authors.add("Frank Shipman");
		obj.setAuthors(authors);
		
		List<String> keywords = new ArrayList<String>();
		keywords.add("personal");
		keywords.add("library");
		
		obj.setCreatedDate(new Date());
		obj.setTasks(null);
		obj.setContent("");*/
		
		// convert java object to JSON format,
		// and returned as JSON formatted string
		String json = gson.toJson(data);

		try {
			// write converted json data to a file named "file.json"
			FileWriter writer = new FileWriter("file.json");
			writer.write(json);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(json);

	}
	
	public static void main(String[] args) throws OAuthSystemException {

		Gson gson = new Gson();

		try {

			OAuthClientRequest request = OAuthClientRequest.tokenLocation("https://api.mendeley.com/oauth/token")
					.setClientId("2332").setClientSecret("8M7xcT16oThMDouU").setGrantType(GrantType.REFRESH_TOKEN)
					.setRefreshToken(
							"MSw0NTE5NDA5MzEsMTAyOCxhbGwsNTA5NC1hNzdiMWUyOTA1MTc4NDI1OWI0Zjc0ODkxODIzNTkxOWZmYS02LDEwOTQtYTc3YjFlMjkwNTE3ODQyNTliNGY3NDg5MTgyMzU5MTlmZmEtbSwxNDQ1NTczNjg1NzI5LDBEMENJRVo1N08xblRCSWpZelNDdy1uekFBWQ")
					.setRedirectURI("http://localhost:8080/pfadfinder").setScope("all").buildBodyMessage();

			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthJSONAccessTokenResponse tokenResponse;
			try {
				tokenResponse = oAuthClient.accessToken(request, OAuthJSONAccessTokenResponse.class);

				HttpGet httpGet = new HttpGet("https://api.mendeley.com/documents");
				httpGet.setHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());
				DefaultHttpClient apacheHttpClient = ApacheHttpTransport.newDefaultHttpClient();
				HttpResponse httpResponse = apacheHttpClient.execute(httpGet);

				String responseAsString = EntityUtils.toString(httpResponse.getEntity());
				String newResponse = responseAsString.replace("abstract", "abstracts");
				
				JSONArray array = new JSONArray(newResponse);

				for (int i = 0; i < array.length(); i++) {
					Publication obj = gson.fromJson(array.getJSONObject(i).toString(), Publication.class);

					System.out.println(obj);
				}

			} catch (OAuthProblemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
}
