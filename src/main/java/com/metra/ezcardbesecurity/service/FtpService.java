package com.metra.ezcardbesecurity.service;

import com.metra.ezcardbesecurity.commons.FtpClient;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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


    private final String BACKSLASH = "/";


    public List<String> uploadFiles(MultipartFile[] file, String id, String domain) {
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
                uploadedLinks.add(String.valueOf(Paths.get(ftpServer, path)));
            }
            ftpClient.close();
            return uploadedLinks;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
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
                    if (!client.makeDirectory(dir)) {
                        throw new IOException("Unable to create remote directory '" + dir + "'.  error='" + client.getReplyString() + "'");
                    }
                    if (!client.changeWorkingDirectory(dir)) {
                        throw new IOException("Unable to change into newly created remote directory '" + dir + "'.  error='" + client.getReplyString() + "'");
                    }
                }
            }
        }
        client.changeWorkingDirectory(BACKSLASH);
    }
}
