package com.gok.sbi.credebilia.service;

public interface FileTransferService {


	boolean downloadFile(String localFilePath, String remoteFilePath);
	boolean copyFromOneFolderToAnother(String localFilePath, String remoteFilePath);

}
