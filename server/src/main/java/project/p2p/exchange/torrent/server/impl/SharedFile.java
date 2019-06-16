package project.p2p.exchange.torrent.server.impl;

import java.util.Map;
import java.util.Set;

public class SharedFile {

	private String name;
	private Set<String> seeders;
	private Map<String, Object> additionalProperties;
	
	public String getName() {
		return name;
	}
	
	public Set<String> getSeeders() {
		return seeders;
	}
	
	public void addSeeder(String seeder) {
		if (seeder != null) {
			if (!seeders.contains(seeder)) {
				seeders.add(seeder);
			}
		}
	}
	
	public boolean isSeededBy(String seeder) {
		return seeders.contains(seeder);
	}
}
