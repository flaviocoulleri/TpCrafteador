package Clases;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestIngredientesDirectos {
    static Crafteador crafteador;
    static Map<String, Elemento> elementos;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        // Leer y parsear recetas.json
        String recetasJsonStr = Files.readString(Paths.get("recetas.json"));
        ObjectMapper mapper = new ObjectMapper();
        List<RecetaJson> recetasJson = mapper.readValue(recetasJsonStr, new TypeReference<>() {});
        
        // Construcción de elementos y recetas
        elementos = ElementoFactory.construirElementos(recetasJson);
        List<Receta> recetas = RecetaLoader.convertir(recetasJson, elementos);
        crafteador = new Crafteador(recetas);
    }

    @Test
    void ingredientesDirectosAntorcha() {
        // Obtener el objeto "antorcha" del mismo mapa de elementos
        ElementoComplejo antorcha = (ElementoComplejo) elementos.get("antorcha");
        assertNotNull(antorcha, "Elemento 'antorcha' no encontrado");

        // Obtener la receta correspondiente
        Receta receta = crafteador.getReceta(antorcha);
        assertNotNull(receta, "Receta para 'antorcha' no encontrada");

        // Ingredientes directos según recetas.json:
        // - 4 bastones
        // - 1 carbón
        Map<Elemento, Integer> ingredientes = receta.getIngredientesDirectos();

        assertEquals(2, ingredientes.size());
        assertEquals(4, ingredientes.get(elementos.get("baston")));
        assertEquals(1, ingredientes.get(elementos.get("carbon")));
    }
    
    @Test
    void ingredientesDirectosEspada() {
        ElementoComplejo espada = (ElementoComplejo) elementos.get("espada");
        Receta receta = crafteador.getReceta(espada);
        assertNotNull(receta);

        Map<Elemento, Integer> ingredientes = receta.getIngredientesDirectos();

        assertEquals(2, ingredientes.size());
        assertEquals(1, ingredientes.get(elementos.get("baston")));
        assertEquals(2, ingredientes.get(elementos.get("lingote")));
    }
    
    @Test
    void ingredientesDirectosMesa() {
        ElementoComplejo mesa = (ElementoComplejo) elementos.get("mesa");
        Receta receta = crafteador.getReceta(mesa);
        assertNotNull(receta);

        Map<Elemento, Integer> ingredientes = receta.getIngredientesDirectos();

        assertEquals(1, ingredientes.size());
        assertEquals(4, ingredientes.get(elementos.get("madera")));
    }

}
