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

public class TestIngredientesDesdeCero {
    static Crafteador crafteador;
    static Map<String, Elemento> elementos;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        String recetasJsonStr = Files.readString(Paths.get("recetas.json"));
        ObjectMapper mapper = new ObjectMapper();
        List<RecetaJson> recetasJson = mapper.readValue(recetasJsonStr, new TypeReference<>() {});
        elementos = ElementoFactory.construirElementos(recetasJson);
        List<Receta> recetas = RecetaLoader.convertir(recetasJson, elementos);
        crafteador = new Crafteador(recetas);
    }

    @Test
    void ingredientesBasicosParaEspada() {
        ElementoComplejo espada = (ElementoComplejo) elementos.get("espada");

        Map<ElementoSimple, Integer> basicos = crafteador.calcularIngredientesBasicos(espada, 1);

        assertEquals(2, basicos.get((ElementoSimple) elementos.get("madera")));   // Para bastón
        assertEquals(6, basicos.get((ElementoSimple) elementos.get("mineral")));  // 2 lingotes x 3 minerales c/u
        assertEquals(2, basicos.get((ElementoSimple) elementos.get("carbon")));   // 2 lingotes x 1 carbón c/u
        assertEquals(3, basicos.size());
    }
    
    @Test
    void ingredientesBasicosAntorcha() {
        ElementoComplejo antorcha = (ElementoComplejo) elementos.get("antorcha");
        Map<ElementoSimple, Integer> basicos = crafteador.calcularIngredientesBasicos(antorcha, 1);

        // antorcha -> 4 bastones + 1 carbón
        // 4 bastones → 8 maderas
        assertEquals(2, basicos.size());
        assertEquals(8, basicos.get(elementos.get("madera")));
        assertEquals(1, basicos.get(elementos.get("carbon")));
    }

    @Test
    void ingredientesBasicosPico() {
        ElementoComplejo pico = (ElementoComplejo) elementos.get("pico");
        Map<ElementoSimple, Integer> basicos = crafteador.calcularIngredientesBasicos(pico, 1);

        // pico -> 2 bastones + 3 piedras
        // 2 bastones = 4 maderas
        assertEquals(2, basicos.size());
        assertEquals(4, basicos.get(elementos.get("madera")));
        assertEquals(3, basicos.get(elementos.get("piedra")));
    }

}


