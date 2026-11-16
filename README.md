# Sistema de administración de empleados — Grupo 6

Proyecto de Programación Orientada a Objetos desarrollado en Java 21 con interfaces Swing editables en Apache NetBeans 22 y persistencia en MongoDB Atlas.

**Autora:** Steffany Lisbeth Reyes Gómez — RG25022. Grupo teórico 3; grupo de proyecto 6.

## Funcionalidades

- Inicio de sesión con contraseñas almacenadas mediante PBKDF2.
- Registro, modificación, búsqueda e inactivación de empleados.
- Remuneración de empleados administrativos, por hora y por comisión.
- Cálculos guardados, historial y reportes.
- Datos ficticios de demostración.

## Ejecutar en Apache NetBeans

1. Instalar JDK 21 y Apache NetBeans 22 con Maven.
2. Abrir esta carpeta como proyecto Maven.
3. Copiar `conexion.properties.ejemplo` como `conexion.properties`, junto a `pom.xml`, y completar la conexión autorizada de Atlas.
4. Autorizar la IP del equipo en Atlas y presionar **F6**.

Clase principal: `com.mycompany.grupo6proyecto.Main`.
Si la base no tiene usuarios, la aplicación solicita crear el administrador inicial.
Las credenciales y los respaldos privados se excluyen del repositorio.

## Formularios visuales

El paquete `com.mycompany.grupo6proyecto.vista` contiene `LoginView`, `MenuPrincipalView`, `EmpleadoFormularioView` y `AdministradorInicialView`. Cada formulario conserva sus archivos `.java` y `.form` para edición desde **Design**.

## Verificación

Se superaron 24 comprobaciones locales de lógica, seguridad y formularios, además de 17 comprobaciones de integración sobre una copia restaurada de Atlas. Se verificaron los datos, la autenticación, las operaciones de empleados, un cálculo, el historial y el reporte. Los registros temporales se retiraron al finalizar.

Las clases de pruebas locales se encuentran en `src/test/java`.
