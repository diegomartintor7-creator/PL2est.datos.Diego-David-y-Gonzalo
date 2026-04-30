package main;

import grafo.GrafoConocimiento;
import grafo.Tripleta;
import io.CargadorDatos;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
  Practica PL2b - Grafo de conocimiento RDF.

  Demuestra todas las consultas del enunciado:
    1. Camino minimo entre dos entidades
    2. Grafo disjunto / conexo
    3. Fisico Nobel nacido en la misma ciudad que Einstein
    4. Aniadir tripleta y listar lugares de nacimiento Nobel
   5. Tipos de nodos del grafo
 */
public class Main {

    private static final String SEP  = "============================================================";
    private static final String SEP2 = "------------------------------------------------------------";

    public static void main(String[] args) {

        System.out.println(SEP);
        System.out.println("  PRACTICA PL2b - GRAFO DE CONOCIMIENTO RDF");
        System.out.println("  Estructuras de Datos - UAH");
        System.out.println(SEP);

        // Resolvemos la ruta a la carpeta datos/ buscando desde el directorio
        // de trabajo actual hacia arriba (funciona tanto en Maven como ejecutando
        // el .jar directamente desde la raiz del proyecto)
        String raiz = resolverRaiz();


        // CONSULTA 2: Grafo conexo vs grafo disjunto

        System.out.println("\n[2] Grafo conexo vs grafo disjunto");
        System.out.println(SEP2);

        comprobarDisjunto(raiz + "datos" + File.separator + "datos_conexo.json",
                "datos_conexo.json");
        System.out.println();
        comprobarDisjunto(raiz + "datos" + File.separator + "datos_disjunto.json",
                "datos_disjunto.json");


        // Carga del grafo Nobel (usado en las consultas 1, 3, 4 y 5)

        System.out.println("\n" + SEP);
        System.out.println("[CARGA] Grafo Nobel");
        System.out.println(SEP2);

        GrafoConocimiento grafo;
        try {
            grafo = CargadorDatos.cargarDesdeFichero(
                    raiz + "datos" + File.separator + "nobel.json");
        } catch (IOException e) {
            System.err.println("ERROR cargando nobel.json: " + e.getMessage());
            return;
        }


        // CONSULTA 1: Camino minimo

        System.out.println("\n[1] Camino minimo entre dos entidades");
        System.out.println(SEP2);

        // 1 salto directo
        mostrarCamino(grafo, "persona:Albert Einstein", "lugar:Ulm");
        // 2 saltos a traves de corresponds_con -> es_padre_de
        mostrarCamino(grafo, "persona:Albert Einstein", "persona:Aage Bohr");
        // Sin camino (grafo dirigido, no hay vuelta atras)
        mostrarCamino(grafo, "lugar:Ulm", "persona:Albert Einstein");


        // CONSULTA 3: Fisico Nobel nacido en la misma ciudad que Einstein

        System.out.println("[3] Fisico Nobel nacido en la misma ciudad que Einstein");
        System.out.println(SEP2);

        String einstein = "persona:Albert Einstein";
        String ciudadE  = grafo.getCiudadNacimiento(einstein);
        System.out.println("  Einstein nacio en: " + ciudadE);

        List<String> colegas = grafo.fisicoNacidoEnMismaCiudad(einstein);
        if (colegas.isEmpty()) {
            System.out.println("  No hay otro fisico Nobel nacido en " + ciudadE);
        } else {
            System.out.println("  Fisicos Nobel nacidos tambien en " + ciudadE + ":");
            for (String f : colegas) {
                System.out.println("    - " + f);
            }
        }

        // Verificacion con Niels Bohr (el y su hijo Aage, ambos de Copenhague)
        System.out.println();
        String bohr    = "persona:Niels Bohr";
        String ciudadB = grafo.getCiudadNacimiento(bohr);
        System.out.println("  Niels Bohr nacio en: " + ciudadB);

        List<String> colegasBohr = grafo.fisicoNacidoEnMismaCiudad(bohr);
        if (colegasBohr.isEmpty()) {
            System.out.println("  No hay otro fisico Nobel nacido en " + ciudadB);
        } else {
            System.out.println("  Fisicos Nobel nacidos tambien en " + ciudadB + ":");
            for (String f : colegasBohr) {
                System.out.println("    - " + f);
            }
        }
        System.out.println();


        // CONSULTA 4: Aniadir tripleta de Antonio y listar lugares Nobel

        System.out.println("[4] Aniadir tripleta y listar lugares de nacimiento Nobel");
        System.out.println(SEP2);

        Tripleta tAntonio = new Tripleta(
                "persona:Antonio",
                "nace_en",
                "lugar:Villarrubia de los Caballeros");
        grafo.agregarTripleta(tAntonio);
        System.out.println("  Tripleta aniadida: " + tAntonio);

        System.out.println("\n  Lugares de nacimiento de premios Nobel:");
        Map<String, String> lugaresNobel = grafo.getLugaresNacimientoNobel();
        if (lugaresNobel.isEmpty()) {
            System.out.println("    (ninguno)");
        } else {
            for (Map.Entry<String, String> e : lugaresNobel.entrySet()) {
                System.out.printf("    %-40s -> %s%n", e.getKey(), e.getValue());
            }
        }
        System.out.println();
        System.out.println("  Antonio aparece en la lista: "
                + lugaresNobel.containsKey("persona:Antonio"));
        System.out.println("  (Antonio tiene 'nace_en' pero NO 'gana_nobel',");
        System.out.println("   por eso no aparece entre los Nobel)");
        System.out.println();


        // CONSULTA 5: Tipos de nodos

        System.out.println("[5] Tipos de nodos del grafo Nobel");
        System.out.println(SEP2);

        Map<String, List<String>> porTipo = grafo.getNodosPorTipo();
        for (Map.Entry<String, List<String>> e : porTipo.entrySet()) {
            System.out.println("  Tipo [" + e.getKey() + "] - "
                    + e.getValue().size() + " nodo(s):");
            for (String nodo : e.getValue()) {
                System.out.println("      * " + nodo);
            }
        }

        System.out.println("\n" + SEP);
        System.out.println("  FIN DE LA PRACTICA PL2b");
        System.out.println(SEP);
    }


    // Metodos auxiliares de presentacion


    private static void comprobarDisjunto(String ruta, String etiqueta) {
        System.out.println("  Fichero: " + etiqueta);
        try {
            GrafoConocimiento g = CargadorDatos.cargarDesdeFichero(ruta);
            boolean disjunto = g.esDisjunto();
            System.out.println("  Es disjunto: " + disjunto);
            List<Set<String>> comps = g.getComponentesConexas();
            System.out.println("  Componentes conexas: " + comps.size());
            for (int i = 0; i < comps.size(); i++) {
                System.out.println("    Componente " + (i + 1) + ": " + comps.get(i));
            }
        } catch (IOException e) {
            System.err.println("  ERROR: " + e.getMessage());
        }
    }

    private static void mostrarCamino(GrafoConocimiento g, String desde, String hasta) {
        System.out.println("  Desde : " + desde);
        System.out.println("  Hasta : " + hasta);
        List<String> camino = g.caminoMinimo(desde, hasta);
        if (camino != null) {
            System.out.println("  Camino (" + (camino.size() - 1) + " salto(s)): "
                    + String.join(" -> ", camino));
        } else {
            System.out.println("  Sin camino (grafo dirigido, no existe ruta).");
        }
        System.out.println();
    }

    /**
      Busca la carpeta "datos/" subiendo hasta 4 niveles desde el directorio
      de trabajo actual. Devuelve el prefijo con separador final, o "" si no
      la encuentra (la ruta relativa funcionara si se ejecuta desde la raiz).
     */
    private static String resolverRaiz() {
        File dir = new File(System.getProperty("user.dir"));
        for (int i = 0; i < 4; i++) {
            if (new File(dir, "datos").isDirectory()) {
                return dir.getAbsolutePath() + File.separator;
            }
            File padre = dir.getParentFile();
            if (padre == null) break;
            dir = padre;
        }
        return "";
    }
}