package Clases;

import java.util.*;

public class Inventario {
    private Map<Elemento, Integer> elementos;

    public Inventario() {
        this.elementos = new HashMap<>();
    }

    public Inventario(Map<Elemento, Integer> elementos) {
        this.elementos = elementos;
    }

    public int getCantidad(Elemento e) {
        return elementos.getOrDefault(e, 0);
    }

    public void agregar(Elemento e, int cantidad) {
        elementos.merge(e, cantidad, Integer::sum);
    }

    public void consumir(Elemento e, int cantidad) {
        elementos.put(e, getCantidad(e) - cantidad);
    }

    public Set<Elemento> getElementos() {
        return elementos.keySet();
    }

    public Map<Elemento, Integer> getMapa() {
        return elementos;
    }

    public void imprimir() {
        System.out.println("📦 Inventario actual:");
        for (Map.Entry<Elemento, Integer> entry : elementos.entrySet()) {
            System.out.println("- " + entry.getKey().getNombre() + ": " + entry.getValue());
        }
    }
}
