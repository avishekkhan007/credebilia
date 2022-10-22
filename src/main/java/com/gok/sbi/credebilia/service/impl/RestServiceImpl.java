package com.gok.sbi.credebilia.service.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gok.sbi.credebilia.service.RestService;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class RestServiceImpl implements RestService {

	private Logger logger = LoggerFactory.getLogger(RestServiceImpl.class);

	@Override
	public String getWithHeader() {

		return null;
	}

	public String postFileWithApiKeyAuth(String filePath, String host, String restUrl, String apiKey) throws IOException {
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("text/plain");
			RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
					.addFormDataPart("files", filePath,
							RequestBody.create(new File(filePath),
									MediaType.parse("application/octet-stream")))
					.build();
			Request request = new Request.Builder()
					.url(host+restUrl).method("POST", body)
					.addHeader("x-api-key",
							apiKey)
					.build();
			Response response = client.newCall(request).execute();

			logger.info("The response is " + response.body().string());

			if (response.isSuccessful()) {
				return "success";
			}

		} catch (IOException e) {
			logger.error("Exception in processing the rest query to upload file", e);
		}
		return "failed";

	}

}
