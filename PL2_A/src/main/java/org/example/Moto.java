package org.example;

public class Moto implements Comparable<Moto>{
    private String marca;
    private int caballos;
    private int cilindrada;

    public Moto (String marca, int caballos, int cilindrada){
        this.marca = marca;
        this.caballos = caballos;
        this.cilindrada = cilindrada;
    }

    public String toString(){
        return marca + " [" + caballos + " cv, " + cilindrada + " cc]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Moto otraMoto = (Moto) obj;
        return this.caballos == otraMoto.caballos &&
                this.cilindrada == otraMoto.cilindrada &&
                this.marca.equals(otraMoto.marca);
    }


    public int compareTo(Moto otraMoto){
        return this.caballos - otraMoto.caballos;
    }

}