package Clases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Crafteador {
    private Recetario recetario;
    private List<RegistroCrafteo> historial;

    public Crafteador(List<Receta> recetas) {
        this.recetario = new Recetario();
        this.historial = new ArrayList<>();
        for (Receta r : recetas) {
            this.recetario.agregarReceta(r);
        }
    }

    public Recetario getRecetario() {
        return recetario;
    }
    
    public List<RegistroCrafteo> getHistorial(){
    	return historial;
    }

      /**
     * Devuelve la receta asociada a un ElementoComplejo, o null si no existe.
     */
    public Receta getReceta(ElementoComplejo objetivo) {
        Receta receta = recetario.obtenerReceta(objetivo);
        if (receta == null) {
            System.err.println("⚠️ Receta no encontrada para: " + objetivo.getNombre());
        }
        return receta;
    }


    /**
     * 1. Devuelve los ingredientes directos necesarios para craftear el objeto.
     */
    public Resultado calcularIngredientesDirectos(ElementoComplejo objetivo, int cantidad) {
        Receta receta = recetario.obtenerReceta(objetivo);
        if (receta == null) {
            throw new IllegalArgumentException("No hay receta para: " + objetivo.getNombre());
        }

        Map<Elemento, Integer> ingredientesDirectos = new HashMap<>();
        int cantidadPorReceta = receta.getCantidadResultado();
        int veces = (int) Math.ceil(cantidad / (double) cantidadPorReceta);

        for (Map.Entry<Elemento, Integer> entry : receta.getIngredientes().entrySet()) {
            ingredientesDirectos.put(entry.getKey(), entry.getValue() * veces);
        }

        int tiempoTotal = receta.getTiempo() * veces;

        return new Resultado(objetivo.getNombre(), tiempoTotal, cantidad, ingredientesDirectos);
    }
    
    /**
     * 2. Devuelve el mapa de ingredientes simples necesarios para craftear `cantidad` unidades del `objetivo`.
     * Recorre recursivamente sub-recetas.
     */
    public Resultado calcularIngredientesBasicos(Elemento objetivo, int cantidad) {
        return calcularIngredientesBasicosInterno(objetivo, cantidad);
    }

    /**
     * 3. Devuelve los ingredientes directos faltantes para craftear el objeto.
     */
    public Resultado calcularIngredientesDirectosFaltantes(ElementoComplejo objetivo, int cantidad, Inventario inventario) {
        Receta receta = recetario.obtenerReceta(objetivo);
        if (receta == null) {
            throw new IllegalArgumentException("No hay receta para: " + objetivo.getNombre());
        }

        Map<Elemento, Integer> faltantes = new HashMap<>();
        int cantidadPorReceta = receta.getCantidadResultado();
        int veces = (int) Math.ceil(cantidad / (double) cantidadPorReceta);

        int tiempoTotal = receta.getTiempo() * veces;

        for (Map.Entry<Elemento, Integer> entry : receta.getIngredientes().entrySet()) {
            Elemento ingrediente = entry.getKey();
            int necesario = entry.getValue() * veces;
            int disponible = inventario.getCantidad(ingrediente);

            if (disponible < necesario) {
                int cantidadFaltante = necesario - disponible;
                faltantes.put(ingrediente, cantidadFaltante);
            }
        }

        return new Resultado(objetivo.getNombre(), tiempoTotal, cantidad, faltantes);
    }


    /**
     * 4. Devuelve los ingredientes básicos faltantes para craftear el objeto desde cero.
     */
    public Resultado calcularIngredientesBasicosFaltantes(Elemento objetivo, int cantidad, Inventario inventario) {
        return ingredientesBasicosFaltantesInterno(objetivo, cantidad, inventario);
    }

    /**
     * 5. Calcula cuántos objetos se pueden craftear con el inventario actual.
     */
    public Resultado cuantosPuedoCraftear(ElementoComplejo objeto, Inventario inventario) {
        return cuantosPuedoCraftearInterno(objeto, inventario);
    }

    /**
     * 6. Realiza el crafteo del objeto especificado, modificando el inventario.
     * Retorna un ResultadoCrafteo con información del proceso.
     */
    public Resultado craftear(ElementoComplejo objeto, int cantidad, Inventario inventario) {
        // 1. Verificar si es posible el crafteo
        Resultado maximoPosible = cuantosPuedoCraftearInterno(objeto, inventario);
        if (maximoPosible.getCantidad() < cantidad) {
            return new Resultado(objeto.getNombre(), 0, 0, null);
        }

        // 2. Realizar el crafteo
            int tiempoTotal = ejecutarCrafteo(objeto, cantidad, inventario);

        return new Resultado(objeto.getNombre(), tiempoTotal, cantidad, null);
    }

    /**
     * Ejecuta el crafteo de forma recursiva, manejando ingredientes intermedios.
     * Retorna el tiempo total empleado.
     */
    private int ejecutarCrafteo(ElementoComplejo objeto, int cantidad, Inventario inventario) {
        Receta receta = recetario.obtenerReceta(objeto);
        if (receta == null) {
            throw new IllegalArgumentException("No hay receta para: " + objeto.getNombre());
        }

        // Calcular cuántas ejecuciones de la receta necesito
        int cantidadPorReceta = receta.getCantidadResultado();
        int ejecucionesNecesarias = (int) Math.ceil((double) cantidad / cantidadPorReceta);
        
        int tiempoTotal = 0;
        
        // Mapa para rastrear los ingredientes consumidos en esta ejecución
        Map<Elemento, Integer> ingredientesConsumidos = new HashMap<>();

        // Procesar cada ingrediente
        for (Map.Entry<Elemento, Integer> ingrediente : receta.getIngredientes().entrySet()) {
            Elemento elemento = ingrediente.getKey();
            int cantidadNecesaria = ingrediente.getValue() * ejecucionesNecesarias;
            
            // Registrar el ingrediente consumido
            ingredientesConsumidos.put(elemento, cantidadNecesaria);
            
            tiempoTotal += consumirIngrediente(elemento, cantidadNecesaria, inventario);
        }

        // Agregar el resultado al inventario
        int cantidadProducida = ejecucionesNecesarias * cantidadPorReceta;
        inventario.agregar(objeto, cantidadProducida);

        // Registrar en el historial (por cada ejecución individual)
        for (int i = 0; i < ejecucionesNecesarias; i++) {
            // Calcular ingredientes para una sola ejecución
            Map<Elemento, Integer> ingredientesPorEjecucion = new HashMap<>();
            for (Map.Entry<Elemento, Integer> entry : receta.getIngredientes().entrySet()) {
                ingredientesPorEjecucion.put(entry.getKey(), entry.getValue());
            }
            
            historial.add(new RegistroCrafteo(receta, ingredientesPorEjecucion));
        }

        // Sumar el tiempo de fabricación de este objeto
        tiempoTotal += receta.getTiempo() * ejecucionesNecesarias;

        return tiempoTotal;
    }

    /**
     * Consume un ingrediente del inventario, crafteándolo si es necesario.
     * Retorna el tiempo empleado en craftear ingredientes intermedios.
     */
    private int consumirIngrediente(Elemento elemento, int cantidad, Inventario inventario) {
        int tiempoEmpleado = 0;

        if (elemento.esSimple()) {
            // Elemento simple: solo consumir del inventario
            inventario.consumir(elemento, cantidad);
        } else {
            // Elemento complejo: usar lo disponible y craftear el resto
            ElementoComplejo complejo = (ElementoComplejo) elemento;
            
            // Usar primero lo que tengo directamente
            int disponibleDirecto = inventario.getCantidad(complejo);
            int usarDirecto = Math.min(disponibleDirecto, cantidad);
            
            if (usarDirecto > 0) {
                inventario.consumir(complejo, usarDirecto);
                cantidad -= usarDirecto;
            }
            
            // Si aún necesito más, craftear la diferencia
            if (cantidad > 0) {
                // Craftear el ingrediente intermedio
                tiempoEmpleado += ejecutarCrafteo(complejo, cantidad, inventario);
                
                inventario.consumir(complejo, cantidad);
            }
        }

        return tiempoEmpleado;
    }

    /**
     * Muestra el historial de crafteos en consola with ingredientes consumidos.
     */
    public void mostrarHistorial() {
        System.out.println("\n📜 Historial de crafteos:");
        if (historial.isEmpty()) {
            System.out.println("- No hay crafteos registrados");
            return;
        }

        for (int i = 0; i < historial.size(); i++) {
            RegistroCrafteo registro = historial.get(i);
            Receta receta = registro.getReceta();
            String nombre = receta.getResultado().getNombre();
            int cantidad = receta.getCantidadResultado();
            int tiempo = receta.getTiempo();

            System.out.println(registro.getTurno() + ". " + nombre + " (x" + cantidad + ") - ⏱ " + tiempo + "s");
            System.out.println("   Ingredientes consumidos:");
            for (Map.Entry<Elemento, Integer> ingrediente : registro.getIngredientesConsumidos().entrySet()) {
                System.out.println("   - " + ingrediente.getKey().getNombre() + ": " + ingrediente.getValue());
            }
            System.out.println();
        }
    }

        
    // ====== MÉTODOS INTERNOS (PRIVADOS) ======

    /**
     * Método interno para calcular ingredientes básico.
     */
    private Resultado calcularIngredientesBasicosInterno(Elemento objetivo, int cantidad) {
        Map<Elemento, Integer> ingTotales = new HashMap<>();
        int tiempoTotal = 0;
        if (objetivo.esSimple()) {
            ElementoSimple simple = (ElementoSimple) objetivo;
            ingTotales.put(simple, cantidad);
            return new Resultado(simple.getNombre(), 0, cantidad, ingTotales);
        }

        Receta receta = recetario.obtenerReceta((ElementoComplejo) objetivo);
        if (receta == null) {
            throw new IllegalArgumentException("No hay receta para: " + objetivo.getNombre());
        }

        int cantidadPorReceta = receta.getCantidadResultado();
        int veces = (int) Math.ceil(cantidad / (double) cantidadPorReceta);

        for (Map.Entry<Elemento, Integer> entry : receta.getIngredientes().entrySet()) {
            Elemento ingrediente = entry.getKey();
            int cantidadTotal = entry.getValue() * veces;

            Resultado subResultado = calcularIngredientesBasicosInterno(ingrediente, cantidadTotal);
            tiempoTotal += subResultado.getTiempoTotal();
            for (Map.Entry<Elemento, Integer> subEntry : subResultado.getIngredientes().entrySet()) {
                ingTotales.merge(subEntry.getKey(), subEntry.getValue(), Integer::sum);
            }
        }
        tiempoTotal += receta.getTiempo() * veces;
        return new Resultado(objetivo.getNombre(), tiempoTotal, cantidad, ingTotales);
    }

    /**
     * Método interno para calcular ingredientes básicos faltantes.
     */
    private Resultado ingredientesBasicosFaltantesInterno(Elemento objetivo, int cantidad, Inventario inventario) {
        Map<Elemento, Integer> resultado = new HashMap<>();
        int tiempoTotal = 0;

        if (objetivo.esSimple()) {
            ElementoSimple simple = (ElementoSimple) objetivo;
            int enInventario = inventario.getCantidad(simple);
            int faltante = cantidad - enInventario;
            if (faltante > 0) {
                resultado.put(simple, faltante);
            }
            return new Resultado(simple.getNombre(), 0, faltante, resultado);
        }

        ElementoComplejo complejo = (ElementoComplejo) objetivo;
        int yaDisponible = inventario.getCantidad(complejo);
        int cantidadAFabricar = cantidad - yaDisponible;

        if (cantidadAFabricar <= 0) {
            return new Resultado(complejo.getNombre(), 0, 0, resultado);
        }

        Receta receta = recetario.obtenerReceta(complejo);
        if (receta == null) {
            throw new IllegalArgumentException("No hay receta para: " + complejo.getNombre());
        }

        int cantidadPorReceta = receta.getCantidadResultado();
        int veces = (int) Math.ceil(cantidadAFabricar / (double) cantidadPorReceta);

        tiempoTotal += receta.getTiempo() * veces;
        for (Map.Entry<Elemento, Integer> entry : receta.getIngredientes().entrySet()) {
            Elemento ingrediente = entry.getKey();
            int total = entry.getValue() * veces;

            Resultado sub = ingredientesBasicosFaltantesInterno(ingrediente, total, inventario);
            tiempoTotal += sub.getTiempoTotal();
            for (Map.Entry<Elemento, Integer> e : sub.getIngredientes().entrySet()) {
                resultado.merge(e.getKey(), e.getValue(), Integer::sum);
            }
        }

        return new Resultado(complejo.getNombre(), tiempoTotal, cantidad, resultado);
    }

    /**
     * Método interno para calcular cuántos se pueden craftear.
     */
    private Resultado cuantosPuedoCraftearInterno(ElementoComplejo objeto, Inventario inventario) {
        Inventario inventarioSimulado = new Inventario(new HashMap<>(inventario.getMapa()));
        Resultado res = calcularMaximoCrafteable(objeto, inventarioSimulado);
        if(res.getCantidad() == 0) {
            return new Resultado(objeto.getNombre(), 0, 0, null); // No se puede craftear
        }
        return res;
    }

    private Resultado calcularMaximoCrafteable(ElementoComplejo objeto, Inventario inventario) {
        int tiempoTotal = 0;
        Receta receta = recetario.obtenerReceta(objeto);
        if (receta == null) {
            return new Resultado(objeto.getNombre(), 0, 0, null); // No se puede craftear
        }
        
        int maxPosible = Integer.MAX_VALUE; // Iniciar con el valor máximo posible
        
        // Para cada ingrediente, calcular cuántas veces podemos satisfacerlo
        for (Map.Entry<Elemento, Integer> ingrediente : receta.getIngredientes().entrySet()) {
            Elemento elemento = ingrediente.getKey();
            int cantidadNecesaria = ingrediente.getValue();
            
            Resultado disponible = calcularDisponibilidadTotal(elemento, inventario);
            tiempoTotal += disponible.getTiempoTotal();
            int vecesPosibles = disponible.getCantidad() / cantidadNecesaria;

            maxPosible = Math.min(maxPosible, vecesPosibles);
        }

        tiempoTotal += receta.getTiempo() * maxPosible;
        // Considerar la cantidad que produce cada ejecución de la receta
        return new Resultado(objeto.getNombre(), tiempoTotal, maxPosible * receta.getCantidadResultado(), null);
    }
    
    private Resultado calcularDisponibilidadTotal(Elemento elemento, Inventario inventario) {
        if (elemento.esSimple()) {
            return new Resultado(elemento.getNombre(), 0, inventario.getCantidad(elemento), null);
        }
        
        ElementoComplejo complejo = (ElementoComplejo) elemento;
        
        // Cantidad directa en inventario
        int cantidadDirecta = inventario.getCantidad(complejo);
        
        // Cantidad que puedo fabricar con los ingredientes disponibles
        Resultado subRes = calcularMaximoCrafteable(complejo, inventario);

        return new Resultado(complejo.getNombre(), subRes.getTiempoTotal(), cantidadDirecta + subRes.getCantidad(), null);
    }


    /**
     * Calcula el tiempo total necesario para craftear un objeto de forma recursiva.
     */
    /* private int calcularTiempoTotal(Elemento objetivo, int cantidad) {
        if (objetivo.esSimple()) {
            return 0; // Los elementos simples no requieren tiempo de crafteo
        }

        ElementoComplejo complejo = (ElementoComplejo) objetivo;
        Receta receta = recetario.obtenerReceta(complejo);
        if (receta == null) {
            return 0;
        }

        int cantidadPorReceta = receta.getCantidadResultado();
        int veces = (int) Math.ceil(cantidad / (double) cantidadPorReceta);
        
        int tiempoTotal = receta.getTiempo() * veces; // Tiempo del objeto actual

        // Sumar tiempos de ingredientes intermedios
        for (Map.Entry<Elemento, Integer> entry : receta.getIngredientes().entrySet()) {
            Elemento ingrediente = entry.getKey();
            int cantidadIngrediente = entry.getValue() * veces;
            tiempoTotal += calcularTiempoTotal(ingrediente, cantidadIngrediente);
        }

        return tiempoTotal;
    } */

    /**
     * Clase interna para encapsular el resultado de un crafteo.
     */
    public static class ResultadoCrafteo {
        private final boolean exitoso;
        private final int cantidadCrafteada;
        private final int tiempoTotal;
        private final String mensaje;

        public ResultadoCrafteo(boolean exitoso, int cantidadCrafteada, int tiempoTotal, String mensaje) {
            this.exitoso = exitoso;
            this.cantidadCrafteada = cantidadCrafteada;
            this.tiempoTotal = tiempoTotal;
            this.mensaje = mensaje;
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public int getCantidadCrafteada() {
            return cantidadCrafteada;
        }

        public int getTiempoTotal() {
            return tiempoTotal;
        }

        public String getMensaje() {
            return mensaje;
        }

        @Override
        public String toString() {
            return mensaje + (exitoso ? " (Tiempo: " + tiempoTotal + " minutos)" : "");
        }
    }
}
