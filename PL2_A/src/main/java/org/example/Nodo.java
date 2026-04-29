package org.example;

public class Nodo<T> {
    public T valor;
    public Nodo<T> der;
    public Nodo<T> izq;
    public Nodo<T> prev;

    public Nodo(T valor) {
        this.valor = valor;
        der = null;
        izq = null;
        prev = null;
    }

    public T getValor() {
        return valor;
    }

    public void setValor(T valor) {
        this.valor = valor;
    }

    public Nodo<T> getDer() {
        return der;
    }

    public void setDer(Nodo<T> der) {
        this.der = der;
    }

    public Nodo<T> getIzq() {
        return izq;
    }

    public void setIzq(Nodo<T> izq) {
        this.izq = izq;
    }

    public Nodo<T> getPrev() {
        return prev;
    }

    public void setPrev(Nodo<T> prev) {
        this.prev = prev;
    }

}
