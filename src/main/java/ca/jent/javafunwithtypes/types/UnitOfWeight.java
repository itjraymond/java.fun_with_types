package ca.jent.javafunwithtypes.types;


    public enum UnitOfWeight {
        MILLIGRAM,
        GRAM,
        KILOGRAM,
        LBS,
        OUNCE;

        public static final Float ONE_GRAM_IN_LBS = 0.0022046244F;
        public static final Float ONE_GRAM_IN_OUNCE = 0.0352739907F;
        public static final Float ONE_GRAM_IN_MILLIGRAM = 1000.0F;
        public static final Float ONE_GRAM_IN_KILOGRAM = 0.001F;
        public static final Float EPSILON = 0.001F;

    }

