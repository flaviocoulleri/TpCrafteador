package Clases;

import java.util.List;

public class RecetaJson {
    private String resultado;
    private int cantidadResultado;
    private List<IngredienteJson> ingredientes;
    private String tipo;
    private int tiempo;

    public String getResultado() {
        return resultado;
    }

    public int getCantidadResultado() {
        return cantidadResultado;
    }

    public List<IngredienteJson> getIngredientes() {
        return ingredientes;
    }

    public String getTipo() {
        return tipo;
    }

    public int getTiempo() {
        return tiempo;
    }
}
