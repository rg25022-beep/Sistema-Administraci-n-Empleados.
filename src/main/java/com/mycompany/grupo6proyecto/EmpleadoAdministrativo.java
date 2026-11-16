package com.mycompany.grupo6proyecto;

import java.time.LocalDate;

/** Su remuneración es el salario mensual fijo. */
public final class EmpleadoAdministrativo extends Empleado {
    private final double salarioBaseMensual;

    public EmpleadoAdministrativo(String codigo, String nombre, String documento, String cargo,
                                  LocalDate fechaIngreso, boolean activo, double salarioBaseMensual) {
        super(codigo, nombre, documento, cargo, fechaIngreso, activo);
        this.salarioBaseMensual = salarioBaseMensual;
    }
    public double getSalarioBaseMensual() { return salarioBaseMensual; }
    @Override public String getTipo() { return "ADMINISTRATIVO"; }
    @Override public double calcularSalario() { return salarioBaseMensual; }
}
