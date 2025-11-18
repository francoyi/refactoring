package theater;

/**
 * Class representing a performance of a play..
 */
public class Performance {

    /** Identifier of the play that is being performed. */
    private final String playID;

    /** Number of audience members attending this performance. */
    private final int audience;

    /**
     * Creates a performance.
     *
     * @param playID   identifier of the play
     * @param audience audience size
     */
    public Performance(String playID, int audience) {
        this.playID = playID;
        this.audience = audience;
    }

    /**
     * Returns the identifier of the play.
     *
     * @return the play identifier
     */
    public String getPlayID() {
        return playID;
    }

    /**
     * Returns the audience size.
     *
     * @return the number of audience members
     */
    public int getAudience() {
        return audience;
    }
}
