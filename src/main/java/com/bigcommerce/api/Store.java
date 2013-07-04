package com.bigcommerce.api;

import com.bigcommerce.http.HttpMethod;
import com.bigcommerce.http.HttpRequest;
import com.bigcommerce.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.bigcommerce.http.client.HttpClient;
import com.bigcommerce.http.client.ApacheHttpClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Facade for accessing a Bigcommerce store via the REST API.
 */
public class Store {

	private Connection connection;
	private final HttpClient httpClient;
	private URI url;

	/**
	 * A constructor that accepts any object implementing
	 * {@link com.bigcommerce.api.auth.Credentials Credentials}.
	 *
	 * @param storeUrl
	 * @param credentials
	 */
	public Store(String storeUrl, Credentials credentials) throws URISyntaxException {
		this(storeUrl, credentials, new ApacheHttpClient());
	}

	public Store(String url, Credentials credentials, HttpClient httpClient) throws URISyntaxException {
		this.connection = new Connection(url, credentials.getUsername(), credentials.getApiKey());
		this.httpClient = httpClient;

		this.url = new URI(url);
	}

	/**
	 * A simple constructor that accepts a username and API key as credentials.
	 *
	 * @param storeUrl
	 * @param username
	 * @param apiKey
	 */
	public Store(String storeUrl, String username, String apiKey) throws URISyntaxException {
		this(storeUrl, new SimpleCredentials(username, apiKey));
	}

	/**
	 * Products collection
	 */
	public List<Product> getProducts() {
		List<Product> products = new ArrayList<Product>();
		Element xml = this.connection.get("/products").asXml();

		NodeList productTags = xml.getElementsByTagName("product");
		for (int i = 0; i < productTags.getLength(); i++) {
			Element productTag = (Element) productTags.item(i);
			Product product = new Product(productTag);
			products.add(product);
		}

		return products;
	}

	public void getBrands() throws IOException {
		HttpRequest httpRequest;
		try {
			httpRequest = new HttpRequest(new URI(url + "/brands.json"), HttpMethod.GET);
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			InputStream content = httpResponse.getEntity().getContent();
			
			
		} catch (URISyntaxException ex) {
			Logger.getLogger(Store.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}