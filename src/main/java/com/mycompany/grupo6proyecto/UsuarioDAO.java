package com.mycompany.grupo6proyecto;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.Arrays;
import org.bson.Document;

/** Consulta credenciales verificadas contra MongoDB. */
public final class UsuarioDAO {
    public boolean hayUsuarios() { return ConexionMongo.coleccion("usuarios").find().first() != null; }
    public boolean autenticar(String usuario, char[] clave) {
        try {
            if (usuario == null || usuario.isBlank() || clave == null) return false;
            Document doc = ConexionMongo.coleccion("usuarios").find(Filters.eq("usuario", usuario.trim())).first();
            return doc != null && Seguridad.verificar(clave, doc.getString("hash"));
        } finally { if (clave != null) Arrays.fill(clave, '\0'); }
    }

    public boolean crearUsuarioInicial(String usuario, char[] clave) {
        try {
            if (usuario == null || !usuario.matches("[A-Za-z0-9._-]{3,32}"))
                throw new IllegalArgumentException("El usuario debe tener de 3 a 32 caracteres válidos.");
            MongoCollection<Document> col = ConexionMongo.coleccion("usuarios");
            if (col.find(Filters.eq("usuario", usuario)).first() != null) return false;
            col.insertOne(new Document("usuario", usuario).append("hash", Seguridad.generarHash(clave)));
            return true;
        } finally { if (clave != null) Arrays.fill(clave, '\0'); }
    }
}
