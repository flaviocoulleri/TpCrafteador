package Clases;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Recetario {
    private Map<ElementoComplejo, Receta> recetas;

    public Recetario() {
        this.recetas = new HashMap<>();
    }

    public void agregarReceta(Receta receta) {
        this.recetas.put(receta.getResultado(), receta);
    }

    public Receta obtenerReceta(ElementoComplejo objetivo) {
        return this.recetas.get(objetivo);
    }

    public Receta obtenerRecetaPorNombre(String nombre) {
        for (Receta receta : recetas.values()) {
            if (receta.getResultado().getNombre().equals(nombre)) {
                return receta;
            }
        }
        return null; // Si no se encuentra la receta
    }

    public boolean contieneReceta(ElementoComplejo objetivo) {
        return this.recetas.containsKey(objetivo);
    }

    public static List<Receta> cargarRecetasDesdeJson() throws Exception {
		String recetasJsonStr = Files.readString(Paths.get("recetas.json"));
		ObjectMapper mapper = new ObjectMapper();
		List<RecetaJson> recetasJson = mapper.readValue(recetasJsonStr, new TypeReference<>() {});

		Map<String, Elemento> elementos = ElementoFactory.construirElementos(recetasJson);
		return RecetaLoader.convertir(recetasJson, elementos);
	}
}
