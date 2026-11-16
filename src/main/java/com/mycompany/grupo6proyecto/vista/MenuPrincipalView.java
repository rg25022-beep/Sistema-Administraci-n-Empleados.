package com.mycompany.grupo6proyecto.vista;

import com.mycompany.grupo6proyecto.*;
import com.mongodb.MongoWriteException;
import java.awt.Dimension;
import java.awt.Font;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import org.bson.Document;
public final class MenuPrincipalView extends javax.swing.JFrame {
    public MenuPrincipalView() {
        initComponents();
        empleadosTabla.setModel(empleadosModelo);
        historialTabla.setModel(historialModelo);
        empleadoCalculo.setModel(empleadosComboModelo);
        periodo.setText(YearMonth.now().toString());
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 17f));
        resultadoCalculo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        resultadoCalculo.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultado"));
        reporteArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        for (JPanel p : new JPanel[] {empleadosPanel, calculosPanel, reportesPanel})
            p.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 10, 8, 10));
        cabecera.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 16, 10, 16));
        estado.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 14, 7, 12));
        salir.addActionListener(e -> { dispose(); new LoginView().setVisible(true); });
        actualizar.addActionListener(e -> cargarEmpleados());
        buscar.addActionListener(e -> cargarEmpleados());
        filtrar.addActionListener(e -> cargarEmpleados());
        ordenar.addActionListener(e -> cargarEmpleados());
        nuevo.addActionListener(e -> abrirFormulario(null));
        modificar.addActionListener(e -> { Empleado elegido = seleccionado(); if (elegido != null) abrirFormulario(elegido); });
        inactivarBoton.addActionListener(e -> inactivar());
        limpiar.addActionListener(e -> { buscar.setText(""); filtrar.setSelectedIndex(0); ordenar.setSelectedIndex(0); cargarEmpleados(); });
        ejemplos.addActionListener(e -> {
            try {
                int nuevos = InicializarBD.insertarDatosDeEjemplo();
                cargarEmpleados();
                JOptionPane.showMessageDialog(this, "Se agregaron " + nuevos + " ejemplos nuevos. No se duplicaron los existentes.");
            } catch (RuntimeException ex) { error(ex); }
        });
        calcularBoton.addActionListener(e -> calcular());
        historialBoton.addActionListener(e -> cargarHistorial());
        generar.addActionListener(e -> generarReporte());
        pack();
        setLocationRelativeTo(null);
    }

    public MenuPrincipalView(String usuario) {
        this();
        titulo.setText("Sistema de administración de empleados | Usuario: " + usuario);
        cargarEmpleados();
        cargarHistorial();
    }

    private final EmpleadoDAO empleadosDAO = new EmpleadoDAO();
    private final CalculoDAO calculosDAO = new CalculoDAO();
    private final javax.swing.table.DefaultTableModel empleadosModelo = modelo("Código", "Nombre", "Documento", "Cargo", "Ingreso", "Tipo", "Estado", "Remuneración");
    private final javax.swing.table.DefaultTableModel historialModelo = modelo("Código", "Nombre", "Tipo", "Período", "Importe", "Fecha");
    private final javax.swing.DefaultComboBoxModel<Empleado> empleadosComboModelo = new javax.swing.DefaultComboBoxModel<>();
    private static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int fila, int col) { return false; }
        };
    }

    private static String dinero(double v) { return String.format(Locale.US, "$%.2f", v); }

    private Empleado seleccionado() {
        int fila = empleadosTabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado de la tabla.");
            return null;
        }
        String codigo = empleadosModelo.getValueAt(fila, 0).toString();
        try { return empleadosDAO.buscarCodigo(codigo); }
        catch (RuntimeException ex) { error(ex); return null; }
    }

    private void abrirFormulario(Empleado existente) {
        if (existente != null && !existente.isActivo()) {
            JOptionPane.showMessageDialog(this, "No se puede modificar un empleado inactivo.");
            return;
        }
        EmpleadoFormularioView form = new EmpleadoFormularioView(existente);
        JScrollPane scroll = new JScrollPane(form);
        scroll.setPreferredSize(new Dimension(610, 490));
        int opcion = JOptionPane.showConfirmDialog(this, scroll,
            existente == null ? "Registrar empleado" : "Modificar empleado",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) return;
        try {
            Empleado resultado = form.leer();
            if (existente == null) empleadosDAO.registrar(resultado);
            else empleadosDAO.modificar(resultado);
            cargarEmpleados();
            estado.setText("Empleado " + (existente == null ? "registrado" : "modificado") + ": " + resultado.getCodigo());
            JOptionPane.showMessageDialog(this, "Operación realizada correctamente.");
        } catch (RuntimeException ex) { error(ex); }
    }

    private void inactivar() {
        Empleado e = seleccionado();
        if (e == null) return;
        if (!e.isActivo()) { JOptionPane.showMessageDialog(this, "Este empleado ya está inactivo."); return; }
        int ok = JOptionPane.showConfirmDialog(this, "¿Inactivar a " + e.getNombre() + "? No se eliminará el historial.", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try { empleadosDAO.inactivar(e.getCodigo()); cargarEmpleados(); estado.setText("Inactivado: " + e.getCodigo()); }
        catch (RuntimeException ex) { error(ex); }
    }

    private void cargarEmpleados() {
        try {
            List<Empleado> lista = empleadosDAO.listar(buscar.getText(), Objects.toString(filtrar.getSelectedItem()), Objects.toString(ordenar.getSelectedItem()));
            empleadosModelo.setRowCount(0);
            for (Empleado e : lista) empleadosModelo.addRow(new Object[] {e.getCodigo(), e.getNombre(), e.getDocumento(), e.getCargo(), e.getFechaIngreso(), e.getTipo(), e.isActivo() ? "Activo" : "Inactivo", dinero(e.calcularSalario())});
            empleadosComboModelo.removeAllElements();
            for (Empleado e : empleadosDAO.listarTodo()) if (e.isActivo()) empleadosComboModelo.addElement(e);
            estado.setText("Empleados mostrados: " + lista.size());
        } catch (RuntimeException ex) { error(ex); }
    }

    private void calcular() {
        Empleado e = (Empleado) empleadoCalculo.getSelectedItem();
        try {
            if (e == null) throw new IllegalArgumentException("No hay empleados activos registrados.");
            String mes = periodo.getText().trim();
            YearMonth.parse(mes);
            // Reconsultar el empleado para evitar calcular datos antiguos en pantalla.
            Empleado actual = empleadosDAO.buscarCodigo(e.getCodigo());
            double total = calculosDAO.calcularYGuardar(actual, mes);
            resultadoCalculo.setText("Empleado: " + actual.getNombre() + "\nCódigo: " + actual.getCodigo() +
                "\nTipo: " + actual.getTipo() + "\nPeríodo: " + mes + "\nRemuneración: " + dinero(total) +
                "\nRegistro histórico almacenado en MongoDB.");
            cargarHistorial();
            estado.setText("Cálculo guardado para: " + actual.getCodigo());
        } catch (RuntimeException ex) {
            if (ex instanceof java.time.format.DateTimeParseException) JOptionPane.showMessageDialog(this, "Use el período AAAA-MM (ejemplo: 2026-11).");
            else error(ex);
        }
    }

    private void cargarHistorial() {
        try {
            historialModelo.setRowCount(0);
            String codigoFiltrar = buscarHistCodigo.getText().trim().toLowerCase(Locale.ROOT);
            String periodoFiltrar = buscarHistPeriodo.getText().trim();
            for (Document d : calculosDAO.historial(null)) {
                if (!codigoFiltrar.isEmpty() && !d.getString("codigo").toLowerCase(Locale.ROOT).contains(codigoFiltrar)) continue;
                if (!periodoFiltrar.isEmpty() && !periodoFiltrar.equals(d.getString("periodo"))) continue;
                Object instante = d.get("registradoEn");
                historialModelo.addRow(new Object[] {d.getString("codigo"), d.getString("nombre"), d.getString("tipo"), d.getString("periodo"), dinero(((Number) d.get("importe")).doubleValue()), instante});
            }
        } catch (RuntimeException ex) { error(ex); }
    }

    private void generarReporte() {
        try {
            List<Empleado> lista = empleadosDAO.listar("", Objects.toString(tipoReporte.getSelectedItem()), "Tipo");
            StringBuilder sb = new StringBuilder("REPORTE DE EMPLEADOS - GRUPO 6\n\n");
            sb.append(String.format("%-12s %-22s %-18s %-10s %12s%n", "CÓDIGO", "NOMBRE", "TIPO", "ESTADO", "REMUNERACIÓN"));
            sb.append("-".repeat(85)).append("\n");
            double suma = 0;
            int activos = 0;
            for (Empleado e : lista) {
                sb.append(String.format(Locale.US,"%-12s %-22.22s %-18s %-10s %12s%n", e.getCodigo(), e.getNombre(), e.getTipo(), e.isActivo() ? "ACTIVO" : "INACTIVO", dinero(e.calcularSalario())));
                if (e.isActivo()) { suma += e.calcularSalario(); activos++; }
            }
            sb.append("\nTotal de registros: ").append(lista.size()).append("\nEmpleados activos: ").append(activos);
            sb.append("\nSUMA DE REMUNERACIONES DE ACTIVOS: ").append(dinero(suma));
            sb.append("\n\nNota: la suma representa los cálculos con los datos actuales, no el historial de pagos.");
            reporteArea.setText(sb.toString());
            reporteArea.setCaretPosition(0);
            estado.setText("Reporte generado: " + lista.size() + " empleados.");
        } catch (RuntimeException ex) { error(ex); }
    }

    private void error(RuntimeException ex) {
        String msg = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        if (ex instanceof MongoWriteException m && m.getError().getCode() == 11000)
            msg = "El código o documento de empleado ya existe en MongoDB.";
        JOptionPane.showMessageDialog(this, msg, "No se pudo completar la operación", JOptionPane.ERROR_MESSAGE);
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initComponents() {//GEN-BEGIN:initComponents
        cabecera = new javax.swing.JPanel();
        titulo = new javax.swing.JLabel();
        salir = new javax.swing.JButton();
        pestanas = new javax.swing.JTabbedPane();
        empleadosPanel = new javax.swing.JPanel();
        filtros = new javax.swing.JPanel();
        buscarLabel = new javax.swing.JLabel();
        buscar = new javax.swing.JTextField();
        tipoLabel = new javax.swing.JLabel();
        filtrar = new javax.swing.JComboBox<>();
        ordenLabel = new javax.swing.JLabel();
        ordenar = new javax.swing.JComboBox<>();
        actualizar = new javax.swing.JButton();
        empleadosScroll = new javax.swing.JScrollPane();
        empleadosTabla = new javax.swing.JTable(new javax.swing.table.DefaultTableModel(new String[] {"Código", "Nombre", "Documento", "Cargo", "Ingreso", "Tipo", "Estado", "Remuneración"}, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        });
        accionesEmpleados = new javax.swing.JPanel();
        nuevo = new javax.swing.JButton();
        modificar = new javax.swing.JButton();
        inactivarBoton = new javax.swing.JButton();
        limpiar = new javax.swing.JButton();
        ejemplos = new javax.swing.JButton();
        calculosPanel = new javax.swing.JPanel();
        seleccionCalculo = new javax.swing.JPanel();
        empleadoLabel = new javax.swing.JLabel();
        empleadoCalculo = new javax.swing.JComboBox<>();
        periodoLabel = new javax.swing.JLabel();
        periodo = new javax.swing.JTextField();
        calcularBoton = new javax.swing.JButton();
        detalleCalculo = new javax.swing.JPanel();
        resultadoScroll = new javax.swing.JScrollPane();
        resultadoCalculo = new javax.swing.JTextArea();
        historialScroll = new javax.swing.JScrollPane();
        historialTabla = new javax.swing.JTable(new javax.swing.table.DefaultTableModel(new String[] {"Código", "Nombre", "Tipo", "Período", "Importe", "Fecha"}, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        });
        filtrosHistorial = new javax.swing.JPanel();
        histCodigoLabel = new javax.swing.JLabel();
        buscarHistCodigo = new javax.swing.JTextField();
        histPeriodoLabel = new javax.swing.JLabel();
        buscarHistPeriodo = new javax.swing.JTextField();
        historialBoton = new javax.swing.JButton();
        reportesPanel = new javax.swing.JPanel();
        opcionesReporte = new javax.swing.JPanel();
        reporteLabel = new javax.swing.JLabel();
        tipoReporte = new javax.swing.JComboBox<>();
        generar = new javax.swing.JButton();
        reporteScroll = new javax.swing.JScrollPane();
        reporteArea = new javax.swing.JTextArea();
        estado = new javax.swing.JLabel();

        this.setTitle("Grupo 6 - Sistema de administración de empleados");
        this.setDefaultCloseOperation(3);
        this.setMinimumSize(new java.awt.Dimension(900, 580));
        this.setPreferredSize(new java.awt.Dimension(1120, 700));
        getContentPane().setLayout(new java.awt.BorderLayout(8, 8));
        cabecera.setLayout(new java.awt.BorderLayout(8, 8));
        titulo.setText("Sistema de administración de empleados");
        cabecera.add(titulo, java.awt.BorderLayout.CENTER);
        salir.setText("Cerrar sesión");
        cabecera.add(salir, java.awt.BorderLayout.EAST);
        getContentPane().add(cabecera, java.awt.BorderLayout.NORTH);
        empleadosPanel.setLayout(new java.awt.BorderLayout(8, 8));
        filtros.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));
        buscarLabel.setText("Buscar (código/nombre/documento):");
        filtros.add(buscarLabel);
        buscar.setColumns(14);
        buscar.setText("");
        filtros.add(buscar);
        tipoLabel.setText("Tipo:");
        filtros.add(tipoLabel);
        filtrar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {"TODOS", "ADMINISTRATIVO", "POR_HORA", "POR_COMISION"}));
        filtros.add(filtrar);
        ordenLabel.setText("Orden:");
        filtros.add(ordenLabel);
        ordenar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {"Código", "Nombre", "Tipo"}));
        filtros.add(ordenar);
        actualizar.setText("Buscar / actualizar");
        filtros.add(actualizar);
        empleadosPanel.add(filtros, java.awt.BorderLayout.NORTH);
        empleadosTabla.setRowHeight(25);
        empleadosScroll.setViewportView(empleadosTabla);
        empleadosPanel.add(empleadosScroll, java.awt.BorderLayout.CENTER);
        accionesEmpleados.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));
        nuevo.setText("Registrar");
        accionesEmpleados.add(nuevo);
        modificar.setText("Modificar");
        accionesEmpleados.add(modificar);
        inactivarBoton.setText("Inactivar");
        accionesEmpleados.add(inactivarBoton);
        limpiar.setText("Quitar filtros");
        accionesEmpleados.add(limpiar);
        ejemplos.setText("Cargar 6 datos de prueba");
        accionesEmpleados.add(ejemplos);
        empleadosPanel.add(accionesEmpleados, java.awt.BorderLayout.SOUTH);
        pestanas.addTab("Empleados", empleadosPanel);
        calculosPanel.setLayout(new java.awt.BorderLayout(8, 8));
        seleccionCalculo.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));
        empleadoLabel.setText("Empleado activo:");
        seleccionCalculo.add(empleadoLabel);
        empleadoCalculo.setPreferredSize(new java.awt.Dimension(335, 30));
        seleccionCalculo.add(empleadoCalculo);
        periodoLabel.setText("Período (AAAA-MM):");
        seleccionCalculo.add(periodoLabel);
        periodo.setColumns(7);
        periodo.setText("");
        seleccionCalculo.add(periodo);
        calcularBoton.setText("Calcular y guardar en MongoDB");
        seleccionCalculo.add(calcularBoton);
        calculosPanel.add(seleccionCalculo, java.awt.BorderLayout.NORTH);
        detalleCalculo.setLayout(new java.awt.BorderLayout(8, 8));
        resultadoCalculo.setRows(4);
        resultadoCalculo.setColumns(40);
        resultadoCalculo.setEditable(false);
        resultadoCalculo.setLineWrap(true);
        resultadoCalculo.setWrapStyleWord(true);
        resultadoCalculo.setText("Seleccione un empleado y calcule su remuneración.");
        resultadoScroll.setViewportView(resultadoCalculo);
        detalleCalculo.add(resultadoScroll, java.awt.BorderLayout.NORTH);
        historialTabla.setRowHeight(24);
        historialScroll.setViewportView(historialTabla);
        detalleCalculo.add(historialScroll, java.awt.BorderLayout.CENTER);
        calculosPanel.add(detalleCalculo, java.awt.BorderLayout.CENTER);
        filtrosHistorial.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));
        histCodigoLabel.setText("Código (opcional):");
        filtrosHistorial.add(histCodigoLabel);
        buscarHistCodigo.setColumns(10);
        buscarHistCodigo.setText("");
        filtrosHistorial.add(buscarHistCodigo);
        histPeriodoLabel.setText("Período AAAA-MM (opcional):");
        filtrosHistorial.add(histPeriodoLabel);
        buscarHistPeriodo.setColumns(8);
        buscarHistPeriodo.setText("");
        filtrosHistorial.add(buscarHistPeriodo);
        historialBoton.setText("Buscar en historial");
        filtrosHistorial.add(historialBoton);
        calculosPanel.add(filtrosHistorial, java.awt.BorderLayout.SOUTH);
        pestanas.addTab("Cálculos e historial", calculosPanel);
        reportesPanel.setLayout(new java.awt.BorderLayout(8, 8));
        opcionesReporte.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));
        reporteLabel.setText("Mostrar empleados del tipo:");
        opcionesReporte.add(reporteLabel);
        tipoReporte.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {"TODOS", "ADMINISTRATIVO", "POR_HORA", "POR_COMISION"}));
        opcionesReporte.add(tipoReporte);
        generar.setText("Generar reporte");
        opcionesReporte.add(generar);
        reportesPanel.add(opcionesReporte, java.awt.BorderLayout.NORTH);
        reporteArea.setRows(22);
        reporteArea.setColumns(66);
        reporteArea.setEditable(false);
        reporteArea.setLineWrap(true);
        reporteArea.setWrapStyleWord(true);
        reporteArea.setText("Presione «Generar reporte» para consultar los datos guardados en MongoDB.");
        reporteScroll.setViewportView(reporteArea);
        reportesPanel.add(reporteScroll, java.awt.BorderLayout.CENTER);
        pestanas.addTab("Reportes", reportesPanel);
        getContentPane().add(pestanas, java.awt.BorderLayout.CENTER);
        estado.setText("Listo");
        getContentPane().add(estado, java.awt.BorderLayout.SOUTH);
    }//GEN-END:initComponents

    //GEN-BEGIN:variables
    private javax.swing.JPanel cabecera;
    private javax.swing.JLabel titulo;
    private javax.swing.JButton salir;
    private javax.swing.JTabbedPane pestanas;
    private javax.swing.JPanel empleadosPanel;
    private javax.swing.JPanel filtros;
    private javax.swing.JLabel buscarLabel;
    private javax.swing.JTextField buscar;
    private javax.swing.JLabel tipoLabel;
    private javax.swing.JComboBox<String> filtrar;
    private javax.swing.JLabel ordenLabel;
    private javax.swing.JComboBox<String> ordenar;
    private javax.swing.JButton actualizar;
    private javax.swing.JScrollPane empleadosScroll;
    private javax.swing.JTable empleadosTabla;
    private javax.swing.JPanel accionesEmpleados;
    private javax.swing.JButton nuevo;
    private javax.swing.JButton modificar;
    private javax.swing.JButton inactivarBoton;
    private javax.swing.JButton limpiar;
    private javax.swing.JButton ejemplos;
    private javax.swing.JPanel calculosPanel;
    private javax.swing.JPanel seleccionCalculo;
    private javax.swing.JLabel empleadoLabel;
    private javax.swing.JComboBox<com.mycompany.grupo6proyecto.Empleado> empleadoCalculo;
    private javax.swing.JLabel periodoLabel;
    private javax.swing.JTextField periodo;
    private javax.swing.JButton calcularBoton;
    private javax.swing.JPanel detalleCalculo;
    private javax.swing.JScrollPane resultadoScroll;
    private javax.swing.JTextArea resultadoCalculo;
    private javax.swing.JScrollPane historialScroll;
    private javax.swing.JTable historialTabla;
    private javax.swing.JPanel filtrosHistorial;
    private javax.swing.JLabel histCodigoLabel;
    private javax.swing.JTextField buscarHistCodigo;
    private javax.swing.JLabel histPeriodoLabel;
    private javax.swing.JTextField buscarHistPeriodo;
    private javax.swing.JButton historialBoton;
    private javax.swing.JPanel reportesPanel;
    private javax.swing.JPanel opcionesReporte;
    private javax.swing.JLabel reporteLabel;
    private javax.swing.JComboBox<String> tipoReporte;
    private javax.swing.JButton generar;
    private javax.swing.JScrollPane reporteScroll;
    private javax.swing.JTextArea reporteArea;
    private javax.swing.JLabel estado;
    //GEN-END:variables
}
