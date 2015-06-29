import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author ptri7957
 * @SID: 312160461
 * 
 *       COMP3308 Assignment 1
 * 
 */
public class MyProgram {

	/**
	 * Main Method
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {

		File input1 = new File(args[0]);
		File input2 = new File(args[1]);

		// Data structure to hold the dataset
		List<double[]> dataset = new ArrayList<double[]>();
		List<double[]> testset = new ArrayList<double[]>();

		try {

			Scanner scan1 = new Scanner(input1);

			String input = null; // Input string
			
			while (scan1.hasNextLine()) {

				input = scan1.nextLine();
				String[] temp = input.split(",");

				// Array of instances in the data set
				double[] attr = new double[temp.length];

				// Parse the "yes" and "no" results to 1.0 and 0.0
				// Parse the strings to doubles
				for (int i = 0; i < attr.length; i++) {

					if (temp[i].equals("yes")) {

						// "yes" values will be represented by a 1
						attr[i] = 1.0;

					} else if (temp[i].equals("no")) {

						// "no" values will be represented by a 0
						attr[i] = 0.0;

					} else {

						// Parse the number strings to ints
						attr[i] = Double.parseDouble(temp[i]);

					}

				}

				dataset.add(attr);

			}
			scan1.close();
			// Catch exception
		} catch (Exception e) {

			// Print exception
			System.out.println(e);

		}

		try {
			Scanner scan2 = new Scanner(input2);
			
			String testin = null; // Test string
			
			while (scan2.hasNextLine()) {

				testin = scan2.nextLine();
				String[] temp = testin.split(",");

				// Array of instances in the test set
				double[] attr = new double[temp.length];

				// Parse the attributes to doubles
				for (int i = 0; i < attr.length; i++) {

					attr[i] = Double.parseDouble(temp[i]);

				}

				testset.add(attr);

			}
			
			scan2.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		int[] index = new int[dataset.size()];

		// Check to see what algorithm is used
		// Executes the corresponding algorithm
		if (!args[2].isEmpty()) {

			// If the algorithm is KNN, execute the KNN algorithm
			if (args[2].contains("NN")) {
				
				// KNearestNeighbours object
				KNearestNeighbours knn = new KNearestNeighbours(index);

				// Parse the k value to an int
				String n = args[2].substring(0, 1);
				int k = Integer.parseInt(n);

				// Execute the knn algorithm
				for (int i = 0; i < testset.size(); i++) {

					knn.getNeighbours(dataset, testset.get(i), k);
					int result = knn.getPrediction(dataset, k);

					// Print the prediction onto the console
					if (result == 1) {

						// Print yes onto the console if the result = 1
						System.out.println("yes");

					} else {

						// Print no otherwise
						System.out.println("no");

					}

				}

			}

			// If the algorithm is NB, execute Naive Bayes
			if (args[2].equals("NB")) {

				NaiveBayes nb = new NaiveBayes();

				// Split the data into the two classes
				nb.split_data(dataset);

				// Calculate the mean for each class list
				nb.calculate_avg_list();

				// Calculate the standard deviation for the
				// two classes
				nb.calculate_stdev_list();

				// For each test instance, get its result
				for (int i = 0; i < testset.size(); i++) {

					if (nb.getPrediction(testset.get(i)) == 1) {
						System.out.println("yes");
					}
					if (nb.getPrediction(testset.get(i)) == 0) {
						System.out.println("no");
					}
				}

			}
		
		}
	}
}
