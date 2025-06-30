package Clases;

import java.util.HashMap;
import java.util.Map;

public class Receta {
    private ElementoComplejo resultado;
    private int cantidadResultado;
    private Map<Elemento, Integer> ingredientes;
    private String tipo;
    private int tiempo;

    public Receta(ElementoComplejo resultado, int cantidadResultado,
                  Map<Elemento, Integer> ingredientes, String tipo, int tiempo) {
        this.resultado = resultado;
        this.cantidadResultado = cantidadResultado;
        this.ingredientes = ingredientes;
        this.tipo = tipo.toLowerCase();
        this.tiempo = tiempo;
    }

    public ElementoComplejo getResultado() {
        return resultado;
    }

    public int getCantidadResultado() {
        return cantidadResultado;
    }

    public void setCantidadResultado(int cantidadResultado) {
        this.cantidadResultado = cantidadResultado;
    }

    public Map<Elemento, Integer> getIngredientes() {
        return ingredientes;
    }

    public String getTipo() {
        return tipo;
    }

    public int getTiempo() {
        return tiempo;
    }

    public Map<Elemento, Integer> getIngredientesDirectos() {
        return new HashMap<>(ingredientes);
    }

    public void mostrarIngredientesDirectosEnConsola() {
        System.out.println("Ingredientes directos para '" + resultado.getNombre() + "':");
        for (Map.Entry<Elemento, Integer> entry : ingredientes.entrySet()) {
            System.out.println("- " + entry.getKey().getNombre() + ": " + entry.getValue());
        }
    }
}



