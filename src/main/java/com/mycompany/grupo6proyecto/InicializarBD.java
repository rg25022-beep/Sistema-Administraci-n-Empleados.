package com.mycompany.grupo6proyecto;

import java.time.LocalDate;

/** Carga seis empleados de ejemplo sin duplicar los códigos existentes. */
public final class InicializarBD {
    /** Inserta los registros de ejemplo que aún no existen en la base de datos. */
    public static int insertarDatosDeEjemplo() {
            EmpleadoDAO dao = new EmpleadoDAO();
            Empleado[] pruebas = {
                new EmpleadoAdministrativo("EMP001", "Ana López", "DOC10001", "Administración", LocalDate.of(2025, 1, 10), true, 600),
                new EmpleadoAdministrativo("EMP002", "María Pérez", "DOC10002", "Contabilidad", LocalDate.of(2025, 3, 12), true, 850),
                new EmpleadoPorHora("EMP003", "Carlos García", "DOC10003", "Auxiliar", LocalDate.of(2025, 2, 7), true, 5, 40),
                new EmpleadoPorHora("EMP004", "Pedro Martínez", "DOC10004", "Operador", LocalDate.of(2025, 5, 20), true, 6, 36),
                new EmpleadoPorComision("EMP005", "Lucía Hernández", "DOC10005", "Vendedora", LocalDate.of(2025, 6, 10), true, 2000, 5),
                new EmpleadoPorComision("EMP006", "José Ramírez", "DOC10006", "Vendedor", LocalDate.of(2025, 7, 8), true, 3100, 8)
            };
            int nuevos = 0;
            for (Empleado e : pruebas) if (dao.buscarCodigo(e.getCodigo()) == null) { dao.registrar(e); nuevos++; }
            return nuevos;
    }
}
