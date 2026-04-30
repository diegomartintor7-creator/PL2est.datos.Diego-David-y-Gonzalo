package test.grafo;

import grafo.Arista;
import grafo.Nodo;
import grafo.Tripleta;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests unitarios de Tripleta, Nodo y Arista.
 */
public class ModeloTest {

    // -------------------------------------------------------------------------
    // Tripleta
    // -------------------------------------------------------------------------

    @Test
    public void tripleta_getters_devuelvenValoresCorrectos() {
        Tripleta t = new Tripleta("persona:Einstein", "nace_en", "lugar:Ulm");
        assertEquals("persona:Einstein", t.getSujeto());
        assertEquals("nace_en",          t.getPredicado());
        assertEquals("lugar:Ulm",        t.getObjeto());
    }

    @Test
    public void tripleta_toString_contieneLosTresCampos() {
        Tripleta t = new Tripleta("persona:Einstein", "nace_en", "lugar:Ulm");
        String s = t.toString();
        assertTrue(s.contains("persona:Einstein"));
        assertTrue(s.contains("nace_en"));
        assertTrue(s.contains("lugar:Ulm"));
    }

    // -------------------------------------------------------------------------
    // Nodo
    // -------------------------------------------------------------------------

    @Test
    public void nodo_conPrefijo_parseTipoYValorCorrectamente() {
        Nodo n = new Nodo("persona:Marie Curie");
        assertEquals("persona",     n.getTipo());
        assertEquals("Marie Curie", n.getValor());
        assertEquals("persona:Marie Curie", n.getUri());
        assertFalse(n.esLiteral());
    }

    @Test
    public void nodo_sinPrefijo_esLiteral() {
        Nodo n = new Nodo("1921");
        assertEquals("",     n.getTipo());
        assertEquals("1921", n.getValor());
        assertTrue(n.esLiteral());
    }

    @Test
    public void nodo_null_noLanzaExcepcion() {
        Nodo n = new Nodo(null);
        assertEquals("", n.getUri());
        assertTrue(n.esLiteral());
    }

    @Test
    public void nodo_mismaUri_sonIguales() {
        Nodo a = new Nodo("lugar:Ulm");
        Nodo b = new Nodo("lugar:Ulm");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void nodo_distintaUri_sonDistintos() {
        Nodo a = new Nodo("lugar:Ulm");
        Nodo b = new Nodo("lugar:Berlin");
        assertNotEquals(a, b);
    }

    // -------------------------------------------------------------------------
    // Arista
    // -------------------------------------------------------------------------

    @Test
    public void arista_getters_devuelvenValoresCorrectos() {
        Arista a = new Arista("nace_en", "lugar:Ulm");
        assertEquals("nace_en",   a.getEtiqueta());
        assertEquals("lugar:Ulm", a.getNodoDestino());
    }

    @Test
    public void arista_toString_contieneEtiquetaYDestino() {
        Arista a = new Arista("nace_en", "lugar:Ulm");
        String s = a.toString();
        assertTrue(s.contains("nace_en"));
        assertTrue(s.contains("lugar:Ulm"));
    }
}