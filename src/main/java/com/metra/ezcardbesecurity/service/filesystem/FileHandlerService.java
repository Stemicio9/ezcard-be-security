package com.metra.ezcardbesecurity.service.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.metra.ezcardbesecurity.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileHandlerService implements FileStorageService {

    @Value("${storage.location.folder.name}")
    private String location;


    @Override
    public Resource getFile(String link) {
        log.debug("FileHandlerService : getFileNames()");
        Path file = Paths.get(link);
        Resource resource;
        try {
            resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                log.error("FileHandlerService : File is Not Available.");
                throw new ResourceNotFoundException("File is not available or permission denied.");
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Path> getFiles() {
        log.debug("FileHandlerService : getFiles()");
        List<Path> files = new ArrayList<>();
        try {
            Files.walk(Paths.get(location)).filter(Files::isRegularFile).forEach(files::add);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return files;
    }

    @Override
    public List<String> getFileNames() {
        log.debug("FileHandlerService : getFileNames()");
        List<String> fileNames = new ArrayList<>();
        try {
            getFiles().forEach(path -> fileNames.add(path.getFileName().toString()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return fileNames;
    }

    public List<String> uploadFiles(MultipartFile[] files, String id, String type) {
        log.debug("FileHandlerService : uploadFiles()");
        List<String> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadedFiles.add(store(file, id, type));
        }
        return uploadedFiles;
    }

    @Override
    public String store(MultipartFile file, String id, String type) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.debug("FileHandlerService : store() " + fileName);
        try (InputStream inputStream = file.getInputStream()) {
            String link = location + "/" + id + "/" + type + "/" + fileName;
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                log.error("FileHandlerService : File Name Contains Invalid Characters.");
                throw new RuntimeException("File Name Contains Invalid Characters.");
            }
            //check if file already exists, if so, delete it and replace it with the new one
            Path filePath = Paths.get(link);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            Files.createDirectories(Paths.get(location + "/" + id + "/" + type));
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return link;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to store file " + fileName, e);
        }
    }
}