package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Kelvin Mo
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testCharInvert() {
        Alphabet a = new Alphabet("ABCDE");
        Permutation p = new Permutation("(BACDE)", a);
        assertEquals(p.invert('A'), 'B');
        assertEquals(p.invert('B'), 'E');
        assertEquals(p.invert('D'), 'C');

        Alphabet b = new Alphabet("ABCDE");
        Permutation q = new Permutation("(AE) (CD) (B)", b);
        assertEquals(q.invert('E'), 'A');
        assertEquals(q.invert('C'), 'D');
        assertEquals(q.invert('B'), 'B');
    }

    @Test
    public void testCharPermute() {
        Alphabet a = new Alphabet("ABCDE");
        Permutation p = new Permutation("(BACDE)", a);
        assertEquals(p.permute('A'), 'C');
        assertEquals(p.permute('B'), 'A');
        assertEquals(p.permute('D'), 'E');

        Alphabet b = new Alphabet("ABCDEF");
        Permutation q = new Permutation("(AEF) (CD) (B)", b);
        assertEquals(q.permute('E'), 'F');
        assertEquals(q.permute('C'), 'D');
        assertEquals(q.permute('B'), 'B');
    }

    @Test
    public void testIntInvert() {
        Alphabet a = new Alphabet("ABCDE");
        Permutation p = new Permutation("(BACDE)", a);
        assertEquals(p.invert(0), 1);
        assertEquals(p.invert(3), 2);
        assertEquals(p.invert(1), 4);

        Alphabet b = new Alphabet("ABCDEF");
        Permutation q = new Permutation("(AEF) (CD) (B)", b);
        assertEquals(q.invert(5), 4);
        assertEquals(q.invert(0), 5);
        assertEquals(q.invert(3), 2);
    }

    @Test
    public void testIntPermute() {
        Alphabet a = new Alphabet("ABCDE");
        Permutation p = new Permutation("(BACDE)", a);
        assertEquals(p.permute(4), 1);
        assertEquals(p.permute(2), 3);
        assertEquals(p.permute(0), 2);

        Alphabet b = new Alphabet("ABCDEF");
        Permutation q = new Permutation("(AEF) (CD) (B)", b);
        assertEquals(q.permute(5), 0);
        assertEquals(q.permute(3), 2);
        assertEquals(q.permute(0), 4);
    }

    @Test(expected = EnigmaException.class)
    public void testOutOfRange() {
        Permutation p = new Permutation("(BADCE)", new Alphabet("ABCDE"));
        p.invert('F');
    }



}
