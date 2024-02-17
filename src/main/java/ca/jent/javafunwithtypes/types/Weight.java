package ca.jent.javafunwithtypes.types;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NonNull;

import java.util.Comparator;

import static ca.jent.javafunwithtypes.types.UnitOfWeight.*;
import static java.lang.Math.abs;

public record Weight(Float value, UnitOfWeight unit) implements Comparable<Weight> {

    private static final UnitOfWeight standardUnitOfWeight = GRAM;

    public static Weight of(float value, UnitOfWeight unit) {
        return new Weight(value,unit);
    }

    @JsonIgnore
    public Weight getStandarizedWeight() {
        return switch (unit) {
            case MILLIGRAM -> Weight.of(value / ONE_GRAM_IN_MILLIGRAM, standardUnitOfWeight);
            case GRAM -> this;
            case KILOGRAM -> Weight.of(value / ONE_GRAM_IN_KILOGRAM, standardUnitOfWeight);
            case LBS -> Weight.of(value / ONE_GRAM_IN_LBS, standardUnitOfWeight);
            case OUNCE -> Weight.of(value / ONE_GRAM_IN_OUNCE, standardUnitOfWeight);
        };
    }

    @Override
    public int compareTo(@NonNull Weight w2) {
        return weightComparator.compare(this, w2);
    }

    private static final Comparator<Weight> weightComparator = (w1, w2) -> {
        Weight weight1 = w1.getStandarizedWeight();
        Weight weight2 = w2.getStandarizedWeight();
        Float diff = weight1.value - weight2.value;
        if (abs(diff) <= EPSILON ) {
            return 0;
        } else if (diff < 0) {
            return -1;
        } else {
            return 1;
        }
    };
}
