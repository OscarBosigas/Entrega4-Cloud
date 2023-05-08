import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { TasksService } from '../services/tasks.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Usuario } from '../services/usuario';
import { Archivos } from '../services/task';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-crud',
  templateUrl: './crud.component.html',
  styleUrls: ['./crud.component.css']
})
export class CrudComponent implements OnInit {

  constructor(private service : TasksService,
     private router: Router, private route: ActivatedRoute,
     private changeDetectorRef: ChangeDetectorRef) { }

  task : any;

  tasks: Array<Archivos> = [];

  isUploaded = false;

  ngOnInit(): void {
    this.getTasksByUser();
  }

  getTaskById() {
    const id = this.route.snapshot.params['id'];
    const usuarioJson = localStorage.getItem('usuario');
    if (usuarioJson !== null) {
      const usuario: Usuario = JSON.parse(usuarioJson);
      this.service.getTaskById(id, usuario)
      .subscribe(data => {
        this.task = data;
      });
    }
    
  }

  getTasksByUser() {
    
    const usuarioJson = localStorage.getItem('usuario');
    if (usuarioJson !== null) {
      const usuario: Usuario = JSON.parse(usuarioJson);
      this.service.getTasksByUser(usuario)
      .subscribe(data => {
        this.tasks = data;
      });
    }
  }

  onBorrar(id: number){
    const usuarioJson = localStorage.getItem('usuario');
    if (usuarioJson !== null) {
      const usuario: Usuario = JSON.parse(usuarioJson);
    this.service.deleteTask(id, usuario)
    .pipe(
      switchMap(async () => this.getTasksByUser())
    )
    .subscribe(res =>{
      this.changeDetectorRef.detectChanges();
      alert('Tarea eliminada')
    });
    }
  }

  onAgregar(){
    this.router.navigate(['archivo'])
  }

  onDescargar(nombre: string, formato: string, status: string){
    const file = nombre +"."+ formato;
    if(status != "uploaded"){
      this.isUploaded = true
      this.service.downloadFile(file);
    }
  }
}
