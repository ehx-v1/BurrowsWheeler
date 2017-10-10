package util;

import core.BurrowsWheelerDecodingConceptPresentation;
import core.BurrowsWheelerFastDecoding;
import core.BurrowsWheelerIntuitiveDecoding;
import core.BurrowsWheelerPermutationDecoding;
import core.BurrowsWheelerPermutationEncoding;
import core.BurrowsWheelerStandardEncoding;
import core.BurrowsWheelerTransformationCore;

/**
 * Created by root on 15.06.2017.
 */
public class AlgorithmUtils {

    public enum Locale {
        DE,
        EN
    }

    public static BurrowsWheelerTransformationCore.AlgorithmImplementationStub createAlgorithm (BurrowsWheelerTransformationCore core, BurrowsWheelerTransformationCore.Algorithms algorithm, Runnable onPreBegin, Runnable onPostEnd) {
        switch (algorithm) {
            case BW_STANDARD_ENCODE:
                return new BurrowsWheelerStandardEncoding(core, onPreBegin, onPostEnd);
            case BW_STANDARD_DECODE_SHOWGENERALIDEAS:
                return new BurrowsWheelerDecodingConceptPresentation(core, onPreBegin, onPostEnd);
            case BW_STANDARD_DECODE_FAST:
                return new BurrowsWheelerFastDecoding(core, onPreBegin, onPostEnd);
            case BW_STANDARD_DECODE_INTUITIVE:
                return new BurrowsWheelerIntuitiveDecoding(core, onPreBegin, onPostEnd);
            case BW_PERMUTATIONS_ENCODE:
                return new BurrowsWheelerPermutationEncoding(core, onPreBegin, onPostEnd);
            case BW_PERMUTATIONS_DECODE:
                return new BurrowsWheelerPermutationDecoding(core, onPreBegin, onPostEnd);
            default:
                throw new ThisShouldNotHappenException("Enum value not in enum");
        }
    }

    public static String algorithmCaption (BurrowsWheelerTransformationCore.Algorithms algorithm, Locale locale) {
        switch (locale) {
            case DE:
                return algorithmCaptionDE(algorithm);
            case EN:
                return algorithmCaptionEN(algorithm);
            default:
                throw new ThisShouldNotHappenException("Enum value not in enum");
        }
    }

    public static String algorithmCaptionEN (BurrowsWheelerTransformationCore.Algorithms algorithm) {
        switch (algorithm) {
            case BW_STANDARD_ENCODE:
                return "Forward-side standard transformation";
            case BW_STANDARD_DECODE_SHOWGENERALIDEAS:
                return "General backward-side concepts";
            case BW_STANDARD_DECODE_FAST:
                return "More performant backward-side standard transformation";
            case BW_STANDARD_DECODE_INTUITIVE:
                return "More simple backward-side standard transformation";
            case BW_PERMUTATIONS_ENCODE:
                return "Forward-side permutational transformation";
            case BW_PERMUTATIONS_DECODE:
                return "Backward-side permutational transformation";
            default:
                throw new ThisShouldNotHappenException("Enum value not in enum");
        }
    }

    public static String algorithmCaptionDE (BurrowsWheelerTransformationCore.Algorithms algorithm) {
        switch (algorithm) {
            case BW_STANDARD_ENCODE:
                return "Vorwärtstransformation";
            case BW_STANDARD_DECODE_SHOWGENERALIDEAS:
                return "Allgemeines zur Rückwärtstransformation";
            case BW_STANDARD_DECODE_FAST:
                return "Rückwärtstransformation, performanteres Verfahren";
            case BW_STANDARD_DECODE_INTUITIVE:
                return "Rückwärtstransformation, verständlicheres Verfahren";
            case BW_PERMUTATIONS_ENCODE:
                return "Permutations-Variante Vorwärtstransformation";
            case BW_PERMUTATIONS_DECODE:
                return "Permutations-Variante Rückwärtstransformation";
            default:
                throw new ThisShouldNotHappenException("Enum value not in enum");
        }
    }

    public static BurrowsWheelerTransformationCore.Algorithms byCaption (String caption) {
        switch (caption) {
            case "Vorwärtstransformation":
            case "Forward-side standard transformation":
                return BurrowsWheelerTransformationCore.Algorithms.BW_STANDARD_ENCODE;
            case "Allgemeines zur Rückwärtstransformation":
            case "General backward-side concepts":
                return BurrowsWheelerTransformationCore.Algorithms.BW_STANDARD_DECODE_SHOWGENERALIDEAS;
            case "Rückwärtstransformation, performanteres Verfahren":
            case "More performant backward-side standard transformation":
                return BurrowsWheelerTransformationCore.Algorithms.BW_STANDARD_DECODE_FAST;
            case "Rückwärtstransformation, verständlicheres Verfahren":
            case "More simple backward-side standard transformation":
                return BurrowsWheelerTransformationCore.Algorithms.BW_STANDARD_DECODE_INTUITIVE;
            case "Permutations-Variante Vorwärtstransformation":
            case "Forward-side permutational transformation":
                return BurrowsWheelerTransformationCore.Algorithms.BW_PERMUTATIONS_ENCODE;
            case "Permutations-Variante Rückwärtstransformation":
            case "Backward-side permutational transformation":
                return BurrowsWheelerTransformationCore.Algorithms.BW_PERMUTATIONS_DECODE;
            default:
                throw new IllegalArgumentException("Caption has no corresponding enum value");
        }
    }

}
