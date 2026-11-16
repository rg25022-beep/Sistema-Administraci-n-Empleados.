package com.mycompany.grupo6proyecto;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bson.Document;

/** Gestión y persistencia de empleados, incluida la inactivación. */
public final class EmpleadoDAO {
    private MongoCollection<Document> col() { return ConexionMongo.coleccion("empleados"); }

    public void registrar(Empleado empleado) {
        validar(empleado);
        col().insertOne(aDocumento(empleado));
    }

    public void modificar(Empleado empleado) {
        validar(empleado);
        Document anterior = col().find(Filters.eq("codigo", empleado.getCodigo())).first();
        if (anterior == null) throw new IllegalArgumentException("Empleado no encontrado.");
        if (!Boolean.TRUE.equals(anterior.getBoolean("activo"))) throw new IllegalArgumentException("No se puede modificar un empleado inactivo.");
        if (col().replaceOne(Filters.eq("codigo", empleado.getCodigo()), aDocumento(empleado)).getMatchedCount() == 0)
            throw new IllegalArgumentException("Empleado no encontrado.");
    }

    public void inactivar(String codigo) {
        if (col().updateOne(Filters.eq("codigo", codigo), Updates.set("activo", false)).getMatchedCount() == 0)
            throw new IllegalArgumentException("Empleado no encontrado.");
    }

    public Empleado buscarCodigo(String codigo) {
        Document doc = col().find(Filters.eq("codigo", codigo.trim())).first();
        return doc == null ? null : desdeDocumento(doc);
    }

    public List<Empleado> listar(String consulta, String filtroTipo, String orden) {
        List<Empleado> resultado = new ArrayList<>();
        String buscar = consulta == null ? "" : consulta.strip().toLowerCase(Locale.ROOT);
        for (Document doc : col().find()) {
            Empleado e = desdeDocumento(doc);
            if (filtroTipo != null && !"TODOS".equals(filtroTipo) && !e.getTipo().equals(filtroTipo)) continue;
            if (!buscar.isEmpty() && !(e.getCodigo().toLowerCase(Locale.ROOT).contains(buscar)
                    || e.getNombre().toLowerCase(Locale.ROOT).contains(buscar)
                    || e.getDocumento().toLowerCase(Locale.ROOT).contains(buscar))) continue;
            resultado.add(e);
        }
        java.util.Comparator<Empleado> comparador = switch (orden == null ? "Código" : orden) {
            case "Nombre" -> java.util.Comparator.comparing(Empleado::getNombre, String.CASE_INSENSITIVE_ORDER);
            case "Tipo" -> java.util.Comparator.comparing(Empleado::getTipo).thenComparing(Empleado::getCodigo);
            default -> java.util.Comparator.comparing(Empleado::getCodigo);
        };
        resultado.sort(comparador);
        return resultado;
    }

    public List<Empleado> listarTodo() { return listar("", "TODOS", "Código"); }

    public static void validar(Empleado e) {
        if (!e.getCodigo().matches("[A-Za-z0-9_-]{2,20}"))
            throw new IllegalArgumentException("El código debe tener 2 a 20 letras, números, guiones o guion bajo.");
        if (e.getNombre().isBlank() || e.getCargo().isBlank())
            throw new IllegalArgumentException("Nombre y cargo son obligatorios.");
        if (!e.getDocumento().matches("[A-Za-z0-9-]{5,25}"))
            throw new IllegalArgumentException("Documento inválido (5 a 25 letras, números o guiones).");
        if (e.getFechaIngreso().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de ingreso no puede ser futura.");
        if (e instanceof EmpleadoAdministrativo a) noNegativo(a.getSalarioBaseMensual(), "Salario base");
        if (e instanceof EmpleadoPorHora h) {
            noNegativo(h.getTarifaPorHora(), "Tarifa por hora");
            noNegativo(h.getHorasTrabajadas(), "Horas trabajadas");
        }
        if (e instanceof EmpleadoPorComision c) {
            noNegativo(c.getVentas(), "Ventas");
            noNegativo(c.getPorcentajeComision(), "Comisión");
            if (c.getPorcentajeComision() > 100) throw new IllegalArgumentException("Comisión máxima: 100%.");
        }
        if (!Double.isFinite(e.calcularSalario())) throw new IllegalArgumentException("La remuneración excede un valor válido.");
    }
    private static void noNegativo(double n, String nombre) {
        if (!Double.isFinite(n) || n < 0) throw new IllegalArgumentException(nombre + " debe ser un número no negativo.");
    }

    private static Document aDocumento(Empleado e) {
        Document d = new Document("codigo", e.getCodigo()).append("nombre", e.getNombre().trim())
                .append("documento", e.getDocumento()).append("cargo", e.getCargo().trim())
                .append("fechaIngreso", e.getFechaIngreso().toString()).append("tipo", e.getTipo())
                .append("activo", e.isActivo());
        if (e instanceof EmpleadoAdministrativo a) d.append("salarioBaseMensual", a.getSalarioBaseMensual());
        else if (e instanceof EmpleadoPorHora h) d.append("tarifaPorHora", h.getTarifaPorHora()).append("horasTrabajadas", h.getHorasTrabajadas());
        else if (e instanceof EmpleadoPorComision c) d.append("ventas", c.getVentas()).append("porcentajeComision", c.getPorcentajeComision());
        return d;
    }

    private static double numero(Document d, String campo) {
        Number n = d.get(campo, Number.class);
        return n == null ? 0 : n.doubleValue();
    }
    private static Empleado desdeDocumento(Document d) {
        String codigo = d.getString("codigo"), nombre = d.getString("nombre"), documento = d.getString("documento"), cargo = d.getString("cargo");
        LocalDate ingreso = LocalDate.parse(d.getString("fechaIngreso"));
        boolean activo = Boolean.TRUE.equals(d.getBoolean("activo"));
        return switch (d.getString("tipo")) {
            case "ADMINISTRATIVO" -> new EmpleadoAdministrativo(codigo,nombre,documento,cargo,ingreso,activo,numero(d,"salarioBaseMensual"));
            case "POR_HORA" -> new EmpleadoPorHora(codigo,nombre,documento,cargo,ingreso,activo,numero(d,"tarifaPorHora"),numero(d,"horasTrabajadas"));
            case "POR_COMISION" -> new EmpleadoPorComision(codigo,nombre,documento,cargo,ingreso,activo,numero(d,"ventas"),numero(d,"porcentajeComision"));
            default -> throw new IllegalArgumentException("Tipo desconocido en MongoDB: " + d.getString("tipo"));
        };
    }
}
