package test.grafo;

import grafo.GrafoConocimiento;
import grafo.Tripleta;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

/**
 * Tests unitarios de GrafoConocimiento.
 */
public class Grafoconocimientotest {

    private GrafoConocimiento grafo;

    @Before
    public void setUp() {
        grafo = new GrafoConocimiento();
        grafo.agregarTipo("persona");
        grafo.agregarTipo("lugar");

        // Einstein: fisico Nobel nacido en Ulm
        grafo.agregarTripleta(new Tripleta("persona:Einstein", "nace_en",    "lugar:Ulm"));
        grafo.agregarTripleta(new Tripleta("persona:Einstein", "gana_nobel", "1921"));
        grafo.agregarTripleta(new Tripleta("persona:Einstein", "area",       "F\u00edsica"));

        // Bohr: fisico Nobel en Copenhague, conectado con Einstein
        grafo.agregarTripleta(new Tripleta("persona:Einstein", "corresponde_con", "persona:Bohr"));
        grafo.agregarTripleta(new Tripleta("persona:Bohr",     "nace_en",         "lugar:Copenhague"));
        grafo.agregarTripleta(new Tripleta("persona:Bohr",     "gana_nobel",      "1922"));
        grafo.agregarTripleta(new Tripleta("persona:Bohr",     "area",            "F\u00edsica"));

        // Muller: fisico Nobel nacido tambien en Ulm (misma ciudad que Einstein)
        grafo.agregarTripleta(new Tripleta("persona:Muller", "nace_en",    "lugar:Ulm"));
        grafo.agregarTripleta(new Tripleta("persona:Muller", "gana_nobel", "1952"));
        grafo.agregarTripleta(new Tripleta("persona:Muller", "area",       "F\u00edsica"));

        // Antonio: tiene nace_en pero NO gana_nobel
        grafo.agregarTripleta(new Tripleta("persona:Antonio", "nace_en", "lugar:Villarrubia"));
    }

    // -------------------------------------------------------------------------
    // Construccion basica
    // -------------------------------------------------------------------------

    @Test
    public void agregarTripleta_creaAmbosnodos() {
        assertTrue(grafo.getNodos().contains("persona:Einstein"));
        assertTrue(grafo.getNodos().contains("lugar:Ulm"));
    }

    @Test
    public void getNumTripletas_devuelveTotalCorrecto() {
        assertEquals(11, grafo.getNumTripletas());
    }

    @Test
    public void getTiposDeclarados_contieneLosTiposAgregados() {
        List<String> tipos = grafo.getTiposDeclarados();
        assertTrue(tipos.contains("persona"));
        assertTrue(tipos.contains("lugar"));
    }

    // -------------------------------------------------------------------------
    // Camino minimo (BFS)
    // -------------------------------------------------------------------------

    @Test
    public void caminoMinimo_unSalto_devuelveCaminoDe2Nodos() {
        List<String> camino = grafo.caminoMinimo("persona:Einstein", "lugar:Ulm");
        assertNotNull(camino);
        assertEquals(2, camino.size());
        assertEquals("persona:Einstein", camino.get(0));
        assertEquals("lugar:Ulm",        camino.get(1));
    }

    @Test
    public void caminoMinimo_dosSaltos_devuelveCaminoDe3Nodos() {
        // Einstein --corresponde_con--> Bohr --nace_en--> Copenhague
        List<String> camino = grafo.caminoMinimo("persona:Einstein", "lugar:Copenhague");
        assertNotNull(camino);
        assertEquals(3, camino.size());
        assertEquals("persona:Einstein",  camino.get(0));
        assertEquals("persona:Bohr",      camino.get(1));
        assertEquals("lugar:Copenhague",  camino.get(2));
    }

    @Test
    public void caminoMinimo_mismoNodo_devuelveListaDeUnElemento() {
        List<String> camino = grafo.caminoMinimo("persona:Einstein", "persona:Einstein");
        assertNotNull(camino);
        assertEquals(1, camino.size());
        assertEquals("persona:Einstein", camino.get(0));
    }

    @Test
    public void caminoMinimo_sinRuta_devuelveNull() {
        // Grafo dirigido: no hay arista de Ulm hacia Einstein
        List<String> camino = grafo.caminoMinimo("lugar:Ulm", "persona:Einstein");
        assertNull(camino);
    }

    @Test
    public void caminoMinimo_nodoInexistente_devuelveNull() {
        List<String> camino = grafo.caminoMinimo("persona:Einstein", "persona:Nadie");
        assertNull(camino);
    }

    // -------------------------------------------------------------------------
    // Disjunto / componentes conexas
    // -------------------------------------------------------------------------

    @Test
    public void esDisjunto_grafoConexo_devuelveFalse() {
        GrafoConocimiento g = new GrafoConocimiento();
        g.agregarTripleta(new Tripleta("persona:A", "conoce", "persona:B"));
        g.agregarTripleta(new Tripleta("persona:B", "conoce", "persona:C"));
        assertFalse(g.esDisjunto());
    }

    @Test
    public void esDisjunto_grafoDisjunto_devuelveTrue() {
        GrafoConocimiento g = new GrafoConocimiento();
        g.agregarTripleta(new Tripleta("persona:A", "conoce", "persona:B"));
        g.agregarTripleta(new Tripleta("persona:C", "conoce", "persona:D"));
        assertTrue(g.esDisjunto());
    }

    @Test
    public void getComponentesConexas_dosGrupos_devuelveDosComponentes() {
        GrafoConocimiento g = new GrafoConocimiento();
        g.agregarTripleta(new Tripleta("persona:A", "conoce", "persona:B"));
        g.agregarTripleta(new Tripleta("persona:C", "conoce", "persona:D"));
        assertEquals(2, g.getComponentesConexas().size());
    }

    @Test
    public void getComponentesConexas_unGrupo_devuelveUnaComponente() {
        GrafoConocimiento g = new GrafoConocimiento();
        g.agregarTripleta(new Tripleta("persona:A", "conoce", "persona:B"));
        g.agregarTripleta(new Tripleta("persona:B", "conoce", "persona:C"));
        assertEquals(1, g.getComponentesConexas().size());
    }

    // -------------------------------------------------------------------------
    // Fisico Nobel nacido en misma ciudad
    // -------------------------------------------------------------------------

    @Test
    public void fisicoNacidoEnMismaCiudad_hayCoincidencia_devuelveResultado() {
        List<String> resultado = grafo.fisicoNacidoEnMismaCiudad("persona:Einstein");
        assertFalse(resultado.isEmpty());
        assertTrue(resultado.contains("persona:Muller"));
    }

    @Test
    public void fisicoNacidoEnMismaCiudad_noIncluyeAlPropio() {
        List<String> resultado = grafo.fisicoNacidoEnMismaCiudad("persona:Einstein");
        assertFalse(resultado.contains("persona:Einstein"));
    }

    @Test
    public void fisicoNacidoEnMismaCiudad_sinCoincidencia_devuelveVacia() {
        // Bohr nacio en Copenhague: nadie mas en el grafo nacio ahi
        List<String> resultado = grafo.fisicoNacidoEnMismaCiudad("persona:Bohr");
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void fisicoNacidoEnMismaCiudad_personaInexistente_devuelveVacia() {
        List<String> resultado = grafo.fisicoNacidoEnMismaCiudad("persona:Nadie");
        assertTrue(resultado.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Lugares de nacimiento Nobel
    // -------------------------------------------------------------------------

    @Test
    public void getLugaresNacimientoNobel_incluyePersonasConNobel() {
        Map<String, String> lugares = grafo.getLugaresNacimientoNobel();
        assertTrue(lugares.containsKey("persona:Einstein"));
        assertTrue(lugares.containsKey("persona:Bohr"));
        assertTrue(lugares.containsKey("persona:Muller"));
    }

    @Test
    public void getLugaresNacimientoNobel_excluyeSinNobel() {
        Map<String, String> lugares = grafo.getLugaresNacimientoNobel();
        assertFalse(lugares.containsKey("persona:Antonio"));
    }

    @Test
    public void getLugaresNacimientoNobel_ciudadCorrecta() {
        Map<String, String> lugares = grafo.getLugaresNacimientoNobel();
        assertEquals("lugar:Ulm", lugares.get("persona:Einstein"));
    }

    // -------------------------------------------------------------------------
    // Tipos de nodos
    // -------------------------------------------------------------------------

    @Test
    public void getNodosPorTipo_contienePersonaYLugar() {
        Map<String, List<String>> tipos = grafo.getNodosPorTipo();
        assertTrue(tipos.containsKey("persona"));
        assertTrue(tipos.containsKey("lugar"));
    }

    @Test
    public void getNodosPorTipo_contieneListeral() {
        Map<String, List<String>> tipos = grafo.getNodosPorTipo();
        assertTrue(tipos.containsKey("literal"));
    }

    @Test
    public void getNodosPorTipo_einsteinEstaEnPersonas() {
        Map<String, List<String>> tipos = grafo.getNodosPorTipo();
        List<String> personas = tipos.get("persona");
        assertNotNull(personas);
        assertTrue(personas.contains("persona:Einstein"));
    }

    // -------------------------------------------------------------------------
    // Metodos auxiliares
    // -------------------------------------------------------------------------

    @Test
    public void getCiudadNacimiento_devuelveCiudadCorrecta() {
        assertEquals("lugar:Ulm", grafo.getCiudadNacimiento("persona:Einstein"));
    }

    @Test
    public void getCiudadNacimiento_personaSinCiudad_devuelveNull() {
        assertNull(grafo.getCiudadNacimiento("persona:Nadie"));
    }

    @Test
    public void tieneNobel_conNobel_devuelveTrue() {
        assertTrue(grafo.tieneNobel("persona:Einstein"));
    }

    @Test
    public void tieneNobel_sinNobel_devuelveFalse() {
        assertFalse(grafo.tieneNobel("persona:Antonio"));
    }

    @Test
    public void esFisico_fisico_devuelveTrue() {
        assertTrue(grafo.esFisico("persona:Einstein"));
    }

    @Test
    public void esFisico_sinArea_devuelveFalse() {
        assertFalse(grafo.esFisico("persona:Antonio"));
    }
}