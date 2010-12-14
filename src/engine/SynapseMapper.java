package engine;

public class SynapseMapper{
	private int cells;
	private int bits;
	
	public SynapseMapper(Options options) {
		cells = options.NEURON_CELLS;
		bits = options.SENSORS;
	}
	
	public int s2cell(int synapse){
		return synapse % cells;
	}

	public int s2bit(int synapse){
		return synapse / cells;
	}
	
	public int bc2s(int bit, int cell){
		return bit * cells + cell;
	}
	
	public int synapses(){
		return bits * cells;
	}
}