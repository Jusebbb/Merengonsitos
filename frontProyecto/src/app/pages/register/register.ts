import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup, FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { EmpresaDTO } from '../../dtos/empresa.dto';
import { EmpresaServices } from '../../services/Empresa/empresa.services';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, HttpClientModule, FormsModule],
  templateUrl: './register.html',
  styleUrls: ['./register.scss'],
})
export class RegisterComponent implements OnInit {

  empresaDto: EmpresaDTO = new EmpresaDTO();


  constructor(private EmpresaServices : EmpresaServices,
    private router: Router
  ){}

  ngOnInit(): void {
    
  }
  
 onSubmit() {
  console.log('DTO que se enviará:', this.empresaDto);
  this.crearEmpresa();
}

crearEmpresa() {
  this.EmpresaServices.crearEmpresa(this.empresaDto).subscribe({
    next: (res) => {
      console.log('Respuesta backend:', res);
    },
    error: (err) => {
      console.error('[Register] ERROR al crear empresa:', err);
      alert('No se pudo crear la empresa. Revisa consola.');
    }
  });
}
}
