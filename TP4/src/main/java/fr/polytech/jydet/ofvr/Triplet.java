package fr.polytech.jydet.ofvr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Triplet<A, B, C> {
    private A a;
    private B b;
    private C c;
}
