/* Ankan Roy - Personal Project - Using Java to Develop an Iris Flower Classification Algortithm */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
This is my representation of an Iris flower classification program. There are three main steps to the program:
- Initialize variables that will store data, including length and with of sepal/petal
- Train data, or store all of the data collected from a csv file to be used for prediction
- Predict the species of Iris using the K nearest neighbors (K-NN) algorithm
*/
public class IrisDetector {
	/* 
 	Private instance variable of type List which can hold "IrisData" objects. 
 	We will be using this variable to hold all of the data for each flower in the Iris Data Set 
  	*/
	private List<IrisData> trainingData;
	// Constructor which, when called, creates an object of the "IrisDetector" class with an initialized empty List "trainingData"
	public IrisDetector() {
		trainingData = new ArrayList<>();
	}
	/* 
 	A static inner class which defines certain aspects of an Iris flower:
 	- Length of sepal
  	- Length of petal
   	- Witdh of sepal
    	- Width of petal
     	- Species name
      	- Distance, which will be used to calculate variation of user-provided dimensions with the dimensions provided in the .csv file
     	*/
	public static class IrisData {
		private double sepalLength;
		private double sepalWidth;
		private double petalLength;
		private double petalWidth;
		private String species;
		private double distance;

		public IrisData(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String species) {
			this.sepalLength = sepalLength;
			this.sepalWidth = sepalWidth;
			this.petalLength = petalLength;
			this.petalWidth = petalWidth;
			this.species = species;
		}

		public double getSepalLength() {
			return sepalLength;
		}

		public double getSepalWidth() {
			return sepalWidth;
		}

		public double getPetalLength() {
			return petalLength;
		}

		public double getPetalWidth() {
			return petalWidth;
		}

		public String getSpecies() {
			return species;
		}

		public void setDistance(double distance) {
			this.distance = distance;
		}
		
		public double getDistance() {
			return distance;
		}
	}

	// This method will traverse all the lines of the Iris Data Set .csv file and add the data in the file to "trainingData"
	public void loadTrainingData(String csvFilePath) throws IOException {
	    // BufferedReader will help read lines within a file
	    try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
	        String line;
		// Traverse every row of the .csv file and add the data into "trainingData"
	        while ((line = br.readLine()) != null) {
		    /* 
      			Uses the .split() method to separate the length and width of the sepal and 
      			petal along with the species name with a comma 
	 	    */
	            String[] data = line.split(",");
		    /* 
      			The .csv file should have 5 values:
      			- Length of sepal and petal
	 		- Width of sepal and petal
    			- Species name
       			If it does not have 5 values, print an error message
		    */
	            if (data.length != 5) {
	                System.err.println("Invalid data format in the CSV file: " + line);
	                continue;
	            }
		    /* 
      			Variables to hold dimensions of the flower. If the data in the .csv file cannot be parsed into a Double type, 
      			it returns an error message. This does not necessarily mean that the program is wrong, as it can mean that the data is 
	 		something such as the title of a column, and if so, simply delete the row with the titles.
		    */
	            double sepalLength, sepalWidth, petalLength, petalWidth;
	            try {
	                sepalLength = Double.parseDouble(data[0]);
	                sepalWidth = Double.parseDouble(data[1]);
	                petalLength = Double.parseDouble(data[2]);
	                petalWidth = Double.parseDouble(data[3]);
	            } catch (NumberFormatException e) {
	                System.err.println("Invalid numeric data in the CSV file: " + line);
	                continue;
	            }
		    // Stores the species name in "species" String variable, and uses .trim() to delete all leading and trailing spaces
	            String species = data[4].trim();
		    // Add the data to "trainingData"
	            trainingData.add(new IrisData(sepalLength, sepalWidth, petalLength, petalWidth, species));
	        }
	    }
	}

	// This method predicts the species of Iris based on the data provided using the KNN algorithm
	public String predictSpecies(double sepalLength, double sepalWidth, double petalLength, double petalWidth, int k) {
		/* Creates an empty List "neighbors" which will hold the distance between the user-provided data points and the new data points */
		List<IrisData> neighbors = new ArrayList<>();
		/* A for loop that iterates through each IrisData object in the trainingData list. In each iteration, 
  		   we calculate the euclidian distance between the current data object and the new data point.*/
		for (IrisData data : trainingData) {
			double distance = Math.sqrt(Math.pow(sepalLength - data.getSepalLength(), 2)
					+ Math.pow(sepalWidth - data.getSepalWidth(), 2)
					+ Math.pow(petalLength - data.getPetalLength(), 2)
					+ Math.pow(petalWidth - data.getPetalWidth(), 2));
			// After calculating the distance, store it in the "data" object using the "setDistance" method
			data.setDistance(distance);
			// Add the data to the "neighbors" List
			neighbors.add(data);
		}

		/* 
  		Lamda expression that sorts each distance in "neighbors" List in ascending order. 
  		- The lambda expression compares two "IrisData" objects, namely "a" and "b"
     		- Sorting uses a comparator to determine the order in which the "IrisData" objects are placed
		*/
		neighbors.sort((a, b) -> Double.compare(a.getDistance(), b.getDistance()));

		/* 
  		Initializes an empty HashMap called speciesCount, which will store the count of occurrences of each species. 
    		- The species name is represented as the key
      		- The count is represented as an integer value
		*/
		Map<String, Integer> speciesCount = new HashMap<>();
		// Count the occurrences of each species up to "k" times as specified in the main method
		for (int i = 0; i < k; i++) {
			// Retrieves the i-th nearest neighbor from the sorted "neighbbors"
			IrisData neighbor = neighbors.get(i);
			/* 
   			Updates the count of the species
      			- Checks if the "speciesCount" map already contains the name as a key
	 		- If it does, return the count associated with the key and increment
    			- If not, return 0 and increment
   			*/
			speciesCount.put(neighbor.getSpecies(), speciesCount.getOrDefault(neighbor.getSpecies(), 0) + 1);
		}

		// Find the most common species
		String predictedSpecies = "";
		int maxCount = 0;
		// Iterate the "species" in "speciesCount" map
		for (String species : speciesCount.keySet()) {
			// Int "count" variable to hold the count of the particular species
			int count = speciesCount.get(species);
			// Update "predictedSpecies" with the current "species" if "count" > "maxCount"
			if (count > maxCount) {
				predictedSpecies = species;
				// Update maxCount accordingly
				maxCount = count;
			}
		}

		// Returns a string that represents the predicted species
		return predictedSpecies;
	}

	public static void main(String[] args) {
		// Creates a new instance of the IrisDetector class
		IrisDetector classifier = new IrisDetector();
		try {
			/* 
   			Calls loadTrainingData() method with the filepath to the Iris Data Set .csv file as the parameter. 
   			Replace FILEPATH with the filepath of where you saved your .csv file on your computer.
      			*/
			classifier.loadTrainingData("FILEPATH");
			// User provides the dimensions for prediction
			double sepalLength = 6.5;
			double sepalWidth = 3;
			double petalLength = 5.4;
			double petalWidth = 2.4;
			int k = 3; // Number of neighbors to consider (Note: a higher "k" value will lead to more accurate results)
			// Calls the predictSpecies() method and stores the result in a string "predictedSpecies"
			String predictedSpecies = classifier.predictSpecies(sepalLength, sepalWidth, petalLength, petalWidth, k);
			// Prints the predicted species to the console
			System.out.println("Predicted species: " + predictedSpecies);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
