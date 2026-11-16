package com.mycompany.grupo6proyecto;

import com.mycompany.grupo6proyecto.vista.*;
import java.time.LocalDate;
import javax.swing.*;

public final class PruebasVistas {
    private static int verificaciones;
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AdministradorInicialView admin = new AdministradorInicialView();
            comprobar(admin.getUsuario().equals("admin") && admin.getClave().length == 0, "Administrador inicial sin clave predeterminada");
            EmpleadoFormularioView nuevo = new EmpleadoFormularioView();
            comprobar(campo(nuevo,"salario").isEnabled() && !campo(nuevo,"tarifa").isEnabled(), "Campos del administrativo");
            combo(nuevo,"tipo").setSelectedItem("POR_HORA");
            comprobar(campo(nuevo,"tarifa").isEnabled() && campo(nuevo,"horas").isEnabled() && !campo(nuevo,"salario").isEnabled(), "Campos del empleado por hora");
            combo(nuevo,"tipo").setSelectedItem("POR_COMISION");
            comprobar(campo(nuevo,"ventas").isEnabled() && campo(nuevo,"comision").isEnabled() && !campo(nuevo,"horas").isEnabled(), "Campos del empleado por comision");
            Empleado hora = new EmpleadoPorHora("EMP003","Carlos Garcia","DOC10003","Auxiliar",LocalDate.of(2025,2,7),true,5,40);
            EmpleadoFormularioView edicion = new EmpleadoFormularioView(hora);
            comprobar(!campo(edicion,"codigo").isEditable() && edicion.leer().calcularSalario() == 200, "Edicion conserva codigo y salario por hora");
            Empleado comision = new EmpleadoPorComision("EMP005","Lucia Hernandez","DOC10005","Vendedora",LocalDate.of(2025,6,10),true,2000,5);
            comprobar(new EmpleadoFormularioView(comision).leer().calcularSalario() == 100, "Edicion conserva comision");
            LoginView login = new LoginView();
            try {
                comprobar(login.getRootPane().getDefaultButton() != null, "Login permite Enter");
                comprobar(campo(login,"clave") instanceof JPasswordField, "Login oculta la clave");
            } finally { login.dispose(); }
            MenuPrincipalView menu = new MenuPrincipalView();
            try {
                comprobar(((JTabbedPane) componente(menu,"pestanas")).getTabCount() == 3, "Tres modulos en la ventana principal");
                comprobar(((JTable) componente(menu,"empleadosTabla")).getColumnCount() == 8, "Columnas del listado");
                comprobar(((JTable) componente(menu,"historialTabla")).getColumnCount() == 6, "Columnas del historial");
                comprobar(!((JTextArea) componente(menu,"reporteArea")).isEditable(), "Reporte de solo lectura");
            } finally { menu.dispose(); }
        });
        System.out.println("OK: " + verificaciones + " comprobaciones de formularios superadas, sin acceder a MongoDB.");
    }
    private static Object componente(Object vista, String nombre) {
        try { var f=vista.getClass().getDeclaredField(nombre); f.setAccessible(true); return f.get(vista); }
        catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }
    private static JTextField campo(Object vista,String nombre) { return (JTextField) componente(vista,nombre); }
    private static JComboBox<?> combo(Object vista,String nombre) { return (JComboBox<?>) componente(vista,nombre); }
    private static void comprobar(boolean cumple,String caso) {
        if (!cumple) throw new AssertionError(caso);
        verificaciones++;
    }
}
