package com.mycompany.grupo6proyecto;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.Arrays;

/** Punto de entrada de la aplicación de escritorio. */
public final class Main {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(ConexionMongo::cerrar));
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                ConexionMongo.base(); // conexión real antes de mostrar Login
                if (!new UsuarioDAO().hayUsuarios() && !crearPrimerUsuario()) return;
                new com.mycompany.grupo6proyecto.vista.LoginView().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                    "No fue posible iniciar con MongoDB Atlas.\nRevise conexion.properties, su acceso a Internet y el acceso de red en Atlas.\n\nTipo de error: " + ex.getClass().getSimpleName(),
                    "Error al iniciar", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /** Configuración de primera ejecución: sin usuarios predefinidos ni contraseñas en texto plano. */
    private static boolean crearPrimerUsuario() {
        var formulario = new com.mycompany.grupo6proyecto.vista.AdministradorInicialView();
        for (int intento = 0; intento < 3; intento++) {
            int respuesta = JOptionPane.showConfirmDialog(null, formulario,
                "Primera ejecución: crear administrador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (respuesta != JOptionPane.OK_OPTION) return false;
            char[] a = formulario.getClave(), b = formulario.getRepeticion();
            try {
                if (!Arrays.equals(a, b)) throw new IllegalArgumentException("Las contraseñas no coinciden.");
                new UsuarioDAO().crearUsuarioInicial(formulario.getUsuario(), a);
                JOptionPane.showMessageDialog(null, "Usuario inicial creado. Inicie sesión para continuar.");
                return true;
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Revise los datos", JOptionPane.WARNING_MESSAGE);
                formulario.limpiarClaves();
            } finally { Arrays.fill(a, '\0'); Arrays.fill(b, '\0'); }
        }
        return false;
    }
}
