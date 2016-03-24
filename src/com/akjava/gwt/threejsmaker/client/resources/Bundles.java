package com.akjava.gwt.threejsmaker.client.resources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface Bundles extends ClientBundle {
Bundles INSTANCE=GWT.create(Bundles.class);
	TextResource classbase();
	TextResource filenames();
	TextResource wrap();
	TextResource wrapreturn();
	TextResource get();
	TextResource set();
	TextResource getset();
	TextResource jsclass();
}
