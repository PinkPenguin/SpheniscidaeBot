package root;

import java.util.ArrayList;

public class Command {

	public String name;
	public ArrayList<String> argumentList;
	public String body;

	public Command(String command) {
		argumentList = new ArrayList<String>();
		String[] workString = command.split(" ", 2);
		this.name = workString[0].toLowerCase();

		while (workString.length > 1 && workString[1].startsWith("-")) {
			workString = workString[1].split(" ", 2);
			this.argumentList.add(workString[0].toLowerCase());
		}

		if (workString.length > 1)
			this.body = workString[1];
	}

	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof Command))
			return false;

		Command c = (Command) o;

		return name.equals(c.name);
	}
}
