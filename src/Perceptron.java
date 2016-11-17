import java.util.ArrayList;

import javax.xml.crypto.Data;

public class Perceptron {
	double[] weight;
	double output;
	double delta;
	double[] weightDelta;
	double a = 0.3;

	public void randomWeight(int length) {
		weightDelta = new double[length + 1];
		weight = new double[length + 1];
		for (int i = 0; i < weight.length; i++) {
				weight[i] = Math.random() * 4-2;
		}
	}

	public double cal(double[] input) {
		double temp = 0;
		// System.out.print("input : ");
		for (int i = 0; i < weight.length; i++) {
			// System.out.print(input[i]+" ");
			temp += weight[i] * input[i];
		}
		// System.out.print("\nweight: ");
		// for(int i = 0 ; i < weight.length; i++){
		// System.out.print(weight[i]+" ");
		// }
		// System.out.println();
		output = 1;
		output /= (1 + Math.exp(-temp));
		// System.out.println(output);
		return output;
	}

	// output
	public double learnCorreter(double learn, double[] input, double expected) {
		
		delta = (output) * (1 - output) * (expected);
		
		for (int i = 0; i < weight.length; i++) {
			weightDelta[i] =  (a*weightDelta[i])+(learn * delta * input[i]);
			weight[i] += weightDelta[i];
		}
		
		return delta * delta;

	}
}
