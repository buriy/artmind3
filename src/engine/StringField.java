package engine;

import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class StringField {
	private SortedMap<String, Double> data;

	public StringField() {
		this.data = new TreeMap<String, Double>();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("");
		Set<Entry<String,Double>> entries = data.entrySet();
		double maxValue = 0;
		String maxKey = "";
		for (Entry<String, Double> entry : entries) {
			if(entry.getValue() > maxValue){
				maxValue = entry.getValue();
				maxKey = entry.getKey();
			}
		}
		result.append(maxKey+"\n");
		result.append("(\n");
		DecimalFormat df = new DecimalFormat("#.#");
		for (Entry<String, Double> entry : entries) {
			double value = entry.getValue().doubleValue();
			result.append("  " + entry.getKey() + "=");
			result.append(df.format(value) + "\n");
		}
		return result + ")";
	}

	public Double get(String key) {
		return data.get(key);
	}

	public void reset() {
		data.clear();
	}

	public void set(String key, Double value) {
		data.put(key, value);
	}
}
