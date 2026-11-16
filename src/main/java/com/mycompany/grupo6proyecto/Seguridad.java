package com.mycompany.grupo6proyecto;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Hash de contraseñas PBKDF2; nunca se almacena la contraseña original. */
public final class Seguridad {
    private static final int ITERACIONES = 210000;
    private static final int LONGITUD_BITS = 256;
    private Seguridad() {}

    public static String generarHash(char[] clave) {
        if (clave == null || clave.length < 8) throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        byte[] hash = derivar(clave, sal, ITERACIONES);
        return "pbkdf2$" + ITERACIONES + "$" + Base64.getEncoder().encodeToString(sal) + "$" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(char[] clave, String almacenado) {
        try {
            if (clave == null || almacenado == null) return false;
            String[] partes = almacenado.split("\\$");
            if (partes.length != 4 || !"pbkdf2".equals(partes[0])) return false;
            int iter = Integer.parseInt(partes[1]);
            if (iter < 100000 || iter > 1000000) return false;
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            return sal.length == 16 && esperado.length == 32
                   && MessageDigest.isEqual(esperado, derivar(clave, sal, iter));
        } catch (RuntimeException ex) { return false; }
    }

    private static byte[] derivar(char[] clave, byte[] sal, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(clave, sal, iteraciones, LONGITUD_BITS);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (Exception e) { throw new IllegalStateException("No se pudo generar el hash", e); }
        finally { spec.clearPassword(); }
    }
}
