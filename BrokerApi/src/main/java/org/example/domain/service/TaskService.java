package org.example.domain.service;

import org.example.domain.dto.TaskDto;
import org.example.persistence.entities.TaskEntity;

import java.util.List;

public interface TaskService {

    void update(int id);

    List<TaskDto> sinComprimir();

}
