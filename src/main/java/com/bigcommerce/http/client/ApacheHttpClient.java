/*
 * The MIT License
 *
 * Copyright 2013 Bigcommerce.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bigcommerce.http.client;

import com.bigcommerce.http.BinaryEntity;
import com.bigcommerce.http.HttpHeaders;
import com.bigcommerce.http.HttpMethod;
import com.bigcommerce.http.HttpRequest;
import com.bigcommerce.http.HttpResponse;
import com.bigcommerce.http.HttpMessage;
import java.io.IOException;
import java.net.URI;
import java.util.Map.Entry;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author luke.eller
 */
public class ApacheHttpClient implements HttpClient {

	private final DefaultHttpClient client;

	public ApacheHttpClient() {
		client = new DefaultHttpClient();
	}

	public HttpResponse execute(HttpRequest request) throws IOException {
		HttpUriRequest apacheRequest = mapRequest(request);
		org.apache.http.HttpResponse apacheResponse = client.execute(apacheRequest);
		return mapResponse(apacheResponse);
	}

	private HttpUriRequest mapRequest(HttpRequest source) {
		HttpUriRequest target = createTargetRequest(source);
		mapHeaders(source, target);
		if (target instanceof HttpEntityEnclosingRequest) {
			mapEntity(source, (HttpEntityEnclosingRequest) target);
		}
		return target;
	}

	private HttpResponse mapResponse(org.apache.http.HttpResponse source) throws IOException {
		HttpResponse target = new HttpResponse();
		mapHeaders(source, target);
		if (source instanceof HttpEntityEnclosingRequest) {
			mapEntity((HttpEntityEnclosingRequest) source, target);
		}
		return target;

	}

	private HttpUriRequest createTargetRequest(HttpRequest source) {
		URI url = source.getUrl();
		HttpMethod method = source.getMethod();

		if (method == HttpMethod.GET) {
			return new HttpGet(url);
		} else if (method == HttpMethod.POST) {
			return new HttpPost(url);
		} else if (method == HttpMethod.PUT) {
			return new HttpPut(url);
		} else if (method == HttpMethod.DELETE) {
			return new HttpDelete(url);
		} else if (method == HttpMethod.PATCH) {
			return new HttpPatch(url);
		} else if (method == HttpMethod.OPTIONS) {
			return new HttpOptions(url);
		} else if (method == HttpMethod.TRACE) {
			return new HttpTrace(url);
		} else if (method == HttpMethod.HEAD) {
			return new HttpHead(url);
		} else {
			throw new UnsupportedOperationException("Support for the given HTTP method is not provided.");
		}
	}

	private void mapHeaders(HttpMessage source, org.apache.http.HttpMessage target) {
		for (Entry<String, String> header : source.getHeaders().entrySet()) {
			target.addHeader(header.getKey(), header.getValue());
		}
	}

	private void mapHeaders(org.apache.http.HttpMessage source, HttpMessage target) {
		HttpHeaders headers = new HttpHeaders();
		for (Header header : source.getAllHeaders()) {
			headers.put(header.getName(), header.getValue());
		}
	}

	private void mapEntity(HttpMessage source, HttpEntityEnclosingRequest target) {
		BasicHttpEntity apacheEntity = new BasicHttpEntity();
		apacheEntity.setContent(source.getEntity().getContent());
		target.setEntity(apacheEntity);
	}

	private void mapEntity(HttpEntityEnclosingRequest source, HttpMessage target) throws IOException {
		// TODO: Examine the runtime type of the source entity.
		target.setEntity(new BinaryEntity(source.getEntity().getContent()));
	}
}
