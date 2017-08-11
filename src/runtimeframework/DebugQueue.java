package runtimeframework;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * This class is what you work upon normally when using the Debug Framework. It holds @link{DebugStep}s and links them to one another, in the order you put them in.
 * Created by root on 04.04.2017.
 */
public class DebugQueue extends LinkedList<DebugStep> {

    private ListIterator<DebugStep> stepStager;
    private DebugStep stagedStep;

    private Runnable end;

    private Runnable begin;

    /**
     * To use the Debug Framework, you first create a @code{DebugQueue} using this constructor, and fill it with @link{DebugStep}s.\n
     * You CANNOT run a @code{DebugQueue} created with this constructor. To have it ready to run, pass it to the other constructor.
     * @see #DebugQueue(DebugQueue, Runnable, Runnable)
     */
    public DebugQueue() {
        super();
        this.begin = () -> {
            throw new RuntimeException("Did not initialize queue cleanly");
        };
        this.end = () -> {
            throw new RuntimeException("Did not initialize queue cleanly");
        };
        this.stepStager = null;
    }

    /**
     * This constructor is for the second step on using the Debug Framework: You give a @code{DebugQueue} some content and pre-begin and post-end tasks, making it ready to run.\n
     * NOTE: After re-creating it using this constructor, the @code{DebugQueue} is no longer modifiable.
     * @param content what to do within the queue
     * @param doWhenPreBegin what to do after stepping out by the beginning
     * @param doWhenAfterEnd what to do after stepping out by the end
     * @see #DebugQueue()
     */
    public DebugQueue(DebugQueue content, Runnable doWhenPreBegin, Runnable doWhenAfterEnd) {
        super();
        this.addAll(content);
        this.begin = doWhenPreBegin;
        this.end = doWhenAfterEnd;
        if (this.isEmpty()) {
            this.end.run();
        }
        this.stepStager = this.listIterator(0);
        if (this.stepStager.hasNext()) {
            this.stagedStep = this.stepStager.next();
        }
    }

    /**
     * Makes a step forward on the compound task.
     */
    public void stepForward() {
        if (this.stepStager == null || (!this.stepStager.hasNext() && this.stagedStep.hasBeenExecuted())) {
            this.end.run();
        } else {
            this.stagedStep.fwd.run();
            this.stagedStep = this.stepStager.hasNext() ? this.stepStager.next(): this.stagedStep;
        }
    }

    /**
     * Makes a step back on the compound task.
     */
    public void stepBack() {
        if (this.stepStager == null || !this.stepStager.hasPrevious()) {
            this.begin.run();
        } else {
            this.stagedStep = this.stepStager.previous();
            this.stagedStep.bkwd.run();
        }
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public boolean add(DebugStep debugStep) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.add(debugStep);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public void add(int index, DebugStep element) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        super.add(index, element);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public boolean addAll(Collection<? extends DebugStep> c) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.addAll(c);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public boolean addAll(int index, Collection<? extends DebugStep> c) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.addAll(index, c);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public boolean offer(DebugStep debugStep) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.offer(debugStep);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public boolean offerFirst(DebugStep debugStep) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.offerFirst(debugStep);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public boolean offerLast(DebugStep debugStep) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.offerLast(debugStep);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public void addFirst(DebugStep debugStep) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        super.addFirst(debugStep);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public void addLast(DebugStep debugStep) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        super.addLast(debugStep);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public void clear() {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        super.clear();
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public void push(DebugStep debugStep) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        super.push(debugStep);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public DebugStep set(int index, DebugStep element) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.set(index, element);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public DebugStep remove() {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.remove();
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public DebugStep remove(int index) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.remove(index);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public DebugStep removeFirst() {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.removeFirst();
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public DebugStep removeLast() {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.removeLast();
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public boolean remove(Object o) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.remove(o);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        super.removeRange(fromIndex, toIndex);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public boolean removeFirstOccurrence(Object o) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.removeFirstOccurrence(o);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.removeAll(c);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public boolean removeLastOccurrence(Object o) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.removeLastOccurrence(o);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.retainAll(c);
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public DebugStep pop() {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.pop();
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public DebugStep poll() {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.poll();
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public DebugStep pollFirst() {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.pollFirst();
    }

    /**
     * @inheritDoc
     * Note: Cannot be called on a running @code{DebugQueue}, as it would alter the queue, causing unintended side effects.
     */
    @Override
    public DebugStep pollLast() {
        if (this.stagedStep != null) throw new RuntimeException("Not alterable when already running");
        return super.pollLast();
    }
}
