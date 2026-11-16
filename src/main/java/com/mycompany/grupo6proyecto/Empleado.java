package com.mycompany.grupo6proyecto;

import java.time.LocalDate;
import java.util.Objects;

/** Superclase de los tres tipos de empleados. */
public abstract class Empleado {
    private final String codigo;
    private final String nombre;
    private final String documento;
    private final String cargo;
    private final LocalDate fechaIngreso;
    private final boolean activo;

    protected Empleado(String codigo, String nombre, String documento, String cargo,
                       LocalDate fechaIngreso, boolean activo) {
        this.codigo = Objects.requireNonNull(codigo);
        this.nombre = Objects.requireNonNull(nombre);
        this.documento = Objects.requireNonNull(documento);
        this.cargo = Objects.requireNonNull(cargo);
        this.fechaIngreso = Objects.requireNonNull(fechaIngreso);
        this.activo = activo;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
    public String getCargo() { return cargo; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public boolean isActivo() { return activo; }

    public abstract String getTipo();
    public abstract double calcularSalario();

    @Override public String toString() { return codigo + " - " + nombre + " (" + getTipo() + ")"; }
}
