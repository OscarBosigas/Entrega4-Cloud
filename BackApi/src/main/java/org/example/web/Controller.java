package org.example.web;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.example.domain.dto.RequestDto;
import org.example.domain.dto.TaskDto;
import org.example.domain.dto.UserDto;
import org.example.domain.service.impl.TaskServiceImpl;
import org.example.domain.service.impl.UserServiceImpl;
import org.example.domain.utils.JwtUtil;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
@RestController
@RequestMapping(value = "/api")
@AllArgsConstructor
public class Controller {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private TaskServiceImpl taskService;

    @GetMapping("/allUsers")
    public ResponseEntity<List<UserDto>> allUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.all());
    }

    @GetMapping("/allTasks")
    public ResponseEntity<List<TaskDto>> allTasks(){
        return ResponseEntity.status(HttpStatus.OK).body(taskService.all());
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserDto> login(@RequestBody RequestDto requestDto){
        return ResponseEntity.status(HttpStatus.OK).
                body(userService.getByCredentials(requestDto.getUsername(), requestDto.getPassword()));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity signup(@RequestBody UserDto userDto){
        Map<String, Object> message = new HashMap<>();
        if(userDto.getPassword1().equals(userDto.getPassword2())){
            try {
                userService.save(userDto);
                message.put("Mensaje", "Usuario Creado");
                return new ResponseEntity<>(message, HttpStatus.CREATED);
            }catch (Exception e){}
        }
        message.put("Mensaje", "No se pudo crear el usuario");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDto>> tasksByUser(@RequestParam String email, @RequestHeader String token){
        if (!JwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getByUser(email));
    }

    @GetMapping("/task")
    public ResponseEntity<TaskDto> taskById(@RequestParam int id, @RequestHeader String token){
        if (!JwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            taskService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(taskService.getById(id));
        }catch (EntityNotFoundException e){

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("/task")
    public ResponseEntity deleteById(@RequestParam int id, @RequestHeader String token){
        Map<String, Object> message = new HashMap<>();
        if (JwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }else {
            try {
                taskService.deleteById(id);
                message.put("Mensaje", "Tarea eliminada");
                return new ResponseEntity<>(message, HttpStatus.OK);
            } catch (Exception e) {
            }
            message.put("Mensaje", "No se pudo eliminar la tarea");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

}
