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

    // Métodos existentes...
    
    /**
     * Devuelve el mapa de ingredientes simples necesarios para craftear `cantidad` unidades del `objetivo`.
     * Recorre recursivamente sub-recetas.
     */
    public Map<ElementoSimple, Integer> calcularIngredientesBasicos(Elemento objetivo, int cantidad) {
        Map<ElementoSimple, Integer> resultado = new HashMap<>();

        if (objetivo.esSimple()) {
            ElementoSimple simple = (ElementoSimple) objetivo;
            resultado.put(simple, cantidad);
            return resultado;
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

            Map<ElementoSimple, Integer> subIngredientes = calcularIngredientesBasicos(ingrediente, cantidadTotal);
            for (Map.Entry<ElementoSimple, Integer> subEntry : subIngredientes.entrySet()) {
                resultado.merge(subEntry.getKey(), subEntry.getValue(), Integer::sum);
            }
        }

        return resultado;
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
    
    // Métodos "Qué me falta para..."
    
    public Map<Elemento, Integer> ingredientesDirectosFaltantes(ElementoComplejo objetivo, int cantidad, Inventario inventario) {
        Receta receta = recetario.obtenerReceta(objetivo);
        if (receta == null) {
            throw new IllegalArgumentException("No hay receta para: " + objetivo.getNombre());
        }

        Map<Elemento, Integer> faltantes = new HashMap<>();
        int cantidadPorReceta = receta.getCantidadResultado();
        int veces = (int) Math.ceil(cantidad / (double) cantidadPorReceta);

        for (Map.Entry<Elemento, Integer> entry : receta.getIngredientes().entrySet()) {
            Elemento ingrediente = entry.getKey();
            int necesario = entry.getValue() * veces;
            int disponible = inventario.getCantidad(ingrediente);

            if (disponible < necesario) {
                faltantes.put(ingrediente, necesario - disponible);
            }
        }

        return faltantes;
    }

    public Map<ElementoSimple, Integer> ingredientesBasicosFaltantes(Elemento objetivo, int cantidad, Inventario inventario) {
        Map<ElementoSimple, Integer> resultado = new HashMap<>();

        // Si es simple, solo resta verificar cuánto falta
        if (objetivo.esSimple()) {
            ElementoSimple simple = (ElementoSimple) objetivo;
            int enInventario = inventario.getCantidad(simple);
            int faltante = cantidad - enInventario;
            if (faltante > 0) {
                resultado.put(simple, faltante);
            }
            return resultado;
        }

        // Es complejo
        ElementoComplejo complejo = (ElementoComplejo) objetivo;
        int yaDisponible = inventario.getCantidad(complejo);
        int cantidadAFabricar = cantidad - yaDisponible;

        if (cantidadAFabricar <= 0) {
            return resultado; // No hace falta fabricar
        }

        Receta receta = recetario.obtenerReceta(complejo);
        if (receta == null) {
            throw new IllegalArgumentException("No hay receta para: " + complejo.getNombre());
        }

        int cantidadPorReceta = receta.getCantidadResultado();
        int veces = (int) Math.ceil(cantidadAFabricar / (double) cantidadPorReceta);

        for (Map.Entry<Elemento, Integer> entry : receta.getIngredientes().entrySet()) {
            Elemento ingrediente = entry.getKey();
            int total = entry.getValue() * veces;

            // Llamada recursiva para descomponer el ingrediente
            Map<ElementoSimple, Integer> sub = ingredientesBasicosFaltantes(ingrediente, total, inventario);

            for (Map.Entry<ElementoSimple, Integer> e : sub.entrySet()) {
                resultado.merge(e.getKey(), e.getValue(), Integer::sum);
            }
        }

        return resultado;
    }

    public int cuantosPuedoCraftear(ElementoComplejo objeto, Inventario inventario) {
        // Crear una copia del inventario para simular el proceso
        Inventario inventarioSimulado = new Inventario(new HashMap<>(inventario.getMapa()));
        return calcularMaximoCrafteable(objeto, inventarioSimulado);
    }
    
    private int calcularMaximoCrafteable(ElementoComplejo objeto, Inventario inventario) {
        Receta receta = recetario.obtenerReceta(objeto);
        if (receta == null) {
            return 0; // No se puede craftear
        }
        
        int maxPosible = Integer.MAX_VALUE; // Iniciar con el valor máximo posible
        
        // Para cada ingrediente, calcular cuántas veces podemos satisfacerlo
        for (Map.Entry<Elemento, Integer> ingrediente : receta.getIngredientes().entrySet()) {
            Elemento elemento = ingrediente.getKey();
            int cantidadNecesaria = ingrediente.getValue();
            
            int disponible = calcularDisponibilidadTotal(elemento, inventario);
            int vecesPosibles = disponible / cantidadNecesaria;
            
            maxPosible = Math.min(maxPosible, vecesPosibles);
        }
        
        // Considerar la cantidad que produce cada ejecución de la receta
        return maxPosible * receta.getCantidadResultado();
    }
    
    private int calcularDisponibilidadTotal(Elemento elemento, Inventario inventario) {
        if (elemento.esSimple()) {
            return inventario.getCantidad(elemento);
        }
        
        ElementoComplejo complejo = (ElementoComplejo) elemento;
        
        // Cantidad directa en inventario
        int cantidadDirecta = inventario.getCantidad(complejo);
        
        // Cantidad que puedo fabricar con los ingredientes disponibles
        int cantidadFabricable = calcularMaximoCrafteable(complejo, inventario);
        
        return cantidadDirecta + cantidadFabricable;
    }

    // NUEVOS MÉTODOS PARA EL PUNTO 6

    /**
     * Realiza el crafteo del objeto especificado, modificando el inventario.
     * Retorna un ResultadoCrafteo con información del proceso.
     */
    public ResultadoCrafteo craftear(ElementoComplejo objeto, int cantidad, Inventario inventario) {
        // 1. Verificar si es posible el crafteo
        int maximoPosible = cuantosPuedoCraftear(objeto, inventario);
        if (maximoPosible < cantidad) {
            return new ResultadoCrafteo(false, 0, 0, 
                "No se puede craftear " + cantidad + " unidades de " + objeto.getNombre() + 
                ". Máximo posible: " + maximoPosible);
        }

        // 2. Realizar el crafteo
            int tiempoTotal = ejecutarCrafteo(objeto, cantidad, inventario);
        
        return new ResultadoCrafteo(true, cantidad, tiempoTotal, 
            "Crafteo exitoso: " + cantidad + " x " + objeto.getNombre());
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
                
                // Después de craftear, consumir exactamente lo que necesito
                // Calcular cuánto se produjo realmente
                // Receta recetaIntermedia = recetas.get(complejo);
                // int cantidadPorReceta = recetaIntermedia.getCantidadResultado();
                // int ejecucionesRealizadas = (int) Math.ceil((double) cantidad / cantidadPorReceta);
                // int cantidadProducida = ejecucionesRealizadas * cantidadPorReceta;
                
                // Consumir solo lo que necesito
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
            System.out.println((registro.getTurno()) + ". " +
                " - " + registro.getReceta().getResultado().getNombre() + 
                " (x" + registro.getReceta().getCantidadResultado() + ")");
            
            // Mostrar ingredientes consumidos
            System.out.println("   Ingredientes consumidos:");
            for (Map.Entry<Elemento, Integer> ingrediente : registro.getIngredientesConsumidos().entrySet()) {
                System.out.println("   - " + ingrediente.getKey().getNombre() + ": " + ingrediente.getValue());
            }
            System.out.println(); // Línea en blanco para separar registros
        }
    }

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