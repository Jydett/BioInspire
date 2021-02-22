package fr.polytech.jydet.evol;

import fr.polytech.jydet.lib.NormalLaw;
import fr.polytech.jydet.reactdiffuse.ReactDiffuseArguments;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;

@AllArgsConstructor
public class ReactDiffuseArgumentsWrapper {

    @Getter
    public ReactDiffuseArguments arguments;

    public String[] toMainArgs() {
        throw new UnsupportedOperationException();
    }

    //FIXME comment on choisis l'eq
    static final NormalLaw normalLaw = new NormalLaw(0, 1);

    public static ReactDiffuseArgumentsWrapper merge(ReactDiffuseArgumentsWrapper arg1, ReactDiffuseArgumentsWrapper arg2) {
        ReactDiffuseArguments newArg = new ReactDiffuseArguments();
        ReactDiffuseArgumentsWrapper res = new ReactDiffuseArgumentsWrapper(newArg);
        ReactDiffuseArguments parent1 = arg1.arguments;
        ReactDiffuseArguments parent2 = arg2.arguments;

        try {
            for (Field field : newArg.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType() == double.class) {
                    double parent1Value = (Double) field.get(parent1);
                    double parent2Value = (Double) field.get(parent2);
                    double min = Math.min(parent2Value, parent1Value);
                    double abs = Math.abs(parent1Value - parent2Value);
                    field.set(newArg, normalLaw.nextValue() * abs + min);
                } else if (field.getType() == int.class) {
                    double parent1Value = ((Integer) field.get(parent1)).doubleValue();
                    double parent2Value = ((Integer) field.get(parent2)).doubleValue();
                    double min = Math.min(parent2Value, parent1Value);
                    double abs = Math.abs(parent1Value - parent2Value);
                    field.set(newArg, (int) (normalLaw.nextValue() * abs + min));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return res;
    }
    static final NormalLaw gaussLaw = new NormalLaw(0, 1);

    public static void mutate(ReactDiffuseArgumentsWrapper arg1) {
        ReactDiffuseArguments newArg = arg1.arguments;
        try {
            for (Field field : newArg.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType() == double.class) {
                    double oldValue = (double) field.get(newArg);
                    field.set(newArg, oldValue + gaussLaw.nextValue() * oldValue * 0.5);
                } else if (field.getType() == int.class) {
                    int oldValue = (int) field.get(newArg);
                    field.set(newArg, (int) (oldValue + gaussLaw.nextValue() * oldValue * 0.5));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public ReactDiffuseArgumentsWrapper copy() {
        ReactDiffuseArguments copy = new ReactDiffuseArguments();
        try {
            for (Field field : ReactDiffuseArguments.class.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(copy, field.get(this.arguments));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return new ReactDiffuseArgumentsWrapper(copy);
    }
}
