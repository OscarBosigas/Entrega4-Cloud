package org.example.domain.service.impl;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.google.common.io.ByteStreams;
import org.example.domain.dto.TaskDto;
import org.example.domain.service.TaskService;
import org.example.persistence.entities.TaskEntity;
import org.example.persistence.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository repository;
    @Override
    public List<TaskDto> all() {
        List<TaskDto> dtos = new ArrayList<>();
        List<TaskEntity> entities = repository.all();
        for (TaskEntity taskEntity: entities){
            TaskDto taskDto = TaskDto.builder().email(taskEntity.getEmail())
                    .id(taskEntity.getId()).fileName(taskEntity.getFileName())
                    .format(taskEntity.getFormat()).status(taskEntity.getStatus())
                    .time(taskEntity.getTime()).build();
            dtos.add(taskDto);
        }
        return dtos;
    }

    @Override
    public List<TaskDto> getByUser(String email) {
        List<TaskDto> dtos = new ArrayList<>();
        List<TaskEntity> entities = repository.getTasksByUser(email);
        for (TaskEntity taskEntity: entities){
            TaskDto taskDto = TaskDto.builder().email(taskEntity.getEmail())
                    .id(taskEntity.getId()).fileName(taskEntity.getFileName())
                    .format(taskEntity.getFormat()).status(taskEntity.getStatus())
                    .time(taskEntity.getTime()).build();
            dtos.add(taskDto);
        }
        return dtos;
    }

    @Override
    public TaskDto getById(int id) {
        TaskEntity taskEntity = repository.getById(id);
        TaskDto taskDto = TaskDto.builder().time(taskEntity.getTime())
                .status(taskEntity.getStatus()).format(taskEntity.getFormat())
                .fileName(taskEntity.getFileName())
                .email(taskEntity.getEmail())
                .id(taskEntity.getId()).build();
        return taskDto;
    }

    @Override
    public void deleteById(int id) {
        repository.deleteById(id);
    }

    @Override
    public void save(TaskDto taskDto) {
        TaskEntity taskEntity = TaskEntity.builder().email(taskDto.getEmail())
                .time(taskDto.getTime()).format(taskDto.getFormat())
                .status(taskDto.getStatus()).fileName(taskDto.getFileName())
                .build();
        this.repository.save(taskEntity);
    }

    public void uploadObjectFromMemory(String objectName, byte[] content) throws IOException {
        // The ID of your GCP project
        String projectId = "trusty-anchor-342404";

        // The ID of your GCS bucket
        String bucketName = "bucket-cloud-oscar";

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(".\\trusty-anchor-342404-b3673e24e9ad.json")))
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
}
