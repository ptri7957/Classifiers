import java.util.List;

/**
 * @author ptri7957
 * @SID: 312160461
 * 
 *       K-Nearest Neighbours algorithm 
 *       
 *       
 *       References: 
 *       - http://people.revoledu.com/kardi/tutorial/KNN/KNN_Numerical-example.html
 * 
 * 
 */
public class KNearestNeighbours {

	private int[] index; // index array

	
	/**
	 * KNearestNeighbours Constructor
	 * 
	 * @param index
	 */
	public KNearestNeighbours(int[] index) {
		this.index = index;
	}

	
	/**
	 * Returns the index array.
	 * The indexes will be used to predict the class
	 * of the test input. The indexes will be sorted
	 * according to the sorted distances, although
	 * the indexes do not correspond to the position
	 * of the attributes after the sort
	 * 
	 * @return index
	 */
	public int[] getIndex() {
		return index;
	}

	
	/**
	 * Returns the euclidean distance between two instances
	 * 
	 * @param instance1
	 * @param instance2
	 * @return distance
	 */
	public double euclideanDistance(double[] instance1, double[] instance2) {

		double distance = 0;

		// Uses the euclidean distance formula
		for (int i = 0; i < instance2.length - 1; i++) {

			// Sum up the sqaure of the distances
			distance += Math.pow((instance1[i] - instance2[i]), 2);
		}

		// Return the square root of the distance sum
		return Math.sqrt(distance);

	}

	
	/**
	 * Returns the set of nearest neighbours from the
	 * test instance. Uses the euclideanDistance()
	 * function to calculate the distance between the 
	 * test instance and the data set and sorts the 
	 * distances from shortest to longest
	 * 
	 * @param dataset
	 * @param instance
	 * @param k
	 * @return neighbours
	 */
	public double[] getNeighbours(List<double[]> dataset, double[] instance,
			int k) {

		// Array of distances
		double[] distances = new double[dataset.size()];

		// Calculate the euclidean distance between the test instance
		// and the values in the dataset
		for (int i = 0; i < dataset.size(); i++) {

			// Get the euclidean distance
			double dist = euclideanDistance(instance, dataset.get(i));

			// Append the distances into the distances array
			distances[i] = dist;
		}

		// Sort the distances from shortest to longest
		// Also sorts the index array according to the distances
		// Used to grab a prediction
		sort(distances);

		double[] neighbours = new double[k];

		// Create a sorted array of distances
		for (int i = 0; i < k; i++) {
			neighbours[i] = distances[i];
		}

		// Return the neighbours
		return neighbours;

	}

	
	/**
	 * Predicts the class of the test instance.
	 * This is determined by the classes of the
	 * k nearest neighbours. If there is a greater
	 * amount of yes', the prediction will be yes.
	 * If there is a greater amount of no's, the
	 * prediction will be no.
	 * 
	 * @param dataset
	 * @param k
	 * @return 1 or 0
	 */
	public int getPrediction(List<double[]> dataset, int k) {

		int yes = 0;
		int no = 0;

		for (int i = 0; i < k; i++) {

			// If the class is "yes", increment the yes counter
			if (dataset.get(index[i])[dataset.get(index[i]).length - 1] == 1.0) {
				yes++;
			}

			// If the class is "no", increment the no counter
			if (dataset.get(index[i])[dataset.get(index[i]).length - 1] == 0.0) {
				no++;
			}
			
		}

		// If there are more yes' than no's, return 1
		if (yes > no) {

			return 1;

		}else if ( yes == no ){
			
			return 1;
			
		}else {

			// Return 0 otherwise
			return 0;
		}

	}
	
	/**
	 * Sorts the distances and the index array.
	 * The function is used to sort the distances
	 * to determine the nearest neighbours from the
	 * test instance.
	 * 
	 * Uses basic selection sort as the sorting
	 * algorithm
	 * 
	 * @param distances
	 */
	public void sort(double[] distances) {

		for (int i = 0; i < index.length; i++) {
			index[i] = i;
		}

		for (int i = 0; i < distances.length; i++) {
			for (int j = i + 1; j < distances.length; j++) {

				if (distances[i] > distances[j]) {
					
					double temp = distances[i];

					distances[i] = distances[j];
					distances[j] = temp;

					int inTemp = index[i];

					index[i] = index[j];
					index[j] = inTemp;
				}
				
			}
			
		}
		
	}
	
}
