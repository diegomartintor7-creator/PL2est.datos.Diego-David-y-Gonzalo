package grafo;

/**
  Representa un nodo del grafo RDF.

  Las URIs tienen el formato  tipo:valor, por ejemplo:
    "persona:Marie Curie"  ->  tipo = "persona",  valor = "Marie Curie"
    "lugar:Ulm"            ->  tipo = "lugar",    valor = "Ulm"
    "1921"                 ->  tipo = "",          valor = "1921"  (literal)
 */
public class Nodo {

    private final String uriCompleta;
    private final String tipo;
    private final String valor;

    public Nodo(String uri) {
        this.uriCompleta = (uri != null) ? uri : "";

        if (uri != null && uri.contains(":")) {
            int sep    = uri.indexOf(':');
            this.tipo  = uri.substring(0, sep);
            this.valor = uri.substring(sep + 1);
        } else {
            this.tipo  = "";
            this.valor = this.uriCompleta;
        }
    }

    public String  getUri()     { return uriCompleta; }
    public String  getTipo()    { return tipo; }
    public String  getValor()   { return valor; }
    public boolean esLiteral()  { return tipo.isEmpty(); }

    @Override
    public String toString() { return uriCompleta; }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Nodo)) return false;
        return uriCompleta.equals(((Nodo) otro).uriCompleta);
    }

    @Override
    public int hashCode() { return uriCompleta.hashCode(); }
}