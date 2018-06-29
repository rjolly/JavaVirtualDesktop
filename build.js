mkdir("build");
mkdir("build/classes");

javac("src", "build/classes");
copy("res", "build/classes");

jar("jvd.jar", "build/classes", ".*", "manifest.mf");
