package ca.jent.javafunwithtypes.types;

import static ca.jent.javafunwithtypes.types.UnitOfWeight.*;

/**
 * Trying to standarize the weight to GRAM turns out not to be a good idea.
 * The main problem: it is based on an assumption (weight will always be in GRAM internally) and the Type itself
 * does not reflect this in any ways.  The burden is 100% on the programmer and error-prone.
 *
 */

public final class WeightImpl_02 {
    private final Float value;
    private final UnitOfWeight originalUnit;

    private WeightImpl_02() {
        this(null, null);
    }
    private WeightImpl_02(Float value, UnitOfWeight originalUnit) {
        this.value = value;
        this.originalUnit = originalUnit;
    }

    public Float getOriginalValue() {
        return switch (originalUnit) {
            case MILLIGRAM -> value / ONE_GRAM_IN_MILLIGRAM;
            case GRAM -> value;
            case KILOGRAM -> value / ONE_GRAM_IN_KILOGRAM;
            case LBS -> value / ONE_GRAM_IN_LBS;
            case OUNCE -> value / ONE_GRAM_IN_OUNCE;
        };
    }

    public static WeightImpl_02 of(Float value, UnitOfWeight unit) {
        // Upon construction, we always transform into standarized unit of weight: GRAM
        return switch (unit) {
            case MILLIGRAM -> WeightImpl_02.of(value / ONE_GRAM_IN_MILLIGRAM, unit);
            case GRAM -> WeightImpl_02.of(value, unit);
            case KILOGRAM -> WeightImpl_02.of(value / ONE_GRAM_IN_KILOGRAM, unit);
            case LBS -> WeightImpl_02.of(value / ONE_GRAM_IN_LBS, unit);
            case OUNCE -> WeightImpl_02.of(value / ONE_GRAM_IN_OUNCE, unit);
        };
    }

}
