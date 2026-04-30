package test.io;

import grafo.GrafoConocimiento;
import io.CargadorDatos;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Tests unitarios de CargadorDatos.
 * Crea ficheros JSON temporales en disco para probar la carga.
 */
public class Cargadordatostest {

    /** Crea un fichero .json temporal con el contenido dado. */
    private File crearJson(String contenido) throws IOException {
        File f = File.createTempFile("grafo_test_", ".json");
        f.deleteOnExit();
        Files.write(f.toPath(), contenido.getBytes(StandardCharsets.UTF_8));
        return f;
    }

    // -------------------------------------------------------------------------
    // Carga correcta
    // -------------------------------------------------------------------------

    @Test
    public void cargar_unaTripleta_creaGrafoConUnTripleta() throws IOException {
        String json = "{"
                + "\"tipos\":[\"persona\",\"lugar\"],"
                + "\"tripletas\":["
                + "  {\"s\":\"persona:Einstein\",\"p\":\"nace_en\",\"o\":\"lugar:Ulm\"}"
                + "]}";
        GrafoConocimiento g = CargadorDatos.cargarDesdeFichero(crearJson(json).getAbsolutePath());
        assertEquals(1, g.getNumTripletas());
    }

    @Test
    public void cargar_unaTripleta_creaAmbosnodos() throws IOException {
        String json = "{"
                + "\"tipos\":[],"
                + "\"tripletas\":["
                + "  {\"s\":\"persona:Einstein\",\"p\":\"nace_en\",\"o\":\"lugar:Ulm\"}"
                + "]}";
        GrafoConocimiento g = CargadorDatos.cargarDesdeFichero(crearJson(json).getAbsolutePath());
        assertTrue(g.getNodos().contains("persona:Einstein"));
        assertTrue(g.getNodos().contains("lugar:Ulm"));
    }

    @Test
    public void cargar_variasTripletas_cargaTodasCorrectamente() throws IOException {
        String json = "{"
                + "\"tipos\":[],"
                + "\"tripletas\":["
                + "  {\"s\":\"persona:A\",\"p\":\"conoce\",\"o\":\"persona:B\"},"
                + "  {\"s\":\"persona:B\",\"p\":\"conoce\",\"o\":\"persona:C\"},"
                + "  {\"s\":\"persona:C\",\"p\":\"conoce\",\"o\":\"persona:A\"}"
                + "]}";
        GrafoConocimiento g = CargadorDatos.cargarDesdeFichero(crearJson(json).getAbsolutePath());
        assertEquals(3, g.getNumTripletas());
        assertEquals(3, g.getNumNodos());
    }

    @Test
    public void cargar_tipos_seAlmacenanEnElGrafo() throws IOException {
        String json = "{"
                + "\"tipos\":[\"persona\",\"lugar\"],"
                + "\"tripletas\":[]"
                + "}";
        GrafoConocimiento g = CargadorDatos.cargarDesdeFichero(crearJson(json).getAbsolutePath());
        assertTrue(g.getTiposDeclarados().contains("persona"));
        assertTrue(g.getTiposDeclarados().contains("lugar"));
    }

    @Test
    public void cargar_nombreConEspacios_parseoCorrectamente() throws IOException {
        String json = "{"
                + "\"tipos\":[],"
                + "\"tripletas\":["
                + "  {\"s\":\"persona:Albert Einstein\",\"p\":\"nace_en\",\"o\":\"lugar:Ulm\"}"
                + "]}";
        GrafoConocimiento g = CargadorDatos.cargarDesdeFichero(crearJson(json).getAbsolutePath());
        assertEquals(1, g.getNumTripletas());
        assertTrue(g.getNodos().contains("persona:Albert Einstein"));
    }

    @Test
    public void cargar_nombreConTildes_parseoCorrectamente() throws IOException {
        String json = "{"
                + "\"tipos\":[],"
                + "\"tripletas\":["
                + "  {\"s\":\"lugar:W\u00fcrzburg\",\"p\":\"pais\",\"o\":\"lugar:Alemania\"}"
                + "]}";
        GrafoConocimiento g = CargadorDatos.cargarDesdeFichero(crearJson(json).getAbsolutePath());
        assertEquals(1, g.getNumTripletas());
        assertTrue(g.getNodos().contains("lugar:W\u00fcrzburg"));
    }

    @Test
    public void cargar_sinTripletas_grafoCeroNodos() throws IOException {
        String json = "{\"tipos\":[\"persona\"],\"tripletas\":[]}";
        GrafoConocimiento g = CargadorDatos.cargarDesdeFichero(crearJson(json).getAbsolutePath());
        assertEquals(0, g.getNumTripletas());
        assertEquals(0, g.getNumNodos());
    }

    // -------------------------------------------------------------------------
    // Error
    // -------------------------------------------------------------------------

    @Test(expected = IOException.class)
    public void cargar_ficheroInexistente_lanzaIOException() throws IOException {
        CargadorDatos.cargarDesdeFichero("ruta/inexistente/fichero.json");
    }
}