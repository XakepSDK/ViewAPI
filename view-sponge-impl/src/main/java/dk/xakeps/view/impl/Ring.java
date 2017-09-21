package dk.xakeps.view.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Ring<T> {
    private final T[] ringItems;
    private final RingCycler<T> ringCycler;

    public Ring(Class<T> arr, int ringSize, RingCycler<T> ringCycler) {
        this.ringItems = (T[]) Array.newInstance(arr, ringSize);
        this.ringCycler = ringCycler;
    }

    public T[] nextRing() {
        move(ringItems.length - 1);
        return Arrays.copyOf(ringItems, ringItems.length);
    }

    private void move(int arrPos) {
        if(arrPos < 0 || arrPos >= ringItems.length) return;
        ringItems[arrPos] = ringCycler.getNext(ringItems[arrPos]);
        if (ringCycler.isLast(ringItems[arrPos])) {
            move(arrPos - 1);
        }
    }

    public interface RingCycler<T> {
        boolean isLast(@Nullable T t);
        @Nonnull
        T getNext(@Nullable T t);
    }
}