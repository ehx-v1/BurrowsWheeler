package core;

import gui.ViewerPane;
import runtimeframework.DebugQueue;
import runtimeframework.DebugStep;

/**
 * Created by root on 14.04.2017.
 */
public class BurrowsWheelerDecodingConceptPresentation implements BurrowsWheelerTransformationCore.AlgorithmImplementationStub{
    private final static int NUMBER_OF_SLIDES = 5; // TODO set actual slide number
    private int currentSlide = 0;

    public BurrowsWheelerDecodingConceptPresentation (BurrowsWheelerTransformationCore core, Runnable onPreBegin, Runnable onPostEnd) {
        core.addImplementation(this, onPreBegin, onPostEnd);
    }

    @Override
    public DebugQueue getExecution() {
        DebugQueue queue = new DebugQueue();
        for (int i = 0; i < NUMBER_OF_SLIDES; i++) {
            queue.add(DebugStep.builder().setForward(() -> currentSlide++)
            .setBackward(() -> currentSlide--)
            .build());
        }
        return queue;
    }

    @Override
    public ViewerPane getViewer() {
        return new ViewerPane(){
            // TODO
        };
    }
}
