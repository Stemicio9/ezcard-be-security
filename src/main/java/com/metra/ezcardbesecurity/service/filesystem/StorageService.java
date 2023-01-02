package com.metra.ezcardbesecurity.service.filesystem;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String store(MultipartFile file, String id, String type);

    Resource getFile(String filename);

    List<String> getFileNames();

}