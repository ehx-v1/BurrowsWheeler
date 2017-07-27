package runtimeframework;

/**
 * The more granular part of the Debug Framework. A @code{DebugStep} encapsulates a single step of the compound task, in a way the @link{DebugQueue} can handle it well.
 */
public class DebugStep {

    /**
     * The main operator of the Builder Pattern instance.
     */
    public static class DebugStepBuilder {
        private Runnable forward;
        private Runnable backward;

        private DebugStepBuilder() {
            this.forward = () -> {};
            this.backward = () -> {};
        }

        /**
         * Defines the forward side @link{Runnable} for the created steps.
         * @param forward the @code{Runnable} every @code{DebugStep} created after the call will have as forward side
         * @return the same Builder (i.e. @code{this})
         */
        public DebugStepBuilder setForward(Runnable forward) {
            this.forward = forward;
            return this;
        }

        /**
         * Defines the back side @link{Runnable} for the created steps.
         * @param backward the @code{Runnable} every @code{DebugStep} created after the call will have as back side
         * @return the same Builder (i.e. @code{this})
         */
        public DebugStepBuilder setBackward(Runnable backward) {
            this.backward = backward;
            return this;
        }

        /**
         * Creates a @code{DebugStep} with the last set @link{Runnable}s.
         * @return the built @code{DebugStep}
         */
        public DebugStep build() {
            return new DebugStep (this.forward, this.backward);
        }

    }

    /**
     * The forward side of the step. Does the action of the step.
     */
    public final Runnable fwd;
    /**
     * The backward side of the step. Assumed to revert the action of the step. For non-reversible actions, either throw an appropriate uncaught exception, or trigger some custom-made
     * reaction mechanism.\n
     * NOTE: The connection between the step sides is being assumed and CANNOT be checked by the Debug Framework! Users of the Debug framework are required to take care of this
     * themselves. Automatically generated reversions of arbitrary code are not yet possible.
     */
    public final Runnable bkwd;
    private boolean executed;

    private DebugStep(Runnable forward, Runnable backward) {
        this.fwd = () -> {
            DebugStep.this.executed = true;
            forward.run();
        };
        this.bkwd = () -> {
            DebugStep.this.executed = false;
            backward.run();
        };
        this.executed = false;
    }

    /**
     * Provides the information whether or not the @code{DebugStep} has been executed, and not been reverted since. Useful for finding out whether it is yet to execute, or also whether
     * it is yet to revert.
     * @return whether the @code{DebugStep} has been executed since last revert
     */
    public boolean hasBeenExecuted() {
        return this.executed;
    }

    /**
     * You create @code{DebugStep}s via the Builder Pattern, a widely used pattern for containers. Use this method to create a new Builder.
     * @return a Builder with default @code{Runnable}s
     */
    public static DebugStepBuilder builder() {
        return new DebugStepBuilder();
    }

}
