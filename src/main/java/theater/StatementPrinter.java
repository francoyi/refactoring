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
        final StringBuilder statement =
                new StringBuilder("Statement for " + invoice.getCustomer()
                        + System.lineSeparator());

        // loop 1: build each performance line
        for (final Performance performance : invoice.getPerformances()) {
            final Play play = getPlay(performance);
            statement.append(String.format(
                    "  %s: %s (%s seats)%n",
                    play.getName(),
                    usd(getAmount(performance)),
                    performance.getAudience()));
        }

        // queries for totals
        final int totalAmount = getTotalAmount();
        final int volumeCredits = getTotalVolumeCredits();

        statement.append(String.format(
                "Amount owed is %s%n", usd(totalAmount)));
        statement.append(String.format(
                "You earned %s credits%n", volumeCredits));

        return statement.toString();
    }

    /**
     * Formats the given amount (in cents) as a US dollar string.
     *
     * @param amount amount in cents
     * @return formatted amount using US currency format
     */
    private String usd(int amount) {
        final NumberFormat formatter =
                NumberFormat.getCurrencyInstance(Locale.US);
        return formatter.format(
                amount / (double) Constants.CENTS_PER_DOLLAR);
    }

    /**
     * Computes the total volume credits for this invoice.
     *
     * @return total volume credits
     */
    private int getTotalVolumeCredits() {
        int result = 0;
        for (final Performance performance : invoice.getPerformances()) {
            final Play play = getPlay(performance);
            result += getVolumeCredits(performance, play);
        }
        return result;
    }

    /**
     * Computes the total amount owed for this invoice.
     *
     * @return total amount in cents
     */
    private int getTotalAmount() {
        int result = 0;
        for (final Performance performance : invoice.getPerformances()) {
            result += getAmount(performance);
        }
        return result;
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
