package grafo;

/**
  Unidad básica de información RDF: una tripleta (Sujeto, Predicado, Objeto).
 */
public class Tripleta {

    private final String sujeto;
    private final String predicado;
    private final String objeto;

    public Tripleta(String sujeto, String predicado, String objeto) {
        this.sujeto    = sujeto;
        this.predicado = predicado;
        this.objeto    = objeto;
    }

    public String getSujeto()    { return sujeto; }
    public String getPredicado() { return predicado; }
    public String getObjeto()    { return objeto; }

    @Override
    public String toString() {
        return "<\"" + sujeto + "\", \"" + predicado + "\", \"" + objeto + "\">";
    }
}