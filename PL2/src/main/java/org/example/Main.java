package org.example;

public class Main {

    public static void main(String[] args) {

        Moto keeway = new Moto("Keeway",15,125);
        Moto honda = new Moto("Honda",20,250);
        Moto trump = new Moto("Trump",25,275);
        Moto yamaha = new Moto("Yamaha",30,300);
        Moto zontes = new Moto("Zontes",40,350);
        Moto kawasaki = new Moto("Kawasaki",45,650);
        Moto bmw = new Moto("BMW",55,850);
        Moto ducati = new Moto("Ducati",60,950);
        Moto suzuki = new Moto("Suzuki",65,900);
        Moto harley = new Moto("Harley",100,1200);

        ArbolBinarioDeBusqueda<Moto> arbol = new ArbolBinarioDeBusqueda<>();

        arbol.insertar(zontes);
        arbol.insertar(trump);
        arbol.insertar(bmw);
        arbol.insertar(honda);
        arbol.insertar(yamaha);
        arbol.insertar(keeway);
        arbol.insertar(kawasaki);
        arbol.insertar(suzuki);
        arbol.insertar(ducati);
        arbol.insertar(harley);


        System.out.print("Grado del arbol:");
        System.out.println(arbol.getGrado());
        System.out.print("Altura del arbol:");
        System.out.println(arbol.getAltura());
        System.out.print("Lista de motos en el nivel 2: ");
        System.out.println(arbol.getListaDatosNivel(2));
        System.out.print("Es homogeneo: ");
        System.out.println(arbol.isArbolHomogeneo());
        System.out.print("Camino para llegar a la bmw: ");
        System.out.println(arbol.getCamino(bmw));
        System.out.print("Camino para llegar a keeway: ");
        System.out.println(arbol.getCamino(keeway));
        System.out.print("Es completo: ");
        System.out.println(arbol.isArbolCompleto());
        System.out.print("Es casi completo: ");
        System.out.println(arbol.isArbolCasiCompleto());

        System.out.print("Lista preorden: ");
        System.out.println(arbol.getListaPreOrden());
        System.out.print("Lista postorden: ");
        System.out.println(arbol.getListaPostOrden());
        System.out.print("Lista orden central: ");
        System.out.println(arbol.getListaOrdenCentral());



        ArbolBinarioDeBusqueda<Moto> arbol_derecho = arbol.getSubArbolDerecha();
        ArbolBinarioDeBusqueda<Moto> arbol_izquierdo = arbol.getSubArbolIzquierda();

        System.out.println("Subarbol derecho: ");
        System.out.print("Lista orden central: ");
        System.out.println(arbol_derecho.getListaOrdenCentral());


        System.out.println("Subarbol izquierdo: ");
        System.out.print("Lista orden central: ");
        System.out.println(arbol_izquierdo.getListaOrdenCentral());

    }
}