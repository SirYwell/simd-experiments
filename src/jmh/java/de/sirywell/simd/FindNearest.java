package de.sirywell.simd;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1, warmups = 0)
@State(Scope.Benchmark)
public class FindNearest {
    private static final VectorSpecies<Integer> PREFERRED = IntVector.SPECIES_PREFERRED;

    @Param({"1024"})
    private int size;
    private Vec2 middle;
    private int[] xs;
    private int[] ys;

    @Setup
    public void setup() {
        Random random = new Random(0);
        xs = new int[size];
        ys = new int[size];
        for (int i = 0; i < size; i++) {
            xs[i] = random.nextInt(-512, 512);
            ys[i] = random.nextInt(-512, 512);
        }
        middle = new Vec2(random.nextInt(-512, 512), random.nextInt(-512, 512));
    }

    @Benchmark
    public Vec2 findNearestFor() {
        int distSquared = Integer.MAX_VALUE;
        int nx = Integer.MIN_VALUE;
        int ny = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            int c = square(xs[i] - middle.x()) + square(ys[i] - middle.y());
            if (c < distSquared) {
                nx = xs[i];
                ny = ys[i];
                distSquared = c;
            }
        }
        return new Vec2(nx, ny);
    }

    @CompilerControl(CompilerControl.Mode.INLINE)
    private int square(int i) {
        return i * i;
    }

    @Benchmark
    public Vec2 findNearestVector() {
        int distSquared = Integer.MAX_VALUE;
        int nx = Integer.MIN_VALUE;
        int ny = Integer.MIN_VALUE;
        int i;
        for (i = 0; i + PREFERRED.length() <= size; i += PREFERRED.length()) {
            IntVector xv = IntVector.fromArray(PREFERRED, xs, i);
            IntVector yv = IntVector.fromArray(PREFERRED, ys, i);
            IntVector xvs = xv.sub(middle.x());
            IntVector yvs = yv.sub(middle.y());
            IntVector result = xvs.mul(xvs).add(yvs.mul(yvs));
            VectorMask<Integer> mask = result.lt(distSquared);
            for (int l = mask.firstTrue(); l <= mask.lastTrue(); l++) {
                if (result.lane(l) < distSquared) {
                    nx = xv.lane(l);
                    ny = yv.lane(l);
                    distSquared = result.lane(l);
                }
            }
        }
        for (; i < size; i++) {
            int c = square(xs[i] - middle.x()) + square(ys[i] - middle.y());
            if (c < distSquared) {
                nx = xs[i];
                ny = ys[i];
                distSquared = c;
            }
        }
        return new Vec2(nx, ny);
    }

    record Vec2(int x, int y) { }
}
