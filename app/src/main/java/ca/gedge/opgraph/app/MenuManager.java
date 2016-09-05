package ca.gedge.opgraph.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import ca.gedge.opgraph.util.ServiceDiscovery;

public class MenuManager {
	
	private final static Logger LOGGER = Logger.getLogger(MenuManager.class.getName());
	
	private List<MenuProvider> menuProviders;
	
	public MenuManager() {
		super();
		
		findProviders();
	}
	
	private void findProviders() {
		// Discover menu extensions
		this.menuProviders = new ArrayList<MenuProvider>();
		final ServiceDiscovery discovery = ServiceDiscovery.getInstance();
		for(Class<? extends MenuProvider> menu : discovery.findProviders(MenuProvider.class)) {
			try {
				menuProviders.add(menu.newInstance());
			} catch(InstantiationException exc) {
				LOGGER.warning("Could not instantiate menu provider: " + menu.getName());
			} catch(IllegalAccessException exc) {
				LOGGER.warning("Could not instantiate menu provider: " + menu.getName());
			}
		}
	}
	
	public List<MenuProvider> getMenuProviders() {
		return Collections.unmodifiableList(this.menuProviders);
	}

}
