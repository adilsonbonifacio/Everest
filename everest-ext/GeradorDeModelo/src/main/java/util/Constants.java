package util;

/***
 * Classe of constants
 * @author camila
 *
 */
public class Constants {
    public static final String TAU = "tau";
    public static final String DELTA_TXT = "delta";
    public static final String DELTA = "δ";
    public static final String DELTA_UNICODE = "\u03b4";
    public static final String DELTA_UNICODE_n = "u03b4";
    public static final String EPSILON = "epsilon";
    public static char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz123456789!#$%&*()/".toCharArray();
    public static char[] ALPHABET_ = "vwxyzabcdefghijklmnopqrstu".toCharArray();//"PQOWIEURYTALSKDJFGMZNXBCV"
    public static final String SEPARATOR = "@";
    public static final char INPUT_TAG = '?';
    public static final char OUTPUT_TAG = '!';
    public static final String EMPTY = "Ø";
    public static final String COMMA = ",";
    public static final String UNDERLINE = "_";

    public static final String RUN_VERDICT_NON_CONFORM = "non conformance";
    public static final String RUN_VERDICT_INCONCLUSIVE = "inconclusive";
    public static final String RUN_VERDICT_PASS = "pass";

    public static final String MSG_CONFORM = "IUT conforms to the specification.";
    public static final String MSG_NOT_CONFORM = "IUT does not conforms to the specification.";

    public static final String MSG_TOTAL_CONFORM = "All IUTs conform to the specification.";
    public static final String MSG_PARTIAL_CONFORM = "One or more IUTs do not conform to the specification.";
    public static final String MSG_TOTAL_NONCONFORM = "No IUTs conform to the specification.";

    public static final String NO_TRANSITION = "there are no transitions of ";

    public static final String MAX_IUT_STATES = "Max IUT states: [";
//	public static final Integer MAX_TEST_CASES = 10;
//	public static final Integer MAX_TEST_CASES_REAL = 5;

    public static final Integer MAX_TEST_CASES = 5;//Integer.MAX_VALUE
//	public static final Integer MAX_TEST_CASES_REAL =MAX_TEST_CASES+ (MAX_TEST_CASES <15? (MAX_TEST_CASES*2): (MAX_TEST_CASES/4));

    public static final String SEPARATOR_MULTIGRAPH_FILE = "#######################################################\n";



}
