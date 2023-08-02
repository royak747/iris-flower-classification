import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IrisDetector {
	private List<IrisData> trainingData;

	public IrisDetector() {
		trainingData = new ArrayList<>();
	}

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

	public void loadTrainingData(String csvFilePath) throws IOException {
	    try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");
	            if (data.length != 5) {
	                System.err.println("Invalid data format in the CSV file: " + line);
	                continue;
	            }

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

	            String species = data[4].trim();
	            trainingData.add(new IrisData(sepalLength, sepalWidth, petalLength, petalWidth, species));
	        }
	    }
	}


	public String predictSpecies(double sepalLength, double sepalWidth, double petalLength, double petalWidth, int k) {
		// Find the k-nearest neighbors
		List<IrisData> neighbors = new ArrayList<>();
		for (IrisData data : trainingData) {
			double distance = Math.sqrt(Math.pow(sepalLength - data.getSepalLength(), 2)
					+ Math.pow(sepalWidth - data.getSepalWidth(), 2)
					+ Math.pow(petalLength - data.getPetalLength(), 2)
					+ Math.pow(petalWidth - data.getPetalWidth(), 2));

			data.setDistance(distance);
			neighbors.add(data);
		}

		neighbors.sort((a, b) -> Double.compare(a.getDistance(), b.getDistance()));

		// Count the occurrences of each species in the k-nearest neighbors
		Map<String, Integer> speciesCount = new HashMap<>();
		for (int i = 0; i < k; i++) {
			IrisData neighbor = neighbors.get(i);
			speciesCount.put(neighbor.getSpecies(), speciesCount.getOrDefault(neighbor.getSpecies(), 0) + 1);
		}

		// Find the most common species
		String predictedSpecies = "";
		int maxCount = 0;
		for (String species : speciesCount.keySet()) {
			int count = speciesCount.get(species);
			if (count > maxCount) {
				predictedSpecies = species;
				maxCount = count;
			}
		}

		return predictedSpecies;
	}

	public static void main(String[] args) {
		IrisDetector classifier = new IrisDetector();
		try {
			classifier.loadTrainingData("/Users/ankanroy/Desktop/IrisDataset/iris.csv");

			// Provide the new data point for prediction
			double sepalLength = 6.5;
			double sepalWidth = 3;
			double petalLength = 5.4;
			double petalWidth = 2.4;
			int k = 3; // Number of neighbors to consider

			String predictedSpecies = classifier.predictSpecies(sepalLength, sepalWidth, petalLength, petalWidth, k);
			System.out.println("Predicted species: " + predictedSpecies);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}