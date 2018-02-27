/**
 * @author Ritwik Banerjee
 */
package sample;

@SuppressWarnings("unused")
public class Drug implements Comparable<Drug> {

    @SuppressWarnings("unused")
    public enum DosageFrequency {
        TWICE_A_DAY, DAILY, WEEKLY, AS_NEEDED
    }

    private final String          name;
    private       int             doseInMg;
    private       DosageFrequency frequency;

    public Drug(String name, int doseInMg, DosageFrequency frequency) {
        this.name = name;
        this.doseInMg = doseInMg;
        this.frequency = frequency;
    }

    public String getName()                             { return name; }

    public int getDoseInMg()                            { return doseInMg; }

    public void setDoseInMg(int doseInMg)               { this.doseInMg = doseInMg; }

    public DosageFrequency getFrequency()               { return frequency; }

    public void setFrequency(DosageFrequency frequency) { this.frequency = frequency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Drug)) return false;

        Drug that = (Drug) o;

        return this.name.equals(that.name) && this.frequency.equals(that.frequency) && this.doseInMg == that.doseInMg;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();

        result = 31 * result + doseInMg;
        return result;
    }

    @Override
    public String toString() {
        return String.format("Drug{name='%s', doseInMg=%d, frequency=%s}", name, doseInMg, frequency);
    }

    @Override
    public int compareTo(Drug that) {
        new Integer(this.doseInMg);
        return this.name.compareTo(that.name) + new Integer(this.doseInMg).compareTo(that.doseInMg) + this.frequency.compareTo(that.frequency);
    }
}
