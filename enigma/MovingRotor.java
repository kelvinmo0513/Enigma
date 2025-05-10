package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Kelvin Mo
 */
class MovingRotor extends Rotor {

    /** Position of notches. */
    private String notch;

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initially in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        this.notch = notches;
    }

    @Override
    void advance() {
        if (setting() + 1 == size()) {
            set(-1);
        }
        set(permutation().wrap(setting() + 1));
    }

    @Override
    String notches() {
        return this.notch;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        if (notch.contains(Character.toString(alphabet().toChar(setting())))) {
            return true;
        }
        return false;
    }
}
