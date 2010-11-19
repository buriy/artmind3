package engine;

public class Synapse {
	private static final int NOT_INITIALIZED = -1;
	private final int bit;
	private final int cell;
	private int perm;

	public Synapse(int bit, int cell) {
		this.bit = bit;
		this.cell = cell;
		this.perm = NOT_INITIALIZED;
	}

	void setPermanence(int value) {
		if(perm != NOT_INITIALIZED)
			throw new IllegalStateException("Synapse permanence value was already set");
		this.perm = value;
	}

	void addPermanence(int value) {
		if(perm == NOT_INITIALIZED)
			throw new IllegalStateException("Should use setPermanence on new Synapse");
		perm = Math.min(perm + value, 100);
	}

	public boolean decPermanence(int value, int minimum) {
		if(perm == NOT_INITIALIZED)
			throw new IllegalStateException("Should use setPermanence on new Synapse");
		perm -= value;
		return perm < minimum;
	}

	public int bit() {
		return bit;
	}

	public int cell() {
		return cell;
	}

	public int permanence() {
		return perm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bit;
		result = prime * result + cell;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Synapse))
			return false;
		Synapse other = (Synapse) obj;
		if (bit != other.bit)
			return false;
		if (cell != other.cell)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Sy("+bit+","+cell+")="+perm;
	}

	public boolean isNew() {
		return perm == NOT_INITIALIZED;
	}
}
