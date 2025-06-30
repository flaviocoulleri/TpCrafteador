package Clases;

import java.util.*;
import java.nio.file.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Cargar recetas JSON
            String recetasJsonStr = Files.readString(Paths.get("recetas.json"));
            ObjectMapper mapper = new ObjectMapper();
            List<RecetaJson> recetasJson = mapper.readValue(recetasJsonStr, new TypeReference<>() {});

            // 2. Construir elementos a partir de recetas
            Map<String, Elemento> elementos = ElementoFactory.construirElementos(recetasJson);
            
            // 3. Cargar inventario desde JSON
            Inventario inventario = InventarioLoader.cargar("inventario.json", elementos);
            inventario.imprimir();

            // 4. Convertir recetas JSON a objetos Receta
            List<Receta> recetas = RecetaLoader.convertir(recetasJson, elementos);

            // 5. Crear Crafteador
            Crafteador crafteador = new Crafteador(recetas);

            // 6. Elegir objeto objetivo
            ElementoComplejo objetivo = (ElementoComplejo) elementos.get("espada");

            // 7. Ingredientes faltantes (directos)
            Map<Elemento, Integer> faltanDirectos = crafteador.ingredientesDirectosFaltantes(objetivo, 1, inventario);
            System.out.println("\n❌ Ingredientes DIRECTOS faltantes para 1 '" + objetivo.getNombre() + "':");
            faltanDirectos.forEach((e, q) -> System.out.println("- " + q + " x " + e.getNombre()));

            // 8. Ingredientes faltantes (básicos)
            Map<ElementoSimple, Integer> faltanBasicos = crafteador.ingredientesBasicosFaltantes(objetivo, 1, inventario);
            System.out.println("\n❌ Ingredientes BÁSICOS faltantes para 1 '" + objetivo.getNombre() + "':");
            faltanBasicos.forEach((e, q) -> System.out.println("- " + q + " x " + e.getNombre()));

            // 9. ¿Cuántas puedo craftear?

            int resultado;
            ElementoComplejo objeto;
            // RESULTADO ESPERADO: 0 -> Necesito 4 maderas, tengo 1
            // ElementoComplejo objeto = new ElementoComplejo("mesa");
            // int resultado = crafteador.cuantosPuedoCraftear(objeto, inventario);
            // System.out.println("\n🛠️ Podés craftear " + resultado + " unidad(es) de '" + objeto.getNombre() + "' con el inventario actual.");

            
            // RESULTADO ESPERADO: 1 -> Tengo ambos ingredientes básicos
            // objeto = (ElementoComplejo) elementos.get("lingote");
            // resultado = crafteador.cuantosPuedoCraftear(objeto, inventario);
            // System.out.println("\n🛠️ Podés craftear " + resultado + " unidad(es) de '" + objeto.getNombre() + "' con el inventario actual.");

            objeto = (ElementoComplejo) elementos.get("espada");
            resultado = crafteador.cuantosPuedoCraftear(objeto, inventario);
            System.out.println("\n🛠️ Podés craftear " + resultado + " unidad(es) de '" + objeto.getNombre() + "' con el inventario actual.");

            // int cantidad = crafteador.cuantosPuedoCraftear(objetivo, inventario);
            // System.out.println("\n🛠️ Podés craftear " + cantidad + " unidad(es) de '" + objetivo.getNombre() + "' con el inventario actual.");


            /* TESTEO DE CRAFTEAR */

            // Mostrar inventario inicial
            System.out.println("\n📦 Inventario ANTES del crafteo:");
            inventario.imprimir();

            // Ejemplo 1: Craftear un lingote (debería ser posible)
            // ElementoComplejo lingote = (ElementoComplejo) elementos.get("lingote");
            // System.out.println("\n🔨 Intentando craftear 1 lingote...");
            // Crafteador.ResultadoCrafteo resultado1 = crafteador.craftear(lingote, 1, inventario);
            // System.out.println("✅ " + resultado1);

            // Mostrar inventario después del primer crafteo
            // System.out.println("\n📦 Inventario DESPUÉS de craftear lingote:");
            // inventario.imprimir();

            // Ejemplo 2: Craftear una espada (ahora debería ser posible)
            ElementoComplejo espada = (ElementoComplejo) elementos.get("espada");
            System.out.println("\n🔨 Intentando craftear 1 espada...");
            Crafteador.ResultadoCrafteo resultado2 = crafteador.craftear(espada, 1, inventario);
            System.out.println("✅ " + resultado2);

            // Mostrar inventario final
            System.out.println("\n📦 Inventario FINAL:");
            inventario.imprimir();

            // Ejemplo 3: Intentar craftear algo imposible
            ElementoComplejo mesa = (ElementoComplejo) elementos.get("mesa");
            System.out.println("\n🔨 Intentando craftear 1 mesa (debería fallar)...");
            Crafteador.ResultadoCrafteo resultado3 = crafteador.craftear(mesa, 1, inventario);
            System.out.println("❌ " + resultado3);

            // Mostrar historial
            crafteador.mostrarHistorial();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

