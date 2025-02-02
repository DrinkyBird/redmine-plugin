package hudson.plugins.redmine;

import hudson.security.GroupDetails;;

public class RedmineGroupDetails extends GroupDetails {
	private String name;
	
	public RedmineGroupDetails(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
