package com.mycompany.grupo6proyecto;

import java.time.LocalDate;

/** Su remuneración es tarifa por hora multiplicada por horas trabajadas. */
public final class EmpleadoPorHora extends Empleado {
    private final double tarifaPorHora;
    private final double horasTrabajadas;

    public EmpleadoPorHora(String codigo, String nombre, String documento, String cargo,
                           LocalDate fechaIngreso, boolean activo, double tarifaPorHora, double horasTrabajadas) {
        super(codigo, nombre, documento, cargo, fechaIngreso, activo);
        this.tarifaPorHora = tarifaPorHora;
        this.horasTrabajadas = horasTrabajadas;
    }
    public double getTarifaPorHora() { return tarifaPorHora; }
    public double getHorasTrabajadas() { return horasTrabajadas; }
    @Override public String getTipo() { return "POR_HORA"; }
    @Override public double calcularSalario() { return tarifaPorHora * horasTrabajadas; }
}
