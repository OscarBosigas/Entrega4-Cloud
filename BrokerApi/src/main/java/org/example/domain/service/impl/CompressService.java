package org.example.domain.service.impl;

import com.google.api.client.util.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CompressService {

    public byte[] compressFile(byte[] file) throws IOException {
        ByteArrayOutputStream compressedContentStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipStream = new GZIPOutputStream(compressedContentStream);
        gzipStream.write(file);
        gzipStream.close();
        byte[] compressedContent = compressedContentStream.toByteArray();
        return compressedContent;
    }
}