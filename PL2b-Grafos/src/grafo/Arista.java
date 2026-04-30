package grafo;

/**
 Arista dirigida del grafo RDF.

 nodoOrigen --[ etiqueta ]--> nodoDestino

  La etiqueta es el predicado de la tripleta (p.ej. "nace_en").
 */
public class Arista {

    private final String etiqueta;
    private final String nodoDestino;

    public Arista(String etiqueta, String nodoDestino) {
        this.etiqueta     = etiqueta;
        this.nodoDestino  = nodoDestino;
    }

    public String getEtiqueta()    { return etiqueta; }
    public String getNodoDestino() { return nodoDestino; }

    @Override
    public String toString() {
        return "--[" + etiqueta + "]--> " + nodoDestino;
    }
}