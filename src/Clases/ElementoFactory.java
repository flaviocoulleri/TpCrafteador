package Clases;

import java.util.*;

public class ElementoFactory {
    // Crea todos los elementos (simples y complejos) a partir de las recetas JSON.
    
    public static Map<String, Elemento> construirElementos(List<RecetaJson> recetasJson) {
        Set<String> nombresIngredientes = new HashSet<>();
        Set<String> nombresResultados = new HashSet<>();

        for (RecetaJson r : recetasJson) {
            nombresResultados.add(r.getResultado());
            for (IngredienteJson ing : r.getIngredientes()) {
                nombresIngredientes.add(ing.getElemento());
            }
        }

        Map<String, Elemento> elementos = new HashMap<>();
        Set<String> todos = new HashSet<>(nombresIngredientes);
        todos.addAll(nombresResultados);

        for (String nombre : todos) {
            if (nombresResultados.contains(nombre)) {
                elementos.put(nombre, new ElementoComplejo(nombre));
            } else {
                elementos.put(nombre, new ElementoSimple(nombre));
            }
        }

        return elementos;
    }
}
