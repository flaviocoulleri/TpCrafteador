package Clases;

public class ElementoSimple extends Elemento {
    public ElementoSimple(String nombre) {
        super(nombre);
    }

    @Override
    public boolean esSimple() {
        return true;
    }
}

