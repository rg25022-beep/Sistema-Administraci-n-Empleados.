package com.mycompany.grupo6proyecto;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import java.time.Instant;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/** Registra los resultados históricos sin alterar cálculos anteriores. */
public final class CalculoDAO {
    public double calcularYGuardar(Empleado e, String periodo) {
        if (e == null) throw new IllegalArgumentException("Empleado inexistente.");
        if (!e.isActivo()) throw new IllegalArgumentException("No se pueden calcular salarios de empleados inactivos.");
        YearMonth.parse(periodo); // YYYY-MM
        EmpleadoDAO.validar(e);
        double total = e.calcularSalario();
        Document valores = new Document();
        if (e instanceof EmpleadoAdministrativo a) valores.append("salarioBaseMensual", a.getSalarioBaseMensual());
        else if (e instanceof EmpleadoPorHora h) valores.append("tarifaPorHora", h.getTarifaPorHora()).append("horasTrabajadas", h.getHorasTrabajadas());
        else if (e instanceof EmpleadoPorComision c) valores.append("ventas", c.getVentas()).append("porcentajeComision", c.getPorcentajeComision());
        Document registro = new Document("codigo", e.getCodigo()).append("nombre", e.getNombre())
                .append("tipo", e.getTipo()).append("periodo", periodo)
                .append("valores", valores).append("importe", total).append("registradoEn", java.util.Date.from(Instant.now()));
        ConexionMongo.coleccion("calculos").insertOne(registro);
        return total;
    }

    public List<Document> historial(String codigo) {
        List<Document> docs = new ArrayList<>();
        var query = codigo == null || codigo.isBlank()
                ? ConexionMongo.coleccion("calculos").find()
                : ConexionMongo.coleccion("calculos").find(Filters.eq("codigo", codigo));
        for (Document d : query.sort(Sorts.descending("registradoEn"))) docs.add(d);
        return docs;
    }
}
