package io.cucucumber.jupiter.engine.resource;

import cucumber.runtime.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;

import static io.cucucumber.jupiter.engine.resource.ClasspathSupport.determineFullyQualifiedResourceName;

public class Resources {
    private static final String CLASSPATH_SCHEME = "classpath:";

    private Resources() {

    }

    public static BiFunction<Path, Path, Resource> createPackageResource(String packageName) {
        return (baseDir, resource) -> new PackageResource(baseDir, packageName, resource);
    }

    public static BiFunction<Path, Path, Resource>  createUriResource() {
        return (baseDir, resource) -> new UriResource(resource);
    }

    public static BiFunction<Path, Path, Resource> createClasspathRootResource() {
        return ClasspathResource::new;
    }

    public static BiFunction<Path, Path, Resource> createClasspathResource(String classpathResourceName) {
        return (baseDir, resource) -> new ClasspathResource(classpathResourceName, resource);
    }

    private static class ClasspathResource implements Resource {

        private final String uri;
        private final Path resource;

        ClasspathResource(Path baseDir, Path resource) {
            this.uri = CLASSPATH_SCHEME + baseDir.relativize(resource).toString();
            this.resource = resource;
        }

        ClasspathResource(String classpathResourceName, Path resource) {
            this.uri = CLASSPATH_SCHEME + classpathResourceName;
            this.resource = resource;
        }

        @Override
        public String getPath() {
            return uri;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return Files.newInputStream(resource);
        }
    }

    private static class UriResource implements Resource {
        private final Path resource;

        UriResource(Path resource) {
            this.resource = resource;
        }

        @Override
        public String getPath() {
            return resource.toUri().toString();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return Files.newInputStream(resource);
        }
    }

    private static class PackageResource implements Resource {
        private final Path resource;
        private final String uri;

        PackageResource(Path baseDir, String packageName, Path resource) {
            String packagePath = ClasspathSupport.packagePath(packageName);
            this.uri = CLASSPATH_SCHEME + determineFullyQualifiedResourceName(baseDir, packagePath, resource);
            this.resource = resource;
        }

        @Override
        public String getPath() {
            return uri;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return Files.newInputStream(resource);
        }
    }

}