/**
 * 
 */
package be.ac.vub.ngui.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import be.ac.vub.ngui.model.Publication.Name;
import be.ac.vub.ngui.utils.Constant;

/**
 * @author thuhuongly
 *
 */
public class MendeleyService {
	
	/**
	 * Get all publication
	 * 
	 * @throws OAuthSystemException
	 */
	public static List<Document> listPublications() throws OAuthSystemException {
		Gson gson = new Gson();
		List<Document> result = new ArrayList<Document>();
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

				HttpGet httpGet = new HttpGet("https://api.mendeley.com/documents?limit=100&view=all");
				httpGet.setHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());
				DefaultHttpClient apacheHttpClient = ApacheHttpTransport.newDefaultHttpClient();
				HttpResponse httpResponse = apacheHttpClient.execute(httpGet);

				String responseAsString = EntityUtils.toString(httpResponse.getEntity());
				String newResponse = responseAsString.replace("abstract", "abstracts");

				JSONArray array = new JSONArray(newResponse);

				for (int i = 0; i < array.length(); i++) {
					Publication publication = gson.fromJson(array.getJSONObject(i).toString(), Publication.class);
					
					Document data = new Document();
					data.setId(publication.getId());
					data.setTitle(publication.getTitle());

					data.setDatatype(Constant.DATA_TYPE_PUBLICATION);

					List<String> authors = new ArrayList<String>();
					List<Name> authorName = publication.getAuthors();
					for (Object object : authorName) {
						authors.add(object.toString());
					}
					
					data.setAuthors(authors);
					data.setKeywords(publication.getKeywords());
					data.setCreatedDate(new Date());
					data.setTasks(publication.getTags());
					data.setContent(publication.getAbstracts());

					/*Gson gson1 = new Gson();
					String json = gson1.toJson(data);*/
					
					result.add(data);
				}

			} catch (OAuthProblemException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return result;
	}
	

	public static void main(String[] args) throws OAuthSystemException {

		try {

			OAuthClientRequest request = OAuthClientRequest.tokenLocation("https://api.mendeley.com/oauth/token")
					.setClientId("2332").setClientSecret("8M7xcT16oThMDouU").setGrantType(GrantType.REFRESH_TOKEN)
					.setRefreshToken("MSw0NTE5NDA5MzEsMTAyOCxhbGwsNTA5NC1hNzdiMWUyOTA1MTc4NDI1OWI0Zjc0ODkxODIzNTkxOWZmYS02LDEwOTQtYTc3YjFlMjkwNTE3ODQyNTliNGY3NDg5MTgyMzU5MTlmZmEtbSwxNDQ1NTczNjg1NzI5LDBEMENJRVo1N08xblRCSWpZelNDdy1uekFBWQ")
					.setRedirectURI("http://localhost:8080/pfadfinder")
					.setScope("all").buildBodyMessage();

			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthJSONAccessTokenResponse tokenResponse;
			try {
				tokenResponse = oAuthClient.accessToken(request, OAuthJSONAccessTokenResponse.class);
				Gson gson = new Gson();
				HttpGet httpGet = new HttpGet("https://api.mendeley.com/documents?view=all");
				httpGet.setHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());
				DefaultHttpClient apacheHttpClient = ApacheHttpTransport.newDefaultHttpClient();
				HttpResponse httpResponse = apacheHttpClient.execute(httpGet);

				String responseAsString = EntityUtils.toString(httpResponse.getEntity());
				String newResponse = responseAsString.replace("abstract", "abstracts");

				JSONArray array = new JSONArray(newResponse);

				for (int i = 0; i < array.length(); i++) {
					Publication publication = gson.fromJson(array.getJSONObject(i).toString(), Publication.class);
					
					Document data = new Document();
					data.setId(publication.getId());
					data.setTitle(publication.getTitle());

					data.setDatatype(Constant.DATA_TYPE_PUBLICATION);

					List<String> authors = new ArrayList<String>();
					List<Name> authorName = publication.getAuthors();
					for (Object object : authorName) {
						authors.add(object.toString());
					}
					
					data.setAuthors(authors);
					data.setKeywords(publication.getKeywords());
					data.setCreatedDate(new Date());
					data.setTasks(publication.getTags());
					data.setContent(publication.getAbstracts());
					

					System.out.println(data);
				}

				//System.out.println(responseAsString);
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
