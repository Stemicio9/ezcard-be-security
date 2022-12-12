package com.metra.ezcardbesecurity.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.PrintWriter;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FtpClient {

    private String server;
    private int port;
    private String user;
    private String password;
    private FTPClient ftp;

    public FtpClient(String ftpServer, int parseInt, String ftpUser, String ftpPassword) {
        this.server = ftpServer;
        this.port = parseInt;
        this.user = ftpUser;
        this.password = ftpPassword;
    }

    public void open() throws IOException {
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.err)));

        ftp.connect(server, port);
        ftp.login(user, password);
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();

        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

    }

    public void close() throws IOException {
        ftp.disconnect();
    }
}