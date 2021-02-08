package fr.polytech.jydet.lib;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NormalLaw {

    private final double u;
    private final double eq;


    public double nextValue() {
        return u + eq * Math.sqrt(-2 * Math.log(Math.random())) * Math.cos(2 * Math.PI * Math.random());
    }
}
