package com.example.aisearchlab.games.waterjug;

import com.example.aisearchlab.algorithms.SearchState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WaterJugState implements SearchState {

    private final int jugAAmount;
    private final int jugBAmount;
    private final int jugCAmount;
    
    private final int jugAMax;
    private final int jugBMax;
    private final int jugCMax;
    private final int target;

    public WaterJugState(int a, int b, int c, int aMax, int bMax, int cMax, int target) {
        this.jugAAmount = a;
        this.jugBAmount = b;
        this.jugCAmount = c;
        this.jugAMax = aMax;
        this.jugBMax = bMax;
        this.jugCMax = cMax;
        this.target = target;
    }

    public int getJugAAmount() { return jugAAmount; }
    public int getJugBAmount() { return jugBAmount; }
    public int getJugCAmount() { return jugCAmount; }
    public int getJugAMax() { return jugAMax; }
    public int getJugBMax() { return jugBMax; }
    public int getJugCMax() { return jugCMax; }
    public int getTarget() { return target; }

    @Override
    public boolean isGoal() {
        return jugAAmount == target || jugBAmount == target || jugCAmount == target;
    }

    @Override
    public List<Transition> getSuccessors() {
        List<Transition> successors = new ArrayList<>();

        // Fill actions
        if (jugAAmount < jugAMax) successors.add(new Transition(new WaterJugState(jugAMax, jugBAmount, jugCAmount, jugAMax, jugBMax, jugCMax, target), "Fill Jug A", 1.0));
        if (jugBAmount < jugBMax) successors.add(new Transition(new WaterJugState(jugAAmount, jugBMax, jugCAmount, jugAMax, jugBMax, jugCMax, target), "Fill Jug B", 1.0));
        if (jugCAmount < jugCMax) successors.add(new Transition(new WaterJugState(jugAAmount, jugBAmount, jugCMax, jugAMax, jugBMax, jugCMax, target), "Fill Jug C", 1.0));

        // Empty actions
        if (jugAAmount > 0) successors.add(new Transition(new WaterJugState(0, jugBAmount, jugCAmount, jugAMax, jugBMax, jugCMax, target), "Empty Jug A", 1.0));
        if (jugBAmount > 0) successors.add(new Transition(new WaterJugState(jugAAmount, 0, jugCAmount, jugAMax, jugBMax, jugCMax, target), "Empty Jug B", 1.0));
        if (jugCAmount > 0) successors.add(new Transition(new WaterJugState(jugAAmount, jugBAmount, 0, jugAMax, jugBMax, jugCMax, target), "Empty Jug C", 1.0));

        // Pour A -> B
        if (jugAAmount > 0 && jugBAmount < jugBMax) {
            int pour = Math.min(jugAAmount, jugBMax - jugBAmount);
            successors.add(new Transition(new WaterJugState(jugAAmount - pour, jugBAmount + pour, jugCAmount, jugAMax, jugBMax, jugCMax, target), "Pour A -> B", 1.0));
        }
        // Pour A -> C
        if (jugAAmount > 0 && jugCAmount < jugCMax) {
            int pour = Math.min(jugAAmount, jugCMax - jugCAmount);
            successors.add(new Transition(new WaterJugState(jugAAmount - pour, jugBAmount, jugCAmount + pour, jugAMax, jugBMax, jugCMax, target), "Pour A -> C", 1.0));
        }
        // Pour B -> A
        if (jugBAmount > 0 && jugAAmount < jugAMax) {
            int pour = Math.min(jugBAmount, jugAMax - jugAAmount);
            successors.add(new Transition(new WaterJugState(jugAAmount + pour, jugBAmount - pour, jugCAmount, jugAMax, jugBMax, jugCMax, target), "Pour B -> A", 1.0));
        }
        // Pour B -> C
        if (jugBAmount > 0 && jugCAmount < jugCMax) {
            int pour = Math.min(jugBAmount, jugCMax - jugCAmount);
            successors.add(new Transition(new WaterJugState(jugAAmount, jugBAmount - pour, jugCAmount + pour, jugAMax, jugBMax, jugCMax, target), "Pour B -> C", 1.0));
        }
        // Pour C -> A
        if (jugCAmount > 0 && jugAAmount < jugAMax) {
            int pour = Math.min(jugCAmount, jugAMax - jugAAmount);
            successors.add(new Transition(new WaterJugState(jugAAmount + pour, jugBAmount, jugCAmount - pour, jugAMax, jugBMax, jugCMax, target), "Pour C -> A", 1.0));
        }
        // Pour C -> B
        if (jugCAmount > 0 && jugBAmount < jugBMax) {
            int pour = Math.min(jugCAmount, jugBMax - jugBAmount);
            successors.add(new Transition(new WaterJugState(jugAAmount, jugBAmount + pour, jugCAmount - pour, jugAMax, jugBMax, jugCMax, target), "Pour C -> B", 1.0));
        }

        return successors;
    }

    @Override
    public String getUniqueId() {
        return jugAAmount + "," + jugBAmount + "," + jugCAmount;
    }

    @Override
    public int getHeuristic() {
        int d1 = Math.abs(jugAAmount - target);
        int d2 = Math.abs(jugBAmount - target);
        int d3 = Math.abs(jugCAmount - target);
        return Math.min(d1, Math.min(d2, d3));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaterJugState that = (WaterJugState) o;
        return jugAAmount == that.jugAAmount &&
               jugBAmount == that.jugBAmount &&
               jugCAmount == that.jugCAmount &&
               jugAMax == that.jugAMax &&
               jugBMax == that.jugBMax &&
               jugCMax == that.jugCMax &&
               target == that.target;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jugAAmount, jugBAmount, jugCAmount, jugAMax, jugBMax, jugCMax, target);
    }
}
