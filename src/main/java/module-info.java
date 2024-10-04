/*
 * Copyright (C) 2015, 2024 Green Screens Ltd.
 */
module io.greenscreens.ffm{
	
	requires java.base;
	requires transitive jdk.unsupported;
	requires org.slf4j;

	exports io.greenscreens.foreign;
	exports io.greenscreens.wkhtmltox;
	exports io.greenscreens.wkhtmltox.callback;

}
