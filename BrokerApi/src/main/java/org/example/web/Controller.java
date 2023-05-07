package org.example.web;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.example.domain.dto.TaskDto;
import org.example.domain.service.impl.EmailService;
import org.example.domain.service.impl.TaskServiceImpl;
import org.example.domain.service.impl.CompressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Log
@RestController
@RequestMapping(value = "/api")
@AllArgsConstructor
@CrossOrigin(value = "*")
public class Controller {
    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private CompressService compressService;

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedDelay = 120000)
    public ResponseEntity<byte[]> compressFile() throws IOException, InterruptedException {
        System.out.println("Iniciando hilo...");

        List<TaskDto> lista = taskService.sinComprimir();

        if(lista.size() > 0) {
            for (int i = 0; i < lista.size(); i++) {
                System.out.println("Comprimiendo archivo " + lista.get(i).getFileName());
                byte[] m = taskService.getFile(lista.get(i).getFileName());
                byte[] compressed = compressService.compressFile(m);
                taskService.uploadObjectFromMemory(
                        lista.get(i).getFileName() + "." + lista.get(i).getFormat(),
                        compressed);
                taskService.update(lista.get(i).getId());
                Thread.sleep(1000);
            }
        }
        //emailService.sendEmail("oscar.bosigas@uptc.edu.co", "Archivo " + file.getOriginalFilename() + " comprimido correctamente");
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }

}
