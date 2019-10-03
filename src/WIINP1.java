import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * CS 3345 Project 1
 * William Ingarfield
 * September 27th, 2019
 */

/**
 * Node class for use in the Trie. Can have arbitrary number of children.
 */
class Node {
    boolean terminal;
    int outDegree;
    Node[] children;

    public Node() {
        // initialize to default values
        outDegree = 0;
        terminal = false;
        children = new Node[26];
    }
}

/**
 * Implementation of a tree for spellchecking.
 */
class Trie {
    private Node root;
    // default constructor
    public Trie() {
        root = new Node();
    }

    /**
     * Helper function to convert String to an array where 'a' = 0, 'b' = 1, ...
     * @param inp input String to generate from
     */
    public char[] stringToNormalizedArray(String inp) {
        char[] chars = inp.toCharArray();
        for(int i = 0; i < chars.length; i++) {
            chars[i] = (char)(chars[i] - 'a');
        }

        return chars;
    }

    /**
     * Insert a String into the Trie.
     * @param s String to insert
     * @return False if s was already present. True otherwise.
     */
    public boolean insert(String s) {
        char[] chars = stringToNormalizedArray(s);
        Node spot = root;
        for(int i = 0; i < chars.length; i++) {
            // if node doesn't exist, we need to add it
            if(spot.children[chars[i]] == null) {
                spot.children[chars[i]] = new Node();
                spot.outDegree += 1;
            }
            spot = spot.children[chars[i]];
        }
        if(spot.terminal) {
            // in this case, word was already in trie
            return false;
        } else{
            // denote that this is the end of a word
            spot.terminal = true;
            return true;
        }
    }

    /**
     * Check if a String is in the Trie
     * @param s String to check
     * @return True if s is present, false otherwise.
     */
    public boolean isPresent(String s) {
        char[] chars = stringToNormalizedArray(s);
        Node spot = root;
        for(int i = 0; i < chars.length; i++) {
            if(spot.children[chars[i]] == null) {
                // we have reached the end of the trie before the end of the word
                return false;
            }

            spot = spot.children[chars[i]];
        }
        return spot.terminal;
    }

    /**
     * Delete a String from the Trie.
     * @param s String to delete
     * @return False if s is not present, true otherwise
     */
    public boolean delete(String s) {
        // trivial case: s is not in the trie
        if(!isPresent(s)) {
            return false;
        }

        // now we need to actually delete the nodes.
        char[] chars = stringToNormalizedArray(s);
        Node spot = root;
        for(int i = 0; i < chars.length; i++) {
            spot = spot.children[chars[i]];
        }
        // in the case where outdegree > 0, we can simply set terminal=false and the deletion is complete.
        if(spot.outDegree > 0) {
            spot.terminal = false;
            return true;
        }
        // handle the second case; find place to remove nodes further up the tree.
        // we go through the tree until membership(spot) == 1. then, we go to the parent and sever the node
        spot = root;
        for(int i = 0; i < chars.length; i++) {
            if(membership(spot.children[chars[i]]) == 1) {
                spot.children[chars[i]] = null;
                return true;
            }
            spot = spot.children[chars[i]];
        }

        return true;
    }

    /**
     * List the number of words in the tree.
     * @return number of words in the tree
     */
    public int membership() {
        return membership(root);
    }

    /**
     * Helper method for membership(). Calculates membership recursively from a given node
     * @param current node to start from
     * @return number of words contained in the trie
     */
    private int membership(Node current) {
        int sum = 0;
        // number of words = number of nodes where spot.terminal = true
        if(current.terminal) {
            sum += 1;
        }
        for(int i = 0; i < 26; i++) {
            // for each child, recursively calculate the membership
            if(current.children[i] != null) {
                sum += membership(current.children[i]);
            }
        }
        return sum;
    }

    /**
     * List all of the words contained in the tree, in alphabetical order.
     * Prints to stdout.
     */
    public void listAll() {
        listAll(root, "");
    }

    /**
     * Helper method for listAll(). Recursively print from a given node.
     * @param current Node to start from
     * @param s String to print
     */
    private void listAll(Node current, String s) {
        // once we get to the terminal node, we have the entire string stored in s.
        if(current.terminal) {
            System.out.println(s);
        }
        for(int i = 0; i < 26; i++) {
            if(current.children[i] != null) {
                // precompute the printable strings and recursively call
                listAll(current.children[i], s + (char)('a' + i));
            }
        }
    }
}

/**
 * This class provides an interface for the Trie data stucture we create.
 * Commands in "TrieData.txt" will be parsed and ran.
 */
public class WIINP1 {
    public static void main(String[] args) throws FileNotFoundException {

        Scanner input = new Scanner(new File("TrieData.txt"));
        String[] command;
        Trie myTestTrie = new Trie();

        while(input.hasNext()) {
            // since each command is prefixed by a single character, followed by 0 or more space separated parameters
            command = input.nextLine().split(" ");

            // parse commands
            switch(command[0]) {
                case "A":
                    // Insert the word ‘‘soap’’
                    // Print one of the lines ‘‘soap inserted’’ or ‘‘soap already exists’’
                    if(myTestTrie.insert(command[1])) {
                        System.out.println(command[1] + " inserted");
                    } else {
                        System.out.println(command[1] + " already exists");
                    }
                    break;
                case "D":
                    // Delete the word ‘‘sin’’
                    // Print ‘one of the lines ‘sin deleted’’ or ‘‘sin not found’’
                    if(myTestTrie.delete(command[1])) {
                        System.out.println(command[1] + " deleted");
                    } else {
                        System.out.println(command[1] + " not found");
                    }
                    break;
                case "S":
                    // Search for the word ‘‘fortune’’
                    // Print one of the lines ‘‘fortune found’’ or ‘‘fortune not found’’
                    if(myTestTrie.isPresent(command[1])) {
                        System.out.println(command[1] + " found");
                    } else {
                        System.out.println(command[1] + " not found");
                    }
                    break;
                case "M":
                    // Print the line ‘‘Membership is ####’’ where #### is the number of words in the Trie
                    System.out.printf("Membership is %4d\n", myTestTrie.membership());
                    break;
                case "T":
                    // Where ‘‘text’’ is a sequence of space-separated words, terminated by a newline character.
                    // For each word in the sequence, do nothing if the word is found in your trie. For each word not found,
                    // print a line, ‘‘Spelling mistake’’ followed by the offending word
                    for(int i = 1; i < command.length; i++) {
                        if(!myTestTrie.isPresent(command[i])) {
                            System.out.println("Spelling mistake " + command[i]);
                        }
                    }
                    break;
                case "L":
                    // Print all the elements of the Trie in alphabetical order, one word per line
                    myTestTrie.listAll();
                    break;
                case "E":
                    // The end of the input file
                    System.exit(0);
            }
        }
    }
}
