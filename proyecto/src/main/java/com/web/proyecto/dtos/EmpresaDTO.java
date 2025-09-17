package com.web.proyecto.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EmpresaDTO {
    private Long id;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "El NIT es obligatorio")
    private String nit;
    @Email(message = "El correo de contacto no es válido")
    @NotBlank(message = "El correo de contacto es obligatorio")
    private String correoContacto;
}
