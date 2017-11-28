package es.ua.dlsi.mpaee.estrellas;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class CuerpoCelesteViewFactory {
    public static CuerpoCelesteViewFactory instance = null;

    private CuerpoCelesteViewFactory() {

    }
    public static final CuerpoCelesteViewFactory getInstance() {
        synchronized (CuerpoCelesteViewFactory.class) {
            if (instance == null) {
                instance = new CuerpoCelesteViewFactory();
            }
        }
        return instance;
    }

    /**
     * Las vistas tienen que tener un constructor al que se le pase un CuerpoCeleste
     * @param cuerpoCeleste
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     */
    public CuerpoCelesteView<?> create(CuerpoCeleste cuerpoCeleste) throws Exception {
        String nombreClase = cuerpoCeleste.getClass().getName() + "View";

        Class<?> claseView = Class.forName(nombreClase);
        Constructor<?> constructor = claseView.getConstructor(cuerpoCeleste.getClass());
        return (CuerpoCelesteView<?>) constructor.newInstance(cuerpoCeleste);
    }


}