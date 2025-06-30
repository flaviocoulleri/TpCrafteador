package Clases;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestIngredientesFaltantesDirectos {
    static Crafteador crafteador;
    static Map<String, Elemento> elementos;
    static Inventario inventario;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        // Cargar recetas
        String recetasJsonStr = Files.readString(Paths.get("recetas.json"));
        ObjectMapper mapper = new ObjectMapper();
        List<RecetaJson> recetasJson = mapper.readValue(recetasJsonStr, new TypeReference<>() {});
        elementos = ElementoFactory.construirElementos(recetasJson);
        List<Receta> recetas = RecetaLoader.convertir(recetasJson, elementos);
        crafteador = new Crafteador(recetas);

        // Cargar inventario desde archivo
        inventario = InventarioLoader.cargar("inventario.json", elementos);
    }

    @Test
    void faltantesDirectosEspada() {
        ElementoComplejo espada = (ElementoComplejo) elementos.get("espada");

        // Solo evaluamos los ingredientes del primer nivel: baston y lingote
        Map<Elemento, Integer> faltantes = crafteador.ingredientesDirectosFaltantes(espada, 1, inventario);

        // Inventario tiene: 1 baston, 4 mineral, 2 carbon, 1 madera
        // Necesito: 1 baston ✔️, 2 lingotes ❌
        assertEquals(1, faltantes.size());
        assertEquals(2, faltantes.get(elementos.get("lingote")));
    }
    
    @Test
    void faltantesDirectosPico() {
        ElementoComplejo pico = (ElementoComplejo) elementos.get("pico");
        Map<Elemento, Integer> faltantes = crafteador.ingredientesDirectosFaltantes(pico, 1, inventario);

        // Requiere: 2 bastones, 3 piedra
        // Tengo: 1 bastón → falta 1; 0 piedra → falta 3
        assertEquals(2, faltantes.size());
        assertEquals(1, faltantes.get(elementos.get("baston")));
        assertEquals(3, faltantes.get(elementos.get("piedra")));
    }

    @Test
    void faltantesDirectosLingote() {
        ElementoComplejo lingote = (ElementoComplejo) elementos.get("lingote");
        Map<Elemento, Integer> faltantes = crafteador.ingredientesDirectosFaltantes(lingote, 2, inventario);

        // Requiere: 6 mineral, 2 carbón
        // Tengo: 4 mineral, 2 carbón
        assertEquals(1, faltantes.size());
        assertEquals(2, faltantes.get(elementos.get("mineral")));
    }

}
