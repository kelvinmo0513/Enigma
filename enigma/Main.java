package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Kelvin Mo
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine mac = readConfig();
        String next = _input.nextLine();
        String result = "";
        setUp(mac, next);
        while (_input.hasNext()) {
            String setup = _input.nextLine();
            if (setup.startsWith("*")) {
                setUp(mac, setup);
            } else {
                result = mac.convert(setup.replaceAll(" ", ""));
                printMessageLine(result);
            }
        }
        if (!_input.hasNextLine()) {
            if (!next.startsWith("*")) {
                throw new EnigmaException("Format Incorrect.");
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alpha = _config.next();
            _alphabet = new Alphabet(alpha);
            int numRotors, numPawls = 0;
            if (_config.hasNextInt()) {
                numRotors = _config.nextInt();
            } else {
                throw new EnigmaException("Does not contain arg numRotors.");
            }
            if (_config.hasNextInt()) {
                numPawls = _config.nextInt();
            } else {
                throw new EnigmaException("Does not contain arg numPawls.");
            }
            ArrayList<Rotor> allRotors = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String perm = "";
            String first = _config.next();
            String rotor = _config.next();
            if (first == null || rotor == null) {
                throw new EnigmaException("Missing arguments");
            }
            while (_config.hasNext("\\(.*")) {
                perm += _config.next();
            }
            if (rotor.charAt(0) == 'M') {
                return new MovingRotor(first,
                        new Permutation(perm, _alphabet), rotor.substring(1));
            } else if (rotor.equals("N")) {
                return new FixedRotor(first, new Permutation(perm, _alphabet));
            } else if (rotor.equals("R")) {
                return new Reflector(first, new Permutation(perm, _alphabet));
            } else {
                throw new EnigmaException("No matching rotors.");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        int num = M.numRotors();
        String[] setting = settings.split("[ ]+");
        if (setting.length - 2 < num) {
            throw new EnigmaException("Incorrect number of args.");
        }
        String[] rotors = new String[num];
        for (int i = 0; i < num; i++) {
            rotors[i] = setting[i + 1];
        }

        for (int i = 0; i < num - 1; i++) {
            for (int k = i + 1; k < num; k++) {
                if (rotors[i].equals(rotors[k])) {
                    throw new EnigmaException("Duplicate of Rotors.");
                }
            }
        }
        M.insertRotors(rotors);
        if (!M.getRotor(0).reflecting()) {
            throw new EnigmaException("Reflector not present.");
        }
        if (num > 1 && M.getRotor(0).rotates()) {
            throw new EnigmaException("Wrong rootor setup.");
        }

        String board = "";
        for (int i = num + 2; i < setting.length; i++) {
            board += setting[i];
        }
        if (setting.length > num + 2 && !setting[num + 2].startsWith("(")) {
            M.setRotors(setting[num + 1] + " " + setting[num + 2]);
        }
        M.setRotors(setting[num + 1]);
        M.setPlugboard(new Permutation(board, _alphabet));
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        msg = msg.trim();
        for (int i = 0; i < msg.length(); i++) {
            _output.print(msg.charAt(i));
            if ((i + 1) % 5 == 0) {
                _output.print(" ");
            }
        }
        _output.println();

    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
