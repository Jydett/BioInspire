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
                    field.set(newArg, normalLaw.nextValue() * (double) field.get(parent1) - (double) field.get(parent2));
                } else if (field.getType() == int.class) {
                    field.set(newArg, (int) (normalLaw.nextValue() * (int) field.get(parent1) - (int) field.get(parent2)));
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
                    field.set(newArg, gaussLaw.nextValue() * (double) field.get(newArg));
                } else if (field.getType() == int.class) {
                    field.set(newArg, (int) (gaussLaw.nextValue() * (int) field.get(newArg)));
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
