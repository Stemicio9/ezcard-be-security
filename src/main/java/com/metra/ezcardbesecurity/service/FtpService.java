package com.metra.ezcardbesecurity.service;

import com.metra.ezcardbesecurity.commons.FtpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class FtpService {

    @Value("${ftp.port}")
    private String ftpPort;

    @Value("${ftp.host}")
    private String ftpServer;

    @Value("${ftp.user}")
    private String ftpUser;

    @Value("${ftp.password}")
    private String ftpPassword;

    @Value("${ftp.baseurl}")
    private String ftpBaseUrl;


    private static final String BACKSLASH = "/";


    public List<String> uploadFiles(MultipartFile[] file, String id, String domain) {
        if (file.length == 0) {
            log.error("No files to upload");
            return Collections.emptyList();
        }
        FtpClient ftpClient = new FtpClient(ftpServer, Integer.parseInt(ftpPort), ftpUser, ftpPassword);
        List<String> uploadedLinks = new ArrayList<>();
        try {
            ftpClient.open();

            String partialFilePath = Paths.get(id, domain).toString();
            ftpCreateDirectoryTree(ftpClient.getFtp(), partialFilePath);

            cleanDirectory(ftpClient.getFtp(), ftpBaseUrl + BACKSLASH + partialFilePath);

            for (MultipartFile multipartFile : file) {
                String path = Paths.get(ftpBaseUrl, partialFilePath, multipartFile.getOriginalFilename()).toString();
                ftpClient.getFtp().storeFile(path, multipartFile.getInputStream());
                uploadedLinks.add(String.valueOf(Paths.get(path)));
            }
            ftpClient.close();
            return uploadedLinks;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public byte[] serveFile(String link) {
        FtpClient ftpClient = new FtpClient(ftpServer, Integer.parseInt(ftpPort), ftpUser, ftpPassword);
        try {
            ftpClient.open();
            ftpClient.getFtp().setCharset(StandardCharsets.UTF_8);
            InputStream is = ftpClient.getFtp().retrieveFileStream(link);

            BufferedInputStream inbf = new BufferedInputStream(is);
            ftpClient.getFtp().completePendingCommand();
            byte[] buffer = new byte[1024];
            int readCount;
            byte[] result = null;
            int length = 0;
            while ((readCount = inbf.read(buffer)) > 0) {
                if (result == null) {
                    result = new byte[readCount];
                    System.arraycopy(buffer, 0, result, 0, readCount);
                } else {
                    byte[] temp = new byte[length + readCount];
                    System.arraycopy(result, 0, temp, 0, length);
                    System.arraycopy(buffer, 0, temp, length, readCount);
                    result = temp;
                }
                length += readCount;
            }
            ftpClient.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private void cleanDirectory(FTPClient ftpClient, String partialFilePath) throws IOException {
        //remove all files from directory ftp
        FTPFile[] files = ftpClient.listFiles(partialFilePath);
        ftpClient.changeWorkingDirectory(partialFilePath);
        for (FTPFile file : files) {
            ftpClient.deleteFile(file.getName());
        }
        ftpClient.changeWorkingDirectory(BACKSLASH);

    }

    private void ftpCreateDirectoryTree(FTPClient client, String dirTree) throws IOException {

        boolean dirExists = true;

        //tokenize the string and attempt to change into each directory level.  If you cannot, then start creating.
        String[] directories = dirTree.split(BACKSLASH);
        for (String dir : directories) {
            if (!dir.isEmpty()) {
                if (dirExists) {
                    dirExists = client.changeWorkingDirectory(dir);
                }
                if (!dirExists) {
                    createRemoteDirectory(client, dir);
                }
            }
        }
        client.changeWorkingDirectory(BACKSLASH);
    }

    private static void createRemoteDirectory(FTPClient client, String dir) throws IOException {
        if (!client.makeDirectory(dir)) {
            throw new IOException("Unable to create remote directory '" + dir + "'.  error='" + client.getReplyString() + "'");
        }
        if (!client.changeWorkingDirectory(dir)) {
            throw new IOException("Unable to change into newly created remote directory '" + dir + "'.  error='" + client.getReplyString() + "'");
        }
    }
}
