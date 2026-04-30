package grafo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
  Grafo de conocimiento RDF representado como grafo DIRIGIDO.

  Cada tripleta (S, P, O) aniade al grafo:
    - dos nodos: S (sujeto) y O (objeto)
    - una arista dirigida de S hacia O con etiqueta P (predicado)

  Estructura interna: lista de adyacencia
    Map< URI nodo origen -> lista de aristas de salida >
 */
public class GrafoConocimiento {

    /** Lista de adyacencia: nodo -> aristas que salen de el */
    private final Map<String, List<Arista>> adyacencia;

    /** Todas las tripletas cargadas */
    private final List<Tripleta> tripletas;

    /** Tipos declarados en la seccion "tipos" del JSON */
    private final List<String> tiposDeclarados;

    public GrafoConocimiento() {
        adyacencia      = new LinkedHashMap<String, List<Arista>>();
        tripletas       = new ArrayList<Tripleta>();
        tiposDeclarados = new ArrayList<String>();
    }


    // Construccion del grafo


    public void agregarTipo(String tipo) {
        tiposDeclarados.add(tipo);
    }

    /**
     Aniade una tripleta. Crea los nodos sujeto y objeto si no existen.
     */
    public void agregarTripleta(Tripleta t) {
        tripletas.add(t);
        String origen  = t.getSujeto();
        String destino = t.getObjeto();
        if (!adyacencia.containsKey(origen)) {
            adyacencia.put(origen, new ArrayList<Arista>());
        }
        if (!adyacencia.containsKey(destino)) {
            adyacencia.put(destino, new ArrayList<Arista>());
        }
        adyacencia.get(origen).add(new Arista(t.getPredicado(), destino));
    }


    // CONSULTA 1: Camino minimo entre dos nodos (BFS)


    /**
     Devuelve el camino mas corto (en numero de saltos) entre dos nodos.
      El grafo es DIRIGIDO: si no hay ruta de inicio a fin, devuelve null.

      @param inicio URI del nodo de partida
      @param fin    URI del nodo destino
      @return lista de URIs que forman el camino, o null si no existe
     */
    public List<String> caminoMinimo(String inicio, String fin) {
        if (!adyacencia.containsKey(inicio) || !adyacencia.containsKey(fin)) {
            return null;
        }
        if (inicio.equals(fin)) {
            List<String> solo = new ArrayList<String>();
            solo.add(inicio);
            return solo;
        }

        // BFS: vinoDe[nodo] = nodo previo en el camino mas corto
        Map<String, String> vinoDe      = new HashMap<String, String>();
        Queue<String>       cola        = new LinkedList<String>();
        Set<String>         visitados   = new HashSet<String>();

        cola.add(inicio);
        visitados.add(inicio);
        vinoDe.put(inicio, null);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            if (actual.equals(fin)) {
                return reconstruirCamino(vinoDe, fin);
            }
            List<Arista> salidas = adyacencia.get(actual);
            if (salidas != null) {
                for (Arista a : salidas) {
                    String vecino = a.getNodoDestino();
                    if (!visitados.contains(vecino)) {
                        visitados.add(vecino);
                        vinoDe.put(vecino, actual);
                        cola.add(vecino);
                    }
                }
            }
        }
        return null;
    }

    private List<String> reconstruirCamino(Map<String, String> vinoDe, String fin) {
        LinkedList<String> camino = new LinkedList<String>();
        String paso = fin;
        while (paso != null) {
            camino.addFirst(paso);
            paso = vinoDe.get(paso);
        }
        return camino;
    }


    // CONSULTA 2: Disjunto / componentes conexas


    /**
      Devuelve true si el grafo tiene mas de una componente conexa.
     */
    public boolean esDisjunto() {
        return getComponentesConexas().size() > 1;
    }

    /**
      Calcula las componentes conexas tratando el grafo como NO DIRIGIDO
      (ignoramos la direccion de las aristas para ver si las entidades
      estan relacionadas entre si de alguna forma).

      @return lista de conjuntos; cada conjunto es una componente conexa
     */
    public List<Set<String>> getComponentesConexas() {
        // Construir grafo bidireccional
        Map<String, List<String>> bidi = new HashMap<String, List<String>>();
        for (String nodo : adyacencia.keySet()) {
            if (!bidi.containsKey(nodo)) {
                bidi.put(nodo, new ArrayList<String>());
            }
        }
        for (String origen : adyacencia.keySet()) {
            List<Arista> salidas = adyacencia.get(origen);
            if (salidas == null) continue;
            for (Arista a : salidas) {
                String destino = a.getNodoDestino();
                if (!bidi.containsKey(destino)) {
                    bidi.put(destino, new ArrayList<String>());
                }
                bidi.get(origen).add(destino);
                bidi.get(destino).add(origen);
            }
        }

        // BFS para encontrar componentes
        Set<String>       visitados  = new HashSet<String>();
        List<Set<String>> componentes = new ArrayList<Set<String>>();

        for (String nodo : bidi.keySet()) {
            if (!visitados.contains(nodo)) {
                Set<String> componente = new LinkedHashSet<String>();
                Queue<String> cola = new LinkedList<String>();
                cola.add(nodo);
                visitados.add(nodo);
                while (!cola.isEmpty()) {
                    String actual = cola.poll();
                    componente.add(actual);
                    List<String> vecinos = bidi.get(actual);
                    if (vecinos != null) {
                        for (String vecino : vecinos) {
                            if (!visitados.contains(vecino)) {
                                visitados.add(vecino);
                                cola.add(vecino);
                            }
                        }
                    }
                }
                componentes.add(componente);
            }
        }
        return componentes;
    }


    // CONSULTA 3: Fisico Nobel nacido en la misma ciudad que una persona


    /**
     Devuelve los fisicos Nobel que nacieron en la misma ciudad que la persona
     de referencia (excluyendola a ella misma).
     */
    public List<String> fisicoNacidoEnMismaCiudad(String personaReferencia) {
        String ciudad = getCiudadNacimiento(personaReferencia);
        List<String> resultado = new ArrayList<String>();
        if (ciudad == null) return resultado;

        for (Tripleta t : tripletas) {
            if ("nace_en".equals(t.getPredicado())
                    && t.getObjeto().equals(ciudad)
                    && !t.getSujeto().equals(personaReferencia)
                    && esFisico(t.getSujeto())
                    && tieneNobel(t.getSujeto())) {
                resultado.add(t.getSujeto());
            }
        }
        return resultado;
    }


    // CONSULTA 4: Lugares de nacimiento de premios Nobel


    /**
      Devuelve un mapa persona -> lugar de nacimiento para todas las personas
      que tienen tripleta "nace_en" Y tripleta "gana_nobel".
     */
    public Map<String, String> getLugaresNacimientoNobel() {
        Map<String, String> resultado = new LinkedHashMap<String, String>();
        for (Tripleta t : tripletas) {
            if ("nace_en".equals(t.getPredicado()) && tieneNobel(t.getSujeto())) {
                resultado.put(t.getSujeto(), t.getObjeto());
            }
        }
        return resultado;
    }

    // -------------------------------------------------------------------------
    // CONSULTA 5: Tipos de nodos
    // -------------------------------------------------------------------------

    /**
      Agrupa todos los nodos por tipo (prefijo antes de ':').
      Los nodos sin prefijo (literales como "1921") se agrupan bajo "literal".
     */
    public Map<String, List<String>> getNodosPorTipo() {
        Map<String, List<String>> resultado = new LinkedHashMap<String, List<String>>();
        for (String uri : adyacencia.keySet()) {
            Nodo n    = new Nodo(uri);
            String tipo = n.getTipo().isEmpty() ? "literal" : n.getTipo();
            if (!resultado.containsKey(tipo)) {
                resultado.put(tipo, new ArrayList<String>());
            }
            resultado.get(tipo).add(uri);
        }
        return resultado;
    }


    // Metodos auxiliares


    /** Devuelve la ciudad de nacimiento de una persona, o null si no consta. */
    public String getCiudadNacimiento(String persona) {
        for (Tripleta t : tripletas) {
            if (t.getSujeto().equals(persona) && "nace_en".equals(t.getPredicado())) {
                return t.getObjeto();
            }
        }
        return null;
    }

    /** Devuelve true si la persona tiene alguna tripleta "gana_nobel". */
    public boolean tieneNobel(String persona) {
        for (Tripleta t : tripletas) {
            if (t.getSujeto().equals(persona) && "gana_nobel".equals(t.getPredicado())) {
                return true;
            }
        }
        return false;
    }

    /** Devuelve true si la persona tiene area = "Fisica". */
    public boolean esFisico(String persona) {
        for (Tripleta t : tripletas) {
            if (t.getSujeto().equals(persona)
                    && "area".equals(t.getPredicado())
                    && "F\u00edsica".equals(t.getObjeto())) {
                return true;
            }
        }
        return false;
    }


    // Getters generales


    public Set<String>    getNodos()           { return adyacencia.keySet(); }
    public List<Tripleta> getTripletas()       { return tripletas; }
    public List<String>   getTiposDeclarados() { return tiposDeclarados; }
    public int            getNumNodos()        { return adyacencia.size(); }
    public int            getNumTripletas()    { return tripletas.size(); }

    /** Imprime todos los nodos con sus aristas de salida. */
    public void mostrarGrafo() {
        System.out.println("Nodos y aristas de salida:");
        for (Map.Entry<String, List<Arista>> entrada : adyacencia.entrySet()) {
            System.out.println("  [" + entrada.getKey() + "]");
            for (Arista a : entrada.getValue()) {
                System.out.println("      " + a);
            }
        }
    }
}