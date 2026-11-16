package com.mycompany.grupo6proyecto.vista;

import com.mycompany.grupo6proyecto.*;
import java.time.LocalDate;
import java.util.Objects;
public final class EmpleadoFormularioView extends javax.swing.JPanel {
    public EmpleadoFormularioView() { this(null); }

    public EmpleadoFormularioView(Empleado actual) {
        initComponents();
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));
        ingreso.setText(LocalDate.now().toString());
            activo = actual == null || actual.isActivo();
            if (actual != null) {
                codigo.setText(actual.getCodigo()); codigo.setEditable(false);
                nombre.setText(actual.getNombre()); documento.setText(actual.getDocumento());
                cargo.setText(actual.getCargo()); ingreso.setText(actual.getFechaIngreso().toString());
                tipo.setSelectedItem(actual.getTipo());
                if (actual instanceof EmpleadoAdministrativo a) salario.setText(Double.toString(a.getSalarioBaseMensual()));
                if (actual instanceof EmpleadoPorHora h) { tarifa.setText(Double.toString(h.getTarifaPorHora())); horas.setText(Double.toString(h.getHorasTrabajadas())); }
                if (actual instanceof EmpleadoPorComision c) { ventas.setText(Double.toString(c.getVentas())); comision.setText(Double.toString(c.getPorcentajeComision())); }
            }
            tipo.addActionListener(e -> habilitar());
            habilitar();
    }

    private final boolean activo;
    private void habilitar() {
            String t = Objects.toString(tipo.getSelectedItem());
            salario.setEnabled(t.equals("ADMINISTRATIVO"));
            tarifa.setEnabled(t.equals("POR_HORA")); horas.setEnabled(t.equals("POR_HORA"));
            ventas.setEnabled(t.equals("POR_COMISION")); comision.setEnabled(t.equals("POR_COMISION"));
        }

    public Empleado leer() {
            String c = codigo.getText().trim(), n = nombre.getText().trim();
            String d = documento.getText().trim(), car = cargo.getText().trim();
            LocalDate fecha;
            try { fecha = LocalDate.parse(ingreso.getText().trim()); }
            catch (RuntimeException e) { throw new IllegalArgumentException("Fecha inválida. Use AAAA-MM-DD."); }
            Empleado e;
            try {
                e = switch (Objects.toString(tipo.getSelectedItem())) {
                    case "ADMINISTRATIVO" -> new EmpleadoAdministrativo(c, n, d, car, fecha, activo, Double.parseDouble(salario.getText().trim()));
                    case "POR_HORA" -> new EmpleadoPorHora(c, n, d, car, fecha, activo,
                            Double.parseDouble(tarifa.getText().trim()), Double.parseDouble(horas.getText().trim()));
                    case "POR_COMISION" -> new EmpleadoPorComision(c, n, d, car, fecha, activo,
                            Double.parseDouble(ventas.getText().trim()), Double.parseDouble(comision.getText().trim()));
                    default -> throw new IllegalArgumentException("Seleccione un tipo válido.");
                };
            } catch (NumberFormatException ex) { throw new IllegalArgumentException("Salario, tarifa, horas, ventas y porcentaje deben contener números válidos."); }
            EmpleadoDAO.validar(e);
            return e;
        }
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initComponents() {//GEN-BEGIN:initComponents
        codigoLabel = new javax.swing.JLabel();
        codigo = new javax.swing.JTextField();
        nombreLabel = new javax.swing.JLabel();
        nombre = new javax.swing.JTextField();
        documentoLabel = new javax.swing.JLabel();
        documento = new javax.swing.JTextField();
        cargoLabel = new javax.swing.JLabel();
        cargo = new javax.swing.JTextField();
        ingresoLabel = new javax.swing.JLabel();
        ingreso = new javax.swing.JTextField();
        tipoLabel = new javax.swing.JLabel();
        tipo = new javax.swing.JComboBox<>();
        salarioLabel = new javax.swing.JLabel();
        salario = new javax.swing.JTextField();
        tarifaLabel = new javax.swing.JLabel();
        tarifa = new javax.swing.JTextField();
        horasLabel = new javax.swing.JLabel();
        horas = new javax.swing.JTextField();
        ventasLabel = new javax.swing.JLabel();
        ventas = new javax.swing.JTextField();
        comisionLabel = new javax.swing.JLabel();
        comision = new javax.swing.JTextField();

        this.setLayout(new java.awt.GridLayout(0, 2, 8, 10));
        codigoLabel.setText("Código único:");
        this.add(codigoLabel);
        codigo.setColumns(18);
        codigo.setText("");
        this.add(codigo);
        nombreLabel.setText("Nombre:");
        this.add(nombreLabel);
        nombre.setColumns(18);
        nombre.setText("");
        this.add(nombre);
        documentoLabel.setText("Documento:");
        this.add(documentoLabel);
        documento.setColumns(18);
        documento.setText("");
        this.add(documento);
        cargoLabel.setText("Cargo:");
        this.add(cargoLabel);
        cargo.setColumns(18);
        cargo.setText("");
        this.add(cargo);
        ingresoLabel.setText("Fecha ingreso AAAA-MM-DD:");
        this.add(ingresoLabel);
        ingreso.setColumns(18);
        ingreso.setText("");
        this.add(ingreso);
        tipoLabel.setText("Tipo:");
        this.add(tipoLabel);
        tipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {"ADMINISTRATIVO", "POR_HORA", "POR_COMISION"}));
        this.add(tipo);
        salarioLabel.setText("Salario base mensual:");
        this.add(salarioLabel);
        salario.setColumns(18);
        salario.setText("0");
        this.add(salario);
        tarifaLabel.setText("Tarifa por hora:");
        this.add(tarifaLabel);
        tarifa.setColumns(18);
        tarifa.setText("0");
        this.add(tarifa);
        horasLabel.setText("Horas trabajadas:");
        this.add(horasLabel);
        horas.setColumns(18);
        horas.setText("0");
        this.add(horas);
        ventasLabel.setText("Monto de ventas:");
        this.add(ventasLabel);
        ventas.setColumns(18);
        ventas.setText("0");
        this.add(ventas);
        comisionLabel.setText("Comisión (0 a 100):");
        this.add(comisionLabel);
        comision.setColumns(18);
        comision.setText("0");
        this.add(comision);
    }//GEN-END:initComponents

    //GEN-BEGIN:variables
    private javax.swing.JLabel codigoLabel;
    private javax.swing.JTextField codigo;
    private javax.swing.JLabel nombreLabel;
    private javax.swing.JTextField nombre;
    private javax.swing.JLabel documentoLabel;
    private javax.swing.JTextField documento;
    private javax.swing.JLabel cargoLabel;
    private javax.swing.JTextField cargo;
    private javax.swing.JLabel ingresoLabel;
    private javax.swing.JTextField ingreso;
    private javax.swing.JLabel tipoLabel;
    private javax.swing.JComboBox<String> tipo;
    private javax.swing.JLabel salarioLabel;
    private javax.swing.JTextField salario;
    private javax.swing.JLabel tarifaLabel;
    private javax.swing.JTextField tarifa;
    private javax.swing.JLabel horasLabel;
    private javax.swing.JTextField horas;
    private javax.swing.JLabel ventasLabel;
    private javax.swing.JTextField ventas;
    private javax.swing.JLabel comisionLabel;
    private javax.swing.JTextField comision;
    //GEN-END:variables
}
