package com.mycompany.grupo6proyecto;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Lee la configuración de conexión desde un archivo externo. */
record ConfiguracionMongo(String uri, String nombre) {
    static ConfiguracionMongo cargar() {
        Path archivo = buscarArchivo();
        Properties valores = new Properties();
        try (Reader entrada = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            valores.load(entrada);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer conexion.properties.");
        }
        String uri = valores.getProperty("mongodb.uri", "").trim();
        String nombre = valores.getProperty("mongodb.database", "grupo6_empleados").trim();
        if (!(uri.startsWith("mongodb+srv://") || uri.startsWith("mongodb://")) || nombre.isBlank()) {
            throw new IllegalStateException("Revise mongodb.uri y mongodb.database en conexion.properties.");
        }
        return new ConfiguracionMongo(uri, nombre);
    }

    private static Path buscarArchivo() {
        Path actual = Path.of("conexion.properties").toAbsolutePath();
        if (Files.isRegularFile(actual)) return actual;
        try {
            Path ubicacion = Path.of(ConfiguracionMongo.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            Path carpeta = Files.isDirectory(ubicacion) ? ubicacion : ubicacion.getParent();
            // Busca la configuración en las carpetas superiores a las clases compiladas.
            for (int nivel = 0; nivel < 4 && carpeta != null; nivel++, carpeta = carpeta.getParent()) {
                Path candidato = carpeta.resolve("conexion.properties");
                if (Files.isRegularFile(candidato)) return candidato;
            }
        } catch (java.net.URISyntaxException e) {
            throw new IllegalStateException("No se pudo localizar la carpeta del proyecto.");
        }
        throw new IllegalStateException("Falta conexion.properties junto al proyecto o al JAR.");
    }

    @Override public String toString() {
        return "ConfiguracionMongo[uri=oculta, nombre=" + nombre + "]";
    }
}
