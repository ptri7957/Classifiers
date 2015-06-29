import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author ptri7957
 * @SID: 312160461
 * 
 * 10 Folds Classifier
 * 
 * Instructions:
 * - args[0] = the name of the dataset
 * - args[1] = foldNB or foldkNN where k is a number
 *
 */
public class TenFoldsClassifier {
	private int num_folds;
	private List<double[]> yes_list = new ArrayList<double[]>();
	private List<double[]> no_list = new ArrayList<double[]>();
	List<ArrayList<double[]>> train = new ArrayList<ArrayList<double[]>>();

	public TenFoldsClassifier(int num_folds) {
		this.num_folds = num_folds;
	}

	public int getNumFolds() {
		return num_folds;
	}

	public List<double[]> getYes() {
		return yes_list;
	}

	public List<double[]> getNo() {
		return no_list;
	}

	public List<ArrayList<double[]>> getTrain() {
		return train;
	}

	public void split_data(List<double[]> dataset) {
		int num_attr = dataset.get(0).length - 1;
		for (int i = 0; i < dataset.size(); i++) {
			if (dataset.get(i)[num_attr] == 0) {
				no_list.add(dataset.get(i));
			}
			if (dataset.get(i)[num_attr] == 1) {
				yes_list.add(dataset.get(i));
			}
		}
	}

	public List<ArrayList<double[]>> split_stratified() {
		List<ArrayList<double[]>> stratified = new ArrayList<ArrayList<double[]>>();
		for (int i = 0; i < num_folds; i++) {
			stratified.add(new ArrayList<double[]>());
		}
		int i = 0;
		for (double[] instance : getYes()) {
			stratified.get(i).add(instance);
			i++;
			if (i == 10) {
				i = 0;
			}
		}

		int j = 0;
		for (double[] instance : getNo()) {
			stratified.get(j).add(instance);
			j++;
			if (j == 10) {
				j = 0;
			}
		}
		return stratified;
	}

	public void fold() throws FileNotFoundException {

		PrintWriter outfile = new PrintWriter("pima-folds.csv");

		int attr = split_stratified().get(0).get(0).length;

		for (int j = 0; j < split_stratified().size(); j++) {
			outfile.write("fold" + (j + 1) + "\n");
			for (int k = 0; k < split_stratified().get(j).size(); k++) {

				for (int l = 0; l < attr; l++) {

					if (l == attr - 1) {
						if (split_stratified().get(j).get(k)[l] == 1.0) {
							outfile.write("yes");
						}
						if (split_stratified().get(j).get(k)[l] == 0.0) {
							outfile.write("no");
						}
					} else {

						outfile.write(split_stratified().get(j).get(k)[l] + ",");
					}

				}
				outfile.write("\n");

			}
			outfile.write("\n");

		}

		outfile.close();

		train = split_stratified();

		// System.out.println(train.size());
	}

	public static void main(String[] args) {
		File input1 = new File(args[0]);

		// Data structure to hold the dataset
		List<double[]> dataset = new ArrayList<double[]>();

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
		TenFoldsClassifier t = new TenFoldsClassifier(10);
		try {
			t.split_data(dataset);
			t.split_stratified();
			t.fold();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		List<ArrayList<double[]>> test = new ArrayList<ArrayList<double[]>>();
		List<ArrayList<double[]>> temp = new ArrayList<ArrayList<double[]>>();
		
		test.add(new ArrayList<double[]>());
		temp.add(0, t.getTrain().remove(t.getTrain().size() - 1));

		int l = t.getTrain().size() - 1;
		
		// If Naive bayes was requested
		if (args[1].equals("foldNB")) {
			
			NaiveBayes nb = new NaiveBayes();

			// The number of correctly classified instances
			double correct = 0;

			// The accuracies of NB per fold
			double[] accuracies = new double[t.getNumFolds()];
			
			// The average accuracy for NB out of the 10 folds
			double avgAccuracy = 0.0;

			for (int i = 0; i < t.getNumFolds(); i++) {

				// Grab the last fold (n) as the test set
				test.add(0, temp.get(0));
				
				for (int j = 0; j < t.getTrain().size(); j++) {
					// Perform the NB classifier
					nb.split_data(t.getTrain().get(j));
					nb.calculate_avg_list();
					nb.calculate_stdev_list();
					
					for (int k = 0; k < test.get(0).size(); k++) {
						// Prediction is the class given in the test instance
						double prediction = test.get(0).get(k)[test.get(0).get(
								k).length - 1];
						
						// If NB's classification is equal to the actual
						// classification of the instance, count it
						if (nb.getPrediction(test.get(0).get(k)) == prediction) {
							correct++;
						}
					}
					
					// Set the accuracy of the cvurrent fold
					accuracies[i] = (correct / test.get(0).size()) * 100;
					correct = 0;

				}
				// Swap out the test instance for the next fold down
				if (l >= 0) {
					t.getTrain().add(temp.get(0));
					temp.add(0, t.getTrain().remove(l--));
				}
			}

			// Calculate the average accuracy for the total folds
			for (int i = 0; i < t.getNumFolds(); i++) {
				avgAccuracy += accuracies[i];
			}

			// Print the accuracy
			System.out.println("NB Accuracy: "
					+ (avgAccuracy / t.getNumFolds()));
		}

		// If nearest neighbours is requested
		if (args[1].contains("NN")) {
			
			double correct = 0;

			double[] accuracies = new double[t.getNumFolds()];
			double avgAccuracy = 0.0;
			
			String n = args[1].substring(4, 5);
			int K = Integer.parseInt(n);
			
			for (int i = 0; i < t.getNumFolds(); i++) {
				
				// Grab the last fold (n) as the test set
				test.add(0, temp.get(0));

				for (int j = 0; j < t.getTrain().size(); j++) {
					
					int[] index = new int[t.getTrain().get(j).size()];
					KNearestNeighbours knn = new KNearestNeighbours(index);
					
					for (int k = 0; k < test.get(0).size(); k++) {
						// Get the nearest neighbours of the test set
						knn.getNeighbours(t.getTrain().get(j), test.get(0).get(k), K);
						double prediction = test.get(0).get(k)[test.get(0).get(
								5).length - 1];
						
						if(knn.getPrediction(t.getTrain().get(j), K) == prediction){
							correct++;
						}

					}
					accuracies[i] = (correct / test.get(0).size()) * 100;
					correct = 0;

				}
				
				// Swap out the test instance for the next fold down
				if (l >= 0) {
					t.getTrain().add(temp.get(0));
					temp.add(0, t.getTrain().remove(l--));
				}
			}

			for (int i = 0; i < t.getNumFolds(); i++) {
				avgAccuracy += accuracies[i];
			}

			System.out.println(K + "NN Accuracy: "
					+ (avgAccuracy / t.getNumFolds()));
		}

	}
}
