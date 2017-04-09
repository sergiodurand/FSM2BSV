package lcr;

import java.util.List;

public class FSM {
	
	private String name;
	private State resetState;
	private List<State> states;
	private List<Transition> transitions;
	private List<DataType> dataTypes;
	private List<Signal> signals;
	private List<Variable> variables;
	private List<BRAM> BRAMs;
	private Boolean thereIsVector = false;
	private Boolean thereIsBRAM = false;

	public Boolean getThereIsBRAM() {
		return thereIsBRAM;
	}

	public void setThereIsBRAM(Boolean thereIsBRAM) {
		this.thereIsBRAM = thereIsBRAM;
	}

	public List<BRAM> getBRAMs() {
		return BRAMs;
	}

	public void setBRAMs(List<BRAM> bRAMs) {
		BRAMs = bRAMs;
	}

	public Boolean getThereIsVector() {
		return thereIsVector;
	}

	public void setThereIsVector(Boolean thereIsVector) {
		this.thereIsVector = thereIsVector;
	}

	public List<DataType> getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(List<DataType> dataTypes) {
		this.dataTypes = dataTypes;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public State getResetState() {
		return resetState;
	}
	
	public void setResetState(State resetState) {
		this.resetState = resetState;
	}
	
	public List<State> getStates() {
		return states;
	}
	
	public void setStates(List<State> states) {
		this.states = states;
	}
	
	public List<Transition> getTransitions() {
		return transitions;
	}
	
	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	public List<Signal> getSignals() {
		return signals;
	}

	public void setSignals(List<Signal> signals) {
		this.signals = signals;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}
	
}
