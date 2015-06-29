import java.util.ArrayList;
import java.util.List;

/**
 * @author ptri7957
 * @SID: 312160461
 * 
 *       Naive Bayes
 * 
 */
public class NaiveBayes {

	private List<double[]> yes_list = new ArrayList<double[]>();
	private List<double[]> no_list = new ArrayList<double[]>();

	private List<Double> avg_yes = new ArrayList<Double>();
	private List<Double> avg_no = new ArrayList<Double>();

	private List<Double> stdev_yes = new ArrayList<Double>();
	private List<Double> stdev_no = new ArrayList<Double>();
	
	private double dataset_size = 0;
	private double yes_size = 0;
	private double yes_count = 0;
	private double no_size = 0;
	private double no_count = 0;
	private double num_attributes = 0;

	public double getDataSize(){
		return dataset_size;
	}
	
	public double getYesSize(){
		return yes_size;
	}
	
	public double getYesCount(){
		return yes_count;
	}
	
	public double getNoSize(){
		return no_size;
	}
	
	public double getNoCount(){
		return no_count;
	}
	
	public double getNumAttr(){
		return num_attributes;
	}
	
	public final List<double[]> getNoList() {
		return no_list;
	}

	public final List<double[]> getYesList() {
		return yes_list;
	}

	public final List<Double> getYesAvg() {
		return avg_yes;
	}

	public final List<Double> getNoAvg() {
		return avg_no;
	}

	public final List<Double> getYesStdev() {
		return stdev_yes;
	}

	public final List<Double> getNoStdev() {
		return stdev_no;
	}

	/**
	 * Split the data into two categories: class = yes and class = no
	 * 
	 * @param dataset
	 */
	public void split_data(List<double[]> dataset) {
		List<double[]>  temp = dataset;
		dataset_size = temp.size();
		int num_attr = dataset.get(0).length - 1;
		num_attributes = num_attr;
		for (int i = 0; i < dataset.size(); i++) {
			if (dataset.get(i)[num_attr] == 0) {
				no_list.add(dataset.get(i));
				no_size++;
			}
			if (dataset.get(i)[num_attr] == 1) {
				yes_list.add(dataset.get(i));
				yes_size++;
			}
		}
		if(yes_size > 0){
			yes_count = yes_size / dataset_size;
		}
		
		if(no_size > 0){
			no_count = no_size / dataset_size;
		}
	}

	/**
	 * Group up the attrubutes in the dataset
	 * 
	 * @param dataset
	 * @return
	 */
	public List<double[]> grouped_attributes(List<double[]> dataset) {
		List<double[]> list = new ArrayList<double[]>();

		int num_attr = dataset.get(0).length - 1;

		for (int i = 0; i < num_attr; i++) {
			double[] temp = new double[dataset.size()];
			for (int j = 0; j < dataset.size(); j++) {
				temp[j] = dataset.get(j)[i];
			}
			list.add(temp);
		}
		return list;
	}

	/**
	 * Calculate the mean of the given array
	 * 
	 * @param array
	 * @return
	 */
	public double mean(double[] array) {
		double avg = 0.0;
		for (int i = 0; i < array.length; i++) {
			avg += array[i];
		}
		return avg / array.length;
	}

	/**
	 * Calculate the standard deviation for the given
	 * array
	 * 
	 * @param array
	 * @return
	 */
	public double stdev(double[] array) {
		double avg = mean(array);
		double var = 0.0;
		double stdev = 0.0;

		if (array.length == 1) {
			return 0;
		}

		for (int i = 0; i < array.length; i++) {
			var += Math.pow((array[i] - avg), 2);
		}

		stdev = var / (array.length - 1);

		return Math.sqrt(stdev);
	}

	/**
	 * Calculate the mean for each attribute in the
	 * dataset
	 * 
	 */
	public void calculate_avg_list() {

		if (!getYesList().isEmpty()) {
			List<double[]> list = grouped_attributes(getYesList());
			for (int i = 0; i < list.size(); i++) {
				avg_yes.add(mean(list.get(i)));
			}
		}

		if (!getNoList().isEmpty()) {
			List<double[]> list = grouped_attributes(getNoList());
			for (int i = 0; i < list.size(); i++) {
				avg_no.add(mean(list.get(i)));
			}
		}
	}

	/**
	 * Calculate the standard deviation for each attribute in 
	 * the dataset
	 * 
	 */
	public void calculate_stdev_list() {

		if (!getYesList().isEmpty()) {
			List<double[]> list = grouped_attributes(getYesList());
			for (int i = 0; i < list.size(); i++) {
				stdev_yes.add(stdev(list.get(i)));
			}
		}

		if (!getNoList().isEmpty()) {
			List<double[]> list = grouped_attributes(getNoList());
			if (getNoList().size() == 1) {
				stdev_no.add((double) 0);
			} else {
				for (int i = 0; i < list.size(); i++) {
					stdev_no.add(stdev(list.get(i)));
				}
			}
		}
	}

	/**
	 * Gaussian Probability density function
	 * 
	 * @param instance
	 * @param avg
	 * @param stdev
	 * @return
	 */
	public double calculate_probability(double instance, double avg,
			double stdev) {
		double denom1 = stdev * Math.sqrt(2 * Math.PI);
		double denom2 = 2 * Math.pow(stdev, 2);
		double num = Math.pow((instance - avg), 2);
		double prob = (1 / denom1) * Math.exp(-(num / denom2));
		return prob;
	}

	/**
	 * Calculate the probability of the test instance
	 * belonging to one of the classes using the
	 * Gaussian Probability density function.
	 * 
	 * @param instance
	 * @return
	 */
	public int getPrediction(double[] instance) {
		double prob_yes = 1.0;
		double prob_no = 1.0;
		int length = (int)num_attributes;
		
		// Calculate the probability of the test instance belonging
		// to the yes class
		if (!getYesList().isEmpty()) {
			for (int i = 0; i < length; i++) {
				prob_yes *= calculate_probability(instance[i],
						getYesAvg().get(i), getYesStdev().get(i));
			}
			prob_yes *= getYesCount();
		}

		// Calculate the probability of the test instance belonging
		// to the no class
		if (!getNoList().isEmpty()) {
			if (getNoList().size() == 1) {
				prob_no = 0;
			} else {
				for (int i = 0; i < length; i++) {
					prob_no *= calculate_probability(instance[i], getNoAvg()
							.get(i), getNoStdev().get(i));
				}
			}
			prob_no *= getNoCount();
		}

		// Return 1 if number of yes' is greater than or equal 
		// to the number of no's. Return 0 otherwise
		if (prob_yes > prob_no || prob_yes == prob_no) {
			return 1;
		} else {
			return 0;
		}
	}
}
