import java.util.ArrayList;
import java.util.Arrays;

public class Main {
	
	public static boolean found = false;
	private static int[] S = setupState(); // S = {0,1,...,255}
	private static final int[] desiredOutputSequence = {130, 189, 254, 192, 238, 132, 216, 132, 82, 173};
	private static final int outputLength = 10;
	private static final int[] keyInitial = {80, 0, 0, 0, 0};
	private static int counter = 0;
	private static ArrayList<Thread> threads = new ArrayList<>();
	
	/**
	 * Key length is 5, first element known: key[0] = 80.
	 * Output is 130, 189, 254, 192, 238, 132, 216, 132, 82, 173.
	 * @param args
	 */
	public static void main(String[] args) {
		print(S);
		bruteForce();
	}
	
	/**
	 * Bruteforce for every S[1] (0 to 255), using a new thread for every iteration.
	 */
	private static void bruteForce() {
		for(int i = 0; i < 256; i++) {
			int[] key = keyInitial.clone(); // new key object for every thread
			key[1] = i;
			System.out.println("new thread, key[1]=" + i);
			Thread thread = new Thread(){
				public void run() {
					bruteForceIndex(key, 2); // initiate the brute force from index 2 onwards
				}
			};
			threads.add(thread);
			thread.start();
		}
	}
	
	/**
	 * Recursively check every key possible
	 */
	private static void bruteForceIndex(int[] key, int index) {
		for(int i = 0; i < 256; i++) {
			key[index] = i;
			int[] output = encrypt(key); // run the algorithm
			counter++;
			
			if (Arrays.equals(output, desiredOutputSequence)) {
				finish(output, key);
			}
			
			if (index < key.length - 1)
				bruteForceIndex(key, index + 1);
		}
	}
	
	private static int[] encrypt(int[] key) {
		RC4setup(key);
		return RC4keystream();
	}

	private static void RC4setup(int[] key) {
		S = setupState();
		int j = 0;
		int L = key.length;
		
		for(int i = 0; i < 256; i++) {
			j = (j + S[i] + key[i % L]) % 256;
			swap(S, i, j);
		}
	}

	private static int[] RC4keystream() {
		int i = 0;
		int j = 0;
		int[] outputSequence = new int[outputLength];

		for(int k = 0; k < outputLength; k++) {
			i = (i + 1) % 256;
			j = (j + S[i]) % 256;
			swap(S, i, j);
			int outputBit = S[(S[i] + S[j]) % 256];
			outputSequence[k] = outputBit;
		}
		return outputSequence;
	}
	
	/**
	 * Solution found, print data and stop program.
	 * @param output Output sequence
	 * @param key Key used to find the output sequence.
	 */
	private static void finish(int[] output, int[] key) {
		System.out.println("Output found!");
		print(output);
		System.out.println("Outputs checked: " + counter);
		System.out.print("Key: ");
		print(key);
		found = true;
		System.exit(0);
	}
	
	/**
	 * Swaps two elements in array S at index i and index j.
	 * @param S array
	 * @param i index to swap
	 * @param j index to swap
	 */
	private static void swap(int[] S, int i, int j) {
		int temp = S[i];
		S[i] = S[j];
		S[j] = temp;
	}
	
	/**
	 * @return array with elements {0,1,...,255}
	 */
	private static int[] setupState() {
		int[] S = new int[256];
		for(int i = 0; i < S.length; i++)
			S[i] = i;
		return S;
	}

	/**
	 * Prints elements of array
	 */
	private static void print(int[] array) {
		for(int i = 0 ; i < array.length; i++) {
			System.out.print(array[i]);
			System.out.print(", ");
		}
		System.out.println();
	}
	
	/**
	 * Returns the elapsed time in HH:MM:SS:ms format
	 */
	public static String formattedTime(final long ms) {
	    long millis = ms % 1000;
	    long x = ms / 1000;
	    long seconds = x % 60;
	    x /= 60;
	    long minutes = x % 60;
	    x /= 60;
	    long hours = x % 24;
	    return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
	}
}
