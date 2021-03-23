package fr.polytech.jydet.ant;

import fr.polytech.jydet.utils.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class Ant {
    private Vector<Double> position;
    @Getter
    private HuntResult huntRes;
    private final Double reach;
    @Getter
    private final AtomicInteger failCounter = new AtomicInteger(0);

    public void init(Vector<Double> randomLocInRange) {
        position = randomLocInRange;
        failCounter.set(0);
        huntRes = null;
    }

    public void routine() {
        searchAndLogAtPosition();
        position = randomLocInRange();
    }

    private Vector<Double> randomLocInRange() {
        Vector<Double> res = new Vector<>(position.size());
        for (int i = 0; i < position.size(); i++) {
            Double valueForDim = position.get(i);
            Tuple<Double> borne = API.borne.get(i);
            res.add(ThreadLocalRandom.current().nextDouble(
                Math.max(borne.get_1(), valueForDim - reach),
                Math.min(borne.get_2(), valueForDim + reach)
                )
            );
        }
        return res;
    }

    private void searchAndLogAtPosition() {
        var valAtPos = API.function.applyAsDouble(position);
        if (huntRes == null) {
            this.huntRes = new HuntResult(position, valAtPos, 1);
        } else {
            if (this.huntRes.bestRes > valAtPos) {
                this.huntRes.position = position;
                this.huntRes.bestRes = valAtPos;
                failCounter.set(0);
            } else {
                failCounter.incrementAndGet();
            }
            this.huntRes.tries++;
        }
    }

    @Data
    @AllArgsConstructor
    public static class HuntResult {
        private Vector<Double> position;
        private Double bestRes;
        private int tries;
    }
}
