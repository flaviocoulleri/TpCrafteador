package Clases;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.*;
import java.util.*;

public class InventarioLoader {
    public static Inventario cargar(String archivo, Map<String, Elemento> elementos) {
        try {
            String json = Files.readString(Paths.get(archivo));
            ObjectMapper mapper = new ObjectMapper();
            List<ItemInventarioJson> lista = mapper.readValue(json, new TypeReference<List<ItemInventarioJson>>() {});
            Map<Elemento, Integer> mapa = new HashMap<>();

            for (ItemInventarioJson item : lista) {
                Elemento e = elementos.get(item.getElemento());
                if (e != null) {
                    mapa.put(e, item.getCantidad());
                } else {
                    System.out.println("⚠️ Elemento desconocido en inventario: " + item.getElemento());
                }
            }

            return new Inventario(mapa);
        } catch (Exception e) {
            e.printStackTrace();
            return new Inventario();
        }
    }
}
