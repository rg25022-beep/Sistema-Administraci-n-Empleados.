package com.mycompany.grupo6proyecto;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import java.util.concurrent.TimeUnit;
import org.bson.Document;

/** Conexión compartida a MongoDB y creación de índices de unicidad. */
public final class ConexionMongo {
    private static MongoClient cliente;
    private static MongoDatabase base;
    private ConexionMongo() {}

    public static synchronized MongoDatabase base() {
        if (base == null) {
            ConfiguracionMongo configuracion = ConfiguracionMongo.cargar();
            String uri = configuracion.uri();
            String nombre = configuracion.nombre();
            MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(uri))
                    .applyToClusterSettings(b -> b.serverSelectionTimeout(5, TimeUnit.SECONDS)).build();
            cliente = MongoClients.create(settings);
            try {
                base = cliente.getDatabase(nombre);
                base.runCommand(new Document("ping", 1));
                base.getCollection("usuarios").createIndex(Indexes.ascending("usuario"), new IndexOptions().unique(true));
                base.getCollection("empleados").createIndex(Indexes.ascending("codigo"), new IndexOptions().unique(true));
                base.getCollection("empleados").createIndex(Indexes.ascending("documento"), new IndexOptions().unique(true));
                base.getCollection("calculos").createIndex(Indexes.ascending("codigo", "periodo"));
            } catch (RuntimeException e) {
                cliente.close(); cliente = null; base = null;
                throw e;
            }
        }
        return base;
    }
    public static MongoCollection<Document> coleccion(String nombre) { return base().getCollection(nombre); }
    public static synchronized void cerrar() {
        if (cliente != null) cliente.close();
        cliente = null; base = null;
    }
}
