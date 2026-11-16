package com.mycompany.grupo6proyecto;

import java.time.LocalDate;

/** Calcula la remuneración como ventas por porcentaje dividido entre 100. */
public final class EmpleadoPorComision extends Empleado {
    private final double ventas;
    private final double porcentajeComision;

    public EmpleadoPorComision(String codigo, String nombre, String documento, String cargo,
                               LocalDate fechaIngreso, boolean activo, double ventas, double porcentajeComision) {
        super(codigo, nombre, documento, cargo, fechaIngreso, activo);
        this.ventas = ventas;
        this.porcentajeComision = porcentajeComision;
    }
    public double getVentas() { return ventas; }
    public double getPorcentajeComision() { return porcentajeComision; }
    @Override public String getTipo() { return "POR_COMISION"; }
    @Override public double calcularSalario() { return ventas * porcentajeComision / 100.0; }
}
