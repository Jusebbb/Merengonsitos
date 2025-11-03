import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-inicio-usuario',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inicio-usuario.html',  
  styleUrls: ['./inicio-usuario.scss'], 
})
export class InicioUsuarioComponent implements OnInit{

  ngOnInit(): void {
    
  }
  navegarProcesos(){

  }
  navegarActividades(){

  }
  navegarGateways(){
    
  }

}
