package xerus.util.helpers;

import java.util.HashMap;
import java.util.Map;

public class DistributedRandom<T> {
	
	private HashMap<T,Float> distribution;
	private double sum;
	
	public DistributedRandom() {
		distribution = new HashMap<>();
	}
	
	public void clear() {
		distribution = new HashMap<>();
		sum = 0;
	}
	
	public boolean isEmpty() {
		return distribution.isEmpty();
	}
	
	public void add(T value, float probability) {
		if (distribution.get(value) != null) {
			sum -= distribution.get(value);
		}
		distribution.put(value, probability);
		sum += probability;
	}
	
	public T generate() {
		double rand = Math.random() * sum;
		for (Map.Entry<T,Float> entry : distribution.entrySet()) {
			rand -= entry.getValue();
			if (rand < 0)
				return entry.getKey();
		}
		if (distribution.size() == 0)
			return null;
		throw new RuntimeException("Randomness didn't go as expected!");
	}

	/*public void test() {
		int multiplier = 1000;
		int max = multiplier * 100;
		Map<Integer, MutableInt> results = new HashMap<>();
		for(Integer i : distribution.keySet())
			results.put(i, new MutableInt());
		for (int i = 0; i < max; i++) {
			results.get(generate()).increment();
		}
		for(Entry<Integer, MutableInt> entry : results.entrySet()) {
			float res = entry.getValue().floatValue() / multiplier;
			double expected = distribution.get(entry.getKey())/sum * 100;
			double deviation = Math.abs(1 - res/expected);
		}
	}
	
	public static void main(String[] args) {
		DistributedRandom gen = new DistributedRandom();
		Random rand = new Random();
		Timer.start();
		for(int i=1; i<10000; i++)
			gen.add(i, rand.nextInt(10));
		Timer.finish("Generated");
		for(int i=0; i<3; i++) {
			Timer.start();
			gen.test();
			Timer.finish();
		}
	}*/
	
}