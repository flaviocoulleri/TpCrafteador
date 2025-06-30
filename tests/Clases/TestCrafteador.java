package Clases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestCrafteador {
	static Crafteador crafteador;
	static Inventario inventario;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// Cargar recetas y crear Crafteador antes de ejecutar los tests
		List<Receta> recetas = Recetario.cargarRecetasDesdeJson();
		crafteador = new Crafteador(recetas);
		inventario = new Inventario();
		inventario.agregar(new ElementoSimple("madera"),1);
		inventario.agregar(new ElementoSimple("carbon"),2);
		inventario.agregar(new ElementoSimple("mineral"),4);
		inventario.agregar(new ElementoComplejo("baston"),1);
	}

	@Test
	void cuantosPuedoCraftearConIngredientesBasicosDisponibles() {
		ElementoComplejo objetivo = new ElementoComplejo("lingote");
		int resultadoEsperado = 1;
		int resultadoObtenido = crafteador.cuantosPuedoCraftear(objetivo, inventario);
		assertEquals(resultadoEsperado, resultadoObtenido);
	}

	@Test
	void cuantosPuedoCraftearAfectadoPorCantidadResultado() {
		inventario.agregar(new ElementoSimple("mineral"), 3);

		// Modificamos la receta de lingote para que el resultado crafteado sea 2
		ElementoComplejo objetivo = new ElementoComplejo("lingote");
		Receta recetaLingote = crafteador.getRecetario().obtenerReceta(objetivo);
		recetaLingote.setCantidadResultado(2);

		
		int resultadoEsperado = 4;
		int resultadoObtenido = crafteador.cuantosPuedoCraftear(objetivo, inventario);
		assertEquals(resultadoEsperado, resultadoObtenido);
	}

	@Test
	void cuantosPuedoCraftearConIngredientesIntermediosCrafteados() {
		// Modificamos la receta de lingote para que el resultado crafteado sea 2
		ElementoComplejo objetivo = new ElementoComplejo("lingote");
		Receta recetaLingote = crafteador.getRecetario().obtenerReceta(objetivo);
		recetaLingote.setCantidadResultado(2);

		ElementoComplejo objetivoEspada = new ElementoComplejo("espada");
		int resultadoEsperado = 1;
		int resultadoObtenido = crafteador.cuantosPuedoCraftear(objetivoEspada, inventario);
		assertEquals(resultadoEsperado, resultadoObtenido);
	}


	@Test
	void cuantosPuedoCraftearConIngredientesCrafteadosMasExistentes(){
		// USO UN BASTON EXISTENTE Y CRAFTEO 4
		// CRAFTEO 5 LINGOTES
	
		inventario.agregar(new ElementoSimple("madera"),3); //total: 4
		inventario.agregar(new ElementoSimple("carbon"),3); //total: 5
		inventario.agregar(new ElementoSimple("mineral"),11); //total: 15

		Receta recetaBaston = crafteador.getRecetario().obtenerRecetaPorNombre("baston");
		recetaBaston.setCantidadResultado(2);

		Receta recetaLingote = crafteador.getRecetario().obtenerRecetaPorNombre("lingote");
		recetaLingote.setCantidadResultado(2);

		ElementoComplejo objetivo = new ElementoComplejo("espada");
		int resultadoEsperado = 5; 
		int resultadoObtenido = crafteador.cuantosPuedoCraftear(objetivo, inventario);
		assertEquals(resultadoEsperado, resultadoObtenido);

	}

	@Test
	void cuantosPuedoCraftearConIngredientesFaltantes() {
		ElementoComplejo objetivo = new ElementoComplejo("espada");
		int resultadoEsperado = 0; // No tengo los ingredientes necesarios
		int resultadoObtenido = crafteador.cuantosPuedoCraftear(objetivo, inventario);
		assertEquals(resultadoEsperado, resultadoObtenido);
	}

}
