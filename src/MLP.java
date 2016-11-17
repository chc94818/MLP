import java.util.ArrayList;

public class MLP {
	double[][] vT;
	double learn;
	double convergence;
	double[][] dataTrain;
	double[][] dataTest;
	double[] weight;
	double maxD;
	double minD;
	double testl = 0.5;
	double testc = 0.001;
	double maxTemp;
	double minTemp;
	double reguTemp;
	double reguNu;
	double[][] data;
	double rmse;
	Perceptron p[][];

	public void initialData(ArrayList<double[]> temp) {
		
		data = new double[temp.size()][temp.get(0).length];
		
		for (int i = 0; i < temp.size() ; i++) {
			for(int j = 0 ; j < temp.get(0).length ; j++){
				data[i][j] = temp.get(i)[j];
			}
		}
		
		for(int j = 0 ; j < data[0].length-1 ; j++){
			maxTemp = 0;
			minTemp = 1000000000;
			for (int i = 0; i < data.length ; i++) {
				if(data[i][j]>maxTemp){
					maxTemp = data[i][j];
				}
				if(data[i][j]<minTemp){
					minTemp = data[i][j];
				}
			}
			reguNu = maxTemp-minTemp;
			for (int i = 0; i < data.length ; i++) {
				
				reguTemp = (data[i][j] - minTemp)/reguNu;
				data[i][j] = reguTemp;
			}
		}
		
	}

	public void randomData() {
		double[] exchange = new double[data[0].length];
	
		for (int i = 0; i < data.length * 3; i++) {
			int index1 = (int) (Math.random() * data.length);
			int index2 = (int) (Math.random() * data.length);
			exchange = data[index1];
			data[index1] = data[index2];
			data[index2] = exchange;
		}
	}

	public double cal(double learnTemp,
			Perceptron temp[][], ArrayList<Double> ex) {
		int count = 1;
		int ctemp = 2;
		while (ctemp < ex.size()) {
			count++;
			ctemp *= 2;
		}
		vT = new double[ex.size()][count + 1];
		for (int i = 0; i < vT.length; i++) {
			vT[i][0] = ex.get(i);
			int s = i;
			for (int j = count; j > 0; j--) {
				if (s >= Math.pow(2, j - 1)) {
					s -= Math.pow(2, j - 1);
					vT[i][j] = 1;
				} else {
					vT[i][j] = 0;
				}
			}

		}

		randomData();
		
		
		
		p = temp;
		learn = learnTemp;
		convergence = testc;

		if (learn == 0) {
			learn = testl;
		}
		

		dataTrain = new double[data.length / 3 * 2][data[0].length + 1];
		dataTest = new double[data.length - data.length / 3 * 2][data[0].length + 1];
		maxD = -100;
		minD = 100;

		for (int i = 0; i < dataTrain.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				dataTrain[i][0] = -1;
				dataTrain[i][j + 1] = data[i][j];
			}
			if (dataTrain[i][dataTrain[i].length - 1] > maxD) {
				maxD = dataTrain[i][dataTrain[i].length - 1];
				// System.out.println("max:"+dataTrain[i][dataTrain[i].length-1]);
			}
			if (dataTrain[i][dataTrain[i].length - 1] < minD) {
				minD = dataTrain[i][dataTrain[i].length - 1];
				// System.out.println("min:"+
				// dataTrain[i][dataTrain[i].length-1]);
			}
		}
		for (int i = dataTrain.length; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				dataTest[i - dataTrain.length][0] = -1;
				dataTest[i - dataTrain.length][j + 1] = data[i][j];
			}
			if (dataTest[i - dataTrain.length][dataTest[i - dataTrain.length].length - 1] > maxD) {
				maxD = dataTest[i - dataTrain.length][dataTest[i
						- dataTrain.length].length - 1];
				// System.out.println("max:"+maxD);
			}
			if (dataTest[i - dataTrain.length][dataTrain[i - dataTrain.length].length - 1] < minD) {
				minD = dataTest[i - dataTrain.length][dataTest[i
						- dataTrain.length].length - 1];
				// System.out.println("max:"+minD);
			}
		}

		// 初始化node
		for (int i = 0; i < p[p.length - 1].length; i++) {
			p[p.length - 1][i].randomWeight(dataTrain[0].length - 2);
		}
		for (int i = p.length - 2; i > -1; i--) {
			for (int j = 0; j < p[i].length; j++) {
				p[i][j].randomWeight(p[i + 1].length);
			}
		}

		double e;
		double s = 0;
		rmse = 1000;
		while ( rmse> convergence && s < 10000) {
			rmse = 0;
			s++;
			for (int d = 0; d < dataTrain.length; d++) {

				e = dataTrain[d][dataTrain[d].length - 1];
				double[] eA = new double[vT[0].length];
				for (int x = 0; x < vT.length; x++) {
					if (e == vT[x][0]) {
						eA = vT[x];
						break;
					}
				}

				rmse += train(dataTrain[d], p.length - 1, eA);
			}
			
			rmse /= dataTrain.length;
			System.out.println("s : " + s);
			System.out.println("rmse : " + rmse);
		}
		double ans =test(dataTest);
		System.out.println("rate: "+ans);
		return ans;
	}

	public double train(double[] input, int layer, double[] expected) {
		double eTemp;
		rmse = 0;
		// for (int i = 0; i < dataTrain.length; i++) {
		// for(int j = 0 ; j < dataTrain[0].length ; j++){
		// System.out.print(dataTrain[i][j]+" ");
		// }
		// System.out.println();
		// }

		// 計算

		double[] nextInput = new double[p[layer].length + 1];
		nextInput[0] = -1;
		for (int i = 0; i < p[layer].length; i++) {
			// System.out.println("第" + (layer) + "層,第" + (i + 1) + "個");
			nextInput[i + 1] = p[layer][i].cal(input);
		}
		if (layer == 0) {
			for (int i = 0; i < p[layer].length; i++) {
				eTemp = 0;

				eTemp = expected[i + 1] - p[layer][i].output;

				// System.out.print("輸出層 : "+(i+1)+" eTemp : "+eTemp);
				rmse += p[layer][i].learnCorreter(learn, input, eTemp);
				// System.out.println(" delta : "+p[layer][i].delta);
				// for (int k = 0; k < p[layer][i].weight.length; k++) {
				// System.out.print(p[layer][i].weight[k]+" ");
				// }
				// System.out.println();
				// System.out.println("==========================================");
			}
			rmse /= 2;
			return rmse;
		} else {
			rmse = train(nextInput, layer - 1, expected);
		}

		for (int i = 0; i < p[layer].length; i++) {
			eTemp = 0;
			for (int j = 0; j < p[layer - 1].length; j++) {
				eTemp += p[layer - 1][j].delta * p[layer - 1][j].weight[i + 1];
			}
			// System.out.println("隱藏層 : "+layer+" 第"+(i+1)+"個"
			// +" eTemp : "+eTemp);
			p[layer][i].learnCorreter(learn, input, eTemp);
		}

		return rmse;
	}

	public double test(double[][] input) {
		for (int i = 0; i < vT.length; i++) {
			for (int j = 0; j < vT[0].length; j++) {
				System.out.print(vT[i][j] + " ");
			}
			System.out.println();
		}
		
		double c = 0;
		double idRate = 0;
		double[] eTemp;
		System.out.println("============================================");
		for (int i = 0; i < input.length; i++) {
			eTemp = testWeight(input[i], p.length - 1);
			//System.out.print("output : ");
			for (int j = 1; j < eTemp.length; j++) {
				if (eTemp[j] > 0.5) {
					eTemp[j] = 1;
				} else {
					eTemp[j] = 0;
				}
				///System.out.print(eTemp[j] + " ");

			}
			//System.out.println();
			for (int v = 0; v < vT.length; v++) {
				for (int j = 1; j < eTemp.length; j++) {
					//System.out.println(vT[v][j] + " " + eTemp[j]);
					if (eTemp[j] != vT[v][j]) {
						break;
					}
					if (j == eTemp.length - 1) {
						if(vT[v][0]==input[i][input[i].length - 1]){
							c++;
						}
					}

				}
			}

			//System.out.println("ans : "+input[i][input[i].length - 1]);

		}
		idRate = c/dataTest.length * 100;
		return idRate;
	}

	public double[] testWeight(double[] input, int layer) {
		double[] nextInput = new double[p[layer].length + 1];
		nextInput[0] = -1;
		for (int i = 0; i < p[layer].length; i++) {
			// System.out.println("第" + (layer) + "層,第" + (i + 1) + "個");
			nextInput[i + 1] = p[layer][i].cal(input);
		}
		if (layer == 0) {
			return nextInput;
		} else {
			nextInput = testWeight(nextInput, layer - 1);
		}
		return nextInput;
	}
}
