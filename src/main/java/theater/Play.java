package theater;

/**
 * Represents a play with a name and a type (for example tragedy or comedy).
 *
 * @author Zhengyu YI
 * @version 1.0
 */
public class Play {

    /** Human-readable name of the play. */
    private final String name;

    /** Type of the play, such as "tragedy" or "comedy". */
    private final String type;

    /**
     * Creates a new play.
     *
     * @param name the title of the play
     * @param type the type of the play
     */
    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Returns the name of this play.
     *
     * @return the play's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of this play.
     *
     * @return the play's type
     */
    public String getType() {
        return type;
    }
}

