package Clases;

import java.util.Map;
import java.util.HashMap;

public class RegistroCrafteo {
    
    private static int contadorTurno = 1; // Contador estático para los turnos
    private int turno;

    private Receta receta;
    private Map<Elemento, Integer> ingredientesConsumidos;

    public RegistroCrafteo(Receta receta, Map<Elemento, Integer> ingredientesConsumidos) {
        this.turno = contadorTurno++;
        this.receta = receta;
        this.ingredientesConsumidos = new HashMap<>(ingredientesConsumidos); // Copia defensiva
    }

    public Receta getReceta() {
        return receta;
    }

    public int getTurno() {
        return turno;
    }

    public Map<Elemento, Integer> getIngredientesConsumidos() {
        return new HashMap<>(ingredientesConsumidos); // Copia defensiva
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RegistroCrafteo{");
        sb.append("turno=").append(turno);
        sb.append(", objeto=").append(receta.getResultado().getNombre());
        sb.append(", cantidad=").append(receta.getCantidadResultado());
        sb.append(", ingredientesConsumidos={");
        
        boolean first = true;
        for (Map.Entry<Elemento, Integer> entry : ingredientesConsumidos.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey().getNombre()).append(":").append(entry.getValue());
            first = false;
        }
        
        sb.append("}}");
        return sb.toString();
    }
}