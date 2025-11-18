package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {

    private final Invoice invoice;
    private final Map<String, Play> plays;

    /**
     * Creates a StatementPrinter for the given invoice and play catalog.
     *
     * @param invoice the invoice whose performances will be printed
     * @param plays   mapping from play id to play
     */
    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     *
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;

        final StringBuilder statement =
                new StringBuilder("Statement for " + invoice.getCustomer()
                        + System.lineSeparator());

        final NumberFormat frmt =
                NumberFormat.getCurrencyInstance(Locale.US);

        for (final Performance performance : invoice.getPerformances()) {
            final int thisAmount = getAmount(performance);
            final Play play = getPlay(performance);

            // add volume credits
            volumeCredits += getVolumeCredits(performance, play);

            // print line for this order
            statement.append(String.format(
                    "  %s: %s (%s seats)%n",
                    play.getName(),
                    frmt.format(thisAmount
                            / (double) Constants.CENTS_PER_DOLLAR),
                    performance.getAudience()));

            totalAmount += thisAmount;
        }

        statement.append(String.format(
                "Amount owed is %s%n",
                frmt.format(totalAmount
                        / (double) Constants.CENTS_PER_DOLLAR)));
        statement.append(String.format(
                "You earned %s credits%n", volumeCredits));

        return statement.toString();
    }

    /**
     * Computes the volume credits earned for a single performance.
     *
     * @param performance the performance that was attended
     * @param play        the play that was performed
     * @return the number of credits earned for this performance
     */
    private int getVolumeCredits(Performance performance, Play play) {
        int result = 0;

        result += Math.max(
                performance.getAudience()
                        - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        if ("comedy".equals(play.getType())) {
            result += performance.getAudience()
                    / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }

        return result;
    }

    /**
     * Computes the amount owed for a single performance.
     *
     * @param performance the performance to price
     * @return the calculated amount in cents
     * @throws RuntimeException if the play type of the performance is unknown
     */
    private int getAmount(Performance performance) {
        final Play play = getPlay(performance);
        int amount;

        switch (play.getType()) {
            case "tragedy":
                amount = Constants.TRAGEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    amount += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience()
                            - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;

            case "comedy":
                amount = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    amount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience()
                            - Constants.COMEDY_AUDIENCE_THRESHOLD);
                }
                amount += Constants.COMEDY_AMOUNT_PER_AUDIENCE
                        * performance.getAudience();
                break;
            default:
                throw new RuntimeException(
                        String.format("unknown type: %s", play.getType()));
        }

        return amount;
    }

    /**
     * Looks up the play corresponding to the given performance.
     *
     * @param performance the performance whose play is requested
     * @return the play associated with the performance
     */
    private Play getPlay(Performance performance) {
        return plays.get(performance.getPlayID());
    }
}
