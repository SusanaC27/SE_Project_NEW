package pt.ulisboa.tecnico.learnjava.sibs.domain.states;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;

public class Retry implements State {

	@Override
	public void process(TransferOperation wrapper, Services services) throws AccountException {
		if (wrapper.counter > 0) {
			wrapper.counter--;
			State state = wrapper.getLastState();
			wrapper.setState(state);
		} else {
			wrapper.setState(new Error());
			wrapper.counter = 3;
			if (wrapper.getLastState() instanceof Withdrawn) {
				services.deposit(wrapper.getSourceIban(), wrapper.getValue());
			} else if (wrapper.getLastState() instanceof Deposited) {
				services.deposit(wrapper.getSourceIban(), wrapper.getValue());
				services.withdraw(wrapper.getTargetIban(), wrapper.getValue());
			}
		}
	}

	@Override
	public void cancel(TransferOperation wrapper, Services services) throws AccountException {
	}
}
