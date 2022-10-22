package com.gok.sbi.credebilia.service;

import java.io.IOException;

public interface RestService {


	String getWithHeader();

	String postFileWithApiKeyAuth(String filePath, String host, String restUrl, String apiKey) throws IOException;
}
