package enigma;
import java.util.HashMap;
import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Kelvin Mo
 */
class Permutation {

    /** Array used for splitting the cycles. */
    private String[] _cycles;

    /** Hashmap for storing mappings. */
    private HashMap<Character, Character> map;

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        String cycle = cycles;
        cycle = cycle.replace("(", "");
        cycle = cycle.replace(")", " ");
        _cycles = cycle.split(" ");

        map = new HashMap<Character, Character>();
        for (int i = 0; i < _alphabet.size(); i++) {
            map.put(alphabet.toChar(i), alphabet.toChar(i));
        }
        for (int i = 0; i < _cycles.length; i++) {
            addCycle(_cycles[i]);
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int i = 0; i < cycle.length(); i++) {
            if (i != cycle.length() - 1) {
                map.put(cycle.charAt(i), cycle.charAt(i + 1));
            } else {
                map.put(cycle.charAt(i), cycle.charAt(0));
            }
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return map.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char c = _alphabet.toChar(wrap(p));
        return _alphabet.toInt(permute(c));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char ch = _alphabet.toChar(wrap(c));
        return _alphabet.toInt(invert(ch));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (_alphabet.contains(p)) {
            return map.get(p);
        }
        throw new EnigmaException("Does not contain character.");
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (map.containsValue(c)) {
            for (char key: map.keySet()) {
                if (map.get(key) == c) {
                    return key;
                }
            }
        }
        throw new EnigmaException("Does not contain character.");
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int count = 0;
        for (int i = 0; i < _cycles.length; i++) {
            count += _cycles[i].length();
        }
        if (count != _alphabet.size()) {
            return false;
        }
        return true;

    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

}
