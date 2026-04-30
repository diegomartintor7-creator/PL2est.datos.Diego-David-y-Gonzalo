package io;

import grafo.GrafoConocimiento;
import grafo.Tripleta;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
  Carga un GrafoConocimiento desde un fichero JSON con el formato del enunciado.

  El formato esperado es:
  {
    "tipos": [ "persona", "lugar", ... ],
    "tripletas": [
      { "s": "persona:Einstein", "p": "nace_en", "o": "lugar:Ulm" },
      ...
    ]
 }
 */
public class CargadorDatos {

    /**
     * Lee el fichero JSON y construye el grafo.

      @param rutaFichero ruta al fichero .json
      @return GrafoConocimiento cargado
      @throws IOException si el fichero no existe o no se puede leer
     */
    public static GrafoConocimiento cargarDesdeFichero(String rutaFichero) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(rutaFichero));
        String json  = new String(bytes, StandardCharsets.UTF_8);

        GrafoConocimiento grafo = new GrafoConocimiento();

        // Cargar tipos
        List<String> tipos = extraerArrayStrings(json, "tipos");
        for (String tipo : tipos) {
            grafo.agregarTipo(tipo);
        }

        // Cargar tripletas
        List<String> bloques = extraerBloques(json, "tripletas");
        for (String bloque : bloques) {
            String s = extraerCampo(bloque, "s");
            String p = extraerCampo(bloque, "p");
            String o = extraerCampo(bloque, "o");
            if (s != null && p != null && o != null) {
                grafo.agregarTripleta(new Tripleta(s, p, o));
            }
        }

        System.out.println("  [OK] '" + rutaFichero + "' cargado: "
                + grafo.getNumNodos() + " nodos, "
                + grafo.getNumTripletas() + " tripletas.");
        return grafo;
    }


    // Metodos privados de parseo


    /**
      Extrae los strings del array JSON con nombre dado.
        "clave": [ "v1", "v2", ... ]
     */
    private static List<String> extraerArrayStrings(String json, String clave) {
        List<String> resultado = new ArrayList<String>();

        // Buscar la clave
        String patron = "\"" + clave + "\"";
        int pos = json.indexOf(patron);
        if (pos == -1) return resultado;

        // Buscar el '[' de apertura
        int abre = json.indexOf('[', pos + patron.length());
        if (abre == -1) return resultado;

        // Buscar el ']' de cierre (sin arrays anidados en este caso)
        int cierra = json.indexOf(']', abre);
        if (cierra == -1) return resultado;

        String contenido = json.substring(abre + 1, cierra);

        // Extraer cada "valor" entre comillas
        Matcher m = Pattern.compile("\"([^\"]*)\"").matcher(contenido);
        while (m.find()) {
            resultado.add(m.group(1));
        }
        return resultado;
    }

    /**
      Extrae los bloques objeto { ... } del array con nombre dado.
      Acota la busqueda al interior del array para no salirse de el.
     */
    private static List<String> extraerBloques(String json, String clave) {
        List<String> bloques = new ArrayList<String>();

        String patron = "\"" + clave + "\"";
        int pos = json.indexOf(patron);
        if (pos == -1) return bloques;

        int abreArray = json.indexOf('[', pos + patron.length());
        if (abreArray == -1) return bloques;

        // Encontrar el ']' que cierra este array contando corchetes
        int cierraArray = abreArray + 1;
        int profArray   = 1;
        while (cierraArray < json.length() && profArray > 0) {
            char c = json.charAt(cierraArray);
            if      (c == '[') profArray++;
            else if (c == ']') profArray--;
            cierraArray++;
        }
        // cierraArray apunta al caracter despues del ']'
        int limiteArray = cierraArray - 1;

        // Extraer cada objeto { ... } dentro del array
        int i = abreArray + 1;
        while (i < limiteArray) {
            int abreObj = json.indexOf('{', i);
            if (abreObj == -1 || abreObj >= limiteArray) break;

            // Avanzar hasta el '}' de cierre contando llaves
            int profObj = 1;
            int j = abreObj + 1;
            while (j < json.length() && profObj > 0) {
                char c = json.charAt(j);
                if      (c == '{') profObj++;
                else if (c == '}') profObj--;
                j++;
            }
            bloques.add(json.substring(abreObj, j));
            i = j;
        }
        return bloques;
    }

    /**
      Extrae el valor de  "clave": "valor"  dentro de un bloque JSON.
      El valor puede contener mayusculas, espacios, tildes y caracteres especiales.

      @return el valor encontrado, o null si la clave no existe en el bloque
     */
    private static String extraerCampo(String bloque, String clave) {
        // Patron: "clave" seguido de : y el valor entre comillas
        // El valor es cualquier secuencia de caracteres excepto comilla doble
        String patternStr = "\"" + Pattern.quote(clave) + "\"\\s*:\\s*\"([^\"]*)\"";
        Matcher m = Pattern.compile(patternStr).matcher(bloque);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}