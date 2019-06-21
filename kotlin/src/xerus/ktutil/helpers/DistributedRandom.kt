package xerus.ktutil.helpers

import java.util.*

class DistributedRandom<T> {
	
	private var distribution: HashMap<T, Float>? = null
	private var sum: Double = 0.toDouble()
	
	val isEmpty: Boolean
		get() = distribution!!.isEmpty()
	
	init {
		distribution = HashMap()
	}
	
	fun clear() {
		distribution = HashMap()
		sum = 0.0
	}
	
	fun add(value: T, probability: Float) {
		if(distribution!![value] != null) {
			sum -= distribution!![value]!!.toDouble()
		}
		distribution!!.put(value, probability)
		sum += probability.toDouble()
	}
	
	fun generate(): T? {
		var rand = Math.random() * sum
		for((key, value) in distribution!!) {
			rand -= value.toDouble()
			if(rand < 0)
				return key
		}
		if(distribution!!.size == 0)
			return null
		throw RuntimeException("Randomness didn't go as expected!")
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