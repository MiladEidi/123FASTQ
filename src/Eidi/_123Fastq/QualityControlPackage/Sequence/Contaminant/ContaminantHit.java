package Eidi._123Fastq.QualityControlPackage.Sequence.Contaminant;

public class ContaminantHit {

	private Contaminant contaminant;
	private int direction;
	private int length;
	private int percentID;

	public static final int FORWARD = 1;
	public static final int REVERSE = 2;

	public ContaminantHit (Contaminant contaminant, int direction, int length, int percentID) {
		if (direction == FORWARD || direction == REVERSE) {
			this.direction = direction;
		}
		else {
			throw new IllegalArgumentException("Direction of hit must be FORWARD or REVERSE");
		}
		this.contaminant = contaminant;
		this.length = length;
		this.percentID = percentID;
	}


	public Contaminant contaminant () {
		return contaminant;
	}

	public int direction () {
		return direction;
	}

	public int length () {
		return length;
	}

	public int percentID () {
		return percentID;
	}

	public String toString () {
		return contaminant.name()+" ("+percentID+"% over "+length+"bp)";
	}
}
