package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Kelvin Mo
 */
class Alphabet {

    /** Alphabet of characters. */
    private String characters;

    /** A new alphabet containing CHARS. The K-th character has index.
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        this.characters = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return this.characters.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < this.characters.length(); i++) {
            if (this.characters.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < 0 || index > this.characters.length()) {
            throw new EnigmaException("Index must be within the "
                    + "range of 0 to its size.");
        }
        return this.characters.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (!this.characters.contains(Character.toString(ch))) {
            throw new EnigmaException("Character does not exist.");
        }
        return characters.indexOf(ch);
    }

}
