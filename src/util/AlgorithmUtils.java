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

    public static String algorithmCaption (BurrowsWheelerTransformationCore.Algorithms algorithm) {
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

}
