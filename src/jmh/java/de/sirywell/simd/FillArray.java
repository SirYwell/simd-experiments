package de.sirywell.simd;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1, warmups = 0, jvmArgs = "--add-modules=jdk.incubator.vector")
@State(Scope.Benchmark)
public class FillArray {

    private static final VectorSpecies<Integer> PREFERRED = IntVector.SPECIES_PREFERRED;
    private static final IntVector INDEX = IntVector.fromArray(PREFERRED, new int[]{0, 1, 2, 3, 4, 5, 6, 7}, 0);

    @Param({"1024", "4096", "4095"})
    private int size;
    private int[] intArray;

    @Setup
    public void setup() {
        intArray = new int[size];
    }

    /*@Benchmark
    public void fillFor() {
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = i;
        }
    }*/

    /*@Benchmark
    public void fillVector() {
        int i;
        for (i = 0; i + PREFERRED.length() <= intArray.length; i += PREFERRED.length()) {
            IntVector base = IntVector.broadcast(PREFERRED, i);
            IntVector vector = IntVector.fromArray(PREFERRED, intArray, i);
            IntVector result = vector.add(base.add(INDEX));
            result.intoArray(intArray, i);
        }
        for (; i < intArray.length; i++) {
            intArray[i] = i;
        }
    }*/

    /*@Benchmark
    public void prefix() {
        Arrays.fill(intArray, 1);
        intArray[0] = 0;
        Arrays.parallelPrefix(intArray, Integer::sum);
    }*/
}
