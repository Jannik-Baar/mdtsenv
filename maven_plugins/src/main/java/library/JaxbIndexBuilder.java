package library;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Plugin to generate jaxb.index file containing all classes from the library.
 * jaxb.index will be written to library/src/main/resources/library/model/jaxb.index
 * to generate jaxb.index: mvn pgmtss:maven_plugins:1.0:build-jaxbindex -e
 */
@Mojo(name = "build-jaxbindex", defaultPhase = LifecyclePhase.CLEAN)
public class JaxbIndexBuilder extends AbstractMojo {

    @Parameter(property = "scope")
    String scope;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    String dir;
    String baseDir;
    ArrayList<String> relevantPaths = new ArrayList<>();

    public void execute() {
        dir = project.getBasedir().toString();
        relevantPaths.add(dir);

        baseDir = dir.substring(0, dir.lastIndexOf(File.separator));
        List<Dependency> dependencies = project.getDependencies();
        for (Dependency dep : dependencies) {
            if (dep.getArtifactId().contains("library")) {
                relevantPaths.add(baseDir + File.separator + dep.getArtifactId());
            }
        }
        buildJaxbIndex();
    }

    private void buildJaxbIndex() {
        ArrayList<String> allFiles = new ArrayList<>();
        listSuitableFilesForPaths(relevantPaths, allFiles);

        StringBuilder stringBuilder = new StringBuilder();

        for (String string : allFiles) {
            System.out.println("STRING: " + string);
            String subString = null;
            if (string.contains("model")) {
                subString = string.substring(string.indexOf("model" + File.separator) + ("model.".length()), string.indexOf(".java")).replace(File.separator, ".");
            }
            System.out.println("SUBSTRING: " + subString);

            try {
                String readFile = new String(Files.readAllBytes(Paths.get(string)));
                if (readFile.contains("public interface")) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            stringBuilder.append(subString).append("\n");
        }

        try {
            File targetDir = new File(dir + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "library");
            while (!targetDir.exists()) {
                targetDir.mkdirs();
                if (!targetDir.isDirectory()) {
                    throw new IOException();
                }
                targetDir = new File(dir + File.separator+ "src" + File.separator + "main" + File.separator+ "resources" + File.separator+ "library" + File.separator + "model");
            }
            File target = new File(dir + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "library" + File.separator + "model" + File.separator + "jaxb.index");
            if (!target.exists()) {
                System.out.println("TARGET: " + target);
                target.createNewFile();
            }
            Files.write(target.toPath(), stringBuilder.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("WRITE TARGET: " + target.toPath());
            System.out.println("CONTENT: " + stringBuilder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Iterates over the given Path List and adds all suitable Filepaths within it.
     *
     * @param paths
     * @param filePaths
     */
    private void listSuitableFilesForPaths(final ArrayList<String> paths, ArrayList<String> filePaths) {
        for (String path : paths) {
            System.out.println("PATH: " + path);
            listSuitableFilesForFolder(new File(path + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "library"), filePaths);
        }
    }

    /**
     * If a file is within a libraries model folder and ends with '.java', add to paths
     *
     * @param folder
     * @param paths
     */
    private void listSuitableFilesForFolder(final File folder, ArrayList<String> paths) {
        if (folder.listFiles() == null) {
            return;
        }
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            System.out.println("FILE :" + fileEntry);
            String path = fileEntry.getAbsolutePath();
            if (!path.contains("library") || path.contains("test")) {
                continue;
            }
            if (fileEntry.isDirectory()) {
                listSuitableFilesForFolder(fileEntry, paths);
            } else if ((path.contains("model") && path.contains(".java") && !paths.contains(fileEntry.getAbsolutePath()))) {
                paths.add(fileEntry.getAbsolutePath());
            }
        }
    }
}