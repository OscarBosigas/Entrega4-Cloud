package org.example.domain.service.impl;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import org.example.domain.dto.TaskDto;
import org.example.domain.service.TaskService;
import org.example.persistence.entities.TaskEntity;
import org.example.persistence.repositories.TaskRepository;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void update(int id) {
        try {
            this.taskRepository.updateStatus(id);
        }catch (JpaSystemException e){

        }
    }

    @Override
    public List<TaskDto> sinComprimir() {
        List<TaskDto> lista = new ArrayList<>();
        List<TaskEntity> entities = new ArrayList<>();
        entities = this.taskRepository.sinComprimir();
        for(TaskEntity taskEntity : entities){
            TaskDto taskDto = TaskDto.builder().id(taskEntity.getId())
                    .format(taskEntity.getFormat()).status(taskEntity.getStatus())
                    .email(taskEntity.getEmail()).fileName(taskEntity.getFileName())
                    .time(taskEntity.getTime()).build();
            lista.add(taskDto);
        }
        System.out.println("obteniendo archivos sin comprimir...");
        return lista;
    }

    public byte[] getFile(String fileName) throws IOException {
        String projectId = "trusty-anchor-342404";
        String bucketName = "bucket-cloud-oscar";
        long startByte = 0;
        long endBytes = 1024;
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream("trusty-anchor-342404-b3673e24e9ad.json")))
                .setProjectId(projectId).build()
                .getService();
        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);

        return blob.getContent();
    }

    public void uploadObjectFromMemory(String objectName, byte[] content) throws IOException {
        // The ID of your GCP project
        String projectId = "trusty-anchor-342404";

        // The ID of your GCS bucket
        String bucketName = "bucket-cloud-oscar";

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream("trusty-anchor-342404-b3673e24e9ad.json")))
                .setProjectId(projectId).build()
                .getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.createFrom(blobInfo, new ByteArrayInputStream(content));

        System.out.println(
                "Object "
                        + objectName
                        + " uploaded to bucket "
                        + bucketName
                        + " with contents "
        );
    }
}
