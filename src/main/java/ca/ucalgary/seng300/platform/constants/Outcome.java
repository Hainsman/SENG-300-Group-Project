package ca.ucalgary.seng300.platform.constants;

public enum Outcome {
    // NOTE(Dylan): We can change these numbers later
    // [or get fancy by overriding them :)]
    LOSE(0),
    DRAW(0.5),
    WIN(1);

    final double deltaElo;
    Outcome(double deltaElo) {
        this.deltaElo = deltaElo;
    }
}
