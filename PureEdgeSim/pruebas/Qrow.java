package pruebas;

public class Qrow {
	private String rule;
	private int accion;
	private double value;
	private int updatesCount;
	
	public Qrow(String rule, int accion, double value) {
		this.rule = rule;
		this.accion = accion;
		this.value = value;
		this.updatesCount = 1;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public int getAccion() {
		return accion;
	}

	public void setAccion(int accion) {
		this.accion = accion;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getUpdatesCount() {
		return updatesCount;
	}

	public void setUpdatesCount(int updatesCount) {
		this.updatesCount = updatesCount;
	}
	
	public void increaseUpdatesCount() {
		this.updatesCount++;
	}
	
	

}
