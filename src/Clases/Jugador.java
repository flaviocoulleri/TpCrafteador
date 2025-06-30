package Clases;

import java.util.*;
import java.util.Map.Entry;

public class Jugador {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Cargar recetas y elementos desde JSON usando el método nuevo
        Recetario.DatosRecetario datos = Recetario.cargarDatosDesdeJson();
        Crafteador crafteador = new Crafteador(datos.recetas);
        Inventario inventario = InventarioLoader.cargar("inventario.json", datos.elementos);

        System.out.println("🛠️ Bienvenido al sistema de crafteo interactivo 🧱");

        while (true) {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Ver inventario");
            System.out.println("2. Ver todas las recetas");
            System.out.println("3. ¿Qué necesito para craftear un objeto? (directo)");
            System.out.println("4. ¿Qué necesito para craftear un objeto desde cero?");
            System.out.println("5. ¿Qué me falta para craftear un objeto? (directo)");
            System.out.println("6. ¿Qué me falta para craftear un objeto desde cero?");
            System.out.println("7. ¿Cuántos puedo craftear?");
            System.out.println("8. Realizar un crafteo");
            System.out.println("9. Ver historial de crafteos");
            System.out.println("10. Ver gráfico de tiempos de crafteo");
            System.out.println("0. Salir");

            System.out.print("Opción: ");
            int opcion;
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada inválida.");
                continue;
            }

            switch (opcion) {
            case 1 -> {
                inventario.imprimir();
            }
            case 2 -> {
                crafteador.getRecetario().mostrarRecetas();
            }
            case 3 -> {
                ElementoComplejo obj = pedirElemento(scanner);
                if (!crafteador.getRecetario().contieneReceta(obj)) {
                    System.out.println("❌ No existe receta para: " + obj.getNombre());
                    break;
                }
                Resultado r = crafteador.calcularIngredientesDirectos(obj, 1);
                mostrarResultado(r);
            }
            case 4 -> {
                ElementoComplejo obj = pedirElemento(scanner);
                if (!crafteador.getRecetario().contieneReceta(obj)) {
                    System.out.println("❌ No existe receta para: " + obj.getNombre());
                    break;
                }
                Resultado r = crafteador.calcularIngredientesBasicos(obj, 1);
                mostrarResultado(r);
            }
            case 5 -> {
                ElementoComplejo obj = pedirElemento(scanner);
                if (!crafteador.getRecetario().contieneReceta(obj)) {
                    System.out.println("❌ No existe receta para: " + obj.getNombre());
                    break;
                }
                Resultado r = crafteador.calcularIngredientesDirectosFaltantes(obj, 1, inventario);
                mostrarResultado(r);
            }
            case 6 -> {
                ElementoComplejo obj = pedirElemento(scanner);
                if (!crafteador.getRecetario().contieneReceta(obj)) {
                    System.out.println("❌ No existe receta para: " + obj.getNombre());
                    break;
                }
                Resultado r = crafteador.calcularIngredientesBasicosFaltantes(obj, 1, inventario);
                mostrarResultado(r);
            }
            case 7 -> {
                ElementoComplejo obj = pedirElemento(scanner);
                if (!crafteador.getRecetario().contieneReceta(obj)) {
                    System.out.println("❌ No existe receta para: " + obj.getNombre());
                    break;
                }
                Resultado r = crafteador.cuantosPuedoCraftear(obj, inventario);
                System.out.println("🔧 Puedes craftear: " + r.getCantidad() + " unidad(es) de " + r.getNombre());
                System.out.println("⏱ Tiempo total requerido: " + r.getTiempoTotal());
            }
            case 8 -> {
                ElementoComplejo obj = pedirElemento(scanner);
                if (!crafteador.getRecetario().contieneReceta(obj)) {
                    System.out.println("❌ No existe receta para: " + obj.getNombre());
                    break;
                }
                System.out.print("¿Cuántos querés craftear?: ");
                int cant = Integer.parseInt(scanner.nextLine());
                Resultado r = crafteador.craftear(obj, cant, inventario);
                if (r.getCantidad() > 0) {
                    System.out.println("✅ Crafteo realizado con éxito. Tiempo total: " + r.getTiempoTotal());
                } else {
                    System.out.println("❌ No fue posible craftear esa cantidad.");
                }
            }
            case 9 -> crafteador.mostrarHistorial();
            case 10 -> mostrarGraficoHistorial(crafteador);
            case 0 -> {
                System.out.println("👋 ¡Hasta luego!");
                return;
            }
            default -> System.out.println("❌ Opción inválida.");
        }
            pausar(scanner);
        }
    }
    
    private static void mostrarGraficoHistorial(Crafteador crafteador) {
        List<RegistroCrafteo> historial = crafteador.getHistorial();

        if (historial.isEmpty()) {
            System.out.println("📊 No hay crafteos registrados.");
            return;
        }

        System.out.println("\n📊 Tiempos de crafteo:");
        for (RegistroCrafteo reg : historial) {
            String nombre = reg.getReceta().getResultado().getNombre();
            int tiempo = reg.getReceta().getTiempo();
            String barra = "█".repeat(Math.max(1, tiempo));
            System.out.printf("%-12s | %s (%ds)%n", nombre, barra, tiempo);
        }
    }

    private static ElementoComplejo pedirElemento(Scanner scanner) {
        System.out.print("Ingrese el nombre del objeto: ");
        String nombre = scanner.nextLine().trim().toLowerCase();
        return new ElementoComplejo(nombre);
    }

    private static void mostrarResultado(Resultado r) {
        if (r.getIngredientes() == null || r.getIngredientes().isEmpty()) {
            System.out.println("❌ No hay ingredientes que mostrar.");
            return;
        }

        System.out.println("🔧 Ingredientes necesarios:");
        for (Entry<Elemento, Integer> e : r.getIngredientes().entrySet()) {
            System.out.println("- " + e.getKey().getNombre() + ": " + e.getValue());
        }
        System.out.println("⏱ Tiempo total: " + r.getTiempoTotal());
    }
    
    private static void pausar(Scanner scanner) {
        System.out.println("\nPresioná ENTER para continuar...");
        scanner.nextLine();  // Espera ENTER
    }
}
