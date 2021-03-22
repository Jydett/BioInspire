package fr.polytech.jydet.ant;

import lombok.Data;

import java.util.Vector;

public class Ant {
    Vector<HuntResult> huntRes;

    private int ens;

    public Double findLocalSol() {
        //TODO
    }

    @Data
    private class HuntResult {
        private Vector<Double> position;
        private Double bestRes;
        private int tries;
    }
}
