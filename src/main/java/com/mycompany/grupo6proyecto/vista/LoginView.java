package com.mycompany.grupo6proyecto.vista;

import com.mycompany.grupo6proyecto.*;
import javax.swing.*;
public final class LoginView extends javax.swing.JFrame {
    public LoginView() {
        initComponents();
        ((JPanel) getContentPane()).setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 22, 22, 22));
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 19f));
        ingresarBoton.addActionListener(e -> ingresar());
        clave.addActionListener(e -> ingresar());
        getRootPane().setDefaultButton(ingresarBoton);
        pack();
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        String u = usuario.getText().trim();
        char[] p = clave.getPassword();
        if (u.isBlank() || p.length == 0) {
            java.util.Arrays.fill(p, '\0');
            JOptionPane.showMessageDialog(this, "Ingrese usuario y contraseña.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (new UsuarioDAO().autenticar(u, p)) {
                dispose();
                new MenuPrincipalView(u).setVisible(true);
            } else {
                clave.setText("");
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo verificar el acceso. Revise la conexión con MongoDB.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally { java.util.Arrays.fill(p, '\0'); }
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initComponents() {//GEN-BEGIN:initComponents
        titulo = new javax.swing.JLabel();
        credencialesContenedor = new javax.swing.JPanel();
        credenciales = new javax.swing.JPanel();
        usuarioLabel = new javax.swing.JLabel();
        usuario = new javax.swing.JTextField();
        claveLabel = new javax.swing.JLabel();
        clave = new javax.swing.JPasswordField();
        ingresarBoton = new javax.swing.JButton();

        this.setTitle("Grupo 6 - Inicio de sesión");
        this.setDefaultCloseOperation(3);
        this.setResizable(false);
        this.setPreferredSize(new java.awt.Dimension(440, 290));
        getContentPane().setLayout(new java.awt.BorderLayout(8, 8));
        titulo.setText("Administración de empleados");
        titulo.setHorizontalAlignment(0);
        getContentPane().add(titulo, java.awt.BorderLayout.NORTH);
        credencialesContenedor.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));
        credenciales.setLayout(new java.awt.GridLayout(0, 2, 8, 10));
        usuarioLabel.setText("Usuario:");
        credenciales.add(usuarioLabel);
        usuario.setColumns(18);
        usuario.setText("");
        credenciales.add(usuario);
        claveLabel.setText("Contraseña:");
        credenciales.add(claveLabel);
        clave.setColumns(18);
        credenciales.add(clave);
        credencialesContenedor.add(credenciales);
        getContentPane().add(credencialesContenedor, java.awt.BorderLayout.CENTER);
        ingresarBoton.setText("Iniciar sesión");
        getContentPane().add(ingresarBoton, java.awt.BorderLayout.SOUTH);
    }//GEN-END:initComponents

    //GEN-BEGIN:variables
    private javax.swing.JLabel titulo;
    private javax.swing.JPanel credencialesContenedor;
    private javax.swing.JPanel credenciales;
    private javax.swing.JLabel usuarioLabel;
    private javax.swing.JTextField usuario;
    private javax.swing.JLabel claveLabel;
    private javax.swing.JPasswordField clave;
    private javax.swing.JButton ingresarBoton;
    //GEN-END:variables
}
