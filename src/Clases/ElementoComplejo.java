package Clases;

public class ElementoComplejo extends Elemento {
    public ElementoComplejo(String nombre) {
        super(nombre);
    }

    @Override
    public boolean esSimple() {
        return false;
    }
}
