package Clases;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestIngredientesFaltantesDesdeCero {
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
    void faltantesBasicosEspada() {
        ElementoComplejo espada = (ElementoComplejo) elementos.get("espada");

        // Cálculo recursivo: necesito 2 lingotes y 1 baston
        // Para 2 lingotes: 6 mineral y 2 carbon
        // Inventario tiene: 4 mineral, 2 carbon, 1 baston, 1 madera
        // Faltan: 2 mineral

        Map<ElementoSimple, Integer> faltantes = crafteador.ingredientesBasicosFaltantes(espada, 1, inventario);

        assertEquals(1, faltantes.size());
        assertEquals(2, faltantes.get(elementos.get("mineral")));
    }
    
    @Test
    void faltantesBasicosAntorcha() {
        ElementoComplejo antorcha = (ElementoComplejo) elementos.get("antorcha");
        Map<ElementoSimple, Integer> faltantes = crafteador.ingredientesBasicosFaltantes(antorcha, 1, inventario);

        // Necesito 4 bastones y 1 carbón
        // Tengo 2 carbones (alcanza) y 1 bastón (no alcanza, me faltan otros 3)
        // Para 3 bastones, necesito 6 de madera, y tengo 1
        // Me faltarían 5 de madera
        assertEquals(1, faltantes.size());
        assertEquals(5, faltantes.get(elementos.get("madera")));
    }

    @Test
    void faltantesBasicosMesa() {
        ElementoComplejo mesa = (ElementoComplejo) elementos.get("mesa");
        Map<ElementoSimple, Integer> faltantes = crafteador.ingredientesBasicosFaltantes(mesa, 1, inventario);

        // Necesita 4 maderas, tengo 1
        assertEquals(1, faltantes.size());
        assertEquals(3, faltantes.get(elementos.get("madera")));
    }

}


