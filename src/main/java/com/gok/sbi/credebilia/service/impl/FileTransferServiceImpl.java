package com.gok.sbi.credebilia.service.impl;

import com.gok.sbi.credebilia.service.FileTransferService;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class FileTransferServiceImpl implements FileTransferService {

	private Logger logger = LoggerFactory.getLogger(FileTransferServiceImpl.class);

	@Value("${com.gok.sbi.sftp.host}")
	private String host;

	@Value("${com.gok.sbi.sftp.port}")
	private Integer port;

	@Value("${com.gok.sbi.sftp.username}")
	private String username;

	@Value("${com.gok.sbi.sftp.password}")
	private String password;

	@Value("${com.gok.sbi.sftp.sessionTimeout}")
	private Integer sessionTimeout;

	@Value("${com.gok.sbi.sftp.channelTimeout}")
	private Integer channelTimeout;

	@Override
	public boolean downloadFile(String localFilePath, String remoteFilePath) {
		ChannelSftp channelSftp = createChannelSftp();
		OutputStream outputStream;
		try {
			File file = new File(localFilePath);
			outputStream = new FileOutputStream(file);
			channelSftp.get(remoteFilePath, outputStream);
			file.createNewFile();
			return true;
		} catch (SftpException | IOException ex) {
			logger.error("Error download file", ex);
		} finally {
			disconnectChannelSftp(channelSftp);
		}

		return false;
	}

	private ChannelSftp createChannelSftp() {
		try {
			JSch jSch = new JSch();
			Session session = jSch.getSession(username, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect(sessionTimeout);
			Channel channel = session.openChannel("sftp");
			channel.connect(channelTimeout);
			return (ChannelSftp) channel;
		} catch (JSchException ex) {
			logger.error("Create ChannelSftp error", ex);
		}

		return null;
	}

	private void disconnectChannelSftp(ChannelSftp channelSftp) {
		try {
			if (channelSftp == null)
				return;

			if (channelSftp.isConnected())
				channelSftp.disconnect();

			if (channelSftp.getSession() != null)
				channelSftp.getSession().disconnect();

		} catch (Exception ex) {
			logger.error("SFTP disconnect error", ex);
		}
	}

	@Override
	public boolean copyFromOneFolderToAnother(String localFilePath, String remoteFilePath) {
		File source = new File(remoteFilePath);
		File dest = new File(localFilePath);

		if (dest.exists()) {
			dest.delete();
		}
		try {
			FileSystemUtils.copyRecursively(source, dest);
			return true;
		} catch (IOException e) {
			logger.error("Local File copy Error", e);

		}
		return false;
	}
}
