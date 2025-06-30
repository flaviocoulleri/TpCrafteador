package Clases;

import java.util.*;

public class RecetaLoader {
    public static List<Receta> convertir(List<RecetaJson> data, Map<String, Elemento> elementos) {
        List<Receta> recetas = new ArrayList<>();

        for (RecetaJson rj : data) {
            ElementoComplejo resultado = (ElementoComplejo) elementos.get(rj.getResultado());
            Map<Elemento, Integer> ingredientes = new HashMap<>();

            for (IngredienteJson ing : rj.getIngredientes()) {
                Elemento e = elementos.get(ing.getElemento());
                ingredientes.put(e, ing.getCantidad());
            }

            Receta receta = new Receta(resultado, rj.getCantidadResultado(), ingredientes, rj.getTipo(), rj.getTiempo());
            recetas.add(receta);
        }

        return recetas;
    }
}


