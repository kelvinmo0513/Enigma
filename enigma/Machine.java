package enigma;

import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Kelvin Mo
 */
class Machine {

    /** Number of rotors. */
    private int numOfRotors;

    /** Number of pawls. */
    private int numPawls;

    /** All possible rotors to be passed in. */
    private Object[] totalRotors;

    /** Rotors that are active in using.*/
    private Rotor[] availableRotors;

    /** Permutation of plugboard. */
    private Permutation board;

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        this.numOfRotors = numRotors;
        this.numPawls = pawls;
        this.totalRotors = allRotors.toArray();
        this.availableRotors = new Rotor[numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return this.numOfRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return this.numPawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return this.availableRotors[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors.length != this.numOfRotors) {
            throw new EnigmaException("Number of rotors does not match.");
        }
        for (int i = 0; i < this.totalRotors.length; i++) {
            for (int j = 0; j < rotors.length; j++) {
                String rotorName = ((Rotor) this.totalRotors[i]).name();
                if ((rotors[j].toString()).equals(rotorName)) {
                    this.availableRotors[j] = (Rotor) this.totalRotors[i];
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != this.numOfRotors - 1) {
            throw new EnigmaException("Setting does not match numRotors()-1.");
        }
        for (int i = 1; i < this.numOfRotors; i++) {
            if (!alphabet().contains(setting.charAt(i - 1))) {
                throw new EnigmaException("Setting not contained in alphabet.");
            } else {
                this.availableRotors[i].set(setting.charAt(i - 1));
            }
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return this.board;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        this.board = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        boolean[] advance = new boolean[this.availableRotors.length];
        advance[this.availableRotors.length - 1] = true;
        for (int i = 1; i < this.availableRotors.length - 1; i++) {
            if (this.availableRotors[i + 1].atNotch()) {
                advance[i] = true;
            }
        }
        for (int i = 2; i < this.availableRotors.length; i++) {
            if (this.availableRotors[i].atNotch()) {
                if (this.availableRotors[i - 1].rotates()) {
                    advance[i] = true;
                }
            }
        }
        for (int i = 0; i < advance.length; i++) {
            if (advance[i]) {
                this.availableRotors[i].advance();
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int result = c;
        int length = this.availableRotors.length;
        for (int i = length - 1; i >= 0; i--) {
            result = getRotor(i).convertForward(result);
        }
        for (int i = 1; i < length; i++) {
            result = getRotor(i).convertBackward(result);
        }
        return result;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            int num = _alphabet.toInt(msg.charAt(i));
            result += _alphabet.toChar(convert(num));
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

}
