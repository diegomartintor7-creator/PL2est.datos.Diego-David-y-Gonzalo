package org.example;
import java.util.ArrayList;
public class ArbolBinarioDeBusqueda<T extends Comparable<T>> {
    private Nodo<T> raiz;
    public void insertar(T moto){
        Nodo<T> nuevo = new Nodo<>(moto);
        if(raiz==null){
            raiz=nuevo;
            return;
        }
        Nodo<T> actual = raiz;
        while(actual != null){
            if(moto.compareTo(actual.getValor())>0){
                if(actual.getDer() == null){
                    actual.setDer(nuevo);
                    nuevo.setPrev(actual);
                    return;
                }
                actual = actual.getDer();
            }
            else if(moto.compareTo(actual.getValor())<0){
                if(actual.getIzq() == null){
                    actual.setIzq(nuevo);
                    nuevo.setPrev(actual);
                    return;
                }
                actual = actual.getIzq();
            }
            else{
                return;
            }
        }
    }
    public int getGrado(){
        return getGradoRec(raiz);
    }
    private int getGradoRec(Nodo<T> actual){
        if(actual == null) return 0;
        int hijos = 0;
        if(actual.getDer() != null) hijos++;
        if(actual.getIzq() != null) hijos++;
        int gradoIzq = getGradoRec(actual.getIzq());
        int gradoDer = getGradoRec(actual.getDer());
        return Math.max(hijos, Math.max(gradoIzq, gradoDer));
    }
    public int getAltura(){
        return getAturaRec(raiz);
    }
    private int getAturaRec(Nodo<T> actual){
        if(actual == null) return 0;

        int alturaIzq = getAturaRec(actual.getIzq());
        int alturaDer = getAturaRec(actual.getDer());

        return 1 + Math.max(alturaIzq, alturaDer);
    }
    public ArrayList<T> getListaDatosNivel(int nivel){
        ArrayList<T> lista = new ArrayList<>();
        getListaRec(lista, nivel, raiz);
        return lista;
    }
    private void getListaRec(ArrayList<T> lista, int nivel, Nodo<T> actual){
        if(actual == null) return;
        else if(nivel == 0) lista.add(actual.getValor());
        else{
            getListaRec(lista, nivel-1, actual.getIzq());
            getListaRec(lista, nivel-1, actual.getDer());
        }
    }
    public boolean isArbolHomogeneo(){
        return isArbolHomogeneoRec(raiz);
    }
    private boolean isArbolHomogeneoRec(Nodo<T> actual){
        if(actual == null) return true;

        boolean tieneIzq = actual.getIzq() != null;
        boolean tieneDer = actual.getDer() != null;

        if(tieneIzq != tieneDer) return false;

        return isArbolHomogeneoRec(actual.getDer()) && isArbolHomogeneoRec(actual.getIzq());
    }
    public ArrayList<T> getCamino(T moto){
        ArrayList<T> lista = new ArrayList<>();
        Nodo<T> actual = raiz;
        while(actual != null && moto.compareTo(actual.getValor()) != 0){
            lista.add(actual.getValor());
            if (moto.compareTo(actual.getValor())<0)actual=actual.getIzq();
            else actual=actual.getDer();
        }
        return lista;
    }
    public boolean isArbolCompleto() {
        int profundidad = profundidadHoja(raiz);
        return esArbolCompletoRec(raiz, 0, profundidad);
    }
    private int profundidadHoja(Nodo actual) {
        int nivel = 0;
        while (actual.getIzq() != null) {
            actual = actual.getIzq();
            nivel++;
        }
        return nivel;
    }
    private boolean esArbolCompletoRec(Nodo actual, int nivel, int profundidadEsperada) {
        if (actual == null) {
            return true;
        }
        // si es hoja
        if (actual.getIzq() == null && actual.getDer() == null) {
            return nivel == profundidadEsperada;
        }
        return esArbolCompletoRec(actual.getIzq(), nivel + 1, profundidadEsperada) && esArbolCompletoRec(actual.getDer(), nivel + 1, profundidadEsperada);
    }
    public boolean isArbolCasiCompleto() {

        java.util.ArrayList<Nodo> lista = new java.util.ArrayList<>();
        lista.add(raiz);
        boolean huecoEncontrado = false;
        for(int i=0; i<lista.size(); i++){

            Nodo actual = lista.get(i);

            if(actual == null){
                huecoEncontrado = true;
            }
            else{

                if(huecoEncontrado){
                    return false;
                }

                lista.add(actual.getIzq());
                lista.add(actual.getDer());
            }
        }

        return true;
    }
    public ArbolBinarioDeBusqueda getSubArbolIzquierda(){

        ArbolBinarioDeBusqueda subarbol =
                new ArbolBinarioDeBusqueda();
        if(raiz != null){
            subarbol.raiz = raiz.getIzq();
        }
        return subarbol;
    }
    public ArbolBinarioDeBusqueda getSubArbolDerecha(){

        ArbolBinarioDeBusqueda subarbol =
                new ArbolBinarioDeBusqueda();
        if(raiz != null){
            subarbol.raiz = raiz.getDer();
        }
        return subarbol;
    }
    public ArrayList<T> getListaPreOrden(){
        ArrayList<T> lista = new ArrayList<>();
        preOrdenRec(raiz, lista);
        return lista;
    }
    private void preOrdenRec(Nodo<T> actual, ArrayList<T> lista){

        if(actual == null){
            return;
        }

        lista.add(actual.getValor());
        preOrdenRec(actual.getIzq(), lista);
        preOrdenRec(actual.getDer(), lista);
    }
    public ArrayList<T> getListaPostOrden(){
        ArrayList<T> lista = new ArrayList<>();
        postOrdenRec(raiz, lista);
        return lista;
    }
    private void postOrdenRec(Nodo<T> actual, ArrayList<T> lista){

        if(actual == null){
            return;
        }
        postOrdenRec(actual.getIzq(), lista);
        postOrdenRec(actual.getDer(), lista);
        lista.add(actual.getValor());
    }
    public ArrayList<T> getListaOrdenCentral(){
        ArrayList<T> lista = new ArrayList<>();
        ordenCentralRec(raiz, lista);
        return lista;
    }
    private void ordenCentralRec(Nodo<T> actual, ArrayList<T> lista){

        if(actual == null){
            return;
        }
        ordenCentralRec(actual.getIzq(), lista);
        lista.add(actual.getValor());
        ordenCentralRec(actual.getDer(), lista);
    }
}
